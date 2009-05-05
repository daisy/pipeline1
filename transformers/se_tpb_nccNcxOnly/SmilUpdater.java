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
package se_tpb_nccNcxOnly;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Update a Daisy 2.02 SMIL file.
 * @author Linus Ericson
 */
class SmilUpdater extends StaxFilter {

	private BookmarkedXMLEventReader mBookmarkedEventReader;
	private boolean mUpdateTextSrc;
	private NccIdUriList mNccIdUriList;
	private String mSmilFilename;
	
	private static final String START_PAR = "start par";
	
	public SmilUpdater(BookmarkedXMLEventReader ber, OutputStream outStream, NccIdUriList niul, String smilFilename) throws XMLStreamException {
		super(ber, outStream);	
		mBookmarkedEventReader = ber;
		mUpdateTextSrc = false;
		mNccIdUriList = niul;
		mSmilFilename = smilFilename;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
	 */
	protected StartElement startElement(StartElement event) {
		if ("par".equals(event.getName().getLocalPart())) {	
			Attribute id = event.getAttributeByName(new QName("id"));
			if (id != null && mNccIdUriList.isNextNccItem(mSmilFilename, id.getValue())) {
				mUpdateTextSrc = true;				
			} else {
				mUpdateTextSrc = this.peek();
			}
		}
		if ("text".equals(event.getName().getLocalPart())) {
			if (mUpdateTextSrc) {
				mNccIdUriList.advance();
			}
			List<Attribute> attributes = new ArrayList<Attribute>();
			for (Iterator<?> it = event.getAttributes(); it.hasNext(); ) {
				Attribute att = (Attribute)it.next();
				if ("src".equals(att.getName().getLocalPart())) {
					attributes.add(this.getEventFactory().createAttribute("src", mNccIdUriList.getCurrentUriToNcc().toString()));
				} else {
					attributes.add(att);
				}
			}			
			return this.getEventFactory().createStartElement(event.getName(), attributes.iterator(), event.getNamespaces());
		}
		if ("audio".equals(event.getName().getLocalPart())) {
			// Reorder attributes to make all players happy
			Attribute attrId = AttributeByName.get(new QName("id"), event);
			Attribute attrClipBegin = AttributeByName.get(new QName("clip-begin"), event);
			Attribute attrClipEnd = AttributeByName.get(new QName("clip-end"), event);
			Attribute attrSrc = AttributeByName.get(new QName("src"), event);
			Collection<Attribute> coll = new ArrayList<Attribute>();
			if (attrSrc != null) {
				coll.add(attrSrc);
			}
			if (attrClipBegin != null) {
				coll.add(attrClipBegin);
			}
			if (attrClipEnd != null) {
				coll.add(attrClipEnd);
			}
			if (attrId != null) {
				coll.add(attrId);
			}
			return this.getEventFactory().createStartElement(event.getName(), coll.iterator(), event.getNamespaces());			
		}
		return super.startElement(event);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.xml.stax.StaxFilter#endElement(javax.xml.stream.events.EndElement)
	 */
	protected EndElement endElement(EndElement event) {
		if ("par".equals(event.getName().getLocalPart())) {			
			mUpdateTextSrc = false;
		}	
		return super.endElement(event);
	}

	/**
	 * Peek into the stream until the closing par is found.
	 * Look for id:s that match the next NCC item.
	 * If there is a match, we must be prepared to update the
	 * src attribute of the text element in this par.
	 * @return
	 * @throws XMLStreamException 
	 */
	private boolean peek() {
		boolean matchingIdFound = false;
		int level = 1;
		mBookmarkedEventReader.setBookmark(START_PAR);
		
		try {
			while (level > 0) {
				XMLEvent event = mBookmarkedEventReader.nextEvent();
				if (event.isStartElement()) {
					level++;
					Attribute id = event.asStartElement().getAttributeByName(new QName("id"));
					if (id != null) {
						if (mNccIdUriList.isNextNccItem(mSmilFilename, id.getValue())) {
							matchingIdFound = true;
						}
					}
				} else if (event.isEndElement()) {
					level--;
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		mBookmarkedEventReader.gotoAndRemoveBookmark(START_PAR);
		return matchingIdFound;
	}
	
}
