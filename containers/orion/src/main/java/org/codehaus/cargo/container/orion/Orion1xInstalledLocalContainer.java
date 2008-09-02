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
package org.codehaus.cargo.container.orion;

import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.orion.internal.AbstractOrionInstalledLocalContainer;

/**
 * Special container support for the Orin 1.x application server.
 * 
 * @version $Id$
 */
public class Orion1xInstalledLocalContainer extends AbstractOrionInstalledLocalContainer
{
    /**
     * Unique container id.
     */
    public static final String ID = "orion1x";

    /**
     * {@inheritDoc}
     * @see AbstractOrionInstalledLocalContainer#AbstractInstalledLocalContainer(org.codehaus.cargo.container.configuration.LocalConfiguration)
     */
    public Orion1xInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getId()
     */
    public final String getId()
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.Container#getName()
     */
    public String getName()
    {
        return "Orion 1.x";
    }

    /**
     * {@inheritDoc}
     * @see AbstractOrionInstalledLocalContainer#getStartClassname()
     */
    protected String getStartClassname()
    {
        return "com.evermind.server.ApplicationServer";
    }

    /**
     * {@inheritDoc}
     * @see AbstractOrionInstalledLocalContainer#getStopClassname()
     */
    protected String getStopClassname()
    {
        return "com.evermind.client.orion.OrionConsoleAdmin";
    }

    /**
     * {@inheritDoc}
     * @see AbstractOrionInstalledLocalContainer#getContainerClasspathIncludes()
     */
    protected String getContainerClasspathIncludes()
    {
        return "*.jar";
    }
}
