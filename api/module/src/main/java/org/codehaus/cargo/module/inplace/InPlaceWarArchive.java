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
package org.codehaus.cargo.module.inplace;

import org.codehaus.cargo.module.webapp.WarArchive;
import org.codehaus.cargo.module.webapp.WebXml;
import org.codehaus.cargo.module.webapp.WebXmlIo;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;

/**
 * @version $Id$
 */
public class InPlaceWarArchive extends InPlaceJarArchive
        implements WarArchive
{

    /**
     * Web XML.
     */
    private WebXml webXml;

    public InPlaceWarArchive(File rootDir)
    {
        super(rootDir);
    }

    /**
     * {@inheritDoc}
     *
     * @throws JDOMException
     * @see WarArchive#getWebXml()
     */
    public final WebXml getWebXml()
        throws IOException, JDOMException
    {
        if (this.webXml == null)
        {
            this.webXml = WebXmlIo.getWebXml(this);
        }
        return this.webXml;
    }

    /**
     * @param file1 file
     * @throws IOException error
     * @throws JDOMException error
     */
    public void store(File file1)
        throws IOException, JDOMException
    {
    }


}
