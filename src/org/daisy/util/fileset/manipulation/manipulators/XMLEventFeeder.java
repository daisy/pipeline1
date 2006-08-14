package org.daisy.util.fileset.manipulation.manipulators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.daisy.util.fileset.interfaces.sgml.HtmlFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;
import org.daisy.util.xml.Peeker;
import org.daisy.util.xml.PeekerImpl;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.xml.sax.SAXException;

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
	protected XMLEventReader reader;
	protected XMLEventWriter writer;
	protected ContextStack contextStack = null;	
	private File dest;
	private static Peeker peeker;	
	private Charset requestedOutputEncoding = null;
	private String newLocalName = null;
		
	/**
	 * Default Constructor.
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder() throws CatalogExceptionNotRecoverable{
		initialize(null);
	}
	

	/**
	 * Extended Constructor.
	 * @param outputEncoding null is an allowed value
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(Charset outputEncoding) throws CatalogExceptionNotRecoverable {
		initialize(outputEncoding);
	}

	/**
	 * Extended Constructor.
	 * @param newLocalName local name to give to output file
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder( String newLocalName) throws CatalogExceptionNotRecoverable {
		this.newLocalName = newLocalName;
		initialize(null);
	}
	
	/**
	 * Extended Constructor.
	 * @param outputEncoding null is an allowed value
	 * @param newLocalName name to give to output file
	 * @throws CatalogExceptionNotRecoverable
	 */
	public XMLEventFeeder(Charset outputEncoding, String newLocalName) throws CatalogExceptionNotRecoverable {
		this.newLocalName = newLocalName;
		initialize(outputEncoding);
	}
		
	private void initialize(Charset outputEncoding) throws CatalogExceptionNotRecoverable {
		if(xif==null){ //first access to statics
			xif = XMLInputFactory.newInstance();
			xof = XMLOutputFactory.newInstance();
			xef = XMLEventFactory.newInstance();
	        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
	        xif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		}

        xif.setXMLReporter(this);
		xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		
		if(peeker==null)peeker = new PeekerImpl();			
		
		if(outputEncoding!=null) requestedOutputEncoding = outputEncoding;
	}
	
	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException {
		if(!allowDestinationOverwrite&&destination.exists()) throw new FilesetManipulationException(destination.getName() + " exists");
		
		if(null==newLocalName) {
			dest = destination;
		}else{
			dest = new File(destination.getParentFile(),newLocalName);
		}
		
		contextStack = new ContextStack(true);
		try{

			reader = xif.createXMLEventReader(new FileInputStream((File)inFile),getEncoding(inFile));			
			while(reader.hasNext()) {
				XMLEvent xe = reader.nextEvent();				
				//invoke separate method for each type
				//in order to allow finely grained overrides
				switch (xe.getEventType()) {
					case XMLEvent.ATTRIBUTE:
						contextStack.addEvent(xe);
						writeAttribute((Attribute)xe);
						break;
					case XMLEvent.CDATA:
						contextStack.addEvent(xe);
						writeCDATA(xe);
						break;
					case XMLEvent.CHARACTERS:
						contextStack.addEvent(xe);
						writeCharacters(xe.asCharacters());
						break;
					case XMLEvent.COMMENT:
						contextStack.addEvent(xe);
						writeComment(xe);
						break;
					case XMLEvent.DTD:
						contextStack.addEvent(xe);
						writeDTD(xe);
						break;
					case XMLEvent.END_DOCUMENT:
						contextStack.addEvent(xe);
						writeEndDocument(xe);
						break;
					case XMLEvent.END_ELEMENT:
						contextStack.addEvent(xe);
						writeEndElement(xe);
						break;
					case XMLEvent.ENTITY_DECLARATION:
						contextStack.addEvent(xe);
						writeEntityDeclaration(xe);
						break;
					case XMLEvent.ENTITY_REFERENCE:
						contextStack.addEvent(xe);
						writeEntityReference(xe);
						break;
					case XMLEvent.NAMESPACE:	
						contextStack.addEvent(xe);
						writeNamespace(xe);
						break;
					case XMLEvent.NOTATION_DECLARATION:
						contextStack.addEvent(xe);
						writeNotationDeclaration(xe);
						break;
					case XMLEvent.PROCESSING_INSTRUCTION:
						contextStack.addEvent(xe);
						writeProcessingInstruction(xe);
						break;
					case XMLEvent.SPACE:
						contextStack.addEvent(xe);
						writeSpace(xe);
						break;
					case XMLEvent.START_DOCUMENT:
						contextStack.addEvent(xe);
						writeStartDocument((StartDocument)xe);
						break;
					case XMLEvent.START_ELEMENT:
						//break the startelement up
						//and report all separately
						StartElement se = xe.asStartElement();
						contextStack.addEvent(se);
						writeStartElement(xef.createStartElement(se.getName(),null,se.getNamespaces()));
						for (Iterator iter = se.getAttributes(); iter.hasNext();) {
							Attribute a = (Attribute)iter.next();
							contextStack.addEvent(a);
							writeAttribute(a);							
						}
						//this causes a classcastexception in woodstox 203
//						for (Iterator iter = se.getNamespaces(); iter.hasNext();) {
//							Namespace n = (Namespace)iter.next();	
//							Namespace newn = xef.createNamespace("",n.getNamespaceURI());
//							System.err.println(newn.getNamespaceURI());
//							writeNamespace(newn);							
//						}																		
						break;
				} //switch		
			}
			writer.flush();
			writer.close();
			reader.close();
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
		writer.add(xe);				
	}

	/**
	 * note - overriders of this method must remember to initialize the event writer...
	 */
	protected void writeStartDocument(StartDocument xe) throws XMLStreamException {		
		String enc;
		if(this.requestedOutputEncoding!=null) {
			enc = requestedOutputEncoding.name();
		}else{
			enc = xe.getCharacterEncodingScheme();
		}	
		
		if(null==enc||enc.length()<1)enc="utf-8";
		
		try{
			writer = xof.createXMLEventWriter(new FileOutputStream(dest),enc); 
		}catch (Exception e) {
			throw new  XMLStreamException(e.getMessage(),e);
		}	
		writer.add(xef.createStartDocument(enc));
	}

	protected void writeSpace(XMLEvent xe) throws XMLStreamException {
		writer.add(xe);		
	}

	protected void writeProcessingInstruction(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);	
	}

	protected void writeNotationDeclaration(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);	
	}

	protected void writeNamespace(XMLEvent xe)  throws XMLStreamException {
		writer.add(xe);		
	}

	protected void writeEntityReference(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);	
	}

	protected void writeEntityDeclaration(XMLEvent xe)  throws XMLStreamException {
		writer.add(xe);	
	}

	protected void writeEndElement(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);			
	}

	protected void writeEndDocument(XMLEvent xe)  throws XMLStreamException {
		writer.add(xe);	
	}

	protected void writeDTD(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);		
	}

	protected void writeComment(XMLEvent xe) throws XMLStreamException  {
		writer.add(xe);
	}

	protected void writeCharacters(Characters xe) throws XMLStreamException  {
		writer.add(xe);
	}

	protected void writeCDATA(XMLEvent xe)  throws XMLStreamException {
		writer.add(xe);
	}
	
	protected void writeAttribute(Attribute xe) throws XMLStreamException {		
		writer.add(xe);		
		
	}

	//
	//
	//  end methods for subclasses to override
	//
	//

	
	private String getEncoding(FilesetFile inFile) throws SAXException, IOException {
		peeker.reset();		
		peeker.peek(inFile.getFile().toURI());
		String enc = peeker.getEncoding();
		if(null!=enc) return enc;		 
		return "utf-8";
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
