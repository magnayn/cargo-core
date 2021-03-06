/*
 * ========================================================================
 *
 * Copyright 2004-2008 Vincent Massol.
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
package org.codehaus.cargo.generic.configuration;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.spi.AbstractIntrospectionGenericHintFactory;
import org.codehaus.cargo.generic.internal.util.RegistrationKey;
import org.codehaus.cargo.generic.internal.util.FullContainerIdentity;
import org.codehaus.cargo.generic.AbstractFactoryRegistry;
import org.codehaus.cargo.util.FileHandler;
import org.codehaus.cargo.util.DefaultFileHandler;

import java.lang.reflect.Constructor;

/**
 * Default {@link ConfigurationFactory} implementation that has all the known container
 * configurations registered against their containers. It also supports registering new
 * configurations against any container.
 *
 * @version $Id$
 */
public class DefaultConfigurationFactory extends AbstractIntrospectionGenericHintFactory
    implements ConfigurationFactory
{
    /**
     * File utility class.
     */
    private FileHandler fileHandler = new DefaultFileHandler();

    /**
     * @see GenericParameters
     */
    private static class ConfigurationFactoryParameters implements GenericParameters
    {
        /**
         * The home directory for the configuration.
         */
        public String home;
    }

    /**
     * Register default configurations.
     */
    public DefaultConfigurationFactory()
    {
        this(null);
    }

    /**
     * Register configuration name mappings.
     *
     * @param classLoader
     *      ClassLoader to discover implementations from. See
     *      {@link AbstractFactoryRegistry#register(ClassLoader, ConfigurationFactory)}
     *      for the details of what this value means.
     */
    public DefaultConfigurationFactory(ClassLoader classLoader)
    {
        super();

        // Note: We register containers using introspection so that we don't have to depend on
        // those containers at build time nor at runtime. More specifically this allows a user
        // to use the generic API and choose what container implementation jar he wants to use
        // without having to add all container implementations jars in the classpath.

        // Note: Sorted by container id alphabetical order
        registerGeronimo();

        registerJBoss();

        registerJetty();

        registerJO();
        
        registerJOnAS();
        
        registerJRun();

        registerOrion();

        registerResin();

        registerTomcat();

        registerWeblogic();

        AbstractFactoryRegistry.register(classLoader, this);
    }

    /**
     * Register Apache Geronimo
     */
    public void registerGeronimo()
    {
        registerConfiguration("geronimo1x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.geronimo.Geronimo1xStandaloneLocalConfiguration");
        registerConfiguration("geronimo1x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.geronimo.Geronimo1xExistingLocalConfiguration");
    }

    /**
     * Register JBoss
     */
    public void registerJBoss()
    {
        registerConfiguration("jboss3x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jboss.JBossStandaloneLocalConfiguration");
        registerConfiguration("jboss3x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jboss.JBossExistingLocalConfiguration");

        registerConfiguration("jboss4x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jboss.JBossStandaloneLocalConfiguration");
        registerConfiguration("jboss4x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jboss.JBossExistingLocalConfiguration");
        registerConfiguration("jboss4x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jboss.JBossRuntimeConfiguration");

        registerConfiguration("jboss42x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jboss.JBossStandaloneLocalConfiguration");
        registerConfiguration("jboss42x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jboss.JBossExistingLocalConfiguration");
        registerConfiguration("jboss42x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jboss.JBossRuntimeConfiguration");

        registerConfiguration("jboss5x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jboss.JBoss5xStandaloneLocalConfiguration");
        registerConfiguration("jboss5x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jboss.JBossExistingLocalConfiguration");
        registerConfiguration("jboss5x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jboss.JBossRuntimeConfiguration");

        registerConfiguration("jboss51x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jboss.JBoss51xStandaloneLocalConfiguration");
        registerConfiguration("jboss51x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jboss.JBossExistingLocalConfiguration");
        registerConfiguration("jboss51x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jboss.JBossRuntimeConfiguration");
    }

    /**
     * Register Jetty
     */
    public void registerJetty()
    {
        registerConfiguration("jetty4x", ContainerType.EMBEDDED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty4xEmbeddedStandaloneLocalConfiguration");

        registerConfiguration("jetty5x", ContainerType.EMBEDDED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty5xEmbeddedStandaloneLocalConfiguration");

        registerConfiguration("jetty6x", ContainerType.EMBEDDED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty6xEmbeddedStandaloneLocalConfiguration");
        registerConfiguration("jetty6x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty6xStandaloneLocalConfiguration");
        registerConfiguration("jetty6x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jetty.JettyRuntimeConfiguration");

        registerConfiguration("jetty7x", ContainerType.EMBEDDED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty7xEmbeddedStandaloneLocalConfiguration");
        registerConfiguration("jetty7x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jetty.Jetty7xStandaloneLocalConfiguration");
        registerConfiguration("jetty7x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jetty.JettyRuntimeConfiguration");

    }

    /**
     * Register JO!
     */
    public void registerJO()
    {
        registerConfiguration("jo1x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jo.Jo1xStandaloneLocalConfiguration");
    }

    /**
     * Register OW2 JOnAS
     */
    public void registerJOnAS()
    {
        registerConfiguration("jonas4x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jonas.JonasRuntimeConfiguration");
        registerConfiguration("jonas4x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jonas.Jonas4xExistingLocalConfiguration");
        registerConfiguration("jonas4x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jonas.Jonas4xStandaloneLocalConfiguration");
        registerConfiguration("jonas4x", ContainerType.REMOTE, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jonas.Jonas4xStandaloneLocalConfiguration");

        registerConfiguration("jonas5x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.jonas.JonasRuntimeConfiguration");
        registerConfiguration("jonas5x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jonas.Jonas5xExistingLocalConfiguration");
        registerConfiguration("jonas5x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jonas.Jonas5xStandaloneLocalConfiguration");
        registerConfiguration("jonas5x", ContainerType.REMOTE, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jonas.Jonas5xStandaloneLocalConfiguration");
    }
    
    /**
     * Register JRun
     */
    public void registerJRun()
    {
        registerConfiguration("jrun4x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.jrun.JRun4xExistingLocalConfiguration");
        
        registerConfiguration("jrun4x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.jrun.JRun4xStandaloneLocalConfiguration");
    }    

    /**
     * Register Orion
     */
    public void registerOrion()
    {
        registerConfiguration("orion1x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.orion.OrionStandaloneLocalConfiguration");

        registerConfiguration("orion2x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.orion.OrionStandaloneLocalConfiguration");

        registerConfiguration("oc4j9x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.orion.Oc4j9xStandaloneLocalConfiguration");

        registerConfiguration("oc4j10x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.orion.Oc4j10xExistingLocalConfiguration");
    }

    /**
     * Register Resin
     */
    public void registerResin()
    {
        registerConfiguration("resin2x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.resin.Resin2xStandaloneLocalConfiguration");
        registerConfiguration("resin2x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.resin.ResinExistingLocalConfiguration");

        registerConfiguration("resin3x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.resin.Resin3xStandaloneLocalConfiguration");
        registerConfiguration("resin3x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.resin.ResinExistingLocalConfiguration");
    }

    /**
     * Register Tomcat
     */
    public void registerTomcat()
    {
     
        registerConfiguration("tomcat4x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.tomcat.Tomcat4xStandaloneLocalConfiguration");
        registerConfiguration("tomcat4x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.tomcat.TomcatExistingLocalConfiguration");
        registerConfiguration("tomcat4x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration");

        registerConfiguration("tomcat5x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.tomcat.Tomcat5xStandaloneLocalConfiguration");
        registerConfiguration("tomcat5x", ContainerType.EMBEDDED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.tomcat.Tomcat5xStandaloneLocalConfiguration");
        registerConfiguration("tomcat5x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.tomcat.TomcatExistingLocalConfiguration");
        registerConfiguration("tomcat5x", ContainerType.EMBEDDED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.tomcat.TomcatExistingLocalConfiguration");
        registerConfiguration("tomcat5x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration");

        registerConfiguration("tomcat6x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.tomcat.Tomcat6xStandaloneLocalConfiguration");
        registerConfiguration("tomcat6x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.tomcat.TomcatExistingLocalConfiguration");
        registerConfiguration("tomcat6x", ContainerType.REMOTE, ConfigurationType.RUNTIME,
            "org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration");
    }

    /**
     * Register BEA/Oracle Weblogic
     */
    public void registerWeblogic()
    {
        registerConfiguration("weblogic8x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.weblogic.WebLogicStandaloneLocalConfiguration");
        registerConfiguration("weblogic8x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.weblogic.WebLogicExistingLocalConfiguration");

        registerConfiguration("weblogic9x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.weblogic.WebLogic9xStandaloneLocalConfiguration");
        registerConfiguration("weblogic9x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.weblogic.WebLogic9xExistingLocalConfiguration");

        registerConfiguration("weblogic10x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.weblogic.WebLogic10xStandaloneLocalConfiguration");
        registerConfiguration("weblogic10x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.weblogic.WebLogic9xExistingLocalConfiguration");

        registerConfiguration("weblogic103x", ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            "org.codehaus.cargo.container.weblogic.WebLogic103xStandaloneLocalConfiguration");
        registerConfiguration("weblogic103x", ContainerType.INSTALLED, ConfigurationType.EXISTING,
            "org.codehaus.cargo.container.weblogic.WebLogic9xExistingLocalConfiguration");

    }

    /**
     * {@inheritDoc}
     * @see ConfigurationFactory#isConfigurationRegistered
     */
    public boolean isConfigurationRegistered(String containerId, ContainerType containerType,
        ConfigurationType configurationType)
    {
        return hasMapping(new RegistrationKey(new FullContainerIdentity(containerId,
            containerType), configurationType.getType()));
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationFactory#registerConfiguration
     */
    public void registerConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType, Class configurationClass)
    {
        registerImplementation(new RegistrationKey(new FullContainerIdentity(containerId,
            containerType), configurationType.getType()), configurationClass);
    }

    /**
     * Registers a configuration using a class specified as a String.
     *
     * @param containerId {@inheritDoc}
     * @param containerType {@inheritDoc}
     * @param configurationType {@inheritDoc}
     * @param configurationClassName the configuration implementation class to register as a String
     * @see #registerConfiguration(String, org.codehaus.cargo.container.ContainerType, org.codehaus.cargo.container.configuration.ConfigurationType, Class)
     */
    public void registerConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType, String configurationClassName)
    {
        registerImplementation(new RegistrationKey(new FullContainerIdentity(containerId,
            containerType), configurationType.getType()), configurationClassName);
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationFactory#getConfigurationClass
     */
    public Class getConfigurationClass(String containerId, ContainerType containerType,
        ConfigurationType configurationType)
    {
        return getMapping(new RegistrationKey(new FullContainerIdentity(containerId,
            containerType), configurationType.getType()));
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationFactory#createConfiguration(String, org.codehaus.cargo.container.ContainerType, org.codehaus.cargo.container.configuration.ConfigurationType)
     */
    public Configuration createConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType)
    {
        return createConfiguration(containerId, containerType, configurationType, null);
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationFactory#createConfiguration(String, org.codehaus.cargo.container.ContainerType, org.codehaus.cargo.container.configuration.ConfigurationType, String)
     */
    public Configuration createConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType, String home)
    {
        ConfigurationFactoryParameters parameters = new ConfigurationFactoryParameters();
        parameters.home = home;

        return (Configuration) createImplementation(new RegistrationKey(
            new FullContainerIdentity(containerId, containerType), configurationType.getType()),
            parameters, "configuration");
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.generic.spi.AbstractGenericHintFactory#getConstructor(Class, String, GenericParameters)
     */
    protected Constructor getConstructor(Class configurationClass, String hint,
        GenericParameters parameters) throws NoSuchMethodException
    {
        Constructor constructor;

        // Runtime configurations have constructors that do not take any parameter.
        if (ConfigurationType.toType(hint) == ConfigurationType.RUNTIME)
        {
            constructor = configurationClass.getConstructor(new Class[] {});
        }
        else if ((ConfigurationType.toType(hint) == ConfigurationType.EXISTING)
            || (ConfigurationType.toType(hint) == ConfigurationType.STANDALONE))
        {
            constructor = configurationClass.getConstructor(new Class[] {String.class});
        }
        else
        {
            throw new ContainerException("Unknown configuration type [" + hint + "]");
        }

        return constructor;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.generic.spi.AbstractGenericHintFactory#createInstance
     */
    protected Object createInstance(Constructor constructor, String hint,
        GenericParameters parameters) throws Exception
    {
        Object instance;

        String home = ((ConfigurationFactoryParameters) parameters).home;

        // Runtime configurations have constructors that do not take any parameter.
        if (ConfigurationType.toType(hint) == ConfigurationType.RUNTIME)
        {
            if (home != null)
            {
                throw new ContainerException("The configuration home parameter should not be "
                    + "specified for runtime configurations");
            }

            instance = constructor.newInstance(new Object[] {});
        }
        else if ((ConfigurationType.toType(hint) == ConfigurationType.EXISTING)
            || (ConfigurationType.toType(hint) == ConfigurationType.STANDALONE))
        {
            if (home == null)
            {
                // The user has not specified a home directory for the configuration, create one
                // in the temporary directory if it's a standalone configuration
                if (ConfigurationType.toType(hint) == ConfigurationType.EXISTING)
                {
                    throw new ContainerException("The configuration home parameter must be "
                        + "specified for existing configurations");
                }
                else
                {
                    home = this.fileHandler.getTmpPath("conf");
                }
            }

            instance = constructor.newInstance(new Object[] {home});
        }
        else
        {
            throw new ContainerException("Unknown configuration type [" + hint + "]");
        }

        return instance;
    }
}
