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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.entry.ConfigurationFixtureFactory;
import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.property.ResourcePropertySet;
import org.codehaus.cargo.util.CargoException;

public class LocalConfigurationTest extends TestCase
{

    private class LocalConfigurationThatSupportsProperty extends AbstractLocalConfiguration
    {
        private List supportedProperties = null;

        public LocalConfigurationThatSupportsProperty(List supportedProperties)
        {
            super(null);
            this.supportedProperties = supportedProperties;
        }

        protected void doConfigure(LocalContainer container) throws Exception
        {
        }

        public ConfigurationCapability getCapability()
        {
            return new ConfigurationCapability()
            {

                public Map getProperties()
                {
                    return null;
                }

                public boolean supportsProperty(String propertyName)
                {
                    if (supportedProperties.contains(propertyName))
                    {
                        return true;
                    }
                    return false;
                }
            };
        }

        public ConfigurationType getType()
        {
            return null;
        }
    }

    public void testNoResourceSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays.asList(new String[] {}));

        configuration.getResources().add(
            ConfigurationFixtureFactory.createXADataSourceAsResource().buildResource());
        try
        {
            configuration.collectUnsupportedResourcesAndThrowException();
            fail("should have gotten an Exception");

        }
        catch (CargoException e)
        {
            assertEquals(
                "This configuration does not support Resource configuration! JndiName: resource/XADataSource",
                e.getMessage());
        }
    }

    public void testResourceSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays
                .asList(new String[] {ResourcePropertySet.RESOURCE}));

        configuration.getResources().add(
            ConfigurationFixtureFactory.createXADataSourceAsResource().buildResource());
        configuration.collectUnsupportedResourcesAndThrowException();
        assertEquals(1, configuration.getResources().size());
    }

    public void testNoDataSourceSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays.asList(new String[] {}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        try
        {
            configuration.collectUnsupportedDataSourcesAndThrowException();
            fail("should have gotten an Exception");

        }
        catch (CargoException e)
        {
            assertEquals(
                "This configuration does not support DataSource configuration! JndiName: jdbc/CargoDS",
                e.getMessage());
        }
    }

    public void testDataSourceSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays
                .asList(new String[] {DatasourcePropertySet.DATASOURCE}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        configuration.collectUnsupportedDataSourcesAndThrowException();
        assertEquals(1, configuration.getDataSources().size());
    }

    public void testNoDataSourceTransactionEmulationSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays
                .asList(new String[] {DatasourcePropertySet.DATASOURCE}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        configuration.getDataSources().add(
            ConfigurationFixtureFactory
                .createDriverConfiguredDataSourceWithXaTransactionSupport().buildDataSource());

        try
        {
            configuration.collectUnsupportedDataSourcesAndThrowException();
            fail("should have gotten an Exception");

        }
        catch (CargoException e)
        {
            assertEquals(
                "This configuration does not support Transactions on Driver configured DataSources! JndiName: jdbc/CargoDS",
                e.getMessage());
        }
    }

    public void testDataSourceTransactionEmulationSupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays.asList(new String[] {
            DatasourcePropertySet.DATASOURCE, DatasourcePropertySet.TRANSACTION_SUPPORT}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        configuration.getDataSources().add(
            ConfigurationFixtureFactory
                .createDriverConfiguredDataSourceWithXaTransactionSupport().buildDataSource());
        configuration.collectUnsupportedDataSourcesAndThrowException();
        assertEquals(2, configuration.getDataSources().size());
    }

    public void testNoDataSourceXASupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays
                .asList(new String[] {DatasourcePropertySet.DATASOURCE}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createXADataSourceConfiguredDataSource()
                .buildDataSource());
        try
        {
            configuration.collectUnsupportedDataSourcesAndThrowException();
            fail("should have gotten an Exception");

        }
        catch (CargoException e)
        {
            assertEquals(
                "This configuration does not support XADataSource configured DataSources! JndiName: jdbc/CargoDS",
                e.getMessage());
        }
    }

    public void testDataSourceXASupport()
    {
        AbstractLocalConfiguration configuration =
            new LocalConfigurationThatSupportsProperty(Arrays.asList(new String[] {
            DatasourcePropertySet.DATASOURCE, DatasourcePropertySet.CONNECTION_TYPE}));

        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createDataSource().buildDataSource());
        configuration.getDataSources().add(
            ConfigurationFixtureFactory.createXADataSourceConfiguredDataSource()
                .buildDataSource());
        configuration.collectUnsupportedDataSourcesAndThrowException();
        assertEquals(2, configuration.getDataSources().size());
    }
}
