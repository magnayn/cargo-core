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
package org.codehaus.cargo.container.jboss;

/**
 * JBoss-specific properties.
 *
 * @version $Id$
 */
public interface JBossPropertySet
{
    /**
     * The JBoss configuration selected. Examples of valid values: "default", "all", "minimal".
     */
    String CONFIGURATION = "cargo.jboss.configuration";
    
    /**
     * Whether the JBoss Configuration should be clustered.
     */
    String CLUSTERED = "cargo.jboss.clustered";
    
    /**
     * The user to use when calling JBoss server commands.
     */
    String JBOSS_USER = "cargo.jboss.user";
    
    /**
     * The password to use when calling JBoss server commands; 
     */
    String JBOSS_PASSWORD = "cargo.jboss.password";
}
