/*
 * DMFC - The DAISY Multi Format Converter
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
package se_tpb_xmldetection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;
import org.daisy.util.xml.stax.ContextStack;

/**
 * @author Linus Ericson
 */
public class XMLSentenceDetector extends XMLBreakDetector {

    private static Logger logger = Logger.getLogger(XMLSentenceDetector.class.getName());
    
    protected final static String LAST_EVENT = "last event";
    protected final static String LATEST_BREAKING = "latest breaking";    
    
    protected BookmarkedXMLEventReader reader = null;
    
    protected ContextStack contextStack = null;
        
    private Locale lastLocale = null;
    
    /* *** CONSTRUCTORS *** */
    
    public XMLSentenceDetector (File inFile, File outFile) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        super(outFile);
        ContextAwareBreakSettings cabi = new ContextAwareBreakSettings(true); 
        setBreakSettings(cabi);
        setContextStackFilter(cabi);
        Set xmllang = LangDetector.getXMLLangSet(inFile);
        setBreakFinder(new DefaultSentenceBreakFinder(xmllang));
        
        reader = new BookmarkedXMLEventReader(inputFactory.createXMLEventReader(new FileInputStream(inFile)));
        writer = null;
    }
    
    /* *** METHODS *** */
    
    protected void detect() throws UnsupportedDocumentTypeException, FileNotFoundException, XMLStreamException {
        // The buffer for the text to detect sentence breaks in
        StringBuffer buffer = new StringBuffer();
        
        QName firstTagName = null;
        boolean firstTagIsStart = false;
        
        boolean skipContent = false;
        boolean writeWhileSkipping = false;
        int skipContextStackLength = 0;
        
        ArrayList abbrAcronymList = new ArrayList();
        Abbr abbrAcronym = null;
        
        // Main event loop
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            contextStack.addEvent(event);
            //printEvent(event);
                      
            if (skipContent) {
                boolean isEnd = event.isEndElement();
                if (writeWhileSkipping) {
                    writeEvent(event);
                }
                if (isEnd) {
                    int newlen = contextStack.getContext().size();
                    if (newlen == skipContextStackLength) {
                        firstTagName = event.asEndElement().getName();
                        firstTagIsStart = false;
                        skipContent = false;
                        writeElements(false, new Vector());
                        reader.setBookmark(LATEST_BREAKING);
                    }
                }
            } else if (event.isStartElement() || event.isEndElement()) {
                // Event is a start element or an end element
                boolean isStart = false;
                
                QName elementName = null;
                if (event.isStartElement()) {
                    elementName = event.asStartElement().getName();
                    isStart = true;
                } else {
                    elementName = event.asEndElement().getName();
                }
                
                // Is this a sentence breaking tag or a non-processable context?
                if (breakSettings.isSentenceBreaking(elementName) ||
                        (isStart && breakSettings.skipContent(elementName)) ) {
                                        
                    if (reader.bookmarkExists(LATEST_BREAKING)) {
                        // There was an earlier breaking tag
                        
                        // Find the sentence breaks between these tags and write everything
                        //System.err.println("In the buffer: " + buffer.toString());
                        //System.err.println("Locale: " + lastLocale);
                        if (lastLocale != null) {
                            breakFinder.setLocale(lastLocale);
                        }
                        if (shouldBeProcessed(firstTagName, firstTagIsStart, elementName, event.isStartElement())) {
                            //System.err.println("Should be processed.");
                            if (!buffer.toString().matches("\\s*")) {
                                logger.fine(abbrAcronymList.toString());
                                Vector breaks = breakFinder.findBreaks(buffer.toString(), abbrAcronymList);
                                /*
                                System.err.println(buffer);
                                System.err.println(breaks);
                                */
                                writeElements(true, breaks);
                            } else {
                                // Just whitespace in the buffer. 
                                // Write the elements, but no sentence tags.
                                writeElements(false, new Vector());
                            }
                        } else /* shouldBeProcessed(...) */ {
                            //System.err.println("Should NOT be processed.");
                            writeElements(false, new Vector());
                        }                                               
                        
                        if (breakSettings.skipContent(elementName)) {
                            // There was a non-processable start element inside a processable one
                            
                            // Fast forward to the end of that context
                            //skipUntilMatchingClose(elementName, false);
                            skipContextStackLength = contextStack.getParentContext().size();
                            writeWhileSkipping = false;
                            skipContent = true;
                        }
                        abbrAcronymList = new ArrayList();
                        buffer = new StringBuffer();
                        firstTagName = elementName;
                        firstTagIsStart = event.isStartElement();
                        // Set a new latest breaking tag bookmark
                        reader.setBookmark(LATEST_BREAKING);
                    } else /* reader.bookmarkExists(...) */ {                        
                        if (breakSettings.skipContent(elementName)) {
                            // There was a non-processable start element
                            
                            // Write to the end of that context
                            skipContextStackLength = contextStack.getParentContext().size();
                            writeWhileSkipping = true;
                            skipContent = true;
                        } else {                            
                            reader.setBookmark(LATEST_BREAKING);
                            writeEvent(event);
                        }
                    }
                    
                } else if (!reader.bookmarkExists(LATEST_BREAKING)) {
                    // There is no latest breaking tag, so just write the event
                    writeEvent(event);
                } else {
                    // This is a non-breaking tag, and we have seen a breaking tag before
                    if (event.isStartElement()) {
                        if (elementName.equals(breakSettings.getAbbrElement()) && attributesMatch(event.asStartElement(), breakSettings.getAbbrAttributes())) {
                            Attribute expand = event.asStartElement().getAttributeByName(new QName(breakSettings.getAbbrExpandAttribute()));
                            abbrAcronym = new Abbr(null, expand!=null?expand.getValue():null, Abbr.ABBREVIATION, buffer.length(), 0);
                        } else if (elementName.equals(breakSettings.getAcronymElement()) && attributesMatch(event.asStartElement(), breakSettings.getAcronymAttributes())) {
                            Attribute expand = event.asStartElement().getAttributeByName(new QName(breakSettings.getAcronymExpandAttribute()));
                            abbrAcronym = new Abbr(null, expand!=null?expand.getValue():null, Abbr.ACRONYM, buffer.length(), 0);
                        } else if (elementName.equals(breakSettings.getInitialismElement()) && attributesMatch(event.asStartElement(), breakSettings.getInitialismAttributes())) {
                            Attribute expand = event.asStartElement().getAttributeByName(new QName(breakSettings.getInitialismExpandAttribute()));
                            abbrAcronym = new Abbr(null, expand!=null?expand.getValue():null, Abbr.INITIALISM, buffer.length(), 0);
                        }                        
                    } else /* isEndElement */ {
                        // FIXME make sure end matches with start
                        if (elementName.equals(breakSettings.getAbbrElement()) ||
                                elementName.equals(breakSettings.getAcronymElement()) ||
                                elementName.equals(breakSettings.getInitialismElement())) {
                            abbrAcronym = new Abbr(buffer.substring(abbrAcronym.getStart(), buffer.length()), abbrAcronym.getExpansion(), abbrAcronym.getType(), abbrAcronym.getStart(), buffer.length());
                            abbrAcronymList.add(abbrAcronym);
                            logger.info(abbrAcronym.toString());
                        }                        
                    }
                }
            } else if (event.isCharacters()) {
                Characters ch = event.asCharacters();
                if (reader.bookmarkExists(LATEST_BREAKING)) {
                    // Buffer the text for future use...
                    buffer.append(ch.getData());
                } else {
                    // There is no latest breaking tag, why don't we just write this event...
                    writeEvent(ch);
                }
            } else if (event.getEventType() == XMLStreamConstants.DTD) {
                DTD dtd = (DTD)event;                
                parseDoctype(dtd.getDocumentTypeDeclaration());
                writeEvent(event);
            } else if (event.isStartDocument()) { 
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), sd.getCharacterEncodingScheme());
                    writeEvent(event);
                } else {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
                    writeEvent(eventFactory.createStartDocument("utf-8", "1.0"));                    
                }
            } else if (!reader.bookmarkExists(LATEST_BREAKING)) {
                writeEvent(event);
            }
            
            reader.setBookmark(LAST_EVENT);
            lastLocale = contextStack.getCurrentLocale();
        }
        reader.close();
        writer.close();
    }
    
    /**
     * Check if atributes match.
     * For every key-value pair in attributes, check if the given start
     * element has the same attribute key-value pair.
     * @param se
     * @param attributes
     * @return
     */
    private boolean attributesMatch(StartElement se, Map attributes) {
        boolean result = true;
        for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            String value = (String)attributes.get(key);
            Attribute att = se.getAttributeByName(new QName(key));
            if (att != null) {
                if (!att.getValue().equals(value)) {
                    logger.info("Attribute " + key + " has wrong value " + att.getValue() + "(" + value + ")");
                    return false;
                }
            } else {
                logger.info("Missing attribute " + key);
                return false;
            }
        }
        return result;
    }
    
    private boolean isPathAllowed() {
        if (allowedPaths == null) {
            return true;
        }
        String currentPath = contextStack.getContextPath();
        for (Iterator it = allowedPaths.iterator(); it.hasNext(); ) {
            String allowed = (String)it.next();
            if (currentPath.startsWith(allowed)) {
                return true;
            }
        }
        return false;
    }
       
    private void writeElements(boolean writeStartAndEndTag, Vector breaks) throws XMLStreamException {
        //reader.setBookmark("mark");
        if (writeStartAndEndTag) {
            openSentence();
        }
        reader.gotoAndRemoveBookmark(LATEST_BREAKING);
        XMLEvent event = null;
        int offset = 0;
        while (!reader.atBookmark(LAST_EVENT)) {
            event = reader.nextEvent();
            if (event.isCharacters()) {
                offset = writeCharacters(event.asCharacters().getData(), breaks, offset);
            } else {
                writeEvent(event);
            }
        }
        if (writeStartAndEndTag) {
            closeSentence();
        }
        //System.err.println("before last event");
        event = reader.nextEvent();
        writeEvent(event);
    }
    
    private int writeCharacters(String text, Vector breaks, int offset) throws XMLStreamException {
        if (breaks.size() == 0) {
            writeString(text);
            return offset + text.length();
        } 
        
        for (int i = 0; i < breaks.size(); ++i) {
            
            int currentBreak = ((Integer)breaks.elementAt(i)).intValue();
            if (text.length() + offset < currentBreak) {
                writeString(text);
                return offset + text.length();
            }
            if (offset < currentBreak) {
                writeString(text.substring(0, currentBreak - offset));
                text = text.substring(currentBreak - offset, text.length());
                offset = currentBreak;
                QName lastWritten = (QName)writeStack.getContext().pop();
                /*
                 *  FIXME we only need to check that the last element really was
                 * a sentence break element, right? Hmm...
                 */
                if (breakSettings.isSentenceBreaking(lastWritten)) {                
                    breakSentence();                
                }
            }
        }
        if (text.length() > 0) {
            writeString(text);
            offset = offset + text.length();
        }
        return offset;
    }
    
    private void openSentence() throws XMLStreamException {
        writeEvent(eventFactory.createStartElement(getBreakElement(), getBreakAttributes(), null));
    }
    
    private void closeSentence() throws XMLStreamException {
        writeEvent(eventFactory.createEndElement(getBreakElement(), null));
    }
    
    private void breakSentence() throws XMLStreamException {
        EndElement ee = eventFactory.createEndElement(getBreakElement(), null);
        StartElement se = eventFactory.createStartElement(getBreakElement(), getBreakAttributes(), null); 
        try {
	        writeEvent(ee);
	        writeEvent(se);
        } catch (IllegalArgumentException e) {
            logger.warning("XML is unsuitable for sentence tagging...");
            throw e;
        }
    }
    
    private boolean shouldBeProcessed(QName firstName, boolean firstIsStart, QName lastName, boolean lastIsStart) {
        //System.err.println("first: " + firstName + " " + firstIsStart);
        //System.err.println("last: " + lastName + " " + lastIsStart);
        if (!isPathAllowed()) {
            return false;
        }
        if (firstIsStart) {
            // The first tag is a start tag
            return breakSettings.mayContainText(firstName);
        } 
        // The first tag is an end tag        
        if (lastIsStart) {
            // The last tag is a start tag
            return breakSettings.mayContainText((QName)contextStack.getParentContext().get(0));
        } 
        // The last tag is an end tag
        return breakSettings.mayContainText(lastName);
    }
            
    public void setContextStackFilter(ContextStack csf) {
        contextStack = csf;
    }
    
    /* *** TEST *** */
    
    public static void main(String[] args) throws UnsupportedDocumentTypeException, CatalogExceptionNotRecoverable, XMLStreamException, IOException {        
        XMLSentenceDetector detector = new XMLSentenceDetector(new File(args[0]), new File(args[1]));
        detector.detect(null);
    }
    
}
