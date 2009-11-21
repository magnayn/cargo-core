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

package org.codehaus.cargo.module.opt;

import de.schlichtherle.io.File;
import org.codehaus.cargo.module.AbstractDescriptorIo;
import org.codehaus.cargo.module.DefaultJarArchive;
import org.codehaus.cargo.module.Descriptor;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.webapp.WarArchive;
import org.codehaus.cargo.module.webapp.WebXml;
import org.codehaus.cargo.module.webapp.WebXmlIo;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class TZWarArchive extends TZJarArchive
        implements WarArchive
{
    /**
     * Constructor.
     *
     * @param file
     */
    public TZWarArchive(java.io.File file)
    {
        super(file);
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

    public final void store(java.io.File warFile)
        throws IOException, JDOMException
    {
        ((File) getFile()).copyAllTo(warFile);
        List descriptorNames = new ArrayList();
        descriptorNames.add("WEB-INF/" + getWebXml().getFileName());
        for (Iterator vendorDescriptors = getWebXml().getVendorDescriptors(); vendorDescriptors.hasNext(); descriptorNames.add("WEB-INF/" + ((Descriptor) vendorDescriptors.next()).getFileName()))
        {
            ;
        }
        File webXmlEntry = new File(warFile, "WEB-INF/" + getWebXml().getFileName());
        AbstractDescriptorIo.writeDescriptor(getWebXml(), webXmlEntry, null, true);
        Descriptor descriptor;
        File out;
        for (Iterator descriptors = getWebXml().getVendorDescriptors(); descriptors.hasNext(); AbstractDescriptorIo.writeDescriptor(descriptor, out, null, true))
        {
            descriptor = (Descriptor) descriptors.next();
            out = new File(warFile, "WEB-INF/" + descriptor.getFileName());
        }

    }

    /**
     * @param className The name of the class to search for
     * @return
     * @throws IOException
     */
    public final boolean containsClass(String className)
        throws IOException
    {
        boolean containsClass = false;
        String resourceName = "WEB-INF/classes/" + className.replace('.', '/') + ".class";
        if (getResource(resourceName) != null)
        {
            containsClass = true;
        }
        List jars = getResources("WEB-INF/lib/");
        Iterator i = jars.iterator();
        do
        {
            if (!i.hasNext())
            {
                break;
            }
            JarArchive jar = new DefaultJarArchive(getResource((String) i.next()));
            if (jar.containsClass(className))
            {
                containsClass = true;
            }
        }
        while (true);
        return containsClass;
    }


    private WebXml webXml;
}
