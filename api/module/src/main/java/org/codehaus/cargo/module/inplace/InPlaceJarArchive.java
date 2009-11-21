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

import de.schlichtherle.io.FileInputStream;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.util.DefaultFileHandler;
import org.codehaus.cargo.util.FileHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version $Id$
 */
public class InPlaceJarArchive
        implements JarArchive
{

    /**
     *
     */
    private File rootDirectory;

    /**
     * @param rootDirectory Root Directory.
     */
    public InPlaceJarArchive(File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
    }

    /**
     * @param className The name of the class to search for
     * @return Does contain class
     * @throws IOException  error
     */
    public boolean containsClass(String className)
        throws IOException
    {
        String resourceName = className.replace('.', '/') + ".class";
        return getResource(resourceName) != null;
    }

    /**
     * @param path The path to expand to
     * @throws IOException error
     */
    public void expandToPath(String path)
        throws IOException
    {
        FileHandler h = new DefaultFileHandler();
        h.copyDirectory(rootDirectory.getAbsolutePath(), path);
    }

    /**
     * @param s1          item
     * @param filefilter1 file
     * @throws IOException error
     */
    public void expandToPath(String s1, FileFilter filefilter1) throws IOException
    {
    }

    /**
     * @return file
     */
    protected File getFile()
    {
        return rootDirectory;
    }

    /**
     * @param name The name of the resource
     * @return path
     * @throws IOException error
     */
    public String findResource(String name)
        throws IOException
    {
        String result = null;
        List resources = getResources("");
        Iterator i = resources.iterator();
        do
        {
            if (!i.hasNext())
            {
                break;
            }
            String entryPath = (String) i.next();
            String entryName = entryPath;
            int lastSlashIndex = entryName.lastIndexOf('/');
            if (lastSlashIndex >= 0)
            {
                entryName = entryName.substring(lastSlashIndex + 1);
            }
            if (!entryName.equals(name))
            {
                continue;
            }
            result = entryPath;
            break;
        }
        while (true);
        return result;
    }

    /**
     * @param path The path to the resource in the archive
     * @return stream
     * @throws IOException error
     */
    public InputStream getResource(String path)
        throws IOException
    {
        if (!rootDirectory.exists())
        {
            throw new IOException("Archive does not exist");
        }
        File item = new File(rootDirectory, path);
        if (!item.exists())
        {
            return null;
        }
        else
        {
            return new FileInputStream(item);
        }
    }

    /**
     * @param path The directory
     * @return list of resources
     * @throws IOException error
     */
    public List getResources(String path)
        throws IOException
    {
        File item = new File(rootDirectory, path);
        List resources = new ArrayList();
        String res [] = item.list();
        if (res != null)
        {
            for (int i = 0; i < res.length; i++)
            {
                resources.add(res[i]);
            }

        }
        return resources;
    }
}
