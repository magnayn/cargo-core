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

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveUpdateable;

import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version $Id$
 */
public class TZJarArchive
        implements JarArchive, JarArchiveUpdateable
{
    /**
     * Zip File
     */
    private File zipFile;

    /**
     * @param file
     */
    public TZJarArchive(java.io.File file)
    {
        if (file == null)
        {
            throw new NullPointerException();
        }
        else
        {
            zipFile = new File(file);
            return;
        }
    }

    /**
     * @return filename
     */
    public String getFilename()
    {
        return zipFile.getAbsolutePath();
    }

    /**
     * @param className The name of the class to search for
     * @return conains class
     * @throws IOException
     */
    public boolean containsClass(String className)
            throws IOException
    {
        String resourceName = className.replace('.', '/') + ".class";
        return getResource(resourceName) != null;
    }

    /**
     * @param name The name of the resource
     * @return resource
     * @throws IOException
     */
    public final String findResource(String name)
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
     * @throws IOException
     */
    public final InputStream getResource(String path)
        throws IOException
    {
        if (!zipFile.exists())
        {
            throw new IOException("Archive does not exist");
        }
        File item = new File(zipFile, path);
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
     * @return list
     * @throws IOException
     */
    public final List getResources(String path)
        throws IOException
    {
        File item = new File(zipFile, path);
        List resources = new ArrayList();
        String res[] = item.list();
        if (res != null)
        {
            for (int i = 0; i < res.length; i++)
            {
                resources.add(res[i]);
            }

        }
        return resources;
    }

    /**
     * @param path The path to expand to
     * @throws IOException
     */
    public void expandToPath(String path)
        throws IOException
    {
        zipFile.copyAllTo(new File(path), ArchiveDetector.NULL);
    }

    /**
     * @param path   The path to expand to
     * @param filter The filter to use
     * @throws IOException
     */
    public void expandToPath(String path, FileFilter filter)
        throws IOException
    {
        copy(zipFile, new File(path, ArchiveDetector.NULL), filter);
    }

    /**
     * @param src  source
     * @param destination destination
     * @param filter filter
     * @throws IOException error
     */
    private void copy(File src, File destination, FileFilter filter)
        throws IOException
    {
        File files[] = (File[]) (File[]) (File[]) src.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            File item = files[i];
            if (!item.isArchive() && item.isDirectory())
            {
                if (!destination.exists())
                {
                    destination.mkdir();
                }
                copy(item, new File(destination, item.getName()), filter);
                continue;
            }
            if (filter.accept(item))
            {
                item.copyTo(new File(destination, item.getName()));
            }
        }

    }

    /**
     * @param source source
     */
    public void copyAllIntoJar(java.io.File source)
    {
        zipFile.archiveCopyAllFrom(source, ArchiveDetector.NULL);
    }

    /**
     * @return file
     */
    public java.io.File getFile()
    {
        return zipFile;
    }

    /**
     * @param resource resource
     * @return success
     */
    public boolean delete(String resource)
    {
        File item = new File(zipFile, resource, ArchiveDetector.NULL);
        return item.delete();
    }

    /**
     * @param source source
     * @param targetDirectory target directory
     */
    public void copyAllIntoJar(java.io.File source, String targetDirectory)
    {
        File dest = new File(zipFile, targetDirectory, ArchiveDetector.NULL);
        dest.copyAllFrom(source, ArchiveDetector.NULL);
    }

    /**
     * @param directory
     */
    public void mkdir(String directory)
    {
        File item = new File(zipFile, directory);
        item.mkdirs();
    }

    /**
     * @param source
     * @param fileFilter
     * @throws IOException
     */
    public void copyAllIntoJar(java.io.File source, FileFilter fileFilter)
        throws IOException
    {
        File dest = new File(zipFile);
        File src = new File(source);
        copy(src, dest, fileFilter);
    }

    /**
     * @throws IOException error
     */
    public void flush()
            throws IOException
    {
        File.umount();
    }

    /**
     * @throws IOException error
     */
    public void create()
            throws IOException
    {
        zipFile.mkdir();
    }

    /**
     * @param resource error
     * @return success
     * @throws IOException error
     */
    public boolean mkdirs(String resource)
        throws IOException
    {
        return false;
    }

}
