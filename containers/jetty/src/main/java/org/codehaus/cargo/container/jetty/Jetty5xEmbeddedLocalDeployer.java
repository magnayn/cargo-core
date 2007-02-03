/*
 * ======================================================================== 
 * Copyright 2006 Vincent Massol.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * ========================================================================
 */
package org.codehaus.cargo.container.jetty;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.EmbeddedLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedLocalDeployer;

/**
 * Deploys webapps to a Jetty 5.x instance running embedded.
 * 
 * @version $Id$
 */
public class Jetty5xEmbeddedLocalDeployer extends AbstractJettyEmbeddedLocalDeployer
{
    /**
     * {@inheritDoc}
     * @see AbstractJettyEmbeddedLocalDeployer#AbstractJettyEmbeddedLocalDeployer(EmbeddedLocalContainer)
     */
    public Jetty5xEmbeddedLocalDeployer(EmbeddedLocalContainer container)
    {
        super(container);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedLocalDeployer#deployWebApp(org.codehaus.cargo.container.deployable.Deployable)
     */
    public Object deployWebApp(Deployable deployable)
    {
        getLogger().info("Deploying [" + deployable.getFile() + "]", this.getClass().getName());

        if (deployable.getType() == DeployableType.WAR)
        {
            try
            {
                Jetty5xEmbeddedLocalContainer container =
                    (Jetty5xEmbeddedLocalContainer) getContainer();

                Class wac = container.getClassLoader().loadClass(
                    "org.mortbay.jetty.servlet.WebApplicationContext");
                Object webAppContext = container.getServer().getClass().getMethod(
                    "addWebApplication", new Class[] {String.class, String.class}).invoke(
                        container.getServer(),
                        new Object[] {getContext(deployable), deployable.getFile()});

                // set up virtual hosts
                String[] virtualHosts = getVirtualHosts();
                for (int i = 0; virtualHosts != null && i < virtualHosts.length; i++)
                {
                    wac.getMethod("addVirtualHost", new Class[] {String.class})
                        .invoke(webAppContext, new Object[] {virtualHosts[i]});
                }

                // check if extracting the war is wanted
                if (getExtractWar() != null)
                {
                    wac.getMethod("setExtractWAR", new Class[] {Boolean.TYPE})
                        .invoke(webAppContext, new Object[] {getExtractWar()});
                }

                if (getParentLoaderPriority() != null)
                {
                    // check if user wants to invert the class loading hierarchy
                    wac.getMethod("setClassLoaderJava2Compliant", new Class[] {Boolean.TYPE})
                        .invoke(webAppContext, new Object[] {getParentLoaderPriority()});
                }

                // check if a default realm has been set for the server, if so, use it
                container.setDefaultRealm(webAppContext);

                // Activate context
                wac.getMethod("start", null).invoke(webAppContext, null);
                return webAppContext;
            }
            catch (Exception e)
            {
                throw new ContainerException("Failed to deploy [" + deployable.getFile() + "]", e);
            }
        }

        throw new ContainerException("Only WAR archives are supported for deployment in Jetty. "
            + "Got [" + deployable.getFile() + "]");
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedLocalDeployer#undeployWebApp(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void undeployWebApp(Deployable deployable)
    {
        stop(deployable);
        try
        {
            Jetty5xEmbeddedLocalContainer container =
                (Jetty5xEmbeddedLocalContainer) getContainer();

            Object deployedWebAppContext = getDeployedWebAppContext(deployable);
            Class wac = container.getClassLoader().loadClass(
                "org.mortbay.jetty.servlet.WebApplicationContext");
            wac.getMethod("destroy", null).invoke(deployedWebAppContext, null);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to undeploy [" + deployable.getFile() + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#redeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void redeploy(Deployable deployable)
    {
        undeploy(deployable);
        deploy(deployable);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#start(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void start(Deployable deployable)
    {
        try
        {
            Jetty5xEmbeddedLocalContainer container =
                (Jetty5xEmbeddedLocalContainer) getContainer();

            Object deployedWebAppContext = getDeployedWebAppContext(deployable);
            Class wac = container.getClassLoader().loadClass(
                "org.mortbay.jetty.servlet.WebApplicationContext");
            wac.getMethod("start", null).invoke(deployedWebAppContext, null);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to start [" + deployable.getFile() + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#stop(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void stop(Deployable deployable)
    {
        try
        {
            Jetty5xEmbeddedLocalContainer container =
                (Jetty5xEmbeddedLocalContainer) getContainer();

            Object deployedWebAppContext = getDeployedWebAppContext(deployable);
            Class wac = container.getClassLoader().loadClass(
                "org.mortbay.jetty.servlet.WebApplicationContext");
            wac.getMethod("stop", null).invoke(deployedWebAppContext, null);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to stop [" + deployable.getFile() + "]", e);
        }
    }
}
