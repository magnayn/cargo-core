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

import org.codehaus.cargo.container.configuration.RuntimeConfiguration;
import org.codehaus.cargo.container.jboss.internal.AbstractJBossRemoteContainer;

/**
 * Special container support for wrapping a running instance of JBoss 5.x.
 * 
 * @version $Id: JBoss51xRemoteContainer.java 1705 2008-09-02 13:14:55Z adriana $
 */
public class JBoss51xRemoteContainer extends AbstractJBossRemoteContainer
{
    /**
     * Unique container id.
     */
    public static final String ID = "jboss51x";

    /**
     * {@inheritDoc}
     * @see AbstractJBossRemoteContainer#AbstractJBossRemoteContainer(org.codehaus.cargo.container.configuration.RuntimeConfiguration)
     */
    public JBoss51xRemoteContainer(RuntimeConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getName()
     */
    public final String getName()
    {
        return "JBoss 5.1.x Remote";
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getId()
     */
    public final String getId()
    {
        return ID;
    }
}
