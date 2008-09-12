/* 
 * ========================================================================
 * 
 * Copyright 2006-2008 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.cargo.container.jetty;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.jetty.internal.JettyExecutorThread;
import org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedLocalContainer;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.property.User;

/**
 * A Jetty 7.x instance running embedded.
 * 
 * @version $Id: Jetty7xEmbeddedLocalContainer.java 1705 2008-09-02 13:14:55Z adriana $
 */
public class Jetty7xEmbeddedLocalContainer extends AbstractJettyEmbeddedLocalContainer
{
    /**
     * Unique container id.
     */
    public static final String ID = "jetty7x";

    /**
     * A default security realm. If ServletPropertySet.USERS has been specified, then we create a
     * default realm containing those users and then force that realm to be associated with every
     * webapp (see TODO comment on setSecurityRealm())
     */
    private Object defaultRealm;

    /**
     * The ContextHandlerCollection into which deployed webapps are added.
     */
    private Object contextHandlers;

    /**
     * The org.mortbay.jetty.handler.Handler class.
     */
    private Class handlerClass;

    /**
     * The org.mortbay.jetty.handler.HandlerCollection instance.
     */
    private Object handlers;

    /**
     * The method to call to add a handler for a webapp.
     */
    private Method addHandlerMethod;

    /**
     * The method to call to undeploy a handler for a webapp.
     */
    private Method removeHandlerMethod;

    /**
     * {@inheritDoc}
     * 
     * @see AbstractJettyEmbeddedLocalContainer#AbstractJettyEmbeddedLocalContainer(org.codehaus.cargo.container.configuration.LocalConfiguration)
     */
    public Jetty7xEmbeddedLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.Container#getId()
     */
    public String getId()
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.Container#getName()
     */
    public String getName()
    {
        return "Jetty 7.x Embedded";
    }

