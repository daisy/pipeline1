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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/*package*/ class NccItem {

	public static enum Type {
		PAGE_FRONT,
		PAGE_NORMAL,
		PAGE_SPECIAL,
		SIDEBAR,
		OPTIONAL_PRODNOTE,
		NOTEREF,
		H1,
		H2,
		H3,
		H4,
		H5,
		H6
	}
	
	private Type type;
	private String uri;
	
	/**
	 * @param type
	 * @param uri
	 */
	public NccItem(Type type, String uri) {
		this.type = type;
		this.uri = uri;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	
	public static NccItem getNccItem(XMLEventReader reader) throws XMLStreamException {
		if (!reader.hasNext()) {
            return null;
        }
        
        XMLEvent event = reader.nextEvent();
        Type type = null;
        String uri = "";
        int level = 0;
        
        // Skip any leading whitespace
        while (event.isCharacters() && reader.hasNext()) {
            event = reader.nextEvent();
        }        
        while (event != null) {
            if (event.isStartElement()) {
                level++;
                StartElement se = event.asStartElement();
                if ("h1".equals(se.getName().getLocalPart())) {
                	type = Type.H1;
                } else if ("h2".equals(se.getName().getLocalPart())) {
                	type = Type.H2;
                } else if ("h3".equals(se.getName().getLocalPart())) {
                	type = Type.H3;
                } else if ("h4".equals(se.getName().getLocalPart())) {
                	type = Type.H4;
                } else if ("h5".equals(se.getName().getLocalPart())) {
                	type = Type.H5;
                } else if ("h6".equals(se.getName().getLocalPart())) {
                	type = Type.H6;
                } else if ("span".equals(se.getName().getLocalPart())) {
                    String classAttr = se.getAttributeByName(new QName("class")).getValue();
                    if ("page-front".equals(classAttr)) {
                        type = Type.PAGE_FRONT;
                    } else if ("page-normal".equals(classAttr)) {
                        type = Type.PAGE_NORMAL;
                    } else if ("page-special".equals(classAttr)) {
                        type = Type.PAGE_SPECIAL;
                    } else if ("sidebar".equals(classAttr)) {
                        type = Type.SIDEBAR;
                    } else if ("optional-prodnote".equals(classAttr)) {
                        type = Type.OPTIONAL_PRODNOTE;
                    } else if ("noteref".equals(classAttr)) {
                        type = Type.NOTEREF;
                    }
                } else if ("a".equals(se.getName().getLocalPart())) {
                    Attribute href = se.getAttributeByName(new QName("href"));
                    uri = href.getValue();
                } else {
                    return null;
                }
            } else if (event.isEndElement()) {
                level--;
                if (level == 0) {
                    return new NccItem(type, uri);
                }
            } 
            
            if (reader.hasNext()) {
                event = reader.nextEvent();
            } else {
                event = null;
            }
        }
        
        return null;
	}
}
