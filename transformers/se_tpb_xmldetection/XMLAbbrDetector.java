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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
public class XMLAbbrDetector extends XMLWordDetector {

    private ContextAwareAbbrSettings caas = null;
    
    /**
     * @param inFile
     * @param outFile
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public XMLAbbrDetector(File inFile, File outFile) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        this(inFile, outFile, null, false);
    }
    
    /**
     * @param inFile
     * @param outFile
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public XMLAbbrDetector(File inFile, File outFile, URL customLang, boolean override) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        super(inFile, outFile);
        Set xmllang = LangDetector.getXMLLangSet(inFile);
        caas = new ContextAwareAbbrSettings(); 
        setBreakSettings(caas);
        setContextStackFilter(caas);
        setBreakFinder(new DefaultAbbrBreakFinder(xmllang, customLang, override));        
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
        for (Iterator it = breaks.iterator(); it.hasNext(); ) {
            Abbr abbr = (Abbr)it.next();
            int start = abbr.getStart();
            int end = abbr.getEnd();
            if (oldEnd < start) {
                writeString(text.substring(oldEnd, start));
            }
            String exp = abbr.getExpansion(); 
            switch (abbr.getType()) {
            case Abbr.INITIALISM:
                writeEvent(eventFactory.createStartElement(caas.getInitialismElement(), getBreakAttributes(caas.getInitialismAttributes(), caas.getInitialismExpandAttribute(), exp), null));
            	writeString(text.substring(start, end));
            	writeEvent(eventFactory.createEndElement(caas.getInitialismElement(), null));            	
                break;
            case Abbr.ACRONYM:
                writeEvent(eventFactory.createStartElement(caas.getAcronymElement(), getBreakAttributes(caas.getAcronymAttributes(), caas.getAcronymExpandAttribute(), exp), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getAcronymElement(), null));
                break;
            case Abbr.ABBREVIATION:
                writeEvent(eventFactory.createStartElement(caas.getAbbrElement(), getBreakAttributes(caas.getAbbrAttributes(), caas.getAbbrExpandAttribute(), exp), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getAbbrElement(), null));
                break;
            case Abbr.FIX:
                writeEvent(eventFactory.createStartElement(caas.getFixElement(), getBreakAttributes(caas.getFixAttributes(), caas.getFixExpandAttribute(), exp), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getFixElement(), null));
                break;
            }
            oldEnd = end;
        }        
        writeString(text.substring(oldEnd));
    }
    
    private Iterator getBreakAttributes(Map map, String att, String exp) {
        Iterator result = null;
        Vector v = new Vector();
        if (map != null) {
            for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
                String name = (String)it.next();
                String value = (String)map.get(name);
                Attribute attr = eventFactory.createAttribute(name, value);
                v.add(attr);
            }
            result = v.iterator();
        }
        if (att != null && exp != null) {
            Attribute attr = eventFactory.createAttribute(att, exp);
            v.add(attr);
            result = v.iterator();
        }
        return result;
    }
    
    public static void main(String[] args) throws CatalogExceptionNotRecoverable, UnsupportedDocumentTypeException, FileNotFoundException, XMLStreamException {
        XMLAbbrDetector detector;
        LogManager lm = LogManager.getLogManager();
        Logger lg = lm.getLogger("");
        Handler[] handlers = lg.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            lg.removeHandler(handlers[i]);
        }
        try {
            Handler newHandler = new FileHandler("AbbrDetection.log");
            newHandler.setFormatter(new Formatter(){
                public String format(LogRecord record) {
                    return record.getLevel() + ": " + record.getMessage() + "\n";
                }});
            lg.addHandler(newHandler);
            detector = new XMLAbbrDetector(new File(args[0]), new File(args[1]));
            detector.detect(null);
        } catch (CatalogExceptionNotRecoverable e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
}
