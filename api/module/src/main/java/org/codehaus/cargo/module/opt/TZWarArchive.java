// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TZWarArchive.java

package org.codehaus.cargo.module.opt;

import de.schlichtherle.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.cargo.module.AbstractDescriptorIo;
import org.codehaus.cargo.module.DefaultJarArchive;
import org.codehaus.cargo.module.Descriptor;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.webapp.WarArchive;
import org.codehaus.cargo.module.webapp.WebXml;
import org.codehaus.cargo.module.webapp.WebXmlIo;
import org.codehaus.cargo.module.webapp.jboss.JBossWebXmlIo;
import org.codehaus.cargo.module.webapp.orion.OrionWebXmlIo;
import org.codehaus.cargo.module.webapp.resin.ResinWebXmlIo;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXmlIo;
import org.codehaus.cargo.module.webapp.websphere.IbmWebBndXmiIo;
import org.codehaus.cargo.util.CargoException;
import org.jdom.JDOMException;

// Referenced classes of package org.codehaus.cargo.module.opt:
//            TZJarArchive

public class TZWarArchive extends TZJarArchive
        implements WarArchive {

    public TZWarArchive(java.io.File file) {
        super(file);
    }

    public final WebXml getWebXml()
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
            throw new CargoException("Error parsing the web.xml file in " + getFilename(), ex);
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

    public final void store(java.io.File warFile)
            throws IOException, JDOMException {
        ((File) getFile()).copyAllTo(warFile);
        List descriptorNames = new ArrayList();
        descriptorNames.add("WEB-INF/" + getWebXml().getFileName());
        for (Iterator vendorDescriptors = getWebXml().getVendorDescriptors(); vendorDescriptors.hasNext(); descriptorNames.add("WEB-INF/" + ((Descriptor) vendorDescriptors.next()).getFileName()))
            ;
        File webXmlEntry = new File(warFile, "WEB-INF/" + getWebXml().getFileName());
        AbstractDescriptorIo.writeDescriptor(getWebXml(), webXmlEntry, null, true);
        Descriptor descriptor;
        File out;
        for (Iterator descriptors = getWebXml().getVendorDescriptors(); descriptors.hasNext(); AbstractDescriptorIo.writeDescriptor(descriptor, out, null, true)) {
            descriptor = (Descriptor) descriptors.next();
            out = new File(warFile, "WEB-INF/" + descriptor.getFileName());
        }

    }

    public final boolean containsClass(String className)
            throws IOException {
        boolean containsClass = false;
        String resourceName = "WEB-INF/classes/" + className.replace('.', '/') + ".class";
        if (getResource(resourceName) != null)
            containsClass = true;
        List jars = getResources("WEB-INF/lib/");
        Iterator i = jars.iterator();
        do {
            if (!i.hasNext())
                break;
            JarArchive jar = new DefaultJarArchive(getResource((String) i.next()));
            if (jar.containsClass(className))
                containsClass = true;
        } while (true);
        return containsClass;
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
