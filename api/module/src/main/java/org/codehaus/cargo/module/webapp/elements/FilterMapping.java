/* 
 * ========================================================================
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
package org.codehaus.cargo.module.webapp.elements;

import org.codehaus.cargo.module.webapp.WebXmlTag;
import org.codehaus.cargo.module.webapp.WebXmlType;
import org.jdom.Element;

/**
 * @version $Id: $
 */
public class FilterMapping extends WebXmlElement
{
    /**
     * Constructor.
     * @param tag Web Xml Tag definition
     */
    public FilterMapping(WebXmlTag tag)
    {
        super(tag);
    }

    /**
     * Get the URL Pattern.
     * @return URL Pattern
     */
    public String getUrlPattern()
    {
        Element e = getChild(WebXmlType.URL_PATTERN);
        if (e == null)
        {
            return null;
        }
        return e.getText();
    }

    /**
     * Set the URL Pattern.
     * @param urlPattern The URL Pattern
     */
    public void setUrlPattern(String urlPattern)
    {
        Element e = getChild(WebXmlType.URL_PATTERN);
        e.setText(urlPattern);
    }

    /**
     * Get the filter name.
     * @return The filter name
     */
    public String getFilterName()
    {
        Element e = getChild(WebXmlType.FILTER_NAME);
        return e.getText();
    }

    /**
     * Set the filter name.
     * @param filterName The filter name
     */
    public void setFilterName(String filterName)
    {
        Element e = getChild(WebXmlType.FILTER_NAME);
        e.setText(filterName);
    }
}