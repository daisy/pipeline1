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
package org.daisy.util.xml;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Retrieve information on the xml:lang values contained within a document using the quickest parser around.
 * <p>This implementation does not validate that any given language codes are valid.</p> 
 * @author Markus Gylling
 */
public class LanguageReporter {

	private Map<String, Object> mProperties = null;
	private Set<String> mLanguageValues = null; 
	private String mRootLanguage = null;
	
	private static final String localName = "lang";
	private static final String nsURI = XMLConstants.XML_NS_URI;
	
	/**
	 * Constructor.
	 * @throws PoolException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public LanguageReporter(URL document) throws IOException, XMLStreamException {
		
		mLanguageValues = new HashSet<String>();		
		setProperties();

		StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();
		XMLInputFactory xif = pool.acquire(mProperties);
		XMLStreamReader reader = null;
		
		try {			
			Source source = new StreamSource(document.openStream());
			source.setSystemId(document.toExternalForm());				
			reader = xif.createXMLStreamReader(source);
									
			boolean rootElement = true;
						
			while (reader.hasNext()) {				
				reader.next();
				if(reader.isStartElement()){
					String lang = reader.getAttributeValue(nsURI, localName);
					if(lang!=null) mLanguageValues.add(lang);
					if(rootElement) {
						mRootLanguage = lang;
						rootElement = false;
					}					
				}
			}
		} finally {
			reader.close();
			pool.release(xif, mProperties, null);
		}
	}
	
	/**
	 * Retrieve all xml:lang values that are declared within the input document. 
	 * <p>If no languages are declared in the document, null is returned.</p>
	 */
	public Set<String> getLanguages() {
		if(mLanguageValues.isEmpty()) return null;
		return mLanguageValues;
	}

	
	/**
	 * Retrieve the xml:lang value declared at the root of the input document.
	 * <p>If no xml:lang value is declared at the root, null is returned.</p>
	 */
	public String getRootLanguage() {
		return mRootLanguage;
	}
	
	private void setProperties() {
		mProperties = new HashMap<String, Object>();
		mProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		mProperties.put(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
		mProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		mProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		mProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
		try {
			mProperties.put(XMLInputFactory.RESOLVER, new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
			mProperties.put(XMLInputFactory.RESOLVER, null);
		}		
	}
}