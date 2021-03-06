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
package org.codehaus.cargo.generic.configuration;

import org.codehaus.cargo.generic.spi.AbstractIntrospectionGenericHintFactory;
import org.codehaus.cargo.generic.internal.util.RegistrationKey;
import org.codehaus.cargo.generic.internal.util.FullContainerIdentity;
import org.codehaus.cargo.generic.AbstractFactoryRegistry;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.ContainerType;

import java.lang.reflect.Constructor;

/**
 * Default implementation of {@link ConfigurationCapabilityFactory}.
 * Registers all known configuration capabilities.
 *
 * @version $Id$
 */
public class DefaultConfigurationCapabilityFactory extends AbstractIntrospectionGenericHintFactory
    implements ConfigurationCapabilityFactory
{
    /**
     * Initialize configuration capability name mappings with container ids and configuration types.
     */
    public DefaultConfigurationCapabilityFactory()
    {
        this(null);
    }

    /**
     * Register configuration capability name mappings.
     *
     * @param classLoader
     *      ClassLoader to discover implementations from. See
     *      {@link AbstractFactoryRegistry#register(ClassLoader, ConfigurationCapabilityFactory)}
     *      for the details of what this value means.
     */
    public DefaultConfigurationCapabilityFactory(ClassLoader classLoader)
    {
        super();
        // Note: We register configuration capabilities using introspection so that we don't have to
        // depend on those classes at build time nor at runtime. More specifically this allows a
        // user to use the generic API and choose what container implementation jar he wants to use
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
        registerConfigurationCapability("geronimo1x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.geronimo.internal."
                + "GeronimoStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("geronimo1x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.geronimo.internal."
                + "GeronimoExistingLocalConfigurationCapability");
    }

    /**
     * Register JBoss
     */
    public void registerJBoss()
    {
        registerConfigurationCapability("jboss3x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jboss.internal."
                + "JBossStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jboss3x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jboss.internal."
                + "JBossExistingLocalConfigurationCapability");
        registerConfigurationCapability("jboss3x", ContainerType.INSTALLED,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jboss.internal."
                + "JBossRuntimeConfigurationCapability");

        registerConfigurationCapability("jboss4x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jboss.internal."
                + "JBossStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jboss4x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jboss.internal."
                + "JBossExistingLocalConfigurationCapability");
        registerConfigurationCapability("jboss4x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jboss.internal."
                + "JBossRuntimeConfigurationCapability");

        registerConfigurationCapability("jboss42x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jboss.internal."
                + "JBossStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jboss42x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jboss.internal."
                + "JBossExistingLocalConfigurationCapability");
        registerConfigurationCapability("jboss42x", ContainerType.REMOTE,
                ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jboss.internal."
                    + "JBossRuntimeConfigurationCapability");

        registerConfigurationCapability("jboss5x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jboss.internal."
                + "JBossStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jboss5x", ContainerType.INSTALLED,
             ConfigurationType.EXISTING, "org.codehaus.cargo.container.jboss.internal."
                + "JBossExistingLocalConfigurationCapability");
        registerConfigurationCapability("jboss5x", ContainerType.REMOTE,
                ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jboss.internal."
                    + "JBossRuntimeConfigurationCapability");

        registerConfigurationCapability("jboss51x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jboss.internal."
                + "JBossStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jboss51x", ContainerType.INSTALLED,
             ConfigurationType.EXISTING, "org.codehaus.cargo.container.jboss.internal."
                + "JBossExistingLocalConfigurationCapability");
        registerConfigurationCapability("jboss51x", ContainerType.REMOTE,
                ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jboss.internal."
                    + "JBossRuntimeConfigurationCapability");
    }

    /**
     * Register Jetty
     */
    public void registerJetty()
    {
        registerConfigurationCapability("jetty4x", ContainerType.EMBEDDED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty4xEmbeddedStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("jetty5x", ContainerType.EMBEDDED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty5xEmbeddedStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("jetty6x", ContainerType.EMBEDDED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty6xEmbeddedStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jetty6x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty6xStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jetty6x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jetty.internal."
                + "JettyRuntimeConfigurationCapability");
 
        registerConfigurationCapability("jetty7x", ContainerType.EMBEDDED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty7xEmbeddedStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jetty7x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jetty.internal."
                + "Jetty7xStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("jetty7x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jetty.internal."
                + "JettyRuntimeConfigurationCapability");


    }

    /**
     * Register JO!
     */
    public void registerJO()
    {
        registerConfigurationCapability("jo1x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jo.internal."
                + "Jo1xStandaloneLocalConfigurationCapability");
    }

    /**
     * Register OW2 JOnAS
     */
    public void registerJOnAS()
    {
        registerConfigurationCapability("jonas4x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jonas.internal."
                + "JonasRuntimeConfigurationCapability");
        registerConfigurationCapability("jonas4x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jonas.internal."
                + "JonasExistingLocalConfigurationCapability");
        registerConfigurationCapability("jonas4x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jonas.internal."
                + "JonasStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("jonas5x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.jonas.internal."
                + "JonasRuntimeConfigurationCapability");
        registerConfigurationCapability("jonas5x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jonas.internal."
                + "JonasExistingLocalConfigurationCapability");
        registerConfigurationCapability("jonas5x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jonas.internal."
                + "JonasStandaloneLocalConfigurationCapability");
    }

    /**
     * Register JRun
     */
    public void registerJRun()
    {
        registerConfigurationCapability("jrun4x", ContainerType.INSTALLED, 
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.jrun.internal."
                + "JRun4xStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("jrun4x", ContainerType.INSTALLED, 
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.jrun.internal."
                + "JRun4xStandaloneLocalConfigurationCapability");
    }

    /**
     * Register Orion
     */
    public void registerOrion()
    {
        registerConfigurationCapability("orion1x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.orion.internal."
                + "OrionStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("orion2x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.orion.internal."
                + "OrionStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("oc4j9x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.orion.internal."
                + "OrionStandaloneLocalConfigurationCapability");

        registerConfigurationCapability("oc4j10x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.orion.internal."
                + "OrionStandaloneLocalConfigurationCapability");
    }

    /**
     * Register Resin
     */
    public void registerResin()
    {
        registerConfigurationCapability("resin2x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.resin.internal."
                + "ResinStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("resin2x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.resin.internal."
                + "ResinExistingLocalConfigurationCapability");

        registerConfigurationCapability("resin3x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.resin.internal."
                + "ResinStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("resin3x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.resin.internal."
                + "ResinExistingLocalConfigurationCapability");
    }

    /**
     * Register Tomcat
     */
    public void registerTomcat()
    {        
        registerConfigurationCapability("tomcat4x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("tomcat4x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatExistingLocalConfigurationCapability");
        registerConfigurationCapability("tomcat4x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatRuntimeConfigurationCapability");

        registerConfigurationCapability("tomcat5x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("tomcat5x", ContainerType.EMBEDDED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("tomcat5x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatExistingLocalConfigurationCapability");
        registerConfigurationCapability("tomcat5x", ContainerType.EMBEDDED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatExistingLocalConfigurationCapability");
        registerConfigurationCapability("tomcat5x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatRuntimeConfigurationCapability");

        registerConfigurationCapability("tomcat6x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("tomcat6x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatExistingLocalConfigurationCapability");
        registerConfigurationCapability("tomcat6x", ContainerType.REMOTE,
            ConfigurationType.RUNTIME, "org.codehaus.cargo.container.tomcat.internal."
                + "TomcatRuntimeConfigurationCapability");
    }

    /**
     * Register BEA/Oracle Weblogic
     */
    public void registerWeblogic()
    {
        registerConfigurationCapability("weblogic8x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogicStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("weblogic8x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogicExistingLocalConfigurationCapability");

        registerConfigurationCapability("weblogic9x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogic9x10xAnd103xStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("weblogic9x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogicExistingLocalConfigurationCapability");
        
        registerConfigurationCapability("weblogic10x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogic9x10xAnd103xStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("weblogic10x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogicExistingLocalConfigurationCapability");

        registerConfigurationCapability("weblogic103x", ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogic9x10xAnd103xStandaloneLocalConfigurationCapability");
        registerConfigurationCapability("weblogic103x", ContainerType.INSTALLED,
            ConfigurationType.EXISTING, "org.codehaus.cargo.container.weblogic.internal."
                + "WebLogicExistingLocalConfigurationCapability");
        
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationCapabilityFactory#registerConfigurationCapability
     */
    public void registerConfigurationCapability(String containerId, ContainerType containerType,
        ConfigurationType configurationType, Class configurationCapabilityClass)
    {
        registerImplementation(new RegistrationKey(
            new FullContainerIdentity(containerId, containerType), configurationType.getType()),
            configurationCapabilityClass);
    }

    /**
     * Registers a configuration capability using a class specified as a String.
     *
     * @param containerId {@inheritDoc}
     * @param containerType {@inheritDoc}
     * @param configurationType {@inheritDoc}
     * @param configurationCapabilityClass the configuration capability implementation class to
     *        register as a String
     * @see #registerConfigurationCapability(String, org.codehaus.cargo.container.ContainerType, org.codehaus.cargo.container.configuration.ConfigurationType, String)
     */
    public void registerConfigurationCapability(String containerId, ContainerType containerType,
        ConfigurationType configurationType, String configurationCapabilityClass)
    {
        registerImplementation(new RegistrationKey(
            new FullContainerIdentity(containerId, containerType), configurationType.getType()),
            configurationCapabilityClass);
    }

    /**
     * {@inheritDoc}
     * @see ConfigurationCapabilityFactory#createConfigurationCapability
     */
    public ConfigurationCapability createConfigurationCapability(String containerId,
        ContainerType containerType, ConfigurationType configurationType)
    {
        return (ConfigurationCapability) createImplementation(new RegistrationKey(
            new FullContainerIdentity(containerId, containerType), configurationType.getType()),
            null, "configuration capability");
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.generic.spi.AbstractGenericHintFactory#getConstructor
     */
    protected Constructor getConstructor(Class configurationCapabilityClass, String hint,
        GenericParameters parameters) throws NoSuchMethodException
    {
        return configurationCapabilityClass.getConstructor(null);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.generic.spi.AbstractGenericHintFactory#createInstance
     */
    protected Object createInstance(Constructor constructor, String hint,
        GenericParameters parameters) throws Exception
    {
        return constructor.newInstance(null);
    }
}
