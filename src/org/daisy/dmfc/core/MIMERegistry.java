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
import org.xml.sax.SAXException;

/**
 * Singleton MIME registry class. Use the <code>instance</code> method
 * to obtain an (the) object of this class.
 * @author Linus Ericson
 */
public class MIMERegistry {
	private static MIMERegistry registry = null;
	private Map entries = new HashMap();
	
	/**
	 * Private constructor
	 * @throws MIMEException
	 */
	private MIMERegistry() throws MIMEException {
	    File _registryFile = new File(System.getProperty("dmfc.home") + File.separator + "resources", "mimereg.xml");

	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    docBuilderFactory.setValidating(true);
	    try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(_registryFile);
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
			registry = new MIMERegistry();
		}
		return registry;
	}
	
	/**
	 * Reads the MIME types from the dom4j document.
	 * @param a_element
	 * @throws MIMEException
	 */
	private void readProperties(Element a_element) throws MIMEException {
	    Map idEntries = new HashMap();
	    NodeList nodeSet = XPathUtils.selectNodes(a_element, "type");  
	    
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
	 * @param a_subType a sub type
	 * @param a_superType a super type
	 * @return <code>true</code> if <code>a_subType</code> is a type of <code>a_superType</code>
	 * @throws MIMEException
	 */
	public boolean matches(String a_subType, String a_superType) throws MIMEException {
	    MIMEType _sub = (MIMEType)entries.get(a_subType);
	    MIMEType _super = (MIMEType)entries.get(a_superType);
	    if (_sub == null) {
	        throw new MIMEException("MIME type '" + a_subType + "' not found in MIME registry");	        
	    }
	    if (_super == null) {
	        throw new MIMEException("MIME type '" + a_superType + "' not found in MIME registry");	        
	    }
	    return _sub.matches(_super);		
	}
	
	/**
	 * Checks whether the specified MIME type exists in the registry.
	 * @param a_type a MIME type
	 * @return <code>true</code> if the MIME type exists, <code>false</code> otherwise
	 */
	public boolean contains(String a_type) {
	    MIMEType _type = (MIMEType)entries.get(a_type);
	    return _type != null;
	}
}
