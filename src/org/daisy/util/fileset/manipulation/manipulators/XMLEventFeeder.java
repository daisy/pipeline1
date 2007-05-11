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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * <p>Base class for FilesetFile manipulation via javax.xml.stream.</p>
 * <p>This class does nothing but pipe events unaltered between in and out.</p>
 * <p>Subclasses override the methods they need in order to expose
 * wanted data to the user for alteration.</p>
 * <p>This implementation also populates a ContextStack and offers that to subclasses.</p>
 * @author Markus Gylling
 */
public class XMLEventFeeder implements FilesetFileManipulator, XMLReporter {

	private static XMLInputFactory xif;
	private static XMLOutputFactory xof;
	protected static XMLEventFactory xef;
	protected XMLEventReader mReader;
	protected XMLEventWriter mWriter;
	private FileOutputStream mFos = null;
	protected ContextStack mContextStack = null;	
	private File dest;	
	private Charset mRequestedOutputEncoding = null;
	private String newLocalName = null;
		
	private boolean mDebugMode = false;
	private boolean mSeparateWriteAttributes = true;
	/**
	 * Default Constructor.
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder() throws CatalogExceptionNotRecoverable{
		initialize(null, false,false);
	}
	

	/**
	 * Extended Constructor.
	 * @param outputEncoding null is an allowed value
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(Charset outputEncoding) throws CatalogExceptionNotRecoverable {
		initialize(outputEncoding, false,false);
	}

	/**
	 * Extended Constructor.
	 * @param newLocalName local name to give to output file
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(String newLocalName) throws CatalogExceptionNotRecoverable {
		this.newLocalName = newLocalName;
		initialize(null, false,false);
	}
	
	/**
	 * Extended Constructor.
	 * @param outputEncoding null is an allowed value
	 * @param newLocalName name to give to output file
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(Charset outputEncoding, String newLocalName) throws CatalogExceptionNotRecoverable {
		this.newLocalName = newLocalName;
		initialize(outputEncoding, false,false);
	}

	/**
	 * Extended Constructor.
	 * @param newLocalName name to give to output file
	 * @param supportDTD whether to configure the reader to support DTD (defaulting etc), default is false
	 * @param validating whether to configure the reader to validate against DTD, default is false
	 * @param separateWriteAttributes whether to report attributes separated from StartElement events, needed for attribute inclusion in XPath getters. Default is true. Note - If false, attributes will be included in both StartElement iterator and separate attribute events.
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(Charset outputEncoding, String newLocalName, boolean supportDTD, boolean validating, boolean separateWriteAttributes) throws CatalogExceptionNotRecoverable {
		this.newLocalName = newLocalName;
		mSeparateWriteAttributes   = separateWriteAttributes;
		initialize(outputEncoding,supportDTD, validating);
	}
	
	private void initialize(Charset outputEncoding, boolean supportDTD, boolean validating) throws CatalogExceptionNotRecoverable {
		if(System.getProperty("org.daisy.debug")!=null) {
			mDebugMode = true;
		}
		//TODO use pools 
		if(xif==null){ //first access to statics
			xif = XMLInputFactory.newInstance();
			xof = XMLOutputFactory.newInstance();			
			xef = XMLEventFactory.newInstance();
						
			if(xof.isPropertySupported("com.ctc.wstx.outputEscapeCr")) {
				try{
					xof.setProperty("com.ctc.wstx.outputEscapeCr", Boolean.TRUE);
				}catch (IllegalArgumentException e) {
					
				}	
			}	
						
	        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);	        
	        if(validating) {
	        	xif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
	        }else{
	        	xif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
	        }	        
	        if(supportDTD) {
	        	xif.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
	        }else{
	        	xif.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
	        }
		}

        xif.setXMLReporter(this);
		xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        
		if(outputEncoding!=null) mRequestedOutputEncoding = outputEncoding;
	}
	
	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException {
		if(!allowDestinationOverwrite&&destination.exists()) throw new FilesetManipulationException(destination.getName() + " exists");
		
		if(null==newLocalName) {
			dest = destination;
		}else{
			dest = new File(destination.getParentFile(),newLocalName);
		}
		
		mContextStack = new ContextStack(true);
		try{
			InputStream is = inFile.asInputStream();			
			mReader = xif.createXMLEventReader(is);
			while(mReader.hasNext()) {
				XMLEvent xe = mReader.nextEvent();				
				//invoke separate method for each type
				//in order to allow finely grained overrides
				switch (xe.getEventType()) {
					case XMLEvent.ATTRIBUTE:
						mContextStack.addEvent(xe);
						writeAttribute((Attribute)xe);
						break;
					case XMLEvent.CDATA:
						mContextStack.addEvent(xe);
						writeCDATA(xe);
						break;
					case XMLEvent.CHARACTERS:
						mContextStack.addEvent(xe);
						writeCharacters(xe.asCharacters());
						break;
					case XMLEvent.COMMENT:
						mContextStack.addEvent(xe);
						writeComment(xe);
						break;
					case XMLEvent.DTD:
						mContextStack.addEvent(xe);
						writeDTD(xe);
						break;
					case XMLEvent.END_DOCUMENT:
						mContextStack.addEvent(xe);
						writeEndDocument(xe);
						break;
					case XMLEvent.END_ELEMENT:
						mContextStack.addEvent(xe);
						writeEndElement(xe);
						break;
					case XMLEvent.ENTITY_DECLARATION:
						mContextStack.addEvent(xe);
						writeEntityDeclaration(xe);
						break;
					case XMLEvent.ENTITY_REFERENCE:
						mContextStack.addEvent(xe);
						writeEntityReference(xe);
						break;
					case XMLEvent.NAMESPACE:	
						mContextStack.addEvent(xe);
						writeNamespace(xe);
						break;
					case XMLEvent.NOTATION_DECLARATION:
						mContextStack.addEvent(xe);
						writeNotationDeclaration(xe);
						break;
					case XMLEvent.PROCESSING_INSTRUCTION:
						mContextStack.addEvent(xe);
						writeProcessingInstruction(xe);
						break;
					case XMLEvent.SPACE:
						mContextStack.addEvent(xe);
						writeSpace(xe);
						break;
					case XMLEvent.START_DOCUMENT:
						mContextStack.addEvent(xe);
						writeStartDocument((StartDocument)xe);
						break;
					case XMLEvent.START_ELEMENT:
							//mSeparateWriteAttributes
							//break the startelement up
							//and report all separately
							//!mSeparateWriteAttributes is a little weird and not default.
							StartElement se = xe.asStartElement();
							mContextStack.addEvent(se);
							if(mSeparateWriteAttributes){
								writeStartElement(xef.createStartElement(se.getName(),null,se.getNamespaces()));
							}else{
								writeStartElement((StartElement)xe);
							}	
							//report the attributes separately in both cases
							for (Iterator iter = se.getAttributes(); iter.hasNext();) {
								Attribute a = (Attribute)iter.next();
								mContextStack.addEvent(a);
								writeAttribute(a);							
							}
						break;
				} //switch		
			}
			mWriter.flush();
			mWriter.close();
			mReader.close();
			is.close();
			if(mFos!=null)mFos.close();
		}catch (Exception e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}
						
		return destination;
	}

	//
	//
	//  methods for subclasses to override
	//
	//
	
	protected void writeStartElement(StartElement xe) throws XMLStreamException {
		mWriter.add(xe);				
	}

	/**
	 * note - overriders of this method must remember to initialize the event writer...
	 */
	protected void writeStartDocument(StartDocument xe) throws XMLStreamException {		
		String enc;
		if(this.mRequestedOutputEncoding!=null) {
			enc = mRequestedOutputEncoding.name();
		}else{
			enc = xe.getCharacterEncodingScheme();
		}	
		
		if(null==enc||enc.length()<1)enc="utf-8";
		
		try{
			mFos = new FileOutputStream(dest);
			mWriter = xof.createXMLEventWriter(mFos,enc);			
		}catch (Exception e) {
			throw new  XMLStreamException(e.getMessage(),e);
		}	
		mWriter.add(xef.createStartDocument(enc));
	}

	protected void writeSpace(XMLEvent xe) throws XMLStreamException {
		mWriter.add(xe);		
	}

	protected void writeProcessingInstruction(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);	
	}

	protected void writeNotationDeclaration(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);	
	}

	protected void writeNamespace(XMLEvent xe)  throws XMLStreamException {
		mWriter.add(xe);		
	}

	protected void writeEntityReference(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);	
	}

	protected void writeEntityDeclaration(XMLEvent xe)  throws XMLStreamException {
		mWriter.add(xe);	
	}

	protected void writeEndElement(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);			
	}

	protected void writeEndDocument(XMLEvent xe)  throws XMLStreamException {
		mWriter.add(xe);			
		try {
			mWriter.close();
			mFos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void writeDTD(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);		
	}

	protected void writeComment(XMLEvent xe) throws XMLStreamException  {
		mWriter.add(xe);
	}

	protected void writeCharacters(Characters xe) throws XMLStreamException  {
		mWriter.add(xe);
	}

	protected void writeCDATA(XMLEvent xe)  throws XMLStreamException {
		mWriter.add(xe);
	}
	
	protected void writeAttribute(Attribute xe) throws XMLStreamException {		
		mWriter.add(xe);				
	}

	//
	//
	//  end methods for subclasses to override
	//
	//

	
	private String getEncoding(FilesetFile inFile) {
		Peeker peeker = null;
		try{
			peeker = PeekerPool.getInstance().acquire();
			PeekResult result = peeker.peek((File)inFile);
			String enc = result.getPrologEncoding();
			return (null!=enc) ? enc : null;		
		}catch (Exception e) {
			return null;
		}finally{
			try {
				PeekerPool.getInstance().release(peeker);
			} catch (PoolException e) {
				if(mDebugMode) {
					System.out.println("XMLEventFeeder#getEncoding PoolException: " + e.getMessage());
				}
			}
		}		
	}
	
	/**
	 * The XMLReporter interface implementation
	 */
	public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException {
		  //TODO use builder
		  String report = ""; 
	      report += errorType + " in " + location.getSystemId();
	      report +="[line " + location.getLineNumber() + "] [column " + location.getColumnNumber() + "]";
	      report +=message;      
	      System.err.println(report);
	      
	}
	
}
