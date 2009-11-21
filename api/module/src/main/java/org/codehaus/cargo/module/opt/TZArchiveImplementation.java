// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TZArchiveImplementation.java

package org.codehaus.cargo.module.opt;


public class TZArchiveImplementation {

    public static TZArchiveImplementation getInstance() {
        return INSTANCE;
    }

    private TZArchiveImplementation() {
        isAvailable = false;
        isEnabled = false;
        try {
            Class cls = Class.forName("de.schlichtherle.io.File");
            isAvailable = true;
        }
        catch (Exception ex) {
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean shouldUse() {
        return isAvailable && isEnabled;
    }

    private static TZArchiveImplementation INSTANCE = new TZArchiveImplementation();
    private boolean isAvailable;
    private boolean isEnabled;

}
