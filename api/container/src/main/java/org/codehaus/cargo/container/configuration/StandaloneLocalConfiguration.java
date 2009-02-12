/* 
 * ========================================================================
 * 
 * Copyright 2005-2008 Vincent Massol.
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
package org.codehaus.cargo.container.configuration;

import java.util.List;

import org.apache.tools.ant.types.FilterChain;

/**
 * Using a standalone configuration allows Cargo to create a valid configuration for your container
 * in the directory of your choice. It uses default parameters and allows you to modify important
 * ones. If you find that there are parameters that you cannot modify using a standalone
 * configuration you should switch to an {@link ExistingLocalConfiguration}. However, doing so means
 * that you'll need to set up the configuration yourself on your local file system. 
 *  
 * @version $Id$
 */
public interface StandaloneLocalConfiguration extends LocalConfiguration
{

    /**
     * set the config file property for a configuration. 
     * 
     * @param fileConfig The FileConfig to use
     */
    void setConfigFileProperty(FileConfig fileConfig);

    /**
     * set the file property for a configuration. 
     * @param fileConfig The FileConfig to use
     */
    void setFileProperty(FileConfig fileConfig);
    
    /**
     * Returns the file configurations.
     * 
     * @return The configuration file properies
     */
    List getFileProperties();

    /**
     * Returns the filterchain for this configuration.
     * 
     * @return The filterchain
     */
    FilterChain getFilterChain();

}
