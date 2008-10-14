package int_daisy_dtbMigrator.impl.z2005_d202.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.ConvenientXMLEventReader;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Collect information on all nodes in NCX that reference SMIL.
 * The key is content element src attribute value, value is an NcxContentNode
 * TODO FIX several nodes can have the same SMIL uri
 */
class NcxContentNodes extends HashMap<String,NcxContentNode> { 

		NcxContentNodes(Z3986NcxFile ncx) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
			super();
			QName srcQname = new QName(null,"src");
			Map<String,Object> properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(properties);
			xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
			InputStream is = ncx.asInputStream();				
			XMLEventReader xer = xif.createXMLEventReader(is);
			ConvenientXMLEventReader reader = new ConvenientXMLEventReader(ncx.getFile().toURI().toURL(),xer); 
			boolean textElemOpen = false;
			String textValue = null;
			String contentSrcValue = null;
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				if(e.isStartElement()) {
					StartElement se = e.asStartElement();
					if(se.getName().getLocalPart() == "text") {
						textElemOpen = true;
					}else if(se.getName().getLocalPart() == "content") {
						Attribute a = AttributeByName.get(srcQname,se);
						contentSrcValue = a.getValue();
					}
				}else if(e.isEndElement()) {
					EndElement ee = e.asEndElement();
					if(ee.getName().getLocalPart() == "text") {
						textElemOpen = false;
					}else if(ee.getName().getLocalPart() == "navPoint" 
						|| ee.getName().getLocalPart() == "pageTarget" 
							|| ee.getName().getLocalPart() == "navTarget") {
						if(textValue!=null&&contentSrcValue!=null) {
							this.put(contentSrcValue, new NcxContentNode(contentSrcValue,reader.getContextStack().getContextXPath(),textValue));
							textValue = null;
							contentSrcValue=null;
						}	
					}else{
						//System.err.println("excl endelem: " + ee.getName());
					}
				}else if(e.isCharacters() && textElemOpen) {
					textValue = e.asCharacters().getData();
				}
			}		
			reader.close();
			is.close();		
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
					
		private static final long serialVersionUID = -1805994040925550398L;
}


