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
package org.codehaus.cargo.container.resin.internal;

import org.codehaus.cargo.container.configuration.entry.DataSourceFixture;
import org.codehaus.cargo.container.configuration.entry.ResourceFixture;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractLocalConfigurationWithConfigurationBuilderTest;

public abstract class AbstractResinStandaloneLocalConfigurationTest extends
    AbstractLocalConfigurationWithConfigurationBuilderTest
{

    protected String getDataSourceConfigurationFile(DataSourceFixture fixture)
    {
        return configuration.getHome() + "/conf/resin.conf";
    }

    protected String getResourceConfigurationFile(ResourceFixture fixture)
    {
        return getDataSourceConfigurationFile(null);
    }
    
    public void testConfigure() throws Exception
    {
        configuration.configure(container);

        assertTrue(configuration.getFileHandler().exists(
            configuration.getHome() + "/conf/resin.conf"));
        assertTrue(configuration.getFileHandler().exists(
            configuration.getHome() + "/webapps/cargocpc.war"));
    }

    protected abstract void setUpDataSourceFile() throws Exception;

    protected String configureDataSourceViaPropertyAndRetrieveConfigurationFile(
        DataSourceFixture fixture) throws Exception
    {
        setUpDataSourceFile();
        return super.configureDataSourceViaPropertyAndRetrieveConfigurationFile(fixture);
    }

    protected String configureDataSourceAndRetrieveConfigurationFile(DataSourceFixture fixture)
        throws Exception
    {
        setUpDataSourceFile();
        return super.configureDataSourceAndRetrieveConfigurationFile(fixture);
    }

    public void testConfigureCreatesDataSourceForDriverConfiguredDataSourceWithLocalTransactionSupport()
        throws Exception
    {
        try
        {
            super
                .testConfigureCreatesDataSourceForDriverConfiguredDataSourceWithLocalTransactionSupport();
            fail("should have received an exception");

        }
        catch (UnsupportedOperationException e)
        {
            assertEquals(Resin2xConfigurationBuilder.TRANSACTIONS_WITH_XA_OR_JCA_ONLY,
                e.getMessage());
        }
    }

    public void testConfigureCreatesDataSourceForDriverConfiguredDataSourceWithXaTransactionSupport()
        throws Exception
    {
        try
        {
            super
                .testConfigureCreatesDataSourceForDriverConfiguredDataSourceWithXaTransactionSupport();
            fail("should have received an exception");

        }
        catch (UnsupportedOperationException e)
        {
            assertEquals(Resin2xConfigurationBuilder.TRANSACTIONS_WITH_XA_OR_JCA_ONLY,
                e.getMessage());
        }
    }

    public void testConfigureCreatesResourceForXADataSource() throws Exception
    {
        // TODO Resource support is currently unimplemented
    }

    public void testConfigureCreatesResource() throws Exception
    {
        // TODO Resource support is currently unimplemented
    }

    public void testConfigureCreatesTwoResourcesViaProperties() throws Exception
    {
        // TODO Resource support is currently unimplemented
    }

}
