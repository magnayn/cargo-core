/* 
 * ========================================================================
 * 
 * Copyright 2007-2008 OW2.
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
package org.codehaus.cargo.container.jonas.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.jonas.JonasPropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;

/**
 * Support for the JOnAS JEE container.
 * 
 * @version $Id: AbstractJonasInstalledLocalContainer.java 14641 2008-07-25 11:46:29Z alitokmen $
 */
public abstract class AbstractJonasInstalledLocalContainer extends AbstractInstalledLocalContainer
{
    /**
     * Container version.
     */
    private String version;

    /**
     * Capability of the JOnAS container.
     */
    private ContainerCapability capability = new JonasContainerCapability();

    /**
     * {@inheritDoc}
     * 
     * @see AbstractInstalledLocalContainer#AbstractInstalledLocalContainer(org.codehaus.cargo.container.configuration.LocalConfiguration)
     */
    public AbstractJonasInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractInstalledLocalContainer#doStart(Java)
     */
    public abstract void doStart(Java java);

    /**
     * {@inheritDoc}
     * 
     * @see AbstractInstalledLocalContainer#doStart(Java)
     */
    public abstract void doStop(Java java);

    /**
     * Setup of the target server name for the JOnAS admin command call.
     * 
     * @param java the target java ant task to setup
     */
    public void doServerNameParam(Java java)
    {
        String serverName = getConfiguration().getPropertyValue(JonasPropertySet.JONAS_SERVER_NAME);
        if (serverName != null && serverName.trim().length() == 0)
        {
            java.createArg().setValue("-n");
            java.createArg().setValue(serverName);
        }
    }

    /**
     * Setup of the required java system properties to configure JOnAS properly.
     * 
     * @param java the target java ant task to setup
     */
    public void setupSysProps(Java java)
    {
        Map configuredSysProps = getSystemProperties();
        addSysProp(java, configuredSysProps, "install.root", new File(getHome()).getAbsolutePath()
            .replace(File.separatorChar, '/'));
        addSysProp(java, configuredSysProps, "jonas.base", new File(getConfiguration().getHome())
            .getAbsolutePath().replace(File.separatorChar, '/'));
        addSysProp(java, configuredSysProps, "java.endorsed.dirs", new File(getFileHandler()
            .append(getHome(), "lib/endorsed")).getAbsolutePath().replace(File.separatorChar, '/'));
        addSysProp(java, configuredSysProps, "java.security.policy", new File(getFileHandler()
            .append(getConfiguration().getHome(), "conf/java.policy")).getAbsolutePath().replace(
            File.separatorChar, '/'));
        addSysProp(java, configuredSysProps, "java.security.auth.login.config", new File(
            getFileHandler().append(getConfiguration().getHome(), "conf/jaas.config"))
            .getAbsolutePath().replace(File.separatorChar, '/'));
        addSysProp(java, configuredSysProps, "jonas.classpath", "");
        addSysProp(java, configuredSysProps, "java.awt.headless", "true");
        setupExtraSysProps(java, configuredSysProps);
    }
    
    /**
     * Setup of the Extra required java system properties to configure JOnAS properly. The system
     * properties depends on the JOnAS version.
     * 
     * @param java the target java ant task to setup
     * @param configuredSysProps the configured system properties
     */
    protected abstract void setupExtraSysProps(Java java, Map configuredSysProps);

    /**
     * Add java system properties (to configure JOnAS properly).
     * 
     * @param java the target java ant task on which we add the system properties
     * @param configuredSysProps the configured system Properties.
     * @param name the system property Name
     * @param value the system property Value
     */
    public void addSysProp(Java java, Map configuredSysProps, String name, String value)
    {
        if (configuredSysProps == null || !configuredSysProps.containsKey(name))
        {
            java.addSysproperty(getAntUtils().createSysProperty(name, value));
        }
    }

    /**
     * Configuring the target java ant task to launch a JOnAS command.
     * 
     * @param java the target java ant task to setup
     */
    public void doAction(Java java)
    {
        setupSysProps(java);

        java.setClassname("org.objectweb.jonas.server.Bootstrap");

        Path classpath = java.createClasspath();
        classpath.createPathElement().setLocation(
            new File(getHome(), "lib/common/ow_jonas_bootstrap.jar"));
        classpath.createPathElement().setLocation(
            new File(getHome(), "lib/commons/jonas/jakarta-commons/commons-logging-api.jar"));
        classpath.createPathElement().setLocation(new File(getConfiguration().getHome(), "conf"));
        try
        {
            addToolsJarToClasspath(classpath);

        }
        catch (IOException ex)
        {
            throw new ContainerException("IOException occured during java command line setup", ex);
        }

        java.setDir(new File(getConfiguration().getHome()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.Container#getCapability()
     */
    public ContainerCapability getCapability()
    {
        return capability;
    }

    /**
     * @param defaultVersion default version to use if we cannot find out the exact JOnAS version
     * @return the JOnAS version found
     */
    public String getVersion(String defaultVersion)
    {
        String version = this.version;

        if (version == null)
        {
            try
            {
                URLClassLoader classloader = new URLClassLoader(new URL[]
                {new File(getHome(), "/lib/common/ow_jonas_bootstrap.jar").toURL()});
                Class versionClass = classloader
                    .loadClass("org.objectweb.jonas_lib.version.Version");
                Field versionField = versionClass.getField("NUMBER");
                version = (String) versionField.get(null);

                getLogger()
                    .info("Found JOnAS version [" + version + "]", this.getClass().getName());
            }
            catch (Exception e)
            {
                getLogger().debug(
                    "Failed to get JOnAS version, Error = [" + e.getMessage()
                        + "]. Using generic version [" + defaultVersion + "]",
                    this.getClass().getName());
                version = defaultVersion;
            }
        }
        this.version = version;
        return version;
    }

}
