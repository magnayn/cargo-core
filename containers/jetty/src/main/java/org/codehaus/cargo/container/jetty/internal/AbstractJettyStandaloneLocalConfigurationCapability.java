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
package org.codehaus.cargo.container.jetty.internal;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;

/**
 * Capabilities of the Jetty's
 * {@link org.codehaus.cargo.container.jetty.internal.AbstractJettyStandaloneLocalConfiguration}
 * configuration.
 * 
 * @version $Id$
 */
public abstract class AbstractJettyStandaloneLocalConfigurationCapability
    extends AbstractStandaloneLocalConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public AbstractJettyStandaloneLocalConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();

        this.addProperty(GeneralPropertySet.HOSTNAME, Boolean.FALSE);
        this.addProperty(GeneralPropertySet.JVMARGS, Boolean.FALSE);
        this.addProperty(GeneralPropertySet.PROTOCOL, Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     * @see AbstractStandaloneLocalConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }

    /**
     * Add a property.
     * @param property the property to add
     * @param supported true if supported false otherwise
     */
    protected void addProperty(String property, Boolean supported)
    {
        this.supportsMap.put(property, supported);
    }
}
