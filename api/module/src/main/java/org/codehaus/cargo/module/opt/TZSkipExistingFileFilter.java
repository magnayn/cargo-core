// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TZSkipExistingFileFilter.java

package org.codehaus.cargo.module.opt;

import de.schlichtherle.io.File;

import java.io.FileFilter;
import java.io.PrintStream;

public class TZSkipExistingFileFilter
        implements FileFilter {

    public TZSkipExistingFileFilter(java.io.File target) {
        this.target = new File(target);
    }

    public TZSkipExistingFileFilter(java.io.File source, java.io.File target) {
        this.target = new File(target);
        this.source = new File(source);
    }

    public void setSource(java.io.File source) {
        this.source = new File(source);
    }

    protected String getRelativePath(java.io.File pathname) {
        return pathname.getAbsolutePath().substring(source.getAbsolutePath().length() + 1).replace('\\', '/');
    }

    public boolean accept(java.io.File pathname) {
        String path = getRelativePath(pathname);
        File targetPath = new File(target, path);
        if (targetPath.exists()) {
            System.out.println("Skip " + path);
            return false;
        } else {
            return true;
        }
    }

    private File source;
    private File target;
}