    /**
     * If a default realm is available, set it on the given webapp.
     * 
     * @param webapp the webapp to set the realm on
     * @throws Exception on invokation error
     */
    public void setDefaultRealm(Object webapp) throws Exception
    {
        Class userRealmClass = getClassLoader().loadClass("org.mortbay.jetty.security.UserRealm");

        if (this.defaultRealm != null)
        {
            Object securityHandler =
                webapp.getClass().getMethod("getSecurityHandler", new Class[] {}).invoke(webapp,
                    new Object[] {});
            securityHandler.getClass().getMethod("setUserRealm", new Class[] {userRealmClass})
                .invoke(securityHandler, new Object[] {this.defaultRealm});
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractJettyEmbeddedLocalContainer#doStart()
     */
    protected void doStart() throws Exception
    {
        // Server server = new Server();
        // server.setStopAtShutdown(true);
        createServerObject();

        // Connector selectConnector = new SelectChannelConnector();
        // selectConnector.setPort(new
        // Integer(getConfiguration().getPropertyValue(ServletPropertySet.PORT)));
        Class selectConnectorClass =
            getClassLoader().loadClass("org.mortbay.jetty.nio.SelectChannelConnector");
        Object connector = selectConnectorClass.newInstance();
        selectConnectorClass.getMethod("setPort", new Class[] {int.class}).invoke(
            connector,
            new Object[] {new Integer(getConfiguration()
                .getPropertyValue(ServletPropertySet.PORT))});

        // server.addConnector(selectConnector);
        Class connectorClass = getClassLoader().loadClass("org.mortbay.jetty.Connector");
        Object connectorArray =
            Array.newInstance(connectorClass, 1);
        Array.set(connectorArray, 0, connector);
        getServer().getClass().getMethod("addConnector", new Class[] {connectorClass}).invoke(
            getServer(), new Object[] {connector});

        // Set security realm
        setSecurityRealm();

        // Set up the context handler structure
        // HandlerCollection handlers = new HandlerCollection();
        // ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
        // handlers.setHandlers(new Handler[]{contextHandlers, new DefaultHandler(), new
        // RequestLogHandler()});
        // server.setHandler(handlers);
        handlerClass = getClassLoader().loadClass("org.mortbay.jetty.Handler");
        handlers =
            getClassLoader().loadClass("org.mortbay.jetty.handler.HandlerCollection")
                .newInstance();
        contextHandlers =
            getClassLoader().loadClass("org.mortbay.jetty.handler.ContextHandlerCollection")
                .newInstance();
        Object defaultHandler =
            getClassLoader().loadClass("org.mortbay.jetty.handler.DefaultHandler").newInstance();
        Object handlerArray = Array.newInstance(handlerClass, 2);
        Array.set(handlerArray, 0, contextHandlers);
        Array.set(handlerArray, 1, defaultHandler);
        handlers.getClass().getMethod("setHandlers", new Class[] {handlerArray.getClass()})
            .invoke(handlers, new Object[] {handlerArray});
        getServer().getClass().getMethod("setHandler", new Class[] {handlerClass}).invoke(
            getServer(), new Object[] {handlers});

        // Method to add a webappcontext to jetty
        addHandlerMethod =
            contextHandlers.getClass().getMethod("addHandler", new Class[] {handlerClass});

        // Method to remove a webappcontext from jetty
        removeHandlerMethod =
            contextHandlers.getClass().getMethod("removeHandler", new Class[] {handlerClass});

        // Deploy statically deployed WARs
        Iterator it = getConfiguration().getDeployables().iterator();
        while (it.hasNext())
        {
            Deployable deployable = (Deployable) it.next();

            // Only deploy WARs (packed or unpacked).
            if (deployable.getType() == DeployableType.WAR)
            {
                addHandler(createHandler(deployable));
            }
            else
            {
                throw new ContainerException("Only WAR archives are supported for deployment in "
                    + "Jetty. Got [" + deployable.getFile() + "]");
            }
        }

        // Deploy CPC. Note: The Jetty Server class offers a isStarted()
        // method but there is no isStopped() so until we find a better
        // way, we need a CPC.

        addHandler(createHandler("/cargocpc", new File(getConfiguration().getHome(),
            "cargocpc.war").getPath()));

        JettyExecutorThread jettyRunner = new JettyExecutorThread(getServer(), true);
        jettyRunner.setLogger(getLogger());
        jettyRunner.start();
    }

    /**
     * Create a WebAppContext for the Deployable. NB also force the defaultRealm to be set on it if
     * one is present.
     * 
     * @param deployable the cargo webapp to deploy
     * @return a jetty webapp
     * @throws Exception on invokation exception
     */
    public Object createHandler(Deployable deployable) throws Exception
    {
        Object handler =
            getClassLoader().loadClass("org.mortbay.jetty.webapp.WebAppContext").newInstance();

        handler.getClass().getMethod("setContextPath", new Class[] {String.class}).invoke(
            handler, new Object[] {"/" + ((WAR) deployable).getContext()});
        handler.getClass().getMethod("setWar", new Class[] {String.class}).invoke(handler,
            new Object[] {deployable.getFile()});
        /*
         * //always expand packed WARs for now. TODO allow per deployment
         * handler.getClass().getMethod("setExtractWar", new Class[] {Boolean.TYPE})
         * .invoke(handler, new Object[]{Boolean.TRUE}); //copy web-inf to allow jar replacement
         * handler.getClass().getMethod("setCopyDir", new Class[] {Boolean.TYPE}) .invoke(handler,
         * new Object[]{Boolean.TRUE});
         */
        setDefaultRealm(handler);

        return handler;
    }

    /**
     * Create a WebAppContext for the webapp given as a string. NB Also force the defaultRealm to be
     * set if one is present.
     * 
     * @param contextPath the context path for the webapp
     * @param war the webapp
     * @return a jetty webapp
     * @throws Exception on invokation exception
     */
    public Object createHandler(String contextPath, String war) throws Exception
    {
        Object handler =
            getClassLoader().loadClass("org.mortbay.jetty.webapp.WebAppContext").newInstance();
        handler.getClass().getMethod("setContextPath", new Class[] {String.class}).invoke(
            handler, new Object[] {contextPath});
        handler.getClass().getMethod("setWar", new Class[] {String.class}).invoke(handler,
            new Object[] {war});

        setDefaultRealm(handler);

        return handler;
    }

    /**
     * Deploy the handler representing the webapp to jetty. If jetty is already started, then start
     * the handler.
     * 
     * @param handler the handler representing the webapp
     * @throws Exception on invocation exception
     */
    public void addHandler(Object handler) throws Exception
    {
        if (addHandlerMethod == null)
        {
            throw new ContainerException("No Jetty instance to deploy to");
        }
        addHandlerMethod.invoke(contextHandlers, new Object[] {handler});
        Method m = getServer().getClass().getMethod("isStarted", new Class[] {});
        if (((Boolean) m.invoke(getServer(), null)).booleanValue())
        {
            handlerClass.getMethod("start", new Class[] {}).invoke(handler, null);
        }
    }

    /**
     * Undeploy the handler representing the webapp.
     * 
     * @param handler the handler representing the webapp
     * @throws Exception on invocation exception
     */
    public void removeHandler(Object handler) throws Exception
    {
        if (handler == null)
        {
            return;
        }

        if (removeHandlerMethod == null)
        {
            throw new ContainerException("No Jetty instance to deploy to");
        }
        removeHandlerMethod.invoke(contextHandlers, new Object[] {handler});
    }

    /**
     * Defines a security realm and adds defined users to it. If a user has specified the standard
     * ServletPropertySet.USERS property, then we try and turn these into an in-memory default
     * realm, and then set that realm on all of the webapps. TODO: this is not ideal. We need a way
     * to specify N named realms to the server so that individual webapps can find their appropriate
     * realms by name.
     * 
     * @throws Exception in case of error
     */
    private void setSecurityRealm() throws Exception
    {
        if (getConfiguration().getPropertyValue(ServletPropertySet.USERS) != null)
        {
            Class realmClass =
                getClassLoader().loadClass("org.mortbay.jetty.security.HashUserRealm");
            this.defaultRealm =
                realmClass.getConstructor(new Class[] {String.class}).newInstance(
                    new Object[] {"Cargo Test Realm"});

            Iterator users =
                User.parseUsers(getConfiguration().getPropertyValue(ServletPropertySet.USERS))
                    .iterator();
            while (users.hasNext())
            {
                User user = (User) users.next();

                this.defaultRealm.getClass().getMethod("put",
                    new Class[] {Object.class, Object.class}).invoke(this.defaultRealm,
                        new Object[] {user.getName(), user.getPassword()});

                Iterator roles = user.getRoles().iterator();
                while (roles.hasNext())
                {
                    String role = (String) roles.next();

                    this.defaultRealm.getClass().getMethod("addUserToRole",
                        new Class[] {String.class, String.class}).invoke(this.defaultRealm,
                            new Object[] {user.getName(), role});
                }
            }

            Object userRealmsArray =
                Array.newInstance(getClassLoader().loadClass(
                    "org.mortbay.jetty.security.UserRealm"), 1);
            Array.set(userRealmsArray, 0, this.defaultRealm);

            // Add newly created realm to server
            getServer().getClass().getMethod("setUserRealms",
                new Class[] {userRealmsArray.getClass()}).invoke(getServer(),
                    new Object[] {userRealmsArray});

            Object[] realmlist =
                (Object[]) getServer().getClass().getMethod("getUserRealms", new Class[] {})
                    .invoke(getServer(), new Object[] {});
            getLogger().info(
                "Added " + (realmlist == null ? "0" : String.valueOf(realmlist.length))
                    + " realms ", getClass().getName());
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedLocalContainer#createServerObject()
     */
    protected synchronized void createServerObject() throws Exception
    {
        if (this.server == null)
        {
            super.createServerObject();

            this.server.getClass().getMethod("setStopAtShutdown", new Class[] {boolean.class})
                .invoke(this.server, new Object[] {Boolean.TRUE});
        }
    }
}
