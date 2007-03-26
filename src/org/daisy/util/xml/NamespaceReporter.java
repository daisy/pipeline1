package org.daisy.util.xml;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
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
 * Retrieve a list of the namespace URIs contained within a document using the quickest parser around. 
 * @author Markus Gylling
 */
public class NamespaceReporter {

	private Set<String> mNamespaceURIs = null;
	private static Map<String, Object> mProperties = null;

	/**
	 * Constructor.
	 */
	public NamespaceReporter() {
		mNamespaceURIs = new HashSet<String>();
		if (mProperties == null) setProperties();
	}
	
	public Set<String> getNamespaceURIs(URL document) throws IOException, PoolException, XMLStreamException {
		StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();
		XMLInputFactory xif = pool.acquire(mProperties);
		XMLStreamReader reader = null; 
		try {			
			Source source = new StreamSource(document.openStream());
			source.setSystemId(document.toExternalForm());				
			reader = xif.createXMLStreamReader(source);			
			while (reader.hasNext()) {
				//if (XMLStreamConstants.NAMESPACE == reader.next()) {
				if (XMLStreamConstants.START_ELEMENT == reader.next()) {
					mNamespaceURIs.add(reader.getNamespaceURI());
				}
			}
		} finally {
			reader.close();
			pool.release(xif, mProperties, null);
		}

		return mNamespaceURIs;
	}


	private synchronized void setProperties() {
		mProperties = new HashMap<String, Object>();
		mProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
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