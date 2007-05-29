/*
 * ========================================================================
 *
 * Copyright 2003 The Apache Software Foundation. Code from this file
 * was originally imported from the Jakarta Cactus project.
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
package org.codehaus.cargo.module.webapp;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.codehaus.cargo.module.AbstractDocumentBuilderTest;
import org.codehaus.cargo.module.webapp.WebXml;
import org.codehaus.cargo.module.webapp.WebXmlTag;
import org.codehaus.cargo.module.webapp.WebXmlVersion;
import org.codehaus.cargo.module.webapp.elements.ContextParam;
import org.codehaus.cargo.module.webapp.elements.Filter;
import org.codehaus.cargo.module.webapp.elements.SecurityConstraint;
import org.codehaus.cargo.module.webapp.elements.Servlet;
import org.codehaus.cargo.module.webapp.elements.WebXmlElement;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * Unit tests for {@link WebXml}.
 *
 * @version $Id$
 */
public final class WebXmlTest extends AbstractDocumentBuilderTest
{
    /**
     * Tests whether the construction of a WebXml object with a
     * <code>null</code> parameter for the DOM document throws a
     * <code>NullPointerException</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testConstructionWithNullDocument() throws Exception
    {
        try
        {
            new WebXml(null,null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException npe)
        {
            // expected
        }

    }

    protected Element getFirstChild(Element e)
    {
      return (Element)e.getChildren().get(0);
    }
    
    protected Element getLastChild(Element e)
    {
      return (Element)e.getChildren().get( e.getChildren().size() - 1 );
    }
    
    /**
     * Tests whether a servlet API version 2.2 descriptor is correctly detected.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetVersion22() throws Exception
    {
        String xml = "<!DOCTYPE web-app "
            + "PUBLIC '-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN' "
            + "'http://java.sun.com/j2ee/dtds/web-app_2.2.dtd'>"
            + "<web-app></web-app>";
                
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertEquals(WebXmlVersion.V2_2, webXml.getVersion());
    }

    /**
     * Tests whether a servlet API version 2.3 descriptor is correctly detected.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetVersion23() throws Exception
    {
        String xml = "<!DOCTYPE web-app "
            + "PUBLIC '-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN' "
            + "'http://java.sun.com/dtd/web-app_2_3.dtd'>"
            + "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertEquals(WebXmlVersion.V2_3, webXml.getVersion());
    }

    /**
     * Tests whether WebXml#getVersion returns <code>null</code> when the public
     * ID of the <code>DOCTYPE</code> is not recognized.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetVersionUnknown() throws Exception
    {
        String xml = "<!DOCTYPE web-app "
            + "PUBLIC '-//Sun Microsystems, Inc.//DTD Web Application 1.9//EN' "
            + "'http://java.sun.com/dtd/web-app_1_9.dtd'>"
            + "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );        
        assertNull(webXml.getVersion());
    }

    /**
     * Tests whether WebXml#getVersion returns <code>null</code> when the
     * <code>DOCTYPE</code> is missing.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetVersionWithoutDoctype() throws Exception
    {
        String xml = "<web-app></web-app>";
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertNull(webXml.getVersion());
    }

    /**
     * Tests whether calling {@link WebXml.hasFilter} with <code>null</code> as
     * filter name parameter results in a <code>NullPointerException</code>
     * being thrown.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasFilterWithNullName() throws Exception
    {
        String xml = "<web-app></web-app>";
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        try
        {
            webXml.getTag( (String)null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException npe)
        {
            // expected
        }

    }

    /**
     * Tests whether {@link WebXml.hasFilter} returns the correct value for
     * a descriptor containing one filter definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasFilterWithOneFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "</web-app>";
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertNotNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f1"));
        assertNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f2"));
    }

    /**
     * Tests whether {@link WebXml.hasFilter} returns the correct values for
     * a descriptor containing multiple filter definitions.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasFilterWithMultipleFilters() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f2</filter-name>"
            + "    <filter-class>fclass2</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f3</filter-name>"
            + "    <filter-class>fclass3</filter-class>"
            + "  </filter>"
            + "</web-app>";
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertNotNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f1"));
        assertNotNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f2"));
        assertNotNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f3"));
        assertNull(webXml.getTagByIdentifier( WebXmlType.FILTER,"f4"));        
    }

    /**
     * Tests whether a DOM element representing a single filter definition can
     * be correctly retrieved from a descriptor containing only that filter.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterElementWithOneFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>".trim()
            + "    <filter-name>f1</filter-name>".trim()
            + "    <filter-class>fclass1</filter-class>".trim()
            + "  </filter>".trim()
            + "</web-app>";
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
       
        Element servletElement = webXml.getTagByIdentifier( WebXmlType.FILTER,"f1");
        assertNotNull(servletElement);
        assertEquals("filter", servletElement.getName() );
        assertEquals("filter-name",
            getFirstChild(servletElement).getName());
        assertEquals("f1",
            getFirstChild(servletElement).getValue());
        assertEquals("filter-class",
            getLastChild(servletElement).getName());
        assertEquals("fclass1",
            getLastChild(servletElement).getValue());
    }

    /**
     * Tests whether the filter names are retrieved in the expected order.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterNames() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f2</filter-name>"
            + "    <filter-class>fclass2</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f3</filter-name>"
            + "    <filter-class>fclass3</filter-class>"
            + "  </filter>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterNames = webXml.getRootElement().getChildren("filter").iterator();
        assertEquals("f1", ((Filter)filterNames.next()).getFilterName() );
        assertEquals("f2", ((Filter)filterNames.next()).getFilterName());
        assertEquals("f3", ((Filter)filterNames.next()).getFilterName());
        assertTrue(!filterNames.hasNext());
    }

    /**
     * Tests whether a retrieving a filter name by the name of the class
     * implementing the filter works correctly for a descriptor with a single
     * filter definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterNamesForClassWithSingleFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>f1class</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterNames = WebXmlUtils.getFilterNamesForClass(webXml, "f1class");
        assertEquals("f1", filterNames.next());
        assertTrue(!filterNames.hasNext());
    }

    /**
     * Tests whether a retrieving the filter names by the name of the class
     * implementing the filter works correctly for a descriptor with multiple
     * filter definitions.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterNamesForClassWithMultipleFilters() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>f1class</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f2</filter-name>"
            + "    <filter-class>f2class</filter-class>"
            + "  </filter>"
            + "  <filter>"
            + "    <filter-name>f3</filter-name>"
            + "    <filter-class>f1class</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterNames = WebXmlUtils.getFilterNamesForClass(webXml, "f1class");
        assertEquals("f1", filterNames.next());
        assertEquals("f3", filterNames.next());
        assertTrue(!filterNames.hasNext());
    }

    /**
     * Tests whether a filter-mapping is correctly retrieved from a descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterMappingsWithOneMapping() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter-mapping>"
            + "    <filter-name>f1</filter-name>"
            + "    <url-pattern>/f1mapping</url-pattern>"
            + "  </filter-mapping>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterMappings = WebXmlUtils.getFilterMappings(webXml, "f1");
        assertEquals("/f1mapping", filterMappings.next());
        assertTrue(!filterMappings.hasNext());
    }

    /**
     * Tests whether multiple filter-mappings are correctly retrieved from a
     * descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterMappingsWithMultipleMappings() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter-mapping>"
            + "    <filter-name>f1</filter-name>"
            + "    <url-pattern>/f1mapping1</url-pattern>"
            + "  </filter-mapping>"
            + "  <filter-mapping>"
            + "    <filter-name>f1</filter-name>"
            + "    <url-pattern>/f1mapping2</url-pattern>"
            + "  </filter-mapping>"
            + "  <filter-mapping>"
            + "    <filter-name>f1</filter-name>"
            + "    <url-pattern>/f1mapping3</url-pattern>"
            + "  </filter-mapping>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterMappings = WebXmlUtils.getFilterMappings(webXml,"f1");
        assertEquals("/f1mapping1", filterMappings.next());
        assertEquals("/f1mapping2", filterMappings.next());
        assertEquals("/f1mapping3", filterMappings.next());
        assertTrue(!filterMappings.hasNext());
    }

    /**
     * Tests whether a filter-mapping is correctly retrieved from a descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetFilterMappingsWithFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>f1class</filter-class>"
            + "  </filter>"
            + "  <filter-mapping>"
            + "    <filter-name>f1</filter-name>"
            + "    <url-pattern>/f1mapping</url-pattern>"
            + "  </filter-mapping>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator filterMappings = WebXmlUtils.getFilterMappings(webXml,"f1");
        assertEquals("/f1mapping", filterMappings.next());
        assertTrue(!filterMappings.hasNext());
    }

    /**
     * Tests whether a single context-param is correctly inserted into an empty
     * descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddContextParamToEmptyDocument() throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlElement contextParamElement =
            createContextParamElement(doc, "param", "value");
        webXml.addTag(contextParamElement);
        assertTrue( WebXmlUtils.hasContextParam(webXml, "param"));
    }

    /**
     * Tests whether a single filter is correctly inserted into an empty
     * descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddFilterToEmptyDocument() throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlElement filterElement = createFilterElement(doc, "f1", "f1class");
        webXml.addTag(filterElement);
        assertTrue(WebXmlUtils.hasFilter(webXml, "f1"));
    }

    /**
     * Tests whether a single context param is correctly inserted into a
     * descriptor that already contains an other context param definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddContextParamToDocumentWithAnotherContextParam()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <context-param>"
            + "    <param-name>param1</param-name>"
            + "    <param-value>value1</param-value>"
            + "  </context-param>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlElement contextParamElement =
            createContextParamElement(doc, "param2", "value2");
        webXml.addTag(contextParamElement);
        assertTrue(WebXmlUtils.hasContextParam(webXml,"param1"));
        assertTrue(WebXmlUtils.hasContextParam(webXml,"param2"));
    }

    /**
     * Tests whether a single filter is correctly inserted into a descriptor
     * that already contains an other filter definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddFilterToDocumentWithAnotherFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlElement filterElement = createFilterElement(doc, "f2", "f2class");
        webXml.addTag(filterElement);
        assertTrue(WebXmlUtils.hasFilter(webXml,"f1"));
        assertTrue(WebXmlUtils.hasFilter(webXml,"f2"));
    }

    /**
     * Tests whether trying to add a context param to a descriptor that already
     * contains a context param definition with the same name results in an
     * exception.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddContextParamToDocumentWithTheSameContextParam()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <context-param>"
            + "    <param-name>param</param-name>"
            + "    <param-value>value</param-value>"
            + "  </context-param>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        ContextParam contextParamElement =
            new ContextParam("param", "value");
        try
        {
            webXml.addTag(contextParamElement);
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException ise)
        {
            // expected
        }
    }

    /**
     * Tests whether trying to add a filter to a descriptor that already
     * contains a filter definition with the same name results in a exception.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddFilterToDocumentWithTheSameFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlElement filterElement = createFilterElement(doc, "f1", "f1class");
        try
        {
            webXml.addTag(filterElement);
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException ise)
        {
            // expected
        }
    }

    /**
     * Tests whether a single initialization parameter can be added to a filter
     * definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddOneFilterInitParam() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        
        WebXmlUtils.addFilterInitParam(webXml, "f1", "f1param1", "f1param1value");
        Iterator initParams = WebXmlUtils.getFilterInitParamNames(webXml,"f1");
        assertEquals("f1param1", initParams.next());
        assertTrue(!initParams.hasNext());
    }

    /**
     * Tests whether multiple initialization parameter can be added to a filter
     * definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddMultipleFilterInitParams() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>"
            + "    <filter-name>f1</filter-name>"
            + "    <filter-class>fclass1</filter-class>"
            + "  </filter>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addFilterInitParam(webXml,"f1", "f1param1", "f1param1value");
        WebXmlUtils.addFilterInitParam(webXml,"f1", "f1param2", "f1param2value");
        WebXmlUtils.addFilterInitParam(webXml,"f1", "f1param3", "f1param3value");
        Iterator initParams = WebXmlUtils.getFilterInitParamNames(webXml,"f1");
        assertEquals("f1param1", initParams.next());
        assertEquals("f1param2", initParams.next());
        assertEquals("f1param3", initParams.next());
        assertTrue(!initParams.hasNext());
    }

    /**
     * Tests whether a single filter can be added using the method that takes
     * a string for the filter name and a string for the filter class.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddFilterWithNameAndClass() throws Exception
    {
        String xml = "<web-app>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServlet(webXml,"f1", "f1class");
        assertTrue(WebXmlUtils.hasServlet(webXml,"f1"));
    }

    /**
     * Tests whether calling {@link WebXml#hasServlet} with a <code>null</code>
     * parameter as servlet name throws a <code>NullPointerException</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasServletWithNullName() throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        try
        {
            WebXmlUtils.hasServlet(webXml,null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException npe)
        {
            // expected
        }

    }

    /**
     * Tests whether {@link WebXml#hasServlet} reports the correct values for a
     * descriptor containing a single servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasServletWithOneServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
        assertTrue(!WebXmlUtils.hasServlet(webXml,"s2"));
    }

    /**
     * Tests whether {@link WebXml#hasServlet} reports the correct values for a
     * descriptor containing multiple servlet definitions.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasServletWithMultipleServlets() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s2</servlet-name>"
            + "    <servlet-class>sclass2</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s3</servlet-name>"
            + "    <servlet-class>sclass3</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
        assertTrue(WebXmlUtils.hasServlet(webXml,"s2"));
        assertTrue(WebXmlUtils.hasServlet(webXml,"s3"));
        assertTrue(!WebXmlUtils.hasServlet(webXml,"s4"));
    }

    /**
     * Tests whether a servlet element is correctly retrieved from a descriptor
     * containing only one servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletElementWithOneServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>".trim()
            + "    <servlet-name>s1</servlet-name>".trim()
            + "    <servlet-class>sclass1</servlet-class>".trim()
            + "  </servlet>".trim()
            + "</web-app>";

        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Element servletElement = webXml.getTagByIdentifier(WebXmlType.SERVLET,"s1");
        assertNotNull(servletElement);
        assertEquals("servlet", servletElement.getName());
        assertEquals("servlet-name",
            ((Element)servletElement.getChildren().get(0)).getName());
        assertEquals("s1",
            ((Element)servletElement.getChildren().get(0)).getValue());
        assertEquals("servlet-class",
            getLastChild(servletElement).getName());
        assertEquals("sclass1",
            getLastChild(servletElement).getValue());
    }

    /**
     * Tests whether the names of the servlets defined in a descriptor are
     * correctly returned in the expected order.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletNames() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s2</servlet-name>"
            + "    <servlet-class>sclass2</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s3</servlet-name>"
            + "    <servlet-class>sclass3</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletNames = WebXmlUtils.getServletNames(webXml);
        assertEquals("s1", servletNames.next());
        assertEquals("s2", servletNames.next());
        assertEquals("s3", servletNames.next());
        assertTrue(!servletNames.hasNext());
    }

    /**
     * Tests whether a retrieving a servlet name by the name of the class
     * implementing the servlet works correctly for a descriptor with a single
     * servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletNamesForClassWithSingleServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>s1class</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletNames = WebXmlUtils.getServletNamesForClass(webXml,"s1class");
        assertEquals("s1", servletNames.next());
        assertTrue(!servletNames.hasNext());
    }

    /**
     * Tests whether a retrieving the servlet names by the name of the class
     * implementing the servlet works correctly for a descriptor with multiple
     * servlet definitions.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletNamesForClassWithMultipleServlets()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s2</servlet-name>"
            + "    <servlet-class>sclass2</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s3</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletNames = WebXmlUtils.getServletNamesForClass(webXml,"sclass1");
        assertEquals("s1", servletNames.next());
        assertEquals("s3", servletNames.next());
        assertTrue(!servletNames.hasNext());
    }

    /**
     * Tests whether a retrieving a servlet name by the path of the JSP file
     * implementing the servlet works correctly for a descriptor with a single
     * servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletNamesForJspFileWithSingleServlet()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <jsp-file>/s1.jsp</jsp-file>"
            + "  </servlet>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletNames = WebXmlUtils.getServletNamesForJspFile(webXml, "/s1.jsp");
        assertEquals("s1", servletNames.next());
        assertTrue(!servletNames.hasNext());
    }

    /**
     * Tests whether a retrieving the servlet names by the path of the JSP file
     * implementing the servlet works correctly for a descriptor with multiple
     * servlet definitions.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletNamesForJspFileWithMultipleServlets()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <jsp-file>/s1.jsp</jsp-file>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s2</servlet-name>"
            + "    <servlet-class>sclass2</servlet-class>"
            + "  </servlet>"
            + "  <servlet>"
            + "    <servlet-name>s3</servlet-name>"
            + "    <jsp-file>/s3.jsp</jsp-file>"
            + "  </servlet>"
            + "</web-app>";

        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletNames = WebXmlUtils.getServletNamesForJspFile(webXml,"/s3.jsp");
        assertEquals("s3", servletNames.next());
        assertTrue(!servletNames.hasNext());
    }

    /**
     * Tests whether a single serrvlet-mapping is correctly retrieved from a
     * descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletMappingsWithOneMapping() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet-mapping>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <url-pattern>/s1mapping</url-pattern>"
            + "  </servlet-mapping>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletMappings = WebXmlUtils.getServletMappings(webXml,"s1");
        assertEquals("/s1mapping", servletMappings.next());
        assertTrue(!servletMappings.hasNext());
    }

    /**
     * Tests whether multiple servlet mappings are correctly retrieved from a
     * descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletMappingsWithMultipleMappings() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet-mapping>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <url-pattern>/s1mapping1</url-pattern>"
            + "  </servlet-mapping>"
            + "  <servlet-mapping>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <url-pattern>/s1mapping2</url-pattern>"
            + "  </servlet-mapping>"
            + "  <servlet-mapping>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <url-pattern>/s1mapping3</url-pattern>"
            + "  </servlet-mapping>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        Iterator servletMappings = WebXmlUtils.getServletMappings(webXml,"s1");
        assertEquals("/s1mapping1", servletMappings.next());
        assertEquals("/s1mapping2", servletMappings.next());
        assertEquals("/s1mapping3", servletMappings.next());
        assertTrue(!servletMappings.hasNext());
    }

    /**
     * Tests whether a single servlet can be added to an empty descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletToEmptyDocument() throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServlet(webXml,createServletElement(doc, "s1", "s1class"));
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
    }

    /**
     * Tests whether a single servlet can be added to a descriptor already
     * containing an other servlet.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletToDocumentWithAnotherServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        webXml.addTag(createServletElement(doc, "s2", "s2class"));
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
        assertTrue(WebXmlUtils.hasServlet(webXml,"s2"));
    }

    /**
     * Tests whether trying to add a servlet to a descriptor that already
     * contains a servlet with the same name results in an exception.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletToDocumentWithTheSameServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        try
        {
            webXml.addTag(createServletElement(doc, "s1", "s1class"));
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException ise)
        {
            // expected
        }
    }

    /**
     * Tests whether a single initialization parameter is correctly added to an
     * existing servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddOneServletInitParam() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServletInitParam(webXml, "s1", "s1param1", "s1param1value");
        Iterator initParams = WebXmlUtils.getServletInitParamNames(webXml, "s1");
        assertEquals("s1param1", initParams.next());
        assertTrue(!initParams.hasNext());
    }

    /**
     * Tests whether multiple initialization parameters are correctly added to
     * an existing servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddMultipleServletInitParams() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServletInitParam(webXml,"s1", "s1param1", "s1param1value");
        WebXmlUtils.addServletInitParam(webXml,"s1", "s1param2", "s1param2value");
        WebXmlUtils.addServletInitParam(webXml,"s1", "s1param3", "s1param3value");
        Iterator initParams = WebXmlUtils.getServletInitParamNames(webXml, "s1");
        assertEquals("s1param1", initParams.next());
        assertEquals("s1param2", initParams.next());
        assertEquals("s1param3", initParams.next());
        assertTrue(!initParams.hasNext());
    }

    /**
     * Tests whether a single servlet can be added using the method that takes
     * a string for the servlet name and a string for the servlet class.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletWithNameAndClass() throws Exception
    {
        String xml = "<web-app>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServlet(webXml,"s1", "s1class");
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
    }

    /**
     * Tests whether a single servlet can be added using the method that takes
     * a string for the servlet name and a string for the JSP file.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletWithNameAndJspFile() throws Exception
    {
        String xml = "<web-app>"
            + "</web-app>";

        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addJspFile(webXml, "s1", "s1.jsp");
        assertTrue(WebXmlUtils.hasServlet(webXml,"s1"));
    }

    /**
     * Tests whether a security-constraint with no roles is successfully added
     * to an empty descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddSecurityConstraint()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addSecurityConstraint(webXml, "wrn", "/url", Collections.EMPTY_LIST);
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url"));
    }

    /**
     * Tests whether a security-constraint with two roles is successfully added
     * to an empty descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddSecurityConstraintWithRoles()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        List roles = new ArrayList();
        roles.add("role1");
        roles.add("role2");
        WebXmlUtils.addSecurityConstraint(webXml,"wrn", "/url", roles);
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url"));
        SecurityConstraint securityConstraintElement =
          WebXmlUtils.getSecurityConstraint(webXml, "/url");
        assertNotNull(securityConstraintElement);
        Element authConstraintElement = (Element)
            securityConstraintElement.getChild(
                "auth-constraint");
        assertNotNull(authConstraintElement);
        List roleNameElements =
            authConstraintElement.getChildren("role-name");
        assertEquals(2, roleNameElements.size());
        assertEquals("role1",
            ( ((Element)roleNameElements.get(0)).getText() ) );
        assertEquals("role2",
            ( ((Element)roleNameElements.get(1)).getText() ) );
    }

    /**
     * Tests whether checking an empty descriptor for a login configuration
     * results in <code>false</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasLoginConfigEmpty()
        throws Exception
    {
        String xml = "<web-app></web-app>";

        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(!WebXmlUtils.hasLoginConfig(webXml));
    }

    /**
     * Tests whether checking a descriptor with a login configuration for a
     * login configuration results in <code>true</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasLoginConfig()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <login-config>"
            + "    <auth-method>BASIC</auth-method>"
            + "  </login-config>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasLoginConfig(webXml));
    }

    /**
     * Tests retrieving the authentication method from a descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetLoginConfigAuthMethod()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <login-config>"
            + "    <auth-method>BASIC</auth-method>"
            + "  </login-config>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertEquals("BASIC", WebXmlUtils.getLoginConfigAuthMethod(webXml));
    }

    /**
     * Tests retrieving the authentication method from a descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testSetLoginConfigAdding()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.setLoginConfig(webXml, "BASIC", "Test Realm");
        assertTrue(WebXmlUtils.hasLoginConfig(webXml));
        assertEquals("BASIC", WebXmlUtils.getLoginConfigAuthMethod(webXml));
    }

    /**
     * Tests retrieving the authentication method from a descriptor.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testSetLoginConfigReplacing()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <login-config>"
            + "    <auth-method>DIGEST</auth-method>"
            + "  </login-config>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.setLoginConfig(webXml, "BASIC", "Test Realm");
        assertTrue(WebXmlUtils.hasLoginConfig(webXml));
        assertEquals("BASIC", WebXmlUtils.getLoginConfigAuthMethod(webXml));
    }

    /**
     * Tests whether checking an empty descriptor for some security constraint
     * results in <code>false</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasSecurityConstraintEmpty()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(!WebXmlUtils.hasSecurityConstraint(webXml,"/TestUrl"));
    }

    /**
     * Tests whether a single security-constraint element in the descriptor is
     * correctly retrieved.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetSingleSecurityConstraint()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <security-constraint>"
            + "    <web-resource-collection>"
            + "      <web-resource-name>wr1</web-resource-name>"
            + "      <url-pattern>/url1</url-pattern>"
            + "    </web-resource-collection>"
            + "  </security-constraint>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url1"));
        Element securityConstraintElement =
            WebXmlUtils.getSecurityConstraint(webXml,"/url1");
        assertNotNull(securityConstraintElement);
    }

    /**
     * Tests whether multiple security-constraint elements are returned in
     * the expected order.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetMutlipleSecurityConstraints()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <security-constraint>"
            + "    <web-resource-collection>"
            + "      <web-resource-name>wr1</web-resource-name>"
            + "      <url-pattern>/url1</url-pattern>"
            + "    </web-resource-collection>"
            + "  </security-constraint>"
            + "  <security-constraint>"
            + "    <web-resource-collection>"
            + "      <web-resource-name>wr2</web-resource-name>"
            + "      <url-pattern>/url2</url-pattern>"
            + "    </web-resource-collection>"
            + "  </security-constraint>"
            + "  <security-constraint>"
            + "    <web-resource-collection>"
            + "      <web-resource-name>wr3</web-resource-name>"
            + "      <url-pattern>/url3</url-pattern>"
            + "    </web-resource-collection>"
            + "  </security-constraint>"
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url1"));
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url2"));
        assertTrue(WebXmlUtils.hasSecurityConstraint(webXml,"/url3"));
        Iterator securityConstraints =
            webXml.getTags(WebXmlType.SECURITY_CONSTRAINT).iterator();
        assertNotNull(securityConstraints.next());
        assertNotNull(securityConstraints.next());
        assertNotNull(securityConstraints.next());
        assertTrue(!securityConstraints.hasNext());
    }

    /**
     * Tests whether retrieving the login-config from an empty descriptor
     * returns <code>null</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetLoginConfigEmpty()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(!webXml.getTags(WebXmlType.LOGIN_CONFIG).iterator().hasNext());
    }

    /**
     * Tests whether the login-config element can be correctly retrieved.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetLoginConfig()
        throws Exception
    {
        String xml = "<web-app><login-config/></web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(webXml.getTags(WebXmlType.LOGIN_CONFIG).iterator().hasNext());
    }

    /**
     * Tests whether checking an empty descriptor for some security roles
     * results in <code>false</code>.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testHasSecurityRoleEmpty()
        throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(!WebXmlUtils.hasSecurityRole(webXml,"someRole"));
        assertTrue(!WebXmlUtils.getSecurityRoleNames(webXml).hasNext());
    }

    /**
     * Tests whether a single security-role element is correctly retrieved.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetSingleSecurityRole()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <security-role>".trim()
            + "    <role-name>r1</role-name>".trim()
            + "  </security-role>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasSecurityRole(webXml,"r1"));
        Element securityRoleElement = WebXmlUtils.getSecurityRole(webXml, "r1");
        assertNotNull(securityRoleElement);
        assertEquals("security-role", securityRoleElement.getName());
        assertEquals("role-name",
            ((Element)securityRoleElement.getChildren().get(0)).getName());
        assertEquals("r1",
            (((Element)securityRoleElement.getChildren().get(0)).getText() ) );
        Iterator securityRoleNames = WebXmlUtils.getSecurityRoleNames(webXml);
        assertTrue(securityRoleNames.hasNext());
        assertEquals("r1", securityRoleNames.next());
        assertTrue(!securityRoleNames.hasNext());
    }

    /**
     * Tests whether multiple security-role elements are correctly retrieved
     * in the expected order.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetMutlipleSecurityRoles()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <security-role>".trim()
            + "    <role-name>r1</role-name>".trim()
            + "  </security-role>".trim()
            + "  <security-role>".trim()
            + "    <role-name>r2</role-name>".trim()
            + "  </security-role>".trim()
            + "  <security-role>".trim()
            + "    <role-name>r3</role-name>".trim()
            + "  </security-role>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        assertTrue(WebXmlUtils.hasSecurityRole(webXml,"r1"));
        Element securityRoleElement1 = WebXmlUtils.getSecurityRole(webXml, "r1");
        assertNotNull(securityRoleElement1);
        assertEquals("security-role", securityRoleElement1.getName());
        assertEquals("role-name",
            ((Element)securityRoleElement1.getChildren().get(0)).getName());
        assertEquals("r1",
            ((Element)((Element)securityRoleElement1.getChildren().get(0))).
                getText());
        assertTrue(WebXmlUtils.hasSecurityRole(webXml,"r2"));
        Element securityRoleElement2 = WebXmlUtils.getSecurityRole(webXml, "r2");
        assertNotNull(securityRoleElement2);
        assertEquals("security-role", securityRoleElement2.getName());
        assertEquals("role-name",
            ((Element)securityRoleElement2.getChildren().get(0)).getName());
        assertEquals("r2",
            ((Element)((Element)securityRoleElement2.getChildren().get(0))).
                getText());
        assertTrue(WebXmlUtils.hasSecurityRole(webXml,"r3"));
        Element securityRoleElement3 = WebXmlUtils.getSecurityRole(webXml, "r3");
        assertNotNull(securityRoleElement3);
        assertEquals("security-role", securityRoleElement3.getName());
        assertEquals("role-name",
            ((Element)securityRoleElement3.getChildren().get(0)).getName());
        assertEquals("r3",
            ((Element)((Element)securityRoleElement3.getChildren().get(0))).
                getText());
        Iterator securityRoleNames = WebXmlUtils.getSecurityRoleNames(webXml);
        assertTrue(securityRoleNames.hasNext());
        assertEquals("r1", securityRoleNames.next());
        assertTrue(securityRoleNames.hasNext());
        assertEquals("r2", securityRoleNames.next());
        assertTrue(securityRoleNames.hasNext());
        assertEquals("r3", securityRoleNames.next());
        assertTrue(!securityRoleNames.hasNext());
    }

    /**
     * Tests whether a filter is inserted before a servlet element.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testElementOrderFilterBeforeServlet() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>".trim()
            + "    <servlet-name>s1</servlet-name>".trim()
            + "    <servlet-class>s1class</servlet-class>".trim()
            + "  </servlet>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        webXml.addTag(createFilterElement(doc, "f1", "f1class"));
        List order = webXml.getRootElement().getChildren();
        assertEquals("filter", ((Element)order.get(0)).getName());
        assertEquals("servlet", ((Element)order.get(1)).getName());               
    }

    /**
     * Tests whether a filter is inserted before the comment node preceding a
     * servlet definition.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testElementOrderFilterBeforeServletWithComment()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <!-- My servlets -->".trim()
            + "  <servlet>".trim()
            + "    <servlet-name>s1</servlet-name>".trim()
            + "    <servlet-class>s1class</servlet-class>".trim()
            + "  </servlet>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        webXml.addTag(createFilterElement(doc, "f1", "f1class"));
        
        List order = webXml.getRootElement().getContent();
        assertEquals("filter", ((Element)order.get(0)).getName());
        assertEquals(Comment.class, order.get(1).getClass());
        assertEquals("servlet", ((Element)order.get(2)).getName());             
    }

    /**
     * Tests whether a servlet is inserted after a filter.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testElementOrderServletAfterFilter() throws Exception
    {
        String xml = "<web-app>"
            + "  <filter>".trim()
            + "    <filter-name>f1</filter-name>".trim()
            + "    <filter-class>f1class</filter-class>".trim()
            + "  </filter>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        webXml.addTag(createServletElement(doc, "s1", "s1class"));
        List order = webXml.getRootElement().getContent();
        assertEquals("filter",((Element)order.get(0)).getName());
        assertEquals("servlet", ((Element)order.get(1)).getName());
    }

    /**
     * Tests whether a servlet is inserted after a filter that is preceded by
     * a comment node.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testElementOrderServletAfterFilterWithComment()
        throws Exception
    {
        String xml = "<web-app>"
            + "  <!-- My filters -->".trim()
            + "  <filter>".trim()
            + "    <filter-name>f1</filter-name>".trim()
            + "    <filter-class>f1class</filter-class>".trim()
            + "  </filter>".trim()
            + "</web-app>";
        Document doc = this.builder.build(new ByteArrayInputStream(xml.getBytes()));
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        webXml.addTag(createServletElement(doc, "s1", "s1class"));
        

        
        List order = webXml.getRootElement().getContent();
        assertEquals(Comment.class, order.get(0).getClass());
        assertEquals("filter", ((Element)order.get(1)).getName());        
        assertEquals("servlet", ((Element)order.get(2)).getName());             
        
//        List order = doc.getDocumentElement().getChildNodes();
//        assertEquals("#comment", order.item(0).getName());
//        assertEquals("filter", order.item(1).getName());
//        assertEquals("servlet", order.item(2).getName());
    }


    /**
     * Tests that the a servlets run-as role-name can be extracted.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testGetServletRunAsRole() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "    <run-as>"
            + "      <role-name>r1</role-name>"
            + "    </run-as>"
            + "  </servlet>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        String roleName = WebXmlUtils.getServletRunAsRoleName(webXml, "s1");
        assertEquals("r1", roleName);
    }

    /**
     * Tests that a run-as role-name can be added to a servlet.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddServletRunAsRole() throws Exception
    {
        String xml = "<web-app>"
            + "  <servlet>"
            + "    <servlet-name>s1</servlet-name>"
            + "    <servlet-class>sclass1</servlet-class>"
            + "  </servlet>"
            + "</web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        WebXmlUtils.addServletRunAsRoleName(webXml, "s1", "r1");
        String roleName = WebXmlUtils.getServletRunAsRoleName(webXml, "s1");
        assertEquals("r1", roleName);
    }

    /**
     * Tests that a ejb-ref can be added.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddEjbRef() throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        EjbRef ejbRef = new EjbRef("MyEjb", "com.wombat.MyEjb", "com.wombat.MyEjbHome");
        ejbRef.setJndiName("foo");
        WebXmlUtils.addEjbRef(webXml,ejbRef);
        Document doc = webXml.getDocument();
        List nl = doc.getRootElement().getChildren(WebXmlType.EJB_LOCAL_REF);
        Element n = (Element) nl.get(0);
        assertEquals("ejb-local-ref", n.getName());
        Element m = (Element) n.getChildren(WebXmlType.EJB_REF_NAME).get(0);
        assertEquals("ejb-ref-name", m.getName());
        assertEquals("MyEjb", m.getText());
        m = (Element) n.getChildren(WebXmlType.EJB_REF_TYPE).get(0);
        assertEquals("ejb-ref-type", m.getName());
        assertEquals("Session", m.getText());
        m = (Element) n.getChildren(WebXmlType.LOCAL).get(0);
        assertEquals("local", m.getName());
        assertEquals("com.wombat.MyEjb", m.getText());
        m = (Element) n.getChildren(WebXmlType.LOCAL_HOME).get(0);
        assertEquals("local-home", m.getName());
        assertEquals("com.wombat.MyEjbHome", m.getText());
    }

    /**
     * Tests that a ejb-ref can be added.
     *
     * @throws Exception If an unexpected error occurs
     */
    public void testAddEjbRefByLink() throws Exception
    {
        String xml = "<web-app></web-app>";
        
        WebXml webXml = WebXmlIo.parseWebXml(new ByteArrayInputStream(xml.getBytes()), getEntityResolver() );
        EjbRef ejbRef = new EjbRef("MyEjb", "com.wombat.MyEjb", "com.wombat.MyEjbHome");
        ejbRef.setEjbName("MyEjb");
        WebXmlUtils.addEjbRef(webXml,ejbRef);
        Document doc = webXml.getDocument();
        List nl = doc.getRootElement().getChildren(WebXmlType.EJB_LOCAL_REF);
        Element n = (Element)nl.get(0);
        assertEquals("ejb-local-ref", n.getName());
        Element m = (Element)n.getChildren(WebXmlType.EJB_REF_NAME).get(0);
        assertEquals("ejb-ref-name", m.getName());
        assertEquals("MyEjb", m.getText());
        m = (Element)n.getChildren(WebXmlType.EJB_REF_TYPE).get(0);
        assertEquals("ejb-ref-type", m.getName());
        assertEquals("Session", m.getText());
        m = (Element)n.getChildren(WebXmlType.LOCAL).get(0);
        assertEquals("local", m.getName());
        assertEquals("com.wombat.MyEjb", m.getText());
        m = (Element)n.getChildren(WebXmlType.LOCAL_HOME).get(0);
        assertEquals("local-home", m.getName());
        assertEquals("com.wombat.MyEjbHome", m.getText());
        m = (Element)n.getChildren(WebXmlType.EJB_LINK).get(0);
        assertEquals("ejb-link", m.getName());
        assertEquals("MyEjb", m.getText());
    }

