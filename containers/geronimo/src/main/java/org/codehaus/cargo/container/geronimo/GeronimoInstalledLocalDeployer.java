/*
 * ========================================================================
 *
 * Copyright 2006 Vincent Massol.
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
package org.codehaus.cargo.container.geronimo;

import java.io.File;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.geronimo.deployable.GeronimoDeployable;
import org.codehaus.cargo.container.internal.util.AntBuildListener;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.spi.deployer.AbstractInstalledLocalDeployer;
import org.codehaus.cargo.util.AntUtils;

/**
 * A Geronimo deploytool-based deployer to perform deployment to a local container.
 *
 * @version $Id$
 */
public class GeronimoInstalledLocalDeployer extends AbstractInstalledLocalDeployer
{
    /**
     * Ant utility class.
     */
    private AntUtils antUtils;

    /**
     * {@inheritDoc}
     * @see AbstractInstalledLocalDeployer#AbstractInstalledLocalDeployer(org.codehaus.cargo.container.InstalledLocalContainer)
     */
    public GeronimoInstalledLocalDeployer(InstalledLocalContainer container)
    {
        super(container);
        this.antUtils = new AntUtils();
    }

    /**
     * @return the Ant utility class
     */
    protected final AntUtils getAntUtils()
    {
        return this.antUtils;
    }
    
