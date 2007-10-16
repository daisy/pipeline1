package int_daisy_mixedContentNormalizer;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 *
 * @author Markus Gylling
 */
public class InputDocInfoProvider {
	private int mElementCount = 0;
	private Set<String>mInputDocNSURIs = null;
	
	public InputDocInfoProvider(Source doc) throws XMLStreamException {
		Map properties = null;
		XMLInputFactory xif = null;
		InputStream is = null;
		mInputDocNSURIs = new HashSet<String>();
		
		try{
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			XMLStreamReader reader = xif.createXMLStreamReader(doc);
			while(reader.hasNext()) {				
				if(reader.next()==XMLStreamReader.START_ELEMENT) {
					mInputDocNSURIs.add(reader.getNamespaceURI());
					mElementCount++;					
				}
			}
			reader.close();
		}finally{
			StAXInputFactoryPool.getInstance().release(xif, properties);
		}
	}
	
	int getElementCount() {
		return mElementCount;
	}
	
	Set<String> getNamespaces(){
		return mInputDocNSURIs;
	}
}