    // Private Methods ---------------------------------------------------------

    /**
     * Create a <code>context-param</code> element containing the specified
     * text in the child elements.
     *
     * @param theDocument The DOM document
     * @param theParamName The parameter name
     * @param theParamValue The parameter value
     * @return The created element
     */
    public WebXmlElement createContextParamElement(Document theDocument,
        String theParamName, String theParamValue)
    {
        ContextParam contextParamElement = (ContextParam)WebXmlType.getInstance().getTagByName(WebXmlType.CONTEXT_PARAM).create();
        contextParamElement.setParamName(theParamName);
        contextParamElement.setParamValue(theParamValue);

        return contextParamElement;
    }

    /**
     * Create a <code>filter</code> element containing the specified text in
     * the child elements.
     *
     * @param theDocument The DOM document
     * @param theFilterName The name of the filter
     * @param theFilterClass The name of the filter implementation class
     * @return The created element
     */
    public WebXmlElement createFilterElement(Document theDocument,
            String theFilterName, String theFilterClass)
    {
        Filter filterElement = (Filter)WebXmlType.getInstance().getTagByName(WebXmlType.FILTER).create();
        filterElement.setFilterName( theFilterName );
        filterElement.setFilterClass( theFilterClass );        
        return filterElement;
    }

    /**
     * Create a <code>servlet</code> element containing the specified text in
     * the child elements.
     *
     * @param theDocument The DOM document
     * @param theServletName The name of the servlet
     * @param theServletClass The name of the servlet implementation class
     * @return The created element
     */
    public Servlet createServletElement(Document theDocument,
            String theServletName, String theServletClass)
    {
        Servlet servletElement = (Servlet)WebXmlType.getInstance().getTagByName(WebXmlType.SERVLET).create();
        servletElement.setServletName(theServletName);
        servletElement.setServletClass(theServletClass);
        
        return servletElement;
    }

}