    /**
     * @return the same value as {@link #getContainer()} but more type-safe.
     */
    protected InstalledLocalContainer getInstalledContainer()
    {
        return (InstalledLocalContainer) super.getContainer();
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#deploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void deploy(Deployable deployable)
    {
        Java java = createAdminDeployerJava("deploy");
        addPathArgument(java, deployable);
        String deployableId = getModuleId(deployable);

        try
        {
            int retval = java.executeJava();
            if (retval != 0)
            {
                throw new ContainerException("Failed to deploy [" + deployableId + "]");
            }
        }
        catch (BuildException e)
        {
            throw new ContainerException("Failed to deploy [" + deployableId + "]", e);
        }
    }

    /**
     * Distribute a deployable to a running or offline Geronimo server. The deployable is not
     * automatically started.
     *
     * @param deployable the deployable being installed
     * @see org.codehaus.cargo.container.deployer.Deployer#deploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void distribute(Deployable deployable)
    {
        Java java = createDeployerJava("distribute");
        addPathArgument(java, deployable);
        String deployableId = getModuleId(deployable);

        try
        {
            int retval = java.executeJava();
            if (retval != 0)
            {
                throw new ContainerException("Failed to distribute [" + deployableId + "]");
            }
        }
        catch (BuildException e)
        {
            throw new ContainerException("Failed to distribute [" + deployableId + "]", e);
        }

    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#start(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void start(Deployable deployable)
    {
        start(getModuleId(deployable));
    }

    /**
     * Starts a deployable with the given ID.
     *
     * @param deployableId the ID of the deployable being started
     * @see org.codehaus.cargo.container.deployer.Deployer#start(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void start(String deployableId)
    {
        if (deployableId == null)
        {
            throw new ContainerException("Failed: deployable ID cannot be null.");
        }
        else
        {
            Java java = createAdminDeployerJava("start");
            java.createArg().setValue(deployableId);
            try
            {
                int retval = java.executeJava();
                if (retval != 0)
                {
                    throw new ContainerException("Failed to start [" + deployableId + "]");
                }
            }
            catch (BuildException e)
            {
                throw new ContainerException("Failed to start [" + deployableId + "]", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#stop(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void stop(Deployable deployable)
    {
        stop(getModuleId(deployable));
    }

    /**
     * Stops a deployable with the given ID.
     *
     * @param deployableId the ID of the deployable being stopped
     * @see org.codehaus.cargo.container.deployer.Deployer#stop(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void stop(String deployableId)
    {
        if (deployableId == null)
        {
            throw new ContainerException("Failed: deployable ID cannot be null.");
        }
        else
        {
            Java java = createAdminDeployerJava("stop");
            java.createArg().setValue(deployableId);

            try
            {
                int retval = java.executeJava();
                if (retval != 0)
                {
                    throw new ContainerException("Failed to stop [" + deployableId + "]");
                }
            }
            catch (BuildException e)
            {
                throw new ContainerException("Failed to stop [" + deployableId + "]", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#undeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void undeploy(Deployable deployable)
    {
        undeploy(getModuleId(deployable));
    }

    /**
     * Undeploy a deployable with the given ID.
     *
     * @param deployableId the ID of the deployable being undeployed
     * @see org.codehaus.cargo.container.deployer.Deployer#undeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void undeploy(String deployableId)
    {
        if (deployableId == null)
        {
            throw new ContainerException("Failed: deployable ID cannot be null.");
        }
        else
        {
            Java java = createAdminDeployerJava("undeploy");
            java.createArg().setValue(deployableId);

            try
            {
                int retval = java.executeJava();
                if (retval != 0)
                {
                    throw new ContainerException("Failed to undeploy [" + deployableId + "]");
                }
            }
            catch (BuildException e)
            {
                throw new ContainerException("Failed to undeploy [" + deployableId + "]", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.deployer.Deployer#redeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    public void redeploy(Deployable deployable)
    {
        Java java = createAdminDeployerJava("redeploy");
        addPathArgument(java, deployable);
        String deployableId = getModuleId(deployable);
        addModuleIdArgument(java, deployableId);

        try
        {
            int retval = java.executeJava();
            if (retval != 0)
            {
                throw new ContainerException("Failed to redeploy [" + deployableId + "]");
            }
        }
        catch (BuildException e)
        {
            throw new ContainerException("Failed to redeploy [" + deployableId + "]", e);
        }
    }

    /**
     * Create a preinitialized instance of the Ant Java task to be used for managing Geronimo
     * deployables.
     *
     * @return The created task instance
     */
    private Java createJava()
    {
        Java java = (Java) getAntUtils().createAntTask("java");
        java.setFork(true);

        // Add a build listener to the Ant project so that we can catch what the Java task logs
        java.getProject().addBuildListener(
            new AntBuildListener(getLogger(), this.getClass().getName()));

        // Add extra container classpath entries specified by the user.
        addExtraClasspath(java);

        // Add system properties for the container JVM
        addSystemProperties(java);

        // Add JVM args if defined
        String jvmargs = getContainer().getConfiguration().getPropertyValue(
            GeneralPropertySet.JVMARGS);
        if (jvmargs != null)
        {
            java.createJvmarg().setLine(jvmargs);
        }

        return java;
    }

    /**
     * Create an instance of the Ant Java task preinitialized to invoke the deploy tool jar with the
     * specified command.
     *
     * @param action the deployer action to take
     * @return The created task instance
     */
    private Java createDeployerJava(String action)
    {
        Java java = createJava();
        java.setJar(new File(getInstalledContainer().getHome(), "bin/deployer.jar"));
        java.createArg().setValue(action);

        // Add a build listener to the Ant project so that we can catch what the Java task logs
        java.getProject().addBuildListener(
            new AntBuildListener(getLogger(), this.getClass().getName()));

        return java;
    }

    /**
     * Create an instance of the Ant Java task preinitialized to invoke the deploy tool jar with the
     * specified command and admin/manager user auth options.
     *
     * @param action the deployer action to take
     * @return The created task instance
     */
    private Java createAdminDeployerJava(String action)
    {
        Java java = createJava();
        java.createArg().setValue("--user");
        java.createArg().setValue(getContainer().getConfiguration()
            .getPropertyValue(RemotePropertySet.USERNAME));
        java.createArg().setValue("--password");
        java.createArg().setValue(getContainer().getConfiguration()
            .getPropertyValue(RemotePropertySet.PASSWORD));

        java.setJar(new File(getInstalledContainer().getHome(), "bin/deployer.jar"));
        java.createArg().setValue(action);

        return java;
    }

    /**
     * Add deployable path and plan arguments to the deployer Ant Java task.
     * @param java the Ant Java task
     * @param deployable the target deployable
     */
    private void addPathArgument(Java java, Deployable deployable)
    {
        //add deployable path
        java.createArg().setValue(deployable.getFile());

        //add deployable plan
        if (deployable instanceof GeronimoDeployable)
        {
            GeronimoDeployable geronimoDeployable = (GeronimoDeployable) deployable;
            if (geronimoDeployable.getPlan() != null)
            {
                File planFile = new File(geronimoDeployable.getPlan());
                if (!planFile.exists())
                {
                    getLogger().warn("Cannot locate deployment plan [" + planFile.getPath()
                        + "]. Will attempt to deploy without it.", getClass().getName());
                }
                else
                {
                    // Append plan path
                    java.createArg().setValue(geronimoDeployable.getPlan());
                }
            }
        }
    }

    /**
     * Add moduleId argument to the deployer Ant Java task.
     * @param java the Ant Java task
     * @param moduleId the deployable ID
     */
    private void addModuleIdArgument(Java java, String moduleId)
    {
        if (moduleId != null)
        {
            java.createArg().setValue(moduleId);
        }
    }

    /**
     * Add system properties to the Ant java command used to execute the Geronimo deploy tool.
     *
     * @param java the java command that will start the container
     */
    private void addSystemProperties(Java java)
    {
        if (getInstalledContainer().getSystemProperties() != null)
        {
            Iterator keys = getInstalledContainer().getSystemProperties().keySet().iterator();
            while (keys.hasNext())
            {
                String key = (String) keys.next();

                java.addSysproperty(getAntUtils().createSysProperty(key,
                    (String) getInstalledContainer().getSystemProperties().get(key)));
            }
        }
    }

    /**
     * Add extra container classpath entries specified by the user.
     *
     * @param javaCommand the java command used to start/stop the container
     */
    private void addExtraClasspath(Java javaCommand)
    {
        Path classpath = javaCommand.createClasspath();
        if (getInstalledContainer().getExtraClasspath() != null)
        {
            Path path = new Path(getAntUtils().createProject());

            for (int i = 0; i < getInstalledContainer().getExtraClasspath().length; i++)
            {
                Path pathElement = new Path(getAntUtils().createProject(),
                    getInstalledContainer().getExtraClasspath()[i]);
                path.addExisting(pathElement);

                getLogger().debug("Adding [" + pathElement + "] to execution classpath",
                    this.getClass().getName());
            }

            classpath.addExisting(path);
        }
    }

    /**
     * Returns the moduleId for the specified deployable.
     * @param deployable the target deployable
     * @return the moduleId to use for the specified deployable
     */
    private String getModuleId(Deployable deployable)
    {
        String moduleId = null;

        String archiveFile = deployable.getFile();

        if (getFileHandler().exists(archiveFile))
        {
            //TODO: verify how Geronimo computes the default moduleId (i.e. NO plan)
            moduleId = new File(archiveFile).getName().substring(0,
                new File(archiveFile).getName().lastIndexOf("."));
        }

        if (deployable instanceof GeronimoDeployable)
        {
            GeronimoDeployable geronimoDeployable = (GeronimoDeployable) deployable;

            if (geronimoDeployable.getPlan() != null)
            {
                File planFile = new File(geronimoDeployable.getPlan());

                if (planFile.exists())
                {
                    //TODO: parse the configId associated with the deployable from the plan,
                    // then assign to moduleId
                }
                else
                {
                    if (archiveFile != null)
                    {
                        //TODO: detect if plan is packaged inside the archive.
                        // If so, extract the plan,
                        // parse the configId associated with the deployable,
                        // then assign to moduleId
                    }
                }
            }
        }

        getLogger().debug("Computed module id [" + moduleId + "] for deployable ["
            + deployable.getFile() + "]", this.getClass().getName());

        return moduleId;
    }
}
