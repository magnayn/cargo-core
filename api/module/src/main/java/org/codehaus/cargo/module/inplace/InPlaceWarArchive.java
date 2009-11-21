// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InPlaceWarArchive.java

package org.codehaus.cargo.module.inplace;

import java.io.*;

import org.codehaus.cargo.module.webapp.*;
import org.codehaus.cargo.module.webapp.jboss.JBossWebXmlIo;
import org.codehaus.cargo.module.webapp.orion.OrionWebXmlIo;
import org.codehaus.cargo.module.webapp.resin.ResinWebXmlIo;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXmlIo;
import org.codehaus.cargo.module.webapp.websphere.IbmWebBndXmiIo;
import org.codehaus.cargo.util.CargoException;
import org.jdom.JDOMException;

// Referenced classes of package org.codehaus.cargo.module.inplace:
//            InPlaceJarArchive

public class InPlaceWarArchive extends InPlaceJarArchive
        implements WarArchive {

    public InPlaceWarArchive(File rootDir) {
        super(rootDir);
    }

    public WebXml getWebXml()
            throws IOException, JDOMException {
        InputStream in;
        if (webXml != null)
            break MISSING_BLOCK_LABEL_117;
        in = null;
        try {
            in = getResource("WEB-INF/web.xml");
            if (in != null)
                webXml = WebXmlIo.parseWebXml(in, null);
            else
                webXml = new WebXml();
        }
        catch (Exception ex) {
            throw new CargoException("Error parsing the web.xml file in " + getFile(), ex);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_97;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
        addWeblogicDescriptor();
        addOracleDescriptor();
        addWebsphereDescriptor();
        addResinDescriptor();
        addJBossDescriptor();
        return webXml;
    }

    public void store(File file1)
            throws IOException, JDOMException {
    }

    private void addWeblogicDescriptor()
            throws IOException, JDOMException {
        InputStream in = null;
        in = getResource("WEB-INF/weblogic.xml");
        if (in != null) {
            org.codehaus.cargo.module.webapp.weblogic.WeblogicXml descr = WeblogicXmlIo.parseWeblogicXml(in);
            if (descr != null)
                webXml.addVendorDescriptor(descr);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
    }

    private void addResinDescriptor()
            throws IOException, JDOMException {
        InputStream in = null;
        in = getResource("WEB-INF/resin-web.xml");
        if (in != null) {
            org.codehaus.cargo.module.webapp.resin.ResinWebXml descr = ResinWebXmlIo.parseResinXml(in);
            if (descr != null)
                webXml.addVendorDescriptor(descr);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
    }

    private void addOracleDescriptor()
            throws IOException, JDOMException {
        InputStream in = null;
        in = getResource("WEB-INF/orion-web.xml");
        if (in != null) {
            org.codehaus.cargo.module.webapp.orion.OrionWebXml descr = OrionWebXmlIo.parseOrionXml(in);
            if (descr != null)
                webXml.addVendorDescriptor(descr);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
    }

    private void addWebsphereDescriptor()
            throws IOException, JDOMException {
        InputStream in = null;
        in = getResource("WEB-INF/ibm-web-bnd.xmi");
        if (in != null) {
            org.codehaus.cargo.module.webapp.websphere.IbmWebBndXmi descr = IbmWebBndXmiIo.parseIbmWebBndXmi(in);
            if (descr != null)
                webXml.addVendorDescriptor(descr);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
    }

    private void addJBossDescriptor()
            throws IOException, JDOMException {
        InputStream in = null;
        in = getResource("WEB-INF/jboss-web.xml");
        if (in != null) {
            org.codehaus.cargo.module.webapp.jboss.JBossWebXml descr = JBossWebXmlIo.parseJBossWebXml(in);
            if (descr != null)
                webXml.addVendorDescriptor(descr);
        }
        if (in != null)
            in.close();
        break MISSING_BLOCK_LABEL_52;
        Exception exception;
        exception;
        if (in != null)
            in.close();
        throw exception;
    }

    private WebXml webXml;
}
