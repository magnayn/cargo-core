/* 
 * ========================================================================
 * 
 * Copyright 2005 Vincent Massol.
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
package org.codehaus.cargo.container.weblogic;

/**
 * Gathers all WebLogic properties.
 * 
 * @version $Id$
 */
public interface WebLogicPropertySet
{
    /**
     * User with administrator rights.
     */
    String ADMIN_USER = "cargo.weblogic.administrator.user";

    /**
     * Password for user with administrator rights.
     */
    String ADMIN_PWD = "cargo.weblogic.administrator.password";

    /**
     * WebLogic server name.
     */
    String SERVER = "cargo.weblogic.server";
    
    /**
     * The auto-deploy folder, if different then default
     */
    String DEPLOYABLE_FOLDER = "cargo.weblogic.deployable.folder";

    /**
     * BEA Home. This is where bea products are installed
     */
    String BEA_HOME = "cargo.weblogic.bea.home";

    /**
     * Version of the domain configuration. Used in WebLogic 9x+, defaults to 9.2.3.0
     */
    String CONFIGURATION_VERSION = "cargo.weblogic.configuration.version";

    /**
     * Lowest common denominator of the servers in the domain. Used in WebLogic 9x+, defaults to
     * 9.2.3.0
     */
    String DOMAIN_VERSION = "cargo.weblogic.domain.version";
}
