// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InPlaceJarArchive.java

package org.codehaus.cargo.module.inplace;

import de.schlichtherle.io.FileInputStream;

import java.io.*;
import java.util.*;

import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.util.DefaultFileHandler;
import org.codehaus.cargo.util.FileHandler;

public class InPlaceJarArchive
        implements JarArchive {

    public InPlaceJarArchive(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public boolean containsClass(String className)
            throws IOException {
        String resourceName = className.replace('.', '/') + ".class";
        return getResource(resourceName) != null;
    }

    public void expandToPath(String path)
            throws IOException {
        FileHandler h = new DefaultFileHandler();
        h.copyDirectory(rootDirectory.getAbsolutePath(), path);
    }

    public void expandToPath(String s1, FileFilter filefilter1)
            throws IOException {
    }

    protected File getFile() {
        return rootDirectory;
    }

    public String findResource(String name)
            throws IOException {
        String result = null;
        List resources = getResources("");
        Iterator i = resources.iterator();
        do {
            if (!i.hasNext())
                break;
            String entryPath = (String) i.next();
            String entryName = entryPath;
            int lastSlashIndex = entryName.lastIndexOf('/');
            if (lastSlashIndex >= 0)
                entryName = entryName.substring(lastSlashIndex + 1);
            if (!entryName.equals(name))
                continue;
            result = entryPath;
            break;
        } while (true);
        return result;
    }

    public InputStream getResource(String path)
            throws IOException {
        if (!rootDirectory.exists())
            throw new IOException("Archive does not exist");
        File item = new File(rootDirectory, path);
        if (!item.exists())
            return null;
        else
            return new FileInputStream(item);
    }

    public List getResources(String path)
            throws IOException {
        File item = new File(rootDirectory, path);
        List resources = new ArrayList();
        String res[] = item.list();
        if (res != null) {
            for (int i = 0; i < res.length; i++)
                resources.add(res[i]);

        }
        return resources;
    }

    File rootDirectory;
}
