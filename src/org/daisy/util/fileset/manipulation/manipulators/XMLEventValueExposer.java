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
package org.daisy.util.fileset.manipulation.manipulators;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * Exposes XML element textnodes and attribute valuenodes to a registered
 * listener.
 * 
 * @author Markus Gylling
 */
public class XMLEventValueExposer extends XMLEventFeeder {
	
	private XMLEventValueConsumer listener = null;
	private String retval = null;
	private boolean ignoreSpace;
	private List<Integer> eventTypeRestrictions = new ArrayList<Integer>();
		
	/**
	 * Default constructor.
	 */
	public XMLEventValueExposer(XMLEventValueConsumer eve) throws CatalogExceptionNotRecoverable {
		super();
		this.listener = eve;
		this.ignoreSpace = true;		
	}
	
	/**
	 * Extended constructor.
	 */
	public XMLEventValueExposer(XMLEventValueConsumer eve, boolean ignoreXMLWhiteSpace) throws CatalogExceptionNotRecoverable {		
		super();
		this.listener = eve;						
		this.ignoreSpace = ignoreXMLWhiteSpace;			
	}

	/**
	 * Extended constructor.
	 */
	public XMLEventValueExposer(XMLEventValueConsumer eve, boolean ignoreXMLWhiteSpace, Charset outputEncoding) throws CatalogExceptionNotRecoverable {		
		super(outputEncoding);
		this.listener = eve;						
		this.ignoreSpace = ignoreXMLWhiteSpace;			
	}

	/**
	 * Extended constructor.
	 */
	public XMLEventValueExposer(XMLEventValueConsumer eve,  String newLocalName) throws CatalogExceptionNotRecoverable {		
		super(newLocalName);
		this.listener = eve;						
		this.ignoreSpace = true;			
	}
	
	/**
	 * Extended constructor.
	 */
	public XMLEventValueExposer(XMLEventValueConsumer eve, boolean ignoreXMLWhiteSpace, Charset outputEncoding, String newLocalName) throws CatalogExceptionNotRecoverable {		
		super(outputEncoding,newLocalName);
		this.listener = eve;						
		this.ignoreSpace = ignoreXMLWhiteSpace;			
	}
	
	protected void writeAttribute(Attribute xe) throws XMLStreamException {
		if(eventTypeRestrictions.isEmpty()||eventTypeRestrictions.contains(Integer.valueOf(XMLEvent.ATTRIBUTE))) {
			retval = listener.nextValue(xe.getValue(),this.mContextStack);
			if(retval!=null) {
				super.writeAttribute(xef.createAttribute(xe.getName(),retval));
			}else{
				super.writeAttribute(xe);	
			}			
		}else{
			super.writeAttribute(xe);
		}
			
	}
	
	protected void writeCharacters(Characters xe) throws XMLStreamException {	
		if(eventTypeRestrictions.isEmpty()||eventTypeRestrictions.contains(Integer.valueOf(XMLEvent.CHARACTERS))) {
			//isIgnorable only returns true/false if we are DTD token aware...
			if(ignoreSpace && (xe.isIgnorableWhiteSpace()||CharUtils.isXMLWhiteSpace(xe.getData()))) {
				super.writeCharacters(xe);
			}else{			
				retval = listener.nextValue(xe.getData(),this.mContextStack);				
				if(retval!=null) {
					super.writeCharacters(xef.createCharacters(retval));
				}else{
					super.writeCharacters(xe);	
				}		
			}
		}else{
			super.writeCharacters(xe);
		}
	}
	
	/**
	 * Set a restriction on what XMLEvent types to expose to the ValueConsumer.
	 * <p>If no restrictions are set, all (valuecarrying and implemented) XMLEvent 
	 * types will be exposed.</p>
	 * <p>This method can be called several times to add more than one restriction.</p>
	 * @param xmlEventType an event constant from javax.xml.stream.events.XMLEvent that should be exposed to the ValueConsumer.
	 */
	public void setEventTypeRestriction(int xmlEventType) {
		eventTypeRestrictions.add(Integer.valueOf(xmlEventType));		
	}
	
}
