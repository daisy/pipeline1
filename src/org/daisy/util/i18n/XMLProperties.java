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
package org.daisy.util.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * An XML parse-performance optimized extension of java.util.Properties
 * @author Markus Gylling
 */
public class XMLProperties extends Properties {	
	private static Map<String,Object> mXMLInputFactoryProperties = null;
		
	public XMLProperties() {
		super();		
	}

	public XMLProperties(Properties initSet) {
		super(initSet);		
	}
	
	@Override
	public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		
		if(null==in) throw new NullPointerException();
		
		if(null==mXMLInputFactoryProperties)setXMLInputFactoryProperties();
		
		XMLInputFactory inputFactory = null;
		XMLStreamReader reader = null;
		
		try{
			inputFactory = StAXInputFactoryPool.getInstance().acquire(mXMLInputFactoryProperties);			
			reader = inputFactory.createXMLStreamReader(in);
			while(reader.hasNext()) {
				reader.next();
				if(reader.getEventType() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("entry")) {
					String key = reader.getAttributeValue(null, "key");
					String value = reader.getElementText();
					super.setProperty(key, value);
				}				
			}			
		}catch (Exception e) {
			//might be the spartan encoding support in stax, 
			//problem: no predictable exception type
			if(e.getMessage().contains("encoding")) {
				try{
					super.loadFromXML(in);
				}catch (Exception e2){
					throw new InvalidPropertiesFormatException(e2.getMessage());	
				}			
			}else{
				throw new InvalidPropertiesFormatException(e.getMessage());
			}
		}finally{	
			try {
				if(reader!=null)reader.close();			
				StAXInputFactoryPool.getInstance().release(inputFactory, mXMLInputFactoryProperties);
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
	}
	
	private void setXMLInputFactoryProperties() {		
		mXMLInputFactoryProperties = new HashMap<String,Object>();
		mXMLInputFactoryProperties.put(XMLInputFactory.IS_VALIDATING,Boolean.FALSE);
		mXMLInputFactoryProperties.put(XMLInputFactory.SUPPORT_DTD,Boolean.FALSE);
		mXMLInputFactoryProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
		mXMLInputFactoryProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,Boolean.TRUE);
		mXMLInputFactoryProperties.put(XMLInputFactory.IS_COALESCING,Boolean.TRUE);					
	}
	
	private static final long serialVersionUID = -8451482017058366549L;

}