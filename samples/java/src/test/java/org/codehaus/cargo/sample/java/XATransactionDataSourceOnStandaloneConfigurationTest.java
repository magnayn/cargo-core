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
package org.codehaus.cargo.sample.java;

import junit.framework.Test;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.entry.ConfigurationFixtureFactory;
import org.codehaus.cargo.container.configuration.entry.DataSourceFixture;
import org.codehaus.cargo.sample.java.validator.HasEarSupportValidator;
import org.codehaus.cargo.sample.java.validator.HasXASupportValidator;
import org.codehaus.cargo.sample.java.validator.IsInstalledLocalContainerValidator;
import org.codehaus.cargo.sample.java.validator.Validator;
import org.codehaus.cargo.sample.java.validator.HasStandaloneConfigurationValidator;
import org.codehaus.cargo.sample.java.validator.HasDataSourceSupportValidator;

import java.net.MalformedURLException;
import java.util.Collections;

public class XATransactionDataSourceOnStandaloneConfigurationTest extends
    AbstractDataSourceWarCapabilityContainerTestCase
{
    public XATransactionDataSourceOnStandaloneConfigurationTest(String testName,
        EnvironmentTestData testData) throws Exception
    {
        super(testName, testData);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        setContainer(createContainer(createConfiguration(ConfigurationType.STANDALONE)));
    }

    public static Test suite() throws Exception
    {
        CargoTestSuite suite =
            new CargoTestSuite("Tests that run on local containers supporting XADataSource configured DataSources and WAR deployments");

        // Note: We exclude geronimo1x container as it doesn't support static deployments yet.
        suite.addTestSuite(XATransactionDataSourceOnStandaloneConfigurationTest.class,
            new Validator[] {new IsInstalledLocalContainerValidator(),
            new HasStandaloneConfigurationValidator(),
            new HasEarSupportValidator(),// transaction
            // support
            new HasDataSourceSupportValidator(ConfigurationType.STANDALONE),
            new HasXASupportValidator(ConfigurationType.STANDALONE)}, Collections
                .singleton("geronimo1x"));
        return suite;
    }

    /**
     * User configures javax.sql.XADataSource -> container provides javax.sql.DataSource with xa
     * transaction support
     */
    public void testUserConfiguresXADataSourceAndRequestsDataSourceWithXaTransactionSupport()
        throws MalformedURLException
    {
        DataSourceFixture fixture =
            ConfigurationFixtureFactory.createXADataSourceConfiguredDataSource();

        _testServletThatIssuesGetConnectionFrom(fixture, "datasource-cmt-local");
    }

}
