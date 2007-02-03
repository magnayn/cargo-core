/* 
 * ========================================================================
 * 
 * Copyright 2005-2006 Vincent Massol.
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

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import org.codehaus.cargo.sample.java.validator.Validator;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.generic.DefaultContainerFactory;

import junit.framework.TestSuite;
import junit.framework.Test;

public class CargoTestSuite extends TestSuite
{
    private static final String SYSTEM_PROPERTY_CONTAINER_IDS = "cargo.containers";

    private Map registeredContainers;

    private List containerIds;

    public CargoTestSuite(String suiteName)
    {
        super(suiteName);
        initialize();
    }

    private void initialize()
    {
        this.registeredContainers = new DefaultContainerFactory().getContainerIds();
        this.containerIds = new ArrayList();

        if (System.getProperty(SYSTEM_PROPERTY_CONTAINER_IDS) == null)
        {
            throw new RuntimeException("System property \"" + SYSTEM_PROPERTY_CONTAINER_IDS
                + "\" must be defined.");
        }

        StringTokenizer tokens =
            new StringTokenizer(System.getProperty(SYSTEM_PROPERTY_CONTAINER_IDS), ", ");
        while (tokens.hasMoreTokens())
        {
            String token = tokens.nextToken();
            this.containerIds.add(token);
        }
    }

    public void addTestSuite(Class testClass, Validator[] validators)
    {
        addTestSuite(testClass, validators, null);
    }

    public void addTestSuite(Class testClass, Validator[] validators, Set excludedContainerIds)
    {
        Iterator it = this.containerIds.iterator();
        while (it.hasNext())
        {
            String containerId = (String) it.next();

            // Skip container ids that are excluded by the user. Note: This feature is useful for
            // example to exclude geronimo1x as it doesn't support static deployments yet.
            if ((excludedContainerIds != null) && excludedContainerIds.contains(containerId))
            {
                continue;
            }

            // Find out all registered container types for this container id and for each of them
            // verify if the couple (containerId, type) should be added as a container identity
            // that can run tests in this suite.
            Set registeredTypes = (Set) this.registeredContainers.get(containerId);

            if (registeredTypes == null)
            {
                throw new RuntimeException("Invalid container id [" + containerId + "]");
            }

            for (Iterator types = registeredTypes.iterator(); types.hasNext();)
            {
                ContainerType type = (ContainerType) types.next();

                // Verify that the test passes all validators
                boolean shouldAddTest = true;
                for (int i = 0; i < validators.length; i++)
                {
                    if (!validators[i].validate(containerId, type))
                    {
                        shouldAddTest = false;
                        break;
                    }
                }

                // If so, adds the tests to the suite
                if (shouldAddTest)
                {
                    try
                    {
                        addContainerToSuite(containerId, type, testClass);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Failed to add container [" + containerId
                            + "] for test case class [" + testClass.getName() + "]", e);
                    }
                }
            }
        }
    }

    private void addContainerToSuite(String containerId, ContainerType type, Class testClass)
        throws Exception
    {
        // Find all methods starting with "test" and add them
        Method[] methods = testClass.getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().startsWith("test"))
            {
                addContainerToTest(containerId, type, methods[i].getName(), testClass);
            }
        }
    }

    private void addContainerToTest(String containerId, ContainerType type, String testName,
        Class testClass) throws Exception
    {
        String testClassName =
            testClass.getName().substring(testClass.getName().lastIndexOf(".") + 1);

        // Compute a unique directory for the test data based on the container id, the container
        // type and the test name.
        String targetDir = containerId + "/" + type.getType() + "/" + testClassName + "/"
            + testName + "/container";

        EnvironmentTestData testData = new EnvironmentTestData(containerId, type, targetDir);

        Constructor constructor = testClass.getConstructor(
            new Class[] { String.class, EnvironmentTestData.class });
        Test test = (Test) constructor.newInstance(new Object[] { testName, testData });
        addTest(test);
    }
}
