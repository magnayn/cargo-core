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
package org.codehaus.cargo.container.orion.internal;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;

/**
 * Capabilities of Orion's
 * {@link org.codehaus.cargo.container.orion.OrionStandaloneLocalConfiguration} configuration.
 *  
 * @version $Id$
 */
public class OrionStandaloneLocalConfigurationCapability 
    extends AbstractStandaloneLocalConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;
    
    /**
     * Initialize the configuration-specific supports Map.
     */
    public OrionStandaloneLocalConfigurationCapability()
    {
        super();
        
        this.supportsMap = new HashMap();
        this.supportsMap.put(GeneralPropertySet.LOGGING, Boolean.FALSE);
        supportDataSources();
    }

    /**
     * Add capability for all DataSource implementations
     */
    private void supportDataSources()
    {
        this.supportsMap.put(DatasourcePropertySet.DATASOURCE, Boolean.TRUE);
        this.supportsMap.put(DatasourcePropertySet.CONNECTION_TYPE, Boolean.TRUE);
        this.supportsMap.put(DatasourcePropertySet.TRANSACTION_SUPPORT, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     * @see AbstractStandaloneLocalConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }

}
