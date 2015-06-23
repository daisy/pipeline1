package org.daisy.util.dtb.build;

import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;

/**
 * Wrapper for XMLEventWriter that does pretty print of output
 * @author jpritchett@rfbd.org
 *
 */
public class PrettyEventWriter {

	private XMLEventWriter w;
	private String tab;
	private int indentLevel;
	private boolean emptyElement;
	private Characters tabEvent;
	private Characters nlEvent;
	
	/**
	 * @param writer The XMLEventWriter to use for output
	 * @param tabString String representing a single tab in the output
	 */
	public PrettyEventWriter(XMLEventWriter writer, String tabString) {
		this.w = writer;
		this.tab = tabString;
		this.indentLevel = 0;
		this.emptyElement = false;
		XMLEventFactory xef = XMLEventFactory.newInstance();
		tabEvent = xef.createCharacters(tab);
		nlEvent = xef.createCharacters("\n");
	}
	
	/**
	 * Output an event, handling indentation
	 * @param e The event to output
	 * @throws XMLStreamException
	 */
	public void writeEvent(XMLEvent e) throws XMLStreamException {
		// For the DTD:  just put a newline before it
		if (e instanceof DTD) {
			w.add(nlEvent);
		}
		// For all new elements:
		//	1) Write a newline and indentation
		//	2) Increment the indentation level
		//	3) Reset flag to show we are currently in an element that is currently empty
		else if (e.isStartElement()) {
			writeIndentation();
			indentLevel++;
			emptyElement = true;
		}
		// When closing elements:
		//	1) Decrement the indentation level
		//	2) Write a newline and indentation (if this isn't an empty element)
		//	3) Reset flag to show we are not in an empty element
		else if (e.isEndElement()) {
			indentLevel--;
			// TODO Fix underlying problem with meta
			if (!emptyElement && !e.asEndElement().getName().getLocalPart().equals("meta")) {
				writeIndentation();
			}
			emptyElement = false;
		}
		// Any other kind of event makes the current element non-empty
		else {
			emptyElement = false;
		}
		
		// Now we're ready to write the passed event
		w.add(e);
	}

	// Refactored convenience
	private void writeIndentation() throws XMLStreamException {
		w.add(nlEvent);
		for (int i = 0; i < indentLevel; i++) {
			w.add(tabEvent);
		}
	}
	
	/**
	 * Write a bunch of events using pretty-print
	 * @param i Iterator that returns XMLEvents
	 * @throws XMLStreamException
	 */
	public void writeEvents(Iterator<XMLEvent> i) throws XMLStreamException {
		while (i.hasNext()) {
			this.writeEvent(i.next());
		}
	}
}
