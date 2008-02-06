/*
 * ========================================================================
 * Copyright 2003 The Apache Software Foundation. Code from this file was
 * originally imported from the Jakarta Cactus project.
 *
 * Copyright 2005-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.container.jboss.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.jboss.JBossPropertySet;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.internal.AntContainerExecutorThread;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;

/**
 * Abstract class for JBoss container family.
 *
 * @version $Id$
 */
public abstract class AbstractJBossInstalledLocalContainer extends
    AbstractInstalledLocalContainer implements JBossInstalledLocalContainer
{
    /**
     * Capability of the JBoss Container.
     */
    private ContainerCapability capability = new JBossContainerCapability();

    /**
     * JBoss version.
     */
    private String version;

    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalContainer#AbstractInstalledLocalContainer(LocalConfiguration)
     */
    public AbstractJBossInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer#doStart(Java)
     */
    protected void doStart(Java java) throws Exception
    {
        java.addSysproperty(getAntUtils().createSysProperty("java.endorsed.dirs",
            new File(getHome(), "/lib/endorsed")));
        java.addSysproperty(getAntUtils().createSysProperty("jboss.home.dir", getHome()));
        java.addSysproperty(getAntUtils().createSysProperty("jboss.server.home.dir",
            getConfiguration().getHome()));
        java.addSysproperty(getAntUtils().createSysProperty("jboss.server.home.url",
            new File(getConfiguration().getHome()).toURL().toString()));
        java.addSysproperty(getAntUtils().createSysProperty("jboss.server.name", getId()));
        java.addSysproperty(getAntUtils().createSysProperty(
            "jboss.server.lib.url",
            new File(getLibDir(getConfiguration().getPropertyValue(JBossPropertySet.CONFIGURATION)))
                .toURL().toString()));

        // if the jvmArgs don't alread contain memory settings add the default
        String jvmArgs = getConfiguration().getPropertyValue(GeneralPropertySet.JVMARGS);
        if (jvmArgs != null)
        {
        	if (!jvmArgs.contains("-Xms"))
        	{
        		java.createJvmarg().setValue("-Xms128m");
        	}
        	if (!jvmArgs.contains("-Xmx"))
        	{
        		java.createJvmarg().setValue("-Xmx512m");
        	}
        } 
        else
        {
        	java.createJvmarg().setValue("-Xms128m");
        	java.createJvmarg().setValue("-Xmx512m");
        }
        
        java.createArg().setValue("--configuration=" + getId());

        Path classpath = java.createClasspath();
        classpath.createPathElement().setLocation(new File(getHome(), "bin/run.jar"));
        addToolsJarToClasspath(classpath);

        java.setClassname("org.jboss.Main");

        AntContainerExecutorThread jbossRunner = new AntContainerExecutorThread(java);
        jbossRunner.start();
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer#doStop(Java)
     */
    protected void doStop(Java java) throws Exception
    {
        Path classPath = java.createClasspath();
        classPath.createPathElement().setLocation(new File(getHome(), "bin/shutdown.jar"));
        java.setClassname("org.jboss.Shutdown");

        java.createArg().setValue(
            "--server=" + getConfiguration().getPropertyValue(GeneralPropertySet.HOSTNAME) + ":"
                + getConfiguration().getPropertyValue(GeneralPropertySet.RMI_PORT));

        AntContainerExecutorThread jbossRunner = new AntContainerExecutorThread(java);
        jbossRunner.start();

        // Sleep some extra time to fully ensure JBoss is stopped before giving back the control
        // to the user.
        Thread.sleep(2000L);
    }

    /**
     * @see org.codehaus.cargo.container.spi.AbstractLocalContainer#verify()
     */
    protected final void verify()
    {
        super.verify();

        verifyJBossHome();
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getCapability()
     */
    public ContainerCapability getCapability()
    {
        return this.capability;
    }

    /**
     * Parse installed JBoss version.
     *
     * @return the JBoss version, or <code>defaultVersion</code> if the version number could not
     *         be determined
     * @param defaultVersion the default version used if the exact JBoss version can't be determined
     */
    protected final String getVersion(String defaultVersion)
    {
        String version = this.version;

        if (version == null)
        {
            try
            {
                JarFile jarFile = new JarFile(new File(getHome(), "bin/run.jar"));
                ZipEntry entry = jarFile.getEntry("org/jboss/version.properties");
                if (entry != null)
                {
                    Properties properties = new Properties();
                    properties.load(jarFile.getInputStream(entry));
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(properties.getProperty("version.major"));
                    buffer.append(".");
                    buffer.append(properties.getProperty("version.minor"));
                    buffer.append(".");
                    buffer.append(properties.getProperty("version.revision"));
                    version = buffer.toString();
                }
                else
                {
                    version = defaultVersion;
                    getLogger().debug("Couldn't find version.properties in " + jarFile.getName(),
                        this.getClass().getName());
                }
            }
            catch (Exception e)
            {
                version = defaultVersion;
                getLogger().debug(
                    "Failed to find JBoss version, base error [" + e.getMessage() + "]",
                    this.getClass().getName());
            }

            getLogger().info("Parsed JBoss version = [" + version + "]",
                this.getClass().getName());

            this.version = version;
        }
        return version;
    }

    /**
     * {@inheritDoc}
     * @see JBossInstalledLocalContainer#getConfDir(String)
     */
    public String getConfDir(String configurationName)
    {
        return getSpecificConfigurationDir("conf", configurationName);
    }

    /**
     * {@inheritDoc}
     * @see JBossInstalledLocalContainer#getLibDir(String)
     */
    public String getLibDir(String configurationName)
    {
        return getSpecificConfigurationDir("lib", configurationName);
    }

    /**
     * {@inheritDoc}
     * @see JBossInstalledLocalContainer#getDeployDir(String)
     */
    public String getDeployDir(String configurationName)
    {
        return getSpecificConfigurationDir("deploy", configurationName);
    }

    /**
     * @param location the name of the directory to return inside the server configuration
     * @param configurationName the server configuration name to use. A server configuration is
     *            located in the <code>server/</code> directory inside the JBoss installation ir.
     * @return the location of the passed directory name inside the server configuration, as a File
     */
    private String getSpecificConfigurationDir(String location, String configurationName)
    {
        return getFileHandler().append(getHome(), "server/" + configurationName + "/" + location);
    }

    /**
     * Verify that the JBoss directory structure is valid and throw a ContainerException if not.
     * @throws ContainerException if any
     */
    protected void verifyJBossHome()
    {
        List requiredDirs = new ArrayList();
        requiredDirs.add(getFileHandler().append(getHome(), "bin"));
        requiredDirs.add(getFileHandler().append(getHome(), "client"));
        requiredDirs.add(getFileHandler().append(getHome(), "lib"));
        requiredDirs.add(getFileHandler().append(getHome(), "lib/endorsed"));
        requiredDirs.add(getFileHandler().append(getHome(), "server"));

        String errorPrefix = "Invalid JBoss installation. ";
        String errorSuffix = "Make sure the JBoss container home directory you have specified "
            + "points to the right location (It's currently pointing to [" + getHome() + "])";

        for (Iterator it = requiredDirs.iterator(); it.hasNext();)
        {
            String dir = (String) it.next();

            if (!getFileHandler().exists(dir))
            {
                throw new ContainerException(errorPrefix + "The [" + dir
                    + "] directory doesn't exist. " + errorSuffix);
            }
            if (!getFileHandler().isDirectory(dir))
            {
                throw new ContainerException(errorPrefix + "The [" + dir
                    + "] path should be a directory. " + errorSuffix);
            }
            if (getFileHandler().isDirectoryEmpty(dir))
            {
                throw new ContainerException(errorPrefix + "The [" + dir
                    + "] directory is empty and it shouldn't be. " + errorSuffix);
            }
        }

        // Verify minimal jars for doStart() and doStop()
        String[] requiredJars = new String[]{"bin/run.jar", "bin/shutdown.jar"};
        for (int i = 0; i < requiredJars.length; i++)
        {
            String jarFile = getFileHandler().append(getHome(), requiredJars[i]);
            if (!getFileHandler().exists(jarFile))
            {
                throw new ContainerException(errorPrefix + "The [" + jarFile
                    + "] JAR doesn't exist. " + errorSuffix);
            }
        }
    }
}
