/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package int_daisy_mixedContentNormalizer;

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
		Map<String,Object> properties = null;
		XMLInputFactory xif = null;
		mInputDocNSURIs = new HashSet<String>();
		
		try{
			properties = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			xif = StAXInputFactoryPool.getInstance().acquire(properties);
			XMLStreamReader reader = xif.createXMLStreamReader(doc);
			while(reader.hasNext()) {
				reader.next();
				int type = reader.getEventType();
				if(type==XMLStreamReader.START_ELEMENT) {
					mInputDocNSURIs.add(reader.getNamespaceURI());
					mElementCount++;						
				}
//				else
//				if(type==XMLStreamReader.ATTRIBUTE) {
//						mInputDocNSURIs.add(reader.getNamespaceURI());			
//				}
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
