/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.validation;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.location.LocationUtils;
import org.daisy.util.xml.NamespaceReporter;

/**
 *
 * @author Markus Gylling
 */
public class ValidationUtils {
	
	/**
	 * Convert an identifier string into one or several schema (RNG, XSD, SCH) <code>javax.xml.transform.Source</code> objects.
	 * <p>The conversion requires</p>
	 * <ul>
	 * <li>that the identifier can be converted into a URL</li>
	 * <li>that the URL points to an existing resource</li>
	 * <li>that the resource is an XML document that contains elements belonging to one or several canonical schema namespaces</li>
	 * </ul>
	 * <p>If the the resource is a compound schema, a map with several entries will be returned, else one entry.</p> 
	 * @param identifier a resource identifier consisting of an absolute or relative filespec, or a prolog Public or System Id.
	 * @return a Map (Source, SchemaLanguageNamespaceURI) representing the input resource. The map is never empty or null.
	 * @throws TransformerException on any error, and if the resulting map contains no entries (ie no schema namespaces found).
	 */
	public static Map<Source,String> toSchemaSources(String identifier) throws TransformerException {				
		try{
			URL schemaURL = LocationUtils.identifierToURL(identifier);
			if(schemaURL == null) throw new TransformerException("Could not convert identifier " + identifier + " into a URL");
			return ValidationUtils.toSchemaSources(schemaURL);	
		}catch (Exception e) {
			throw new TransformerException(e.getMessage(),e);
		}																
	}

	/**
	 * Convert a URL into one or several schema (RNG, XSD, SCH) <code>javax.xml.transform.Source</code> objects.
	 * <p>The conversion requires</p>
	 * <ul>
	 * <li>that the URL points to an existing resource</li>
	 * <li>that the resource is an XML document that contains elements belonging to one or several canonical schema namespaces</li>
	 * </ul>
	 * <p>If the the resource is a compound schema, a map with several entries will be returned, else one entry.</p> 
	 * @param identifier URL of the single or compound schema.
	 * @return a Map (Source, SchemaLanguageNamespaceURI) representing the input URL. The map is never empty or null.
	 * @throws TransformerException on any error, and if the resulting map contains no entries (ie no schema namespaces found).
	 */
	public static Map<Source,String> toSchemaSources(URL identifier) throws TransformerException {
		Map<Source, String> map = new HashMap<Source, String>();				
		try{										
			NamespaceReporter nsr = new NamespaceReporter(identifier);	
			Set<String> nsURIs = nsr.getNamespaceURIs();
			int nsURIsFound = 0;
			if(nsURIs!=null){
				for (String uri : nsURIs) {
					if(SchemaLanguageConstants.hasEntry(uri)) {
						++nsURIsFound;
						StreamSource schss = new StreamSource(identifier.openStream());
						schss.setSystemId(identifier.toExternalForm());							
						map.put(schss,uri);	    									
					}							
				}
			}
			if(nsURIsFound==0) {
				throw new TransformerException("No schema namespaces found in " + identifier);
			}
		}catch (Exception e) {
			throw new TransformerException(e.getMessage(),e);
		}		
		return map;
	}
	
}
