/* 
 * ========================================================================
 * 
 * Copyright 2004-2005 Vincent Massol.
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
package org.codehaus.cargo.container.resin.internal;

import java.lang.reflect.Field;

import org.codehaus.cargo.container.ContainerException;

/**
 * Set of common Resin utility methods.
 * 
 * @version $Id$
 */
public class ResinUtil
{
    /**
     * @return the Resin version
     */
    public String getResinVersion()
    {
        return getResinVersion(this.getClass().getClassLoader());
    }

    /**
     * @return the Resin version
     * @param classloader the classloader to use to load the Resin Version class
     */
    public String getResinVersion(ClassLoader classloader)
    {
        String version;
        
        try
        {
            Class versionClass = classloader.loadClass("com.caucho.Version");
            Field versionField = versionClass.getField("VERSION");
            version = (String) versionField.get(null);
        }
        catch (Exception e)
        {
            throw new ContainerException("Cannot get Resin version", e);
        }

        return version;
    }
}
