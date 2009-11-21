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

import java.io.FileFilter;

/**
 * @version $Id$
 */
public class TZSkipExistingFileFilter
        implements FileFilter
{

    /**
     * @param target
     */
    public TZSkipExistingFileFilter(java.io.File target)
    {
        this.target = new File(target);
    }

    /**
     * @param source
     * @param target
     */
    public TZSkipExistingFileFilter(java.io.File source, java.io.File target)
    {
        this.target = new File(target);
        this.source = new File(source);
    }

    /**
     * @param source
     */
    public void setSource(java.io.File source)
    {
        this.source = new File(source);
    }

    /**
     * @param pathname
     * @return
     */
    protected String getRelativePath(java.io.File pathname)
    {
        return pathname.getAbsolutePath().substring(source.getAbsolutePath().length() + 1).replace('\\', '/');
    }

    /**
     * @param pathname
     * @return
     */
    public boolean accept(java.io.File pathname)
    {
        String path = getRelativePath(pathname);
        File targetPath = new File(target, path);
        if (targetPath.exists())
        {
            System.out.println("Skip " + path);
            return false;
        }
        else
        {
            return true;
        }
    }

    private File source;
    private File target;
}
