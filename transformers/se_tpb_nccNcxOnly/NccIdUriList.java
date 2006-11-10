/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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
package se_tpb_nccNcxOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * 
 * @author Linus Ericson
 */
class NccIdUriList {
	private List mList;
	private Iterator mListIterator;
	
	private String mCurrentNccId;
	private NccIdUri mNextIdUri;
	
	/**
	 * Inner class for handling a ID, URI pair
	 */
	private class NccIdUri {		
		private String mId;
		private URI mUri;		
		public NccIdUri(String id, URI uri) {
			mId = id;
			mUri = uri;
		}
		public String getId() {
			return mId;
		}
		public URI getUri() {
			return mUri;
		}
	}
	
	/**
	 * Parse a ncc.html to build a list
	 * @param ncc
	 * @return
	 * @throws URISyntaxException
	 * @throws XMLStreamException
	 * @throws CatalogExceptionNotRecoverable
	 * @throws FileNotFoundException
	 */
	public static NccIdUriList parseNcc(File ncc) throws URISyntaxException, XMLStreamException, CatalogExceptionNotRecoverable, FileNotFoundException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        //mFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
		NccIdUriList idUriList = new NccIdUriList();
		String id = null;
		XMLEventReader nccReader = factory.createXMLEventReader(new FileInputStream(ncc));  
		
		while (nccReader.hasNext()) {
			XMLEvent event = nccReader.nextEvent();
			if (event.isStartElement()) {
				StartElement se = event.asStartElement();
				if ("a".equals(se.getName().getLocalPart())) {
					Attribute href = se.getAttributeByName(new QName("href"));	
					idUriList.add(id, new URI(href.getValue()));
					id = null;
				} else if ("h1".equals(se.getName().getLocalPart()) ||
						   "h2".equals(se.getName().getLocalPart()) ||
						   "h3".equals(se.getName().getLocalPart()) ||
						   "h4".equals(se.getName().getLocalPart()) ||
						   "h5".equals(se.getName().getLocalPart()) ||
						   "h6".equals(se.getName().getLocalPart()) ||
						   "span".equals(se.getName().getLocalPart())) {
					id = se.getAttributeByName(new QName("id")).getValue();
				}
			}
		}
		idUriList.reset();
		return idUriList;
	}
	
	public NccIdUriList() {
		mList = new ArrayList();
	}
	
	public void add(String id, URI uri) {
		mList.add(new NccIdUri(id, uri));
	}
	
	public void reset() {
		mListIterator = mList.iterator();
		NccIdUri first = (NccIdUri)mListIterator.next();
		mCurrentNccId = first.getId();
		if (mListIterator.hasNext()) {
			mNextIdUri = (NccIdUri)mListIterator.next();
		} else {
			mNextIdUri = null;
		}
	}
	
	public URI getCurrentUriToNcc() {
		return URI.create("ncc.html#" + mCurrentNccId);
	}
	
	public boolean isNextNccItem(String filename, String fragment) {
		if (mNextIdUri != null) {
			return mNextIdUri.getUri().equals(URI.create(filename + "#" + fragment));
		}
		return false;
	}
	
	public void advance() {
		mCurrentNccId = mNextIdUri.getId();
		if (mListIterator.hasNext()) {
			mNextIdUri = (NccIdUri)mListIterator.next();
		} else {
			mNextIdUri = null;
		}
	}
		
}
