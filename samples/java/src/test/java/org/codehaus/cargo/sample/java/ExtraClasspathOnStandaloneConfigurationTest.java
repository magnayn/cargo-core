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

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.entry.DataSourceFixture;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.sample.java.validator.IsInstalledLocalContainerValidator;
import org.codehaus.cargo.sample.java.validator.Validator;
import org.codehaus.cargo.sample.java.validator.HasStandaloneConfigurationValidator;
import org.codehaus.cargo.sample.java.validator.HasWarSupportValidator;
import org.codehaus.cargo.util.CargoException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

public class ExtraClasspathOnStandaloneConfigurationTest extends
    AbstractCargoTestCase
{
    
    public ExtraClasspathOnStandaloneConfigurationTest(String testName, EnvironmentTestData testData)
        throws Exception
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
            new CargoTestSuite("Tests that run on local containers to test extra classpath");

        // Note: We exclude geronimo1x container as it doesn't support static deployments yet.
        suite.addTestSuite(ExtraClasspathOnStandaloneConfigurationTest.class, new Validator[] {
        new IsInstalledLocalContainerValidator(), new HasStandaloneConfigurationValidator(),
        new HasWarSupportValidator()}, Collections
            .singleton("geronimo1x"));
        return suite;
    }

    /**
     * Tests that a servlet has access to a class in added to the extraclasspath 
     * @throws MalformedURLException
     */
    public void testLoadClass() throws MalformedURLException
    {
        Deployable war =
            new DefaultDeployableFactory().createDeployable(getContainer().getId(), getTestData()
                .getTestDataFileFor("classpath-war"), DeployableType.WAR);
        
        getLocalContainer().getConfiguration().addDeployable(war);
        
        URL warPingURL =
            new URL("http://localhost:" + getTestData().port + "/" + "classpath-war-"
                + getTestData().version + "/test");
        
        getLocalContainer().start();
        
        PingUtils.assertPingTrue("simple war should have been started at this point", warPingURL,
            getLogger());

        getLocalContainer().stop();
        
        PingUtils.assertPingFalse("simple war should have been stopped at this point", warPingURL,
                getLogger());
    }

    public Container createContainer(Configuration configuration)
    {
        InstalledLocalContainer container =
            (InstalledLocalContainer) super.createContainer(configuration);
        
        String simpleJar = System.getProperty("cargo.testdata.simple-jar");
        if (simpleJar != null)
        {
            container.addExtraClasspath(simpleJar);
        } else {
            throw new CargoException("Please set property [cargo.testdata.simple-jar] to a valid location of simple-jar");
        }
        return container;
    }
    
}
