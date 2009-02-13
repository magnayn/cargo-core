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
package org.codehaus.cargo.container.tomcat.internal;

import java.util.Properties;

import org.codehaus.cargo.container.configuration.builder.ConfigurationEntryType;
import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.configuration.entry.Resource;
import org.codehaus.cargo.container.internal.util.PropertyUtils;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractConfigurationBuilder;

/**
 * Constructs xml elements needed to configure a DataSource for Tomcat. Note that this
 * implementation converts DataSources into Resources and then uses an appropriate
 * {@link ConfigurationBuilder} to create the configuration.
 * 
 * @version $Id: $
 */
public abstract class AbstractTomcatConfigurationBuilder extends AbstractConfigurationBuilder
{

    /**
     * {@inheritDoc} this implementation first converts the DataSource to a Resource before
     * returning XML.
     * 
     * @see #convertDataSourceToResourceAndGetXMLEntry(DataSource)
     */
    public String buildEntryForDriverConfiguredDataSourceWithNoTx(DataSource ds)
    {
        return convertDataSourceToResourceAndGetXMLEntry(ds);

    }

    /**
     * This method converts the DataSource to a Resource and then builds the xml entry based on
     * that.
     * 
     * @return a datasource xml fragment that can be embedded directly into the server.xml file
     * @param ds the DataSource we are configuring.
     */
    protected String convertDataSourceToResourceAndGetXMLEntry(DataSource ds)
    {

        Resource dataSourceResource = convertToResource(ds);
        return toConfigurationEntry(dataSourceResource);
    }

    /**
     * This method converts the DataSource to a Resource used in Tomcat.
     * 
     * @return a Resource that can be used in Tomcat.
     * @param ds the DataSource we are configuring.
     */
    protected Resource convertToResource(DataSource ds)
    {
        Properties parameters = new Properties();
        PropertyUtils.setPropertyIfNotNull(parameters, "url", ds.getUrl());
        PropertyUtils.setPropertyIfNotNull(parameters, "user", ds.getUsername());
        PropertyUtils.setPropertyIfNotNull(parameters, "password", ds.getPassword());

        parameters.putAll(ds.getConnectionProperties());

        Resource resource = new Resource(ds.getJndiLocation(), ConfigurationEntryType.DATASOURCE);
        resource.setParameters(parameters);

        PropertyUtils.setPropertyIfNotNull(parameters, "factory", getDataSourceFactoryClass());
        PropertyUtils.setPropertyIfNotNull(parameters, "driverClassName", ds.getDriverClass());
        return resource;
    }

    /**
     * @return the <code>factory</code> responsible for looking creating the connection pool.
     */
    protected abstract String getDataSourceFactoryClass();

    /**
     * {@inheritDoc} This throws an UnsupportedOperationException as Tomcat is not transactional.
     */
    public String buildConfigurationEntryForXADataSourceConfiguredDataSource(DataSource ds)
    {
        throw new UnsupportedOperationException("Tomcat does not support "
            + "XADataSource configured DataSource implementations.");
    }

    /**
     * {@inheritDoc} This throws an UnsupportedOperationException as Tomcat is not transactional.
     */
    public String buildEntryForDriverConfiguredDataSourceWithLocalTx(DataSource ds)
    {
        throw new UnsupportedOperationException("Tomcat does not support "
            + ds.getTransactionSupport() + " for DataSource implementations.");
    }

    /**
     * {@inheritDoc} This throws an UnsupportedOperationException as Tomcat is not transactional.
     */
    public String buildEntryForDriverConfiguredDataSourceWithXaTx(DataSource ds)
    {
        throw new UnsupportedOperationException("Tomcat does not support "
            + ds.getTransactionSupport() + " for DataSource implementations.");
    }

}
