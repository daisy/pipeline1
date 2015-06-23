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
package org.daisy.util.file.detect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * A circular FIFO cache.
 * @author Markus Gylling
 */
/*package*/ class CircularFifoXMLEventBuffer  {
	private ArrayList<XMLEvent> mInnerList = null;
	private int mMaxSize = 128;
	
	/**
	 * Constructor.
	 * @param maxSize The fixed sixe of this buffer.
	 */
	/*package*/ CircularFifoXMLEventBuffer(int maxSize) {		
		mMaxSize = maxSize;
		mInnerList = new ArrayList<XMLEvent>(maxSize);
	}
	
	/*package*/ void append(XMLEvent xe) {
    	mInnerList.add(xe);
        checkSize();
    }
	
	private void checkSize() {
		while(mInnerList.size()>mMaxSize 
				&& mInnerList.size() > 0) {
			mInnerList.remove(0);
		}		
	}

	/**
	 * Does the inparam list match the contents of the buffer?
	 */
	/*package*/ boolean equals(List<XMLEvent> list) {
		if (list.size() != mInnerList.size()) return false;
		for (int i = 0; i < list.size(); i++) {
			XMLEvent x1 = list.get(i);
			XMLEvent x2 = mInnerList.get(i);
			//have to do custom compare since ns prefixes are arbitrary
			if(!compare(x1,x2))return false;			 
		}				
		return true;
	}
			
	private boolean compare(XMLEvent x1, XMLEvent x2) {
		int x1type = x1.getEventType();
		int x2type = x2.getEventType();		
		
		if(x1type!=x2type) return false;
		
		if(x1type == XMLEvent.START_ELEMENT) {
			StartElement se1 = x1.asStartElement();
			StartElement se2 = x1.asStartElement();
			if(!se1.getName().getLocalPart().equals(se2.getName().getLocalPart())) return false;
			if(!se1.getName().getNamespaceURI().equals(se2.getName().getNamespaceURI())) return false;
			Iterator<?> iter = se1.getAttributes();
			while(iter.hasNext()) {
				Attribute a = (Attribute)iter.next();
				Iterator<?> iter2 = se2.getAttributes();
				boolean hadAttribute = false;
				while(iter2.hasNext()) {
					Attribute b = (Attribute)iter2.next();
					if(b.getName().getLocalPart().equals(a.getName().getLocalPart()) 
							&& b.getName().getNamespaceURI().equals(a.getName().getNamespaceURI())
							&& b.getValue().equals(a.getValue())
						) {						
						hadAttribute = true;
					}
				}				
				if(!hadAttribute) return false;
			}
			
		} else if(x1type == XMLEvent.END_ELEMENT) {
			EndElement ee1 = x1.asEndElement();
			EndElement ee2 = x1.asEndElement();
			if(!ee1.getName().getLocalPart().equals(ee2.getName().getLocalPart())) return false;
			if(!ee1.getName().getNamespaceURI().equals(ee2.getName().getNamespaceURI())) return false;	
		}else{
			//TODO may need to do more here for ex Characters.
			return x1.equals(x2);
		}
			
		return true;
	}
	
	private static final long serialVersionUID = -1339316680203935439L;
}
