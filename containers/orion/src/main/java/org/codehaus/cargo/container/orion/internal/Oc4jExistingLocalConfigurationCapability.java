/*
 * ========================================================================
 *
 * Copyright 2003-2004 The Apache Software Foundation. Code from this file
 * was originally imported from the Jakarta Cactus project.
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

import org.codehaus.cargo.container.spi.configuration.AbstractExistingLocalConfigurationCapability;

/**
 * Capabilities of OC4J
 * {@link org.codehaus.cargo.container.orion.Oc4j10xExistingLocalConfiguration} configuration.
 *
 * @version $Id$
 */
public class Oc4jExistingLocalConfigurationCapability extends
    AbstractExistingLocalConfigurationCapability
{

    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public Oc4jExistingLocalConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();
        this.supportsMap.put(Oc4jPropertySet.AUTO_DEPLOY_DIR, Boolean.TRUE);
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
