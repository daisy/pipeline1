package org.daisy.pipeline.gui.util;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Get a title from an XHTML document.
 * @author Markus Gylling
 */
public class XhtmlTitleProvider {

	/**
	 * Retrieve a title descriptor of inparam XHTML document; html/head/title has precedence, followed by the first occuring html/body/hx, if any.
	 * @param xhtmlDoc the locator of the document
	 * @return the title of this document, or null if none was found.
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static String getTitle(URL xhtmlDoc) throws IOException, XMLStreamException  {
		Map properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
		XMLInputFactory xif = null;
		try{
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));	
			XMLStreamReader xsr = xif.createXMLStreamReader(xhtmlDoc.openStream());
			while(xsr.hasNext()) {
				xsr.next();
				if(xsr.isStartElement()) {
					if(xsr.getLocalName().equals("title")||xsr.getLocalName().matches("h1|h2|h3|h4|h5|h6")) {
						return xsr.getElementText();
					}					
				}
			}
			xsr.close();	
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
		}finally{
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
		
		return null;
	}
}
