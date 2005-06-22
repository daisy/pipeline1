/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.exception.MIMEException;
import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Singleton MIME registry class. Use the <code>instance</code> method
 * to obtain an (the) object of this class.
 * @author Linus Ericson
 */
public class MIMERegistry implements ErrorHandler {
	private static MIMERegistry registry = null;
	private Map entries = new HashMap();
	
	/**
	 * Private constructor
	 * @throws MIMEException
	 */
	private MIMERegistry() throws MIMEException {
	    File registryFile = new File(System.getProperty("dmfc.home") + File.separator + "resources", "mimereg.xml");

	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    docBuilderFactory.setValidating(true);
	    try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setErrorHandler(this);
            Document doc = docBuilder.parse(registryFile);
            readProperties(doc.getDocumentElement());
        } catch (MIMEException e) {
            throw new MIMEException("MIME registry file error: " + e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new MIMEException("MIME registry file error: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new MIMEException("MIME registry file error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MIMEException("MIME registry file error: " + e.getMessage(), e);
        }        		
	}
	
	/**
	 * Get an instance of the MIME registry 
	 * @return a MIMERegistry instance
	 * @throws MIMEException
	 */
	public static MIMERegistry instance() throws MIMEException {
		if (registry == null) {
		    synchronized (MIMERegistry.class) {
		        if (registry == null) {
		            registry = new MIMERegistry();
		        }
		    }
		}
		return registry;
	}
	
	/**
	 * Reads the MIME types from the dom4j document.
	 * @param element
	 * @throws MIMEException
	 */
	private void readProperties(Element element) throws MIMEException {
	    Map idEntries = new HashMap();
	    NodeList nodeSet = XPathUtils.selectNodes(element, "type");  
	    
	    // Iterate over all 'type' subelements 
        for (int i = 0; i < nodeSet.getLength(); ++i) {
	        Element type = (Element)nodeSet.item(i);
	        String id = XPathUtils.valueOf(type, "@id");
	        String name = XPathUtils.valueOf(type, "@name");
	        String parents = XPathUtils.valueOf(type, "@parentType");
	        
	        if (idEntries.containsKey(id)) {
	            throw new MIMEException("Duplicate entry, ID " + id);
	        }
	        
	        // Create MIME type
	        MIMEType mimeType = new MIMEType(id, name);
	        
	        // Add references to parent registry entries
			if (!parents.matches("\\s*")) {
				String[] refsArr = parents.split("\\s+");
				for (int j = 0; j < refsArr.length; ++j) {
				    MIMEType refType = (MIMEType)idEntries.get(refsArr[j]);
				    if (refType == null) {
				        throw new MIMEException("Referenced MIME type " + refsArr[j] + " could not be found.");
				    }
				    mimeType.addParent(refType);
				}
			}
			idEntries.put(id, mimeType);
			entries.put(name, mimeType);
	    }
	}
	
	/**
	 * Checks if two MIME types match.
	 * This function would typically return true for (pseudo code) matches("XHTML", "XML"),
	 * but not for matches("XML", "XHTML")
	 * @param subType a sub type
	 * @param superType a super type
	 * @return <code>true</code> if <code>a_subType</code> is a type of <code>a_superType</code>
	 * @throws MIMEException
	 */
	public boolean matches(String subType, String superType) throws MIMEException {
	    MIMEType sub = (MIMEType)entries.get(subType);
	    MIMEType sup = (MIMEType)entries.get(superType);
	    if (sub == null) {
	        throw new MIMEException("MIME type '" + subType + "' not found in MIME registry");	        
	    }
	    if (sup == null) {
	        throw new MIMEException("MIME type '" + superType + "' not found in MIME registry");	        
	    }
	    return sub.matches(sup);		
	}
	
	/**
	 * Checks whether the specified MIME type exists in the registry.
	 * @param type a MIME type
	 * @return <code>true</code> if the MIME type exists, <code>false</code> otherwise
	 */
	public boolean contains(String type) {
	    MIMEType mimeType = (MIMEType)entries.get(type);
	    return mimeType != null;
	}

    
    public void warning(SAXParseException e) throws SAXException {
        throw new SAXException("[Line " + e.getLineNumber() + ", column " + e.getColumnNumber() + "] " + e.getMessage(), e);
    }

    
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException("[Line " + e.getLineNumber() + ", column " + e.getColumnNumber() + "] " + e.getMessage(), e);
    }

    
    public void fatalError(SAXParseException e) throws SAXException {
        throw new SAXException("[Line " + e.getLineNumber() + ", column " + e.getColumnNumber() + "] " + e.getMessage(), e);
    }
}
