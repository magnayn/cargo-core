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
package org.codehaus.cargo.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Encapsulates the DOM representation of a deployment descriptor to provide convenience methods for
 * easy access and manipulation.
 * 
 * @version $Id$
 */
public abstract class AbstractDescriptor extends Document implements Descriptor
{
    /**
     * Grammar of the descriptor.
     */
    private DescriptorType descriptorType;
    
    /**
     * Constructor.
     * 
     * @param rootElement The root element of the document
     * @param descriptorType The type of the descriptor
     */
    public AbstractDescriptor(Element rootElement, DescriptorType descriptorType)
    {
        super(rootElement);
        if (descriptorType == null)
        {
            throw new NullPointerException();
        }
        this.descriptorType = descriptorType;
    }

    /**
     * Return the representation as a document.
     * 
     * @return JDOM Document
     */
    public Document getDocument()
    {
        return this;
    }    
            
    /**
     * Get tags of a particular type.
     * 
     * @param tag type of elements to find
     * @return list of tags
     */
    public List getTags(DescriptorTag tag)
    {
        return getRootElement().getChildren(tag.getTagName(), tag.getTagNamespace());
    }

    /**
     * Get tags of a particular type.
     * 
     * @param tagName type of elements to find
     * @return list of tags
     */
    public List getTags(String tagName)
    {
        return getTags(getDescriptorType().getTagByName(tagName));
    }
    
    /**
     * Returns an iterator over the elements that match the specified tag.
     * 
     * @param tag The descriptor tag of which the elements should be returned
     * @return An iterator over the elements matching the tag, in the order they occur in the
     *         descriptor
     */
    public Iterator getElements(DescriptorTag tag)
    {
        if (tag == null)
        {
            throw new IllegalArgumentException("tag must not be null");
        }

        return getChildElements(getRootElement(), tag, new ArrayList()).iterator();
    }

    /**
     * Returns an iterator over the elements that match the specified tag.
     * 
     * @param tagName The name of a descriptor tag of which the elements should be returned
     * @return An iterator over the elements matching the tag, in the order they occur in the
     *         descriptor
     */
    public Iterator getElements(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("tagName must not be null");
        }
        
