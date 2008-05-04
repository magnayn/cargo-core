/* 
 * ========================================================================
 * 
 * Copyright 2003 The Apache Software Foundation. Code from this file 
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
package org.codehaus.cargo.module.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.cargo.module.AbstractDescriptorIo;
import org.codehaus.cargo.module.DescriptorType;
import org.jdom.JDOMException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Provides convenience methods for reading and writing web deployment descriptors.
 *
 * @version $Id$
 */
public final class WebXmlIo extends AbstractDescriptorIo
{
    
    /**
     * Constructor.
     * @param type descriptor type 
     */
    public WebXmlIo(DescriptorType type)
    {
        super(type);
    }

    /**
     * Implementation of the SAX EntityResolver interface that looks up the web-app DTDs from the
     * JAR.
     */
    private static class WebXmlEntityResolver implements EntityResolver
    {
        /**
         * {@inheritDoc}
         * @see org.xml.sax.EntityResolver#resolveEntity
         */
        public InputSource resolveEntity(String thePublicId, String theSystemId)
            throws SAXException, IOException
        {
            WebXmlVersion version = WebXmlVersion.valueOf(thePublicId);
            if (version != null)
            {
                String fileName = version.getSystemId().substring(
                    version.getSystemId().lastIndexOf('/'));
                InputStream in = this.getClass().getResourceAsStream(
                    "/org/codehaus/cargo/module/internal/resource" + fileName);
                if (in != null)
                {
                    return new InputSource(in);
                }
            }
            return null;
        }

    }

    /**
     * @return the configured entity resolver
     */
    protected EntityResolver getEntityResolver()
    {
        return new WebXmlEntityResolver();
    }
    
    /**
     * Creates a new empty deployment descriptor.
     *
     * @param theVersion The version of the descriptor to create
     *
     * @return The new descriptor
     */
    public static WebXml newWebXml(WebXmlVersion theVersion)         
    {
        return (WebXml) WebXml23Type.getInstance().document(null);        
    }

    /**
     * Parses a deployment descriptor stored in a regular file.
     *
     * @param theFile The file to parse
     * @param theEntityResolver A SAX entity resolver, or <code>null</code> to use the default
     *
     * @return The parsed descriptor
     *
     * @throws JDOMException If the file could not be parsed     
     * @throws IOException If an I/O error occurs  
     */
    public static WebXml parseWebXmlFromFile(File theFile,
        EntityResolver theEntityResolver)
        throws  IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream(theFile);
            return parseWebXml(in, theEntityResolver);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ioe)
                {
                    // we'll pass on the original IO error, so ignore this one
                }
            }
        }
    }

    /**
     * Parses a deployment descriptor provided as input stream.
     *
     * @param theInput The input stream
     * @param theEntityResolver A SAX entity resolver, or <code>null</code> to use the default
     *
     * @return The parsed descriptor
     * @throws IOException If an I/O error occurs
     * @throws JDOMException  If the input could not be parsed

     */
    public static WebXml parseWebXml(InputStream theInput,
        EntityResolver theEntityResolver)
        throws IOException, JDOMException
    {
        // When we are passed an InputStream, we don't know if this is a 2.2, 2.3 or 2.4 stream. We
        // need to create using the correct type, so we need to 'pre-read' te stream to work out
        // which one it is.
      
                   
        WebXmlTypeAwareParser handler = new WebXmlTypeAwareParser(theInput, theEntityResolver);
        return handler.parse();
        
        
    }
    
    
}
