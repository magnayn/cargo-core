/*
 * ========================================================================
 *
 * Copyright 2004-2006 Vincent Massol.
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
package org.codehaus.cargo.container.spi.configuration;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.builder.ConfigurationEntryType;
import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.configuration.entry.Resource;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.internal.util.ResourceUtils;
import org.codehaus.cargo.container.property.DataSourceConverter;
import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ResourceConverter;
import org.codehaus.cargo.container.property.ResourcePropertySet;
import org.codehaus.cargo.container.property.TransactionSupport;
import org.codehaus.cargo.util.AntUtils;
import org.codehaus.cargo.util.CargoException;
import org.codehaus.cargo.util.FileHandler;
import org.codehaus.cargo.util.DefaultFileHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base implementation of
 * {@link org.codehaus.cargo.container.spi.configuration.ContainerConfiguration} that can be
 * specialized for standalone configuration, existing configuration or other local configurations.
 * 
 * @version $Id$
 */
public abstract class AbstractLocalConfiguration extends AbstractConfiguration implements
    LocalConfiguration
{
    /**
     * The path under which the container resources are stored in the JAR.
     */
    protected static final String RESOURCE_PATH =
        "/org/codehaus/cargo/container/internal/resources/";

    /**
     * List of {@link Deployable}s to deploy into the container.
     */
    private List deployables;

    /**
     * The home directory for the configuration. This is where the associated container will be set
     * up to start and where it will deploy its deployables.
     */
    private String home;

    /**
     * Ant utility class.
     */
    private AntUtils antUtils;

    /**
     * Resource utility class.
     */
    private ResourceUtils resourceUtils;

    /**
     * File utility class.
     */
    private FileHandler fileHandler;

    /**
     * List of {@link Resource}s to add to a container.
     */
    private List resources;

    /**
     * List of {@link DataSource}s to add to a container.
     */
    private List dataSources;

    /**
     * @param home the home directory where the container will be set up to start and where it will
     *            deploy its deployables.
     */
    public AbstractLocalConfiguration(String home)
    {
        super();

        this.deployables = new ArrayList();
        this.fileHandler = new DefaultFileHandler();
        this.antUtils = new AntUtils();
        this.resourceUtils = new ResourceUtils();
        this.resources = new ArrayList();
        this.dataSources = new ArrayList();

        this.home = home;
        setProperty(GeneralPropertySet.JAVA_HOME, System.getProperty("java.home"));
    }

    /**
     * @return the file utility class to use for performing all file I/O.
     */
    public FileHandler getFileHandler()
    {
        return this.fileHandler;
    }

    /**
     * @param fileHandler the file utility class to use for performing all file I/O.
     */
    public void setFileHandler(FileHandler fileHandler)
    {
        this.fileHandler = fileHandler;
    }

    /**
     * @return the Ant utility class
     */
    protected final AntUtils getAntUtils()
    {
        return this.antUtils;
    }

    /**
     * @return the Resource utility class
     */
    protected final ResourceUtils getResourceUtils()
    {
        return this.resourceUtils;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.configuration.LocalConfiguration#addDeployable(org.codehaus.cargo.container.deployable.Deployable)
     */
    public synchronized void addDeployable(Deployable newDeployable)
    {
        this.deployables.add(newDeployable);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.configuration.LocalConfiguration#getDeployables()
     */
    public List getDeployables()
    {
        return this.deployables;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.cargo.container.configuration.LocalConfiguration#getHome()
     */
    public String getHome()
    {
        return this.home;
    }

    /**
     * {@inheritDoc}
     * 
     * @see LocalConfiguration#configure(LocalContainer)
     */
    public void configure(LocalContainer container)
    {
        parsePropertiesForPendingConfiguration();
        verify();

        try
        {
            doConfigure(container);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to create a " + container.getName() + " "
                + getType().getType() + " configuration", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see ContainerConfiguration#verify()
     */
    public void verify()
    {
        collectUnsupportedResourcesAndThrowException();
        collectUnsupportedDataSourcesAndThrowException();
        super.verify();
    }

    /**
     * Warn user and throw an Exception if any unsupported {@link Resource}s are setup for this
     * configuration.
     */
    public void collectUnsupportedResourcesAndThrowException()
    {
        if (getResources().size() > 0
            && !this.getCapability().supportsProperty(ResourcePropertySet.RESOURCE))
        {
            StringBuffer errorMessage = new StringBuffer();
            Iterator resourceIterator = getResources().iterator();
            while (resourceIterator.hasNext())
            {

                Resource resource = (Resource) resourceIterator.next();
                String message =
                    "This configuration does not support Resource configuration! JndiName: "
                        + resource.getName();
                getLogger().warn(message, getClass().getName());
                if (!errorMessage.toString().equals(""))
                {
                    errorMessage.append("\n");
                }
                errorMessage.append(message);
            }
            throw new CargoException(errorMessage.toString());
        }
    }

    /**
     * Warn user and throw an Exception if any unsupported {@link DataSource}s are setup for this
     * configuration.
     */
    public void collectUnsupportedDataSourcesAndThrowException()
    {
        StringBuffer errorMessage = new StringBuffer();

        Iterator dataSourceIterator = getDataSources().iterator();
        while (dataSourceIterator.hasNext())
        {
            DataSource dataSource = (DataSource) dataSourceIterator.next();
            String reason = null;
            if (!this.getCapability().supportsProperty(DatasourcePropertySet.DATASOURCE))
            {
                reason = "This configuration does not support DataSource configuration! ";
            }
            else if (ConfigurationEntryType.XA_DATASOURCE.toString().equals(
                dataSource.getConnectionType())
                && !this.getCapability().supportsProperty(DatasourcePropertySet.CONNECTION_TYPE))
            {
                reason =
                    "This configuration does not support XADataSource configured DataSources! ";
            }
            else if (!ConfigurationEntryType.XA_DATASOURCE.toString()
                .equals(dataSource.getConnectionType())
                && !TransactionSupport.NO_TRANSACTION.equals(dataSource.getTransactionSupport())
                && !this.getCapability().supportsProperty(
                    DatasourcePropertySet.TRANSACTION_SUPPORT))
            {
                reason =
                    "This configuration does not support Transactions on Driver configured "
                        + "DataSources! ";
            }
            if (reason != null)
            {
                String message = reason + "JndiName: " + dataSource.getJndiLocation();
                if (!errorMessage.toString().equals(""))
                {
                    errorMessage.append("\n");
                }
                errorMessage.append(message);
                getLogger().warn(message, getClass().getName());
            }
        }
        if (!errorMessage.toString().equals(""))
        {
            throw new CargoException(errorMessage.toString());
        }
    }

    /**
     * Some configuration can be specified as encoded properties. Parse properties and apply what is
     * found to the appropriate pending configuration list.
     */
    public void parsePropertiesForPendingConfiguration()
    {
        addResourcesFromProperties();
        addDataSourcesFromProperties();
    }

    /**
     * Parse properties and add any Resources to pending configuration. Resources will be found if
     * their property name starts with: {@link ResourcePropertySet#RESOURCE}
     */
    protected void addResourcesFromProperties()
    {
        Iterator propertyNames = getProperties().keySet().iterator();
        getLogger().debug("Searching properties for Resource definitions",
            this.getClass().getName());
        while (propertyNames.hasNext())
        {
            String propertyName = propertyNames.next().toString();
            if (propertyName.startsWith(ResourcePropertySet.RESOURCE))
            {
                String resourceProperty = getPropertyValue(propertyName);
                getLogger().debug("Found Resource definition: value [" + resourceProperty + "]",
                    this.getClass().getName());
                Resource resource = new ResourceConverter().fromPropertyString(resourceProperty);
                getResources().add(resource);
            }
        }
    }

    /**
     * Parse properties and add any DataSources to pending configuration. DataSources will be found
     * if their property name starts with: {@link DatasourcePropertySet#DATASOURCE}
     */
    protected void addDataSourcesFromProperties()
    {
        Iterator propertyNames = getProperties().keySet().iterator();
        getLogger().debug("Searching properties for DataSource definitions",
            this.getClass().getName());
        while (propertyNames.hasNext())
        {
            String propertyName = propertyNames.next().toString();
            if (propertyName.startsWith(DatasourcePropertySet.DATASOURCE))
            {
                String dataSourceProperty = getPropertyValue(propertyName);
                getLogger().debug(
                    "Found DataSource definition: value [" + dataSourceProperty + "]",
                    this.getClass().getName());
                DataSource dataSource =
                    new DataSourceConverter().fromPropertyString(dataSourceProperty);
                getDataSources().add(dataSource);
            }
        }
    }

    /**
     * Implementation of {@link LocalConfiguration#configure(LocalContainer)} that all local
     * configuration using this class must implement. This provides the ability to perform generic
     * actions before and after the container-specific implementation. Another way would be to use
     * AOP...
     * 
     * @param container the container to configure
     * @throws Exception if any error is raised during the configuration
     */
    protected abstract void doConfigure(LocalContainer container) throws Exception;

    /**
     * {@inheritDoc}
     * 
     * @see LocalConfiguration#addResource(Resource)
     */
    public void addResource(Resource resource)
    {
        this.resources.add(resource);
    }

    /**
     * @return the configured resources for this container.
     */
    public List getResources()
    {
        return this.resources;
    }

    /**
     * {@inheritDoc}
     * 
     * @see LocalConfiguration#addDataSource(DataSource)
     */
    public void addDataSource(DataSource dataSource)
    {
        this.dataSources.add(dataSource);
    }

    /**
     * @return the configured DataSources for this container.
     */
    public List getDataSources()
    {
        return this.dataSources;
    }
}
