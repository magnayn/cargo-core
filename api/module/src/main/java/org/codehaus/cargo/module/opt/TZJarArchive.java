// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TZJarArchive.java

package org.codehaus.cargo.module.opt;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;

import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveUpdateable;

public class TZJarArchive
        implements JarArchive, JarArchiveUpdateable {

    public TZJarArchive(java.io.File file) {
        if (file == null) {
            throw new NullPointerException();
        } else {
            zipFile = new File(file);
            return;
        }
    }

    public String getFilename() {
        return zipFile.getAbsolutePath();
    }

    public boolean containsClass(String className)
            throws IOException {
        String resourceName = className.replace('.', '/') + ".class";
        return getResource(resourceName) != null;
    }

    public final String findResource(String name)
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

    public final InputStream getResource(String path)
            throws IOException {
        if (!zipFile.exists())
            throw new IOException("Archive does not exist");
        File item = new File(zipFile, path);
        if (!item.exists())
            return null;
        else
            return new FileInputStream(item);
    }

    public final List getResources(String path)
            throws IOException {
        File item = new File(zipFile, path);
        List resources = new ArrayList();
        String res[] = item.list();
        if (res != null) {
            for (int i = 0; i < res.length; i++)
                resources.add(res[i]);

        }
        return resources;
    }

    public void expandToPath(String path)
            throws IOException {
        zipFile.copyAllTo(new File(path), ArchiveDetector.NULL);
    }

    public void expandToPath(String path, FileFilter filter)
            throws IOException {
        copy(zipFile, new File(path, ArchiveDetector.NULL), filter);
    }

    private void copy(File src, File destination, FileFilter filter)
            throws IOException {
        File files[] = (File[]) (File[]) (File[]) src.listFiles();
        for (int i = 0; i < files.length; i++) {
            File item = files[i];
            if (!item.isArchive() && item.isDirectory()) {
                if (!destination.exists())
                    destination.mkdir();
                copy(item, new File(destination, item.getName()), filter);
                continue;
            }
            if (filter.accept(item))
                item.copyTo(new File(destination, item.getName()));
        }

    }

    public void copyAllIntoJar(java.io.File source) {
        zipFile.archiveCopyAllFrom(source, ArchiveDetector.NULL);
    }

    public java.io.File getFile() {
        return zipFile;
    }

    public boolean delete(String resource) {
        File item = new File(zipFile, resource, ArchiveDetector.NULL);
        return item.delete();
    }

    public void copyAllIntoJar(java.io.File source, String targetDirectory) {
        File dest = new File(zipFile, targetDirectory, ArchiveDetector.NULL);
        dest.copyAllFrom(source, ArchiveDetector.NULL);
    }

    public void mkdir(String directory) {
        File item = new File(zipFile, directory);
        item.mkdirs();
    }

    public void copyAllIntoJar(java.io.File source, FileFilter fileFilter)
            throws IOException {
        File dest = new File(zipFile);
        File src = new File(source);
        copy(src, dest, fileFilter);
    }

    public void flush()
            throws IOException {
        File.umount();
    }

    public void create()
            throws IOException {
        zipFile.mkdir();
    }

    public boolean mkdirs(String resource)
            throws IOException {
        return false;
    }

    private File zipFile;
}
