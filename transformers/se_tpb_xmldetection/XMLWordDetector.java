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
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.ContextStack;

/**
 * @author Linus Ericson
 */
public class XMLWordDetector extends XMLBreakDetector {
        
    protected XMLEventReader reader = null;
 
    protected ContextStack contextStack = null;
    
    private Locale lastLocale = null;
    private boolean doctypeSeen = false;

    /* *** CONSTRUCTORS *** */
    
    public XMLWordDetector (File inFile, File outFile) throws CatalogExceptionNotRecoverable, FileNotFoundException, XMLStreamException{
        super(outFile);
        ContextAwareBreakSettings cabi = new ContextAwareBreakSettings(false); 
        setBreakSettings(cabi);
        setContextStackFilter(cabi);
        setBreakFinder(new DefaultWordBreakFinder());
        reader = inputFactory.createXMLEventReader(new FileInputStream(inFile));
        writer = null;
    }
    
    /* *** METHODS *** */
    
    protected void detect() throws UnsupportedDocumentTypeException, FileNotFoundException, XMLStreamException {
        boolean skipContent = false;
        int skipContextStackLength = 0;
        
        // Main event loop
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            contextStack.addEvent(event);
            //printEvent(event);
                      
            if (skipContent) {
                boolean isEnd = event.isEndElement();
                writeEvent(event);
                if (isEnd) {
                    int newlen = contextStack.getContext().size();
                    if (newlen == skipContextStackLength) {
                        skipContent = false;
                    }
                }
            } else if (event.isStartElement()) {
                if (!doctypeSeen) {
                    throw new UnsupportedDocumentTypeException("No DOCTYPE declaration found.");
                }
                StartElement se = event.asStartElement();
                if (breakSettings.skipContent(se.getName())) {
                    skipContextStackLength = contextStack.getParentContext().size();
                    skipContent = true;                    
                }
                writeEvent(event);
            } else if (event.isCharacters()) {
                String data = event.asCharacters().getData();
                int index = contextStack.getContext().size() - 1;
                if (index > 0) {
                    QName current = (QName)contextStack.getContext().get(index);
                
                    if (shouldBeProcessed(current)) {
                        if (lastLocale != null) {
                            breakFinder.setLocale(lastLocale);
                        }
                        //System.err.println("Finding breaks in: '" + data + "'");
                        handleBreaks(data);
                        /*Vector breaks = breakFinder.findBreaks(data);
                        System.err.println(breaks);
                        writeElements(data, breaks);
                        */
                    } else {
                        //System.err.println("Skipping: '" + data + "'");
                        writeEvent(event);
                    }
                } else {
                    writeEvent(event);
                }
            } else if (event.getEventType() == XMLStreamConstants.DTD) {            
                DTD dtd = (DTD)event;                
                parseDoctype(dtd.getDocumentTypeDeclaration());
                writeEvent(event);
                doctypeSeen = true;
            } else if (event.isStartDocument()) { 
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), sd.getCharacterEncodingScheme());
                    writeEvent(event);
                } else {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
                    writeEvent(eventFactory.createStartDocument("utf-8", "1.0"));                    
                }
            } else {
                writeEvent(event);
            }
            
            lastLocale = contextStack.getCurrentLocale();
        }
        reader.close();
        writer.close();
    }
    
    protected void handleBreaks(String data) throws XMLStreamException {
        Vector breaks = breakFinder.findBreaks(data, null);
        //System.err.println(breaks);
        writeElements(data, breaks);
    }
    
    private void writeElements(String text, Vector breaks) throws XMLStreamException {
        if (breaks.size() == 0) {
            writeString(text);
            return;
        }
        int oldEnd = 0;
        for (int i = 0; i < breaks.size(); i+=2) {
            int start = ((Integer)breaks.elementAt(i)).intValue();
            int end = ((Integer)breaks.elementAt(i+1)).intValue();
            if (oldEnd < start) {
                writeString(text.substring(oldEnd, start));
            }            
            boolean hasLetter = hasLetter(text, start, end);
            if (hasLetter) {
                openWord();
            }
            writeString(text.substring(start, end));
            if (hasLetter) {
                closeWord();
            }
            oldEnd = end;
        }
        writeString(text.substring(oldEnd));
    }
    
    private boolean hasLetter(String text, int start, int end) {
        for (int pos = start; pos < end; ++pos) {
            if (Character.isLetterOrDigit(text.codePointAt(pos))) {
                return true;
            }	
        }
        return false;
    }
    
    private void openWord() throws XMLStreamException {
        writeEvent(eventFactory.createStartElement(getBreakElement(), getBreakAttributes(), null));
    }
    
    private void closeWord() throws XMLStreamException {
        writeEvent(eventFactory.createEndElement(getBreakElement(), null));
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
        
    private boolean shouldBeProcessed(QName name) {
        //System.err.println("first: " + firstName + " " + firstIsStart);
        //System.err.println("last: " + lastName + " " + lastIsStart);
        if (!isPathAllowed()) {
            return false;
        }
        if (!breakSettings.mayContainText(name)) {
            return false;
        }        
        return true;
    }
    
    public void setContextStackFilter(ContextStack csf) {
        contextStack = csf;
    }
        
    /* *** TEST *** */
            
    public static void main(String[] args) throws FileNotFoundException, CatalogExceptionNotRecoverable, UnsupportedDocumentTypeException, XMLStreamException {
        XMLWordDetector detector = new XMLWordDetector(new File(args[0]), new File(args[1]));
        detector.detect(null);
    }
}
