/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_skippabilityTweaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;

/**
 * Find the links to the ncc.html files in a discinfo.html file.  
 * @author Linus Ericson
 */
/*package*/ class DiscInfoNccFinder {

	private StAXInputFactoryPool staxPool;
	private Map<String,Object> staxInProperties;
	
	/**
	 * Constructor.
	 * @param staxPool the StAX input factory pool
	 * @param staxInProperties the StAX properties
	 */
	public DiscInfoNccFinder(StAXInputFactoryPool staxPool, Map<String,Object> staxInProperties) {
		this.staxPool = staxPool;
		this.staxInProperties = staxInProperties;
	}
	
	/**
	 * Find the ncc.html links from a discinfo.html file.
	 * @param discInfoFile the discInfo file
	 * @return a list of ncc files.
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public List<File> getNccFilesFromDiscInfo(File discInfoFile) throws IOException, XMLStreamException {
		XMLInputFactory xif = null; 
		XMLEventReader reader = null;
		List<File> nccList = new ArrayList<File>();
		URI discInfoUri = discInfoFile.toURI();
		try {
            xif = staxPool.acquire(staxInProperties);
            
            reader = new BookmarkedXMLEventReader(xif.createXMLEventReader(new FileInputStream(discInfoFile)));
            
            while (reader.hasNext()) {
            	XMLEvent event = reader.nextEvent();
            	if (event.isStartElement()) {
            		StartElement se = event.asStartElement();
            		if ("a".equals(se.getName().getLocalPart())) {
            			Attribute hrefAttr = se.getAttributeByName(new QName("href"));
            			if (hrefAttr != null) {
            				String href = hrefAttr.getValue();
            				URI nccUri = discInfoUri.resolve(href);
            				File nccFile = new File(nccUri);
            				nccList.add(nccFile);
            			}
            		}
            	}
            }            
        } finally {            
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {                
            }
            staxPool.release(xif,staxInProperties);
        }
		
		return nccList;
	}
}
