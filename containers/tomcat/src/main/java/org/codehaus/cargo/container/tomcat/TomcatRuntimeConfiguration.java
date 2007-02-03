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
package org.codehaus.cargo.container.tomcat;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;
import org.codehaus.cargo.container.tomcat.internal.TomcatRuntimeConfigurationCapability;

/**
 * Configuration to use when using a
 * {@link org.codehaus.cargo.container.tomcat.internal.AbstractTomcatRemoteContainer}.
 *  
 * @version $Id$
 */
public class TomcatRuntimeConfiguration extends AbstractRuntimeConfiguration
{
    /**
     * Capability of the Tomcat runtime configuration.
     */
    private static ConfigurationCapability capability = 
        new TomcatRuntimeConfigurationCapability();

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.Configuration#getCapability()
     */
    public ConfigurationCapability getCapability()
    {
        return capability;
    }
    
    /**
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString()
    {
        return "Tomcat Runtime Configuration";
    }
}
