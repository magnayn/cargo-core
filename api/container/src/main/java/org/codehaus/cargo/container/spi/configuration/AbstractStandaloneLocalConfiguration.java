/* 
 * ========================================================================
 * 
 * Copyright 2004-2008 Vincent Massol.
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
package org.codehaus.cargo.container.spi.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.FilterChain;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.FileConfig;
import org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration;
import org.codehaus.cargo.container.property.GeneralPropertySet;

/**
 * Base implementation for a standalone local configuration.
 *
 * @version $Id$
 */
public abstract class AbstractStandaloneLocalConfiguration extends AbstractLocalConfiguration
    implements StandaloneLocalConfiguration
{

    /**
     * List of {@link FileConfig}s to use for the container.
     */
    private List files;

    /**
     * The filterChain for the configuration files. This contains the tokens and what
     * values they should be replaced with.
     */
    private FilterChain filterChain;

    /**
     * {@inheritDoc}
     * @see AbstractLocalConfiguration#AbstractLocalConfiguration(String)
     */
    public AbstractStandaloneLocalConfiguration(String dir)
    {
        super(dir);

        // Add all required properties that are common to all standalone configurations
        setProperty(GeneralPropertySet.LOGGING, "medium");
        this.files = new ArrayList();
    }

    /**
     * Configure the specified container.
     * @param container the container to configure
     */
    public void configure(LocalContainer container)
    {
        super.configure(container);
        configureFiles(getFilterChain());
    }
    
    /**
     * Set up the configuration directory (create it and clean it). We clean it because we want
     * to be sure the container starts with the same set up every time and there's no side effects
     * introduced by a previous run or someone modifying some files in there.
     *
     * <p>Note: We only clean the configuration directory if it's empty or if there is a Cargo
     * timestamp file. This is to prevent deleting not empty directories if the user has mistakenly
     * pointed the configuration dir to an existing location.</p>
     * 
     * @throws IOException if the directory cannot be created
     */
    protected void setupConfigurationDir() throws IOException
    {
        String timestampFile = getFileHandler().append(getHome(), ".cargo");

        // Start by cleaning the configuration directory. Do it only if there's already a Cargo
        // timestamp or if the configuration directory exists but is empty or if the configuration
        // directory doesn't exist.
        if (getFileHandler().exists(timestampFile)
            || (getFileHandler().exists(getHome())
                && getFileHandler().isDirectoryEmpty(getHome()))
            || !getFileHandler().exists(getHome()))
        {
            getFileHandler().delete(getHome());

            getFileHandler().mkdirs(getHome());

            // Create Cargo timestamp file
            getFileHandler().createFile(timestampFile);
        }
        else
        {
            throw new ContainerException("Invalid configuration dir [" + getHome() + "]. "
                + "When using standalone configurations, the configuration dir must point to an "
                + "empty directory. Note that everything in that dir will get deleted by Cargo.");
        }
    }

    /**
     * Creates the default filter chain that should be applied while copying
     * container configuration files to the working directory from which 
     * the container is started.
     * 
     * @return The default filter chain
     */
    protected final FilterChain createFilterChain()
    {
        this.filterChain = new FilterChain();
        
        // add all the token specified in the containers configuration into the filterchain
        getAntUtils().addTokensToFilterChain(filterChain, getProperties());
        
        return filterChain;
    }
    
    /**
     * {@inheritDoc}
     * @see ContainerConfiguration#verify()
     */
    public void verify()
    {
        super.verify();
        
        // Verify that the logging level is a valid level
        verifyLogging();
    }

    /**
     * Verify that the logging level specified is a valid level. 
     */
    private void verifyLogging()
    {
        String level = getPropertyValue(GeneralPropertySet.LOGGING);
        if (!level.equalsIgnoreCase("low")
            && !level.equalsIgnoreCase("medium")
            && !level.equalsIgnoreCase("high"))
        {
            throw new ContainerException("Invalid logging level [" + level 
                + "]. Valid levels are {\"low\", \"medium\", " + "\"high\"}");
        }
    }
   
    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.Configuration#getType()
     */
    public ConfigurationType getType()
    {
        return ConfigurationType.STANDALONE;
    }

    /**
     * {@inheritDoc}
     */
    public FilterChain getFilterChain()
    {
        if (this.filterChain == null)
        {
            this.filterChain = createFilterChain();
        }
        return this.filterChain;
    }
    
    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration#addConfigfile(org.codehaus.cargo.container.configuration.FileConfig)
     */
    public void setFileProperty(FileConfig fileConfig)
    {
        this.files.add(fileConfig);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration#addConfigfile(org.codehaus.cargo.container.configuration.FileConfig)
     */
    public void setConfigFileProperty(FileConfig fileConfig)
    {
        // a configuration file should always overwrite the previous file if it exists
        // since the token value could have changed during.
        fileConfig.setOverwrite(true);
        fileConfig.setConfigfile(true);
        this.setFileProperty(fileConfig);
    }
    
    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration#getConfigfiles()
     */
    public List getFileProperties()
    {
        return this.files;
    }
    
    /**
     *  Copy the customized configuration files into the cargo home directory.
     *  @param filterChain the filter chain to use during the copy
     */
    protected void configureFiles(FilterChain filterChain)
    {
       // List files = getFileProperties();
        List files = this.files;
        
        for (int i = 0; i < files.size(); i++)
        {
            FileConfig fileConfig = (FileConfig) files.get(i);
            boolean isDirectory = false;
            
            if (fileConfig.getFile() == null)
            {
                throw new RuntimeException("File cannot be null");
            }
            
            File origFile = new File(fileConfig.getFile());
            if (origFile.isDirectory())
            {
                isDirectory = true;
            }
              
            String destFile = getDestFileLocation(fileConfig.getFile(), 
                    fileConfig.getToDir(), fileConfig.getToFile());
            
            //we don't want to do anything if the file exists and overwrite is false
            if (!origFile.exists()  ||  fileConfig.getOverwrite())
            {
                if (fileConfig.getConfigfile())
                {
                    getFileHandler().copyFile(fileConfig.getFile(), destFile, filterChain);
                }
                if (isDirectory)
                {
                    String destDir = getDestDirectoryLocation(fileConfig.getFile(), fileConfig
                            .getToDir());
                    getFileHandler().copyDirectory(fileConfig.getFile(), destDir);
                }
                else
                {
                    getFileHandler().copyFile(fileConfig.getFile(), destFile);
                }
            } 
        }
    }

    /**
     * Determines the correct path for the destination file.
     * @param file The path of the original file
     * @param toDir The directory for the copied file
     * @param toFile The file name for the copied file
     * @return The path for the destination file
     */
    protected String getDestFileLocation(String file, String toDir, String toFile)
    {
        String fileName = file;
        String finalFile = null;

        if (fileName == null)
        {
            throw new RuntimeException("file cannot be null");
        } 
        else if (toFile == null && toDir != null)
        {
            // get the filename and add it in the todir directory name
            String filename = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            finalFile = getHome() + "/" + toDir + "/" + filename;
        } 
        else if (toFile != null && toDir == null)
        {
            // just use the tofile filename as the final file
            finalFile = getHome() + "/" + toFile;
        } 
        else if (toFile == null && toDir == null)
        {
            // use the tofile filename and add it into the conf directory
            String filename = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            finalFile = getHome() + "/" + filename;
        } 
        else if (toFile != null && toDir != null)
        {
            // tofile means what name to call the file in the todir directory
            finalFile = getHome() + "/" + toDir + "/" + toFile;
        }

        // replace all double slashes with a single slash
        while (finalFile.indexOf("//") >= 0)
        {
            finalFile = finalFile.replaceAll("//", "/");
        }
        
        return finalFile;
    }
    
    /**
     * Determines the correct path for the destination directory.
     * @param file The path of the original file
     * @param toDir The directory for the copied file
     * @return The path for the destination file
     */
    protected String getDestDirectoryLocation(String file, String toDir)
    {
        String fileName = file;
        String finalDir = null;

        if (fileName == null)
        {
            throw new RuntimeException("file cannot be null");
        } 
        else if (toDir != null)
        {
            finalDir = getHome() + "/" + toDir;
        } 
        else if (toDir == null)
        {
            finalDir = getHome();
        } 
        // replace all double slashes with a single slash
        while (finalDir.indexOf("//") >= 0)
        {
            finalDir = finalDir.replaceAll("//", "/");
        }
        
        return finalDir;
    }
}
