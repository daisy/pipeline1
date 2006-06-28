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
	private List eventTypeRestrictions = new ArrayList();
		
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
			retval = listener.nextValue(xe.getValue(),this.contextStack);
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
				retval = listener.nextValue(xe.getData(),this.contextStack);				
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
