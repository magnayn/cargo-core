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
package org.codehaus.cargo.container.spi.deployer;

import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.util.log.LoggedObject;

import junit.framework.TestCase;

/**
 * Unit tests for {@link AbstractRemoteDeployer}.
 *
 * @version $Id$
 */
public class RemoteDeployerTest extends TestCase
{
    private class TestableAbstractRemoteDeployer extends AbstractRemoteDeployer
    {
        public void deploy(Deployable deployable)
        {
            // This ensures we don't perform any real deployment - This is for testing
        }
    }

    private class DeployableMonitorStub extends LoggedObject implements DeployableMonitor
    {
        private DeployableMonitorListener listener;
        private String deployableName;

        public DeployableMonitorStub(String deployableName)
        {
            this.deployableName = deployableName;
        }

        public void registerListener(DeployableMonitorListener listener)
        {
            this.listener = listener;
        }

        public void monitor()
        {
            this.listener.deployed();
        }

        public long getTimeout()
        {
            return 20000L;
        }

        public String getDeployableName()
        {
            return this.deployableName;
        }
    }

    public void testDeployMethodWithDeployableMonitorParameterCanBeCalled()
    {
        TestableAbstractRemoteDeployer deployer = new TestableAbstractRemoteDeployer();
        deployer.deploy(new WAR("some/file"), new DeployableMonitorStub("some/file"));
    }
}