        return getElements(getDescriptorType().getTagByName(tagName));
    }
    
    /**
     * Recursively get elements matching a particular tag.
     * 
     * @param element the parent element
     * @param tag the tag required
     * @param items collection of items found so far
     * @return List of elements
     */
    private List getChildElements(Element element, DescriptorTag tag, List items)
    {
        if (element == null || tag == null || items == null)
        {
            throw new IllegalArgumentException("Cannot pass null values to getChildElements");
        }
        
        items.addAll(element.getChildren(tag.getTagName(), tag.getTagNamespace()));
        for (Iterator i = element.getChildren().iterator(); i.hasNext();)
        {
            Element e2 = (Element) i.next();
            getChildElements(e2, tag, items);
        }
        return items;
    }

    /**
     * Checks an element whether its name matches the specified name.
     * 
     * @param element The element to check
     * @param expectedTag The expected tag name
     * @throws IllegalArgumentException If the element name doesn't match
     */
    protected void checkElement(Element element, DescriptorTag expectedTag)
        throws IllegalArgumentException
    {
        if (!expectedTag.getTagName().equals(element.getName()))
        {
            throw new IllegalArgumentException("Not a [" + expectedTag + "] element");
        }
    }

    /**
     * Returns an iterator over the child elements of the specified element that match the specified
     * tag.
     * 
     * @param parent The element of which the nested elements should be retrieved
     * @param tag The descriptor tag of which the elements should be returned
     * @return An iterator over the elements matching the tag, in the order they occur in the
     *         descriptor
     */
    protected Iterator getNestedElements(Element parent, DescriptorTag tag)
    {
        List elements = new ArrayList();
        List nodeList = parent.getChildren(tag.getTagName(), tag.getTagNamespace());
        for (int i = 0; i < nodeList.size(); i++)
        {
            elements.add(nodeList.get(i));
        }
        return elements.iterator();
    }

    /**
     * Creates an element that contains nested text.
     * 
     * @param tag The tag to create an instance of
     * @param text The text that should be nested in the element
     * @return The created DOM element
     */
    protected Element createNestedText(DescriptorTag tag, String text)
    {
        Element element = new Element(tag.getTagName(), tag.getTagNamespace());
        element.setText(text);

        return element;
    }

    /**
     * Returns the text nested inside a child element of the specified element.
     * 
     * @param parent The element of which the nested text should be returned
     * @param tag The descriptor tag in which the text is nested
     * @return The text nested in the element
     */
    protected String getNestedText(Element parent, DescriptorTag tag)
    {
        String text = null;
        List nestedElements = parent.getChildren(tag.getTagName(), tag.getTagNamespace());
        if (nestedElements.size() > 0)
        {
            text = getText((Element) nestedElements.get(0));
        }
        return text;
    }

    /**
     * Returns the text value of an element.
     * 
     * @param element the element of wich the text value should be returned
     * @return the text value of an element
     */
    protected String getText(Element element)
    {
        return element.getText();
    }

    /**
     * Gets a certain tag directly under the parent tag.
     * 
     * @param parent the tag to get the cild from
     * @param tag name of the child tag
     * @return tag directly under the parent tag.
     */
    protected Element getImmediateChild(Element parent, DescriptorTag tag)
    {
        Element e = null;
        List nl = parent.getChildren();
        for (int i = 0; i < nl.size(); i++)
        {
            Element n = (Element) nl.get(i);
            if (n.getName().equals(tag.getTagName()))
            {
                e = (Element) n;
            }
        }

        return e;
    }

    /**
     * Returns the text value from a child directly under the parent tag.
     * 
     * @param parent the parent tag to get the child text from
     * @param tag the name of the child tag
     * @return the text value of a child tag directly under the parent tag
     */
    protected String getChildText(Element parent, DescriptorTag tag)
    {
        String text = null;
        Element e = getImmediateChild(parent, tag);
        if (e != null)
        {
            text = getText(e);
        }
        return text;
    }
    
    /**
     * Returns the text value from a child directly under the parent tag.
     * 
     * @param parent the parent tag to get the child text from
     * @param tagName the name of the child tag
     * @return the text value of a child tag directly under the parent tag
     */
    protected String getChildText(Element parent, String tagName)
    {
        return getChildText(parent, getDescriptorType().getTagByName(tagName));
    }

    /**
     * Adds an element of the specified tag to the descriptor.
     * 
     * @param tag The descriptor tag
     * @param child The child element to add
     * @param parent The parent element to add the child to
     * @return the inserted element
     */
    public Element addElement(DescriptorTag tag, Element child, Element parent)
    {
        Element importedNode = (Element) child.detach();

        Element refNode = getInsertionPointFor(tag, parent.getName());

        int idx = parent.getContent().indexOf(refNode);
        if (idx == -1)
        {
            // parent.getChildren().add(importedNode);
            parent.addContent(importedNode);
        }
        else
        {
            // Navigate backwards if the previous item is a comment
            while (idx > 0 && parent.getContent(idx - 1) instanceof Comment)
            {
                idx--;
            }

            parent.addContent(idx, importedNode);
        }

        return importedNode;
    }

    //
    // /**
    // * Replaces all elements of the specified tag with the provided element.
    // *
    // * @param tag The descriptor tag
    // * @param child The element to replace the current elements with
    // * @param parent The parent element to add the child to
    // *
    // * @return the replaced element
    // */
    // public Element replaceElement(DescriptorTag tag, Element child, Element parent)
    // {
    // Iterator elements = getElements(tag);
    // while (elements.hasNext())
    // {
    // Element e = (Element) elements.next();
    // e.getParentNode().removeChild(e);
    // }
    // return addElement(tag, child, parent);
    // }
    //
    /**
     * Returns the node before which the specified tag should be inserted, or <code>null</code> if
     * the node should be inserted at the end of the descriptor.
     * 
     * @param tag The tag that should be inserted
     * @param parent name of the parent tag
     * @return The node before which the tag can be inserted
     */
    protected Element getInsertionPointFor(DescriptorTag tag, String parent)
    {
        List elementOrder = this.getDescriptorType().getGrammar().getElementOrder(parent);
        for (int i = 0; i < elementOrder.size(); i++)
        {
            DescriptorTag orderTag = (DescriptorTag) elementOrder.get(i);
            if (orderTag.equals(tag))
            {
                for (int j = i + 1; j < elementOrder.size(); j++)
                {
                    DescriptorTag theTag = (DescriptorTag) elementOrder.get(j);
                    List elements =
                        getRootElement().getChildren(
                            theTag.getTagName(), theTag.getTagNamespace());
                    if (elements.size() > 0)
                    {
                        Element result = (Element) elements.get(0);
                        return result;
                    }
                }
                break;
            }
        }
        return null;
    }
    
    /**
     * @return the descriptorType
     */
    public DescriptorType getDescriptorType()
    {
        return this.descriptorType;
    }

    /**
     * Get elements of a particular descriptor tag whose identifier matches the passed parameter.
     * 
     * @param tag tag to search for
     * @param value value for the identifier to match
     * @return the element that matches
     */
    public Element getTagByIdentifier(DescriptorTag tag, String value)
    {
        if (value == null || tag == null)
        {
            throw new NullPointerException();
        }
        Identifier id = tag.getIdentifier();
        if (id != null)
        {
            List tags = getTags(tag);

            for (Iterator i = tags.iterator(); i.hasNext();)
            {
                Element e = (Element) i.next();
                if (value.equals(id.getIdentifier(e)))
                {
                    return e;
                }
            }
        }
        return null;
    }
    
    /**
     * Get elements of a particular descriptor tag whose identifier matches the passed parameter.
     * 
     * @param tagName Name of the tag to search for
     * @param value value for the identifier to match
     * @return the element that matches
     */
    public Element getTagByIdentifier(String tagName, String value)
    {
        return getTagByIdentifier(getDescriptorType().getTagByName(tagName), value);    
    }
}
