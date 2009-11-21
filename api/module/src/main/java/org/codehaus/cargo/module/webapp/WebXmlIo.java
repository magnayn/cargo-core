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

import org.codehaus.cargo.module.AbstractDescriptor;
import org.codehaus.cargo.module.AbstractDescriptorIo;
import org.codehaus.cargo.module.DescriptorType;
import org.codehaus.cargo.module.webapp.jboss.JBossWebXml;
import org.codehaus.cargo.module.webapp.jboss.JBossWebXmlIo;
import org.codehaus.cargo.module.webapp.orion.OrionWebXml;
import org.codehaus.cargo.module.webapp.orion.OrionWebXmlIo;
import org.codehaus.cargo.module.webapp.resin.ResinWebXml;
import org.codehaus.cargo.module.webapp.resin.ResinWebXmlIo;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXml;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXmlIo;
import org.codehaus.cargo.module.webapp.websphere.IbmWebBndXmi;
import org.codehaus.cargo.module.webapp.websphere.IbmWebBndXmiIo;
import org.codehaus.cargo.util.CargoException;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
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
        Element root = new Element("web-app");

        Document document = null;

        if (theVersion.equals(WebXmlVersion.V2_2))
        {
            document = new WebXml22Type().document(root);
            document.setDocType(new DocType("web-app",
                    "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN",
                    "http://java.sun.com/j2ee/dtds/web-app_2_2.dtd"));
        } 
        else if (theVersion.equals(WebXmlVersion.V2_3))
        {
            document = new WebXml23Type().document(root);
            document.setDocType(new DocType("web-app",
                    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
                    "http://java.sun.com/dtd/web-app_2_3.dtd"));
        } 
        else
        {
            document = new WebXml24Type().document(root);
            document.setDocType(new DocType("web-app", "http://java.sun.com/xml/ns/j2ee"));
        }
        return (WebXml) document;
    }

    public static WebXml getWebXml(WarArchive warArchive) throws IOException, JDOMException
    {
        WebXml webXml = null;
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/web.xml");
            if (in != null)
            {
                webXml = WebXmlIo.parseWebXml(in, null);
            }
            else
            {
                // need to create something, as otherwise vendor descriptors
                // will fail
                webXml = new WebXml();
            }
        }
        catch (Exception ex)
        {
            throw new CargoException("Error parsing the web.xml file ", ex);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        VendorWebAppDescriptor descriptor;

        descriptor = getWeblogicDescriptor(warArchive);
        if (descriptor != null)
        {
            webXml.addVendorDescriptor(descriptor);
        }

        descriptor = getOracleDescriptor(warArchive);
        if (descriptor != null)
        {
            webXml.addVendorDescriptor(descriptor);
        }

        descriptor = getWebsphereDescriptor(warArchive);
        if (descriptor != null)
        {
            webXml.addVendorDescriptor(descriptor);
        }

        descriptor = getResinDescriptor(warArchive);
        if (descriptor != null)
        {
            webXml.addVendorDescriptor(descriptor);
        }

        descriptor = getJBossDescriptor(warArchive);
        if (descriptor != null)
        {
            webXml.addVendorDescriptor(descriptor);
        }

        return webXml;

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
    
    /**
     * Associates the webXml with a weblogic.xml if one is present in the war.
     * 
     * @throws IOException If there was a problem reading the  deployment descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not be parsed
     * @return Descriptor
     * @param warArchive archive
     */
    private static WeblogicXml getWeblogicDescriptor(WarArchive warArchive)
        throws IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/weblogic.xml");
            if (in != null)
            {
                WeblogicXml descr = WeblogicXmlIo.parseWeblogicXml(in);
                if (descr != null)
                {
                    return descr;
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }

    /**
     * Associates the webXml with a resin-web if one is present in the war.
     *
     * @throws IOException If there was a problem reading the  deployment descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not be parsed
     * @return Descriptor
     */
    private static ResinWebXml getResinDescriptor(WarArchive warArchive)
        throws IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/resin-web.xml");
            if (in != null)
            {
                ResinWebXml descr = ResinWebXmlIo.parseResinXml(in);
                if (descr != null)
                {
                    return descr;
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }

    /**
     * Associates the webXml with a orion-web.xml if one is present in the war.
     *
     * @throws IOException If there was a problem reading the  deployment descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not be parsed
     */
    private static OrionWebXml getOracleDescriptor(WarArchive warArchive)
        throws IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/orion-web.xml");
            if (in != null)
            {
                OrionWebXml descr = OrionWebXmlIo.parseOrionXml(in);
                if (descr != null)
                {
                    return descr;
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }

    /**
     * Associates the webXml with a ibm-web-bnd.xmi, if one is present in the war.
     *
     * @throws IOException If there was a problem reading the  deployment descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not be parsed
     * @return Descriptor
     */
    private static IbmWebBndXmi getWebsphereDescriptor(WarArchive warArchive)
        throws IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/ibm-web-bnd.xmi");
            if (in != null)
            {
                IbmWebBndXmi descr = IbmWebBndXmiIo.parseIbmWebBndXmi(in);
                if (descr != null)
                {
                    return descr;
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }

    /**
     * Associates the webXml with a jboss-web.xml, if one is present in the war.
     *
     * @throws IOException If there was a problem reading the  deployment descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not be parsed
     * @return Descriptor
     */
    private static JBossWebXml getJBossDescriptor(WarArchive warArchive)
        throws IOException, JDOMException
    {
        InputStream in = null;
        try
        {
            in = warArchive.getResource("WEB-INF/jboss-web.xml");
            if (in != null)
            {
                JBossWebXml descr = JBossWebXmlIo.parseJBossWebXml(in);
                if (descr != null)
                {
                    return descr;
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }
}
