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

import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfigurationCapability;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;

import java.util.Map;
import java.util.HashMap;

/**
 * Capabilities of Tomcat's {@link org.codehaus.cargo.container.tomcat.TomcatRuntimeConfiguration}
 * configuration.
 *  
 * @version $Id$
 */
public class TomcatRuntimeConfigurationCapability extends AbstractRuntimeConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public TomcatRuntimeConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();

        this.supportsMap.put(TomcatPropertySet.MANAGER_URL, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     * @see AbstractRuntimeConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }

}
