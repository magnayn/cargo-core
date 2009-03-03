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
package org.codehaus.cargo.container.jrun.internal;

import org.codehaus.cargo.container.spi.configuration.AbstractExistingLocalConfigurationCapability;

import java.util.HashMap;
import java.util.Map;

/**
 * Capabilities of the JRun's {@link ResinExistingLocalConfigurationCapability} configuration.
 *
 * @version $Id: ResinExistingLocalConfigurationCapability.java $
 */
public class JRun4xExistingLocalConfigurationCapability
    extends AbstractExistingLocalConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public JRun4xExistingLocalConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }
}
