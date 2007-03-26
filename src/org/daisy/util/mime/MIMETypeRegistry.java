/*
 * org.daisy.util - The DAISY java utility library
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

package org.daisy.util.mime;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Singleton MIME registry class. Use the <code>getInstance</code> method
 * to obtain an (the) object of this class.
 * 
 * @author Markus Gylling
 * @author Linus Ericson
 */
public class MIMETypeRegistry implements XMLReporter {
	private static MIMETypeRegistry _instance = null;
	private Map entries = new HashMap();

	private MIMETypeRegistry() throws MIMETypeRegistryException {
		try {
			URL docURL = this.getClass().getResource("MIMETypeRegistry.xml");
			XMLStreamReader reader;
			XMLInputFactory xif = XMLInputFactory.newInstance();				
	        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
	        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);                
	        xif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
	        xif.setXMLReporter(this);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));        
			reader = xif.createXMLStreamReader(docURL.openStream());
			buildMap(reader);
		} catch (Exception e) {
			throw new MIMETypeRegistryException(e.getMessage(),e);		
		}                        		
	}

	/**
	 * Reads the MIME types from the document and builds the entries map.
	 * @param reader
	 * @throws XMLStreamException
	 */
	private void buildMap(XMLStreamReader reader) throws XMLStreamException {
		while (true) {
		    int event = reader.next();
		    if (event == XMLStreamConstants.END_DOCUMENT) {
		       reader.close();
		       break;
		    }
		    if (event == XMLStreamConstants.START_ELEMENT) {
		        if(reader.getLocalName().equals("type")) {
		        	String id = null;
		        	String name = null;
		        	String parents = "";
		        	String aliases = "";
		        	String namePatterns = "";
		        	for (int i = 0; i < reader.getAttributeCount(); i++) {
		        		if (reader.getAttributeLocalName(i).equals("id")) {
		        			id = reader.getAttributeValue(i);	
		        		}else if(reader.getAttributeLocalName(i).equals("name")) {
		        			name = reader.getAttributeValue(i);
		        		}else if(reader.getAttributeLocalName(i).equals("parentTypes")) {
		        			parents = reader.getAttributeValue(i);
		        		}else if(reader.getAttributeLocalName(i).equals("aliasTypes")) {
		        			aliases = reader.getAttributeValue(i);
		        		}else if(reader.getAttributeLocalName(i).equals("namePatterns")) {
		        			namePatterns = reader.getAttributeValue(i);
		        		}		    			
					}
					MIMEType mimeType = new MIMETypeImpl(name, id,aliases.trim(), parents.trim(),namePatterns.trim());			
					entries.put(id, mimeType);		
		        }
		    }
		}
		
	}

	/**
	 * Singleton retrieval.
	 * @return The MimeTypeRegistry instance
	 */
	public static MIMETypeRegistry getInstance() throws MIMETypeRegistryException {
		if (null == _instance) {
			synchronized (MIMETypeRegistry.class) {
				if (null == _instance) {
					_instance = new MIMETypeRegistry();
				}
			}
		}		
		return _instance;
	}

	/**
	 * @param key
	 *            the ID of this mime type (as in the registry xml representation)
	 * @return a MimeType if represented in the map, null otherwise.
	 */
	public MIMEType getEntryById(String key) {
		return (MIMEType)entries.get(key);
	}
	
	/**
	 * @param mime the name of a mime type (as in the registry xml representation).
	 *  This string should have the parameters part stripped before calling this method,
	 *  since the canonical representations in the registry does not recognize the parameters part.
	 * @return a MimeType if represented in the map, null otherwise.
	 */
	public MIMEType getEntryByName(String mime) {			
		Collection c = entries.values();
		Iterator i = c.iterator();
		while(i.hasNext()) {
			MIMEType m = (MIMEType) i.next();
			if(mime.equals(m.getString())) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * @return the Map&lt;ID,MimeType&gt; of canonical MimeTypes present in the Registry.
	 */
	public Map getEntries(){
		return entries;
	}
	
	/**
	 * a utility method to use when
	 * updating the constants in MIMEConstants.java
	 */
	private static void printConstants() throws MIMETypeRegistryException {
		List slist = new LinkedList();
		String decl = "public static final String ";
		for (Iterator iter = getInstance().getEntries().keySet().iterator(); iter.hasNext();) {
			MIMEType mt = (MIMEType) getInstance().getEntries().get(iter.next());
			String print = decl + "MIME_" +  mt.getString().toUpperCase().replace(".","_").replace("/","_").replace("-","_").replace("+","_") +" = \"" + mt.getString() + "\";";
			slist.add(print);
		}
		Collections.sort(slist);
		for (Iterator iter = slist.iterator(); iter.hasNext();) {
			String print = (String) iter.next();
			System.err.println(print);
		}		
	}
	
	/**
	 * @param candidate
	 *            a MIME string that may or may not be represented in the
	 *            registry.
	 * @return true if a representation exists, false otherwise. 
	 */
	public boolean hasEntry(String candidate) {
		return getEntryByName(candidate) != null;
	}
	
	/**
	 * Report the desired message in an application specific format. 
	 * Only warnings and non-fatal errors should be reported through this interface. 
	 * Fatal errors should be thrown as XMLStreamException.
	 */
	public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException {
  	  	      System.err.println(errorType + " in " + location.getSystemId());
      	      System.err.println("[line " + location.getLineNumber() + "] [column " + location.getColumnNumber() + "]");
      	      System.err.println(message);      
	}  	      
}
