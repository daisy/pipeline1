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
package org.daisy.util.xml.stax;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

/**
 * Utility class to circumvent the sporadic NPEs ocurring when
 * using Woodstox StartElement.getAttributeByName() 
 * @author Markus Gylling
 */
public class AttributeByName {

	public static Attribute get(QName name, StartElement se) {
		Iterator<?> attributes = se.getAttributes();
		Attribute a;
		while(attributes.hasNext()) {			
			a = (Attribute)attributes.next();
			if(name.equals(a.getName())) return a;			
		}
		return null;
	}
	
	public static String getValue(QName name, XMLStreamReader reader) {
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			if(reader.getAttributeName(i).equals(name)) {
				return reader.getAttributeValue(i);
			}
		}		
		return null;
	}
}
