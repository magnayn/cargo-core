/*
 * ========================================================================
 *
 * Copyright 2006 Vincent Massol.
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
package org.codehaus.cargo.container.stub;

import java.util.Map;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.util.FileHandler;

/**
 * Code common to all Installed local container stubs.
 * 
 * @version $Id: $
 */
public abstract class AbstractInstalledLocalContainerStub extends AbstractLocalContainerStub
    implements InstalledLocalContainer
{
    public String getHome()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setHome(String home)
    {
        // TODO Auto-generated method stub
        
    }

    public FileHandler getFileHandler()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFileHandler(FileHandler handler)
    {
        // TODO Auto-generated method stub
        
    }

    public ContainerType getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void addExtraClasspath(String location)
    {
        // TODO Auto-generated method stub
        
    }

    public void addSharedClasspath(String location)
    {
        // TODO Auto-generated method stub
        
    }

    public String[] getExtraClasspath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getSharedClasspath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Map getSystemProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setExtraClasspath(String[] classpath)
    {
        // TODO Auto-generated method stub
        
    }

    public void setSharedClasspath(String[] classpath)
    {
        // TODO Auto-generated method stub
        
    }

    public void setSystemProperties(Map properties)
    {
        // TODO Auto-generated method stub
        
    }
}
