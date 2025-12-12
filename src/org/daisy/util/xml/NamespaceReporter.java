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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
 * Retrieve information on the namespace URIs contained within a document using the quickest parser around. 
 * @author Markus Gylling
 */
public class NamespaceReporter {

	private Map<String, Object> mProperties = null;
	private Map<String, String> mUriPrefixCollector = null; //<nsuri, prefix>
	private boolean mHasUnqualifiedElements = false;
	
	/**
	 * Constructor.
	 * @throws PoolException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public NamespaceReporter(URL document) throws IOException, XMLStreamException {
		
		mUriPrefixCollector = new HashMap<String, String>();
		setProperties();

		StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();
		XMLInputFactory xif = pool.acquire(mProperties);
		XMLStreamReader reader = null;
		
		try {			
			Source source = new StreamSource(document.openStream());
			source.setSystemId(document.toExternalForm());				
			reader = xif.createXMLStreamReader(source);
			
			String attns = null;
			String prefix = null;
			String nsuri = null;
			
			while (reader.hasNext()) {				
				reader.next();
				if(reader.isStartElement()){
					nsuri = reader.getNamespaceURI();
					prefix = reader.getPrefix();
					
					if(nsuri==null) {
						mHasUnqualifiedElements = true;
					}else{
						if(prefix==null) {
							mUriPrefixCollector.put(nsuri, "");
						}else{
							mUriPrefixCollector.put(nsuri, prefix);
						}
					}
										
					for (int i = 0; i < reader.getAttributeCount(); i++) {
						attns= reader.getAttributeNamespace(i);
						if(attns!=null)mUriPrefixCollector.put(attns,reader.getAttributePrefix(i));
					}
				}
			}
		} finally {
			if (reader != null)
				reader.close();
			pool.release(xif, mProperties, null);
		}
			
	}
	
	/**
	 * Retrieve all namespace URIs that are used within the document, default namespace URI(s) inclusive.
	 * <p>Namespaces that are declared but not used are not included.</p>
	 * <p>If no namespaces at all are declared in the document, null is returned.</p>
	 */
	public Set<String> getNamespaceURIs() {
		if(mUriPrefixCollector.isEmpty()) return null;
		return mUriPrefixCollector.keySet();
	}

	
	/**
	 * Retrieve all default namespace URIs that are used within the document.
	 * <p>If no default namespaces are declared in the document, an empty set is returned.</p>
	 * <p>If no namespaces at all are declared in the document, null is returned.</p>
	 * <p>Namespaces that are declared but not used are not included.</p>
	 */
	public Set<String> getDefaultNamespaceURIs() {
		if(mUriPrefixCollector.isEmpty()) return null;
		Set<String> ret = new HashSet<String>();
		for (Iterator<String> iter = mUriPrefixCollector.keySet().iterator(); iter.hasNext();) {
			String uri = iter.next();
			String prefix = mUriPrefixCollector.get(uri);
			if(prefix.equals("")){
				ret.add(uri);
			}
		}
		return ret;
	}

	/**
	 * Retrieve the prefix used for inparam namespace uri.
	 * <p>If this URI is default within the document, return the empty string.</p>
	 * <p>If this URI did not occur within the document, return null.</p>
	 */
	public String getPrefix(String namespaceURI) {
		return mUriPrefixCollector.get(namespaceURI);
	}
	
	/**
	 * @return true if at least one element in the document is not namespace qualified.
	 */
	public boolean hasUnqualifiedElements() {
		return mHasUnqualifiedElements;
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