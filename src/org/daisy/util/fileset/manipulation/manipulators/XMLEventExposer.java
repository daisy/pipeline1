/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.fileset.manipulation.manipulators;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * Exposes XML Stream Events to a registered listener
 * listener.
 * 
 * @author Markus Gylling
 */
public class XMLEventExposer extends XMLEventFeeder {
	
	private XMLEventConsumer listener = null;
	private List<Integer> mEventTypeRestrictions = new ArrayList<Integer>();
		
	/**
	 * Default constructor.
	 */
	public XMLEventExposer(XMLEventConsumer eve) throws CatalogExceptionNotRecoverable {
		super();
		this.listener = eve;
	}
	
	/**
	 * Extended constructor.
	 */
	public XMLEventExposer(XMLEventConsumer eve, Charset outputEncoding) throws CatalogExceptionNotRecoverable {		
		super(outputEncoding);
		this.listener = eve;									
	}

	/**
	 * Extended constructor.
	 */
	public XMLEventExposer(XMLEventConsumer eve,  String newLocalName) throws CatalogExceptionNotRecoverable {		
		super(newLocalName);
		this.listener = eve;									
	}
	
	/**
	 * Extended constructor.
	 */
	public XMLEventExposer(XMLEventConsumer eve, Charset outputEncoding, String newLocalName) throws CatalogExceptionNotRecoverable {		
		super(outputEncoding,newLocalName);
		this.listener = eve;								
	}

	/**
	 * Extended constructor.
	 */
	public XMLEventExposer(XMLEventConsumer eve, Charset outputEncoding, String newLocalName, boolean supportDTD, boolean validating, boolean separateWriteAttributes) throws CatalogExceptionNotRecoverable {		
		super(outputEncoding,newLocalName, supportDTD, validating, separateWriteAttributes);
		this.listener = eve;								
	}
	
	private boolean useSuper(XMLEvent event) {
		if (mEventTypeRestrictions.contains(Integer.valueOf(event.getEventType()))
				||mEventTypeRestrictions.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private void writeEvents(List<XMLEvent> list)throws XMLStreamException {
		if(list==null) return;
		if(list.isEmpty()) return;
		for (XMLEvent event : list) {
			mWriter.add(event);			
		}
	}
	
	//** overrides **//
	
	private List<XMLEvent> returnedEvents = null;
	
	protected void writeStartElement(StartElement xe) throws XMLStreamException {
		if(useSuper(xe)) {			
			super.writeStartElement(xe);
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));			
		}	
	}

	/**
	 * note - overriders of this method must remember to initialize the event writer...
	 */
	protected void writeStartDocument(StartDocument xe) throws XMLStreamException {		
		if(useSuper(xe)) {
			super.writeStartDocument(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		}	
	}

	protected void writeSpace(XMLEvent xe) throws XMLStreamException {
		if(useSuper(xe)) {
			super.writeSpace(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}		
	}

	protected void writeProcessingInstruction(XMLEvent xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeProcessingInstruction(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));	
		}	
	}

	protected void writeNotationDeclaration(XMLEvent xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeNotationDeclaration(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}		
	}

	protected void writeNamespace(XMLEvent xe)  throws XMLStreamException {
		if(useSuper(xe)) {
			super.writeNamespace(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}		
	}

	protected void writeEntityReference(XMLEvent xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeEntityReference(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}	
	}

	protected void writeEntityDeclaration(XMLEvent xe) throws XMLStreamException {
		if(useSuper(xe)) {
			super.writeEntityDeclaration(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}	
	}

	protected void writeEndElement(XMLEvent xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeEndElement(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}			
	}

	protected void writeEndDocument(XMLEvent xe)  throws XMLStreamException {
		if(useSuper(xe)) {
			super.writeEndDocument(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}	
	}

	protected void writeDTD(XMLEvent xe) throws XMLStreamException  {
		if(!mEventTypeRestrictions.contains(Integer.valueOf(XMLEvent.DTD))) {
			super.writeDTD(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}	
	}

	protected void writeComment(XMLEvent xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeComment(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}
	}

	protected void writeCharacters(Characters xe) throws XMLStreamException  {
		if(useSuper(xe)) {
			super.writeCharacters(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}
	}

	protected void writeCDATA(XMLEvent xe)  throws XMLStreamException {
		if(useSuper(xe)) {
			super.writeCDATA(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}
	}
	
	protected void writeAttribute(Attribute xe) throws XMLStreamException {		
		if(useSuper(xe)) {
			super.writeAttribute(xe);			
		}else{
			writeEvents(listener.nextEvent(xe, mContextStack));		
		}				
	}
	
	//** end overrides **//
		
	/**
	 * Set a restriction on what XMLEvent types to expose to the EventConsumer.
	 * <p>If no restrictions are set, all XMLEvent 
	 * types will be exposed.</p>
	 * <p>This method can be called several times to add more than one restriction.</p>
	 * @param xmlEventType an event constant from javax.xml.stream.events.XMLEvent that should be exposed to the ValueConsumer.
	 */
	public void setEventTypeRestriction(int xmlEventType) {
		mEventTypeRestrictions.add(Integer.valueOf(xmlEventType));		
	}
	
	/**
	 * Set a restriction on what XMLEvent types to expose to the EventConsumer.
	 * <p>If no restrictions are set, all XMLEvent 
	 * types will be exposed.</p>
	 * <p>This method can be called several times to add more than one restriction.</p>
	 * @param xmlEventType an event constant from javax.xml.stream.events.XMLEvent that should be exposed to the ValueConsumer.
	 */
	public void setEventTypeRestrictions(Set<Integer> xmlEventTypes) {
		mEventTypeRestrictions.addAll(xmlEventTypes);		
	}
	
}
