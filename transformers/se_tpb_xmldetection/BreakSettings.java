/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_xmldetection;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * Interface for the break settings for a specific document type (e.g. DTBook, XHTML).
 * @author Linus Ericson
 */
@SuppressWarnings("unchecked")
public interface BreakSettings {

	/**
	 * Initialize using a public and system identifier
	 * @param publicId the public id
	 * @param systemId the system id
	 * @return true if the initialization was successful, false otherwise
	 * @throws UnsupportedDocumentTypeException
	 */
    public boolean setup(String publicId, String systemId) throws UnsupportedDocumentTypeException;
    
    /**
     * Initialize using a namespace declaration
     * @param namespaceURI the namespace
     * @return true if the initialization was successful, false otherwise
     */
    public boolean setup(String namespaceURI);
    
    /**
     * Is the specified element a sentence breaking element?
     * @param elementName the element name
     * @return true if the element should break a sentence, false otherwise
     */
    public boolean isSentenceBreaking(QName elementName);
    
    /**
     * Should the contents within the specified element be skipped from processing?
     * @param elementName the element name
     * @return true if the element should be skipped, false otherwise
     */
    public boolean skipContent(QName elementName);
    
    /**
     * Can the specified element contain text content?
     * @param tagName the element name
     * @return true if the element may contain text content, false otherwise
     */
    public boolean mayContainText(QName tagName);
    
    /**
     * @return the name of the break element
     */
    public QName getBreakElement();
    
    /**
     * Gets the set of extra attributes to add to a break element
     * @return a set of attributes (name, value)
     */
    public Map getBreakAttributes();
    
    /**
     * Gets the set of path expressions that describe the parts of the document
     * to be processed.
     * @return a set of path (xpath subset) expressions
     */
    public Set getDefaultPaths();
    
    /**
     * Gets the name of the abbreviation element
     * @return the abbreviation element
     */
    public QName getAbbrElement();
    
    /**
     * Gets the name of the acronym element
     * @return the acronym element
     */
    public QName getAcronymElement();
    
    /**
     * Gets the name of the initialism element
     * @return the initialism element
     */
    public QName getInitialismElement();
    
    /**
     * Gets the name of the fix element
     * @return the fix element
     */
    public QName getFixElement();
    
    /**
     * @return the attributes to add to an abbreviation element
     */
    public Map getAbbrAttributes();
    
    /**
     * @return the attributes to add to an acronym element
     */
    public Map getAcronymAttributes();
    
    /**
     * @return the attributes to add to an initialism element
     */
    public Map getInitialismAttributes();
    
    /**
     * @return the attributes to add to an fix element
     */
    public Map getFixAttributes();
    
    /**
     * @return the name of the attribute for the expanded initialism
     */
    public String getInitialismExpandAttribute();
    
    /**
     * @return the name of the attribute for the expanded acronym
     */
    public String getAcronymExpandAttribute();
    
    /**
     * @return the name of the attribute for the expanded abbreviation
     */
    public String getAbbrExpandAttribute();
    
    /**
     * @return the name of the attribute for the expanded fix
     */
    public String getFixExpandAttribute();
    
    public QName getExpAttr();
        
}
