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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.daisy.dmfc.exception.MIMEException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

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
	    File _registryFile = new File("resources", "mimereg.xml");

	    try {
            // Parse the registry file into a dom4j Document
            SAXReader _xmlReader = new SAXReader();
            _xmlReader.setValidation(true);
            Document _doc = _xmlReader.read(_registryFile);
            
            readProperties(_doc.getRootElement());
        } catch (DocumentException e) {
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
		XPath _xpathSelector = DocumentHelper.createXPath("type");
		List _types = _xpathSelector.selectNodes(a_element);
		Map _idEntries = new HashMap();
		
		// Iterate over all 'type' subelements 
		for (Iterator _iter = _types.iterator(); _iter.hasNext(); ) {
			Element _type = (Element)_iter.next();
			String _id = _type.valueOf("@id");
			String _name = _type.valueOf("@name");
			String _parents = _type.valueOf("@parentType");
			
			if (_idEntries.containsKey(_id)) {
			    throw new MIMEException("Duplicate entry, ID " + _id);
			}
			
			// Create MIME type
			MIMEType _mimeType = new MIMEType(_id, _name);
			
			// Add references to parent registry entries
			if (!_parents.matches("\\s*")) {
				String[] _refsArr = _parents.split("\\s+");
				for (int i = 0; i < _refsArr.length; ++i) {
				    MIMEType _refType = (MIMEType)_idEntries.get(_refsArr[i]);
				    if (_refType == null) {
				        throw new MIMEException("Referenced MIME type " + _refsArr[i] + " could not be found.");
				    }
				    _mimeType.addParent(_refType);
				}
			}
			_idEntries.put(_id, _mimeType);
			entries.put(_name, _mimeType);
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
