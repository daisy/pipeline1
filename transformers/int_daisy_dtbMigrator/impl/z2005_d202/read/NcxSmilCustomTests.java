package int_daisy_dtbMigrator.impl.z2005_d202.read;

import int_daisy_dtbMigrator.BookStruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.fileset.Z3986NcxFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Represent the smilCustomTests as expressed in the NCX
 * @author Markus Gylling
 */
public class NcxSmilCustomTests extends HashSet<NcxSmilCustomTest> {
	
	/**
	 * Constructor.
	 * @param tests
	 * @throws CatalogExceptionNotRecoverable 
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	NcxSmilCustomTests(Z3986NcxFile ncx) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {		
		super();				
		Map<String,Object> properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
		XMLInputFactory xif = StAXInputFactoryPool.getInstance().acquire(properties);
		xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		InputStream is = ncx.asInputStream();
		XMLStreamReader reader = xif.createXMLStreamReader(is);
		while(reader.hasNext()) {
			reader.next();			
			if(reader.isEndElement() && reader.getLocalName().equals("head")) break;
			if(reader.isStartElement() && reader.getLocalName().equals("smilCustomTest")) {
				String id = null;
				String defaultState = null;
				String override = null;
				String bookStruct = null;
				for (int i = 0; i < reader.getAttributeCount(); i++) {
					if(reader.getAttributeLocalName(i).equals("id")) {
						id = reader.getAttributeValue(i);
					} else if(reader.getAttributeLocalName(i).equals("defaultState")) {
						defaultState = reader.getAttributeValue(i);
					} else if(reader.getAttributeLocalName(i).equals("override")) {
						override = reader.getAttributeValue(i);
					} else if(reader.getAttributeLocalName(i).equals("bookStruct")) {
						bookStruct = reader.getAttributeValue(i);
					}					
				}
				this.add(new NcxSmilCustomTest(id,defaultState,override,bookStruct));
			}		
		}
		reader.close();
		is.close();		
		StAXInputFactoryPool.getInstance().release(xif, properties);
					
	}
			
	/**
	 * Get the smilCustomTest with the given id value, or null if no such test exists.
	 */
	NcxSmilCustomTest get(String id) {
		for(NcxSmilCustomTest test : this) {
			if (test.id != null && test.id.equals(id)) {
				return test;
			}
		}
		return null;
	}
	
	/**
	 * Get the smilCustomTest with the given bookStruct value, or null if no such test exists.
	 */		
	NcxSmilCustomTest get(BookStruct bookStruct) {
		for(NcxSmilCustomTest test : this) {
			if(test.bookStruct!=null) {					
				if(test.bookStruct == bookStruct) return test;
			}				 
		}
		return null;
	}
	
	private static final long serialVersionUID = 7661993534352621565L;

}
