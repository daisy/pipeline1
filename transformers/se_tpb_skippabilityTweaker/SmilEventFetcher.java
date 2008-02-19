/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
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
package se_tpb_skippabilityTweaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/**
 * 
 * @author Linus Ericson
 *
 */
/*package*/ class SmilEventFetcher {
	
    private static final String indent = "\t\t\t\t\t\t\t\t";
    
	private StAXInputFactoryPool staxInPool;
    private StAXOutputFactoryPool staxOutPool;
    private StAXEventFactoryPool staxEvPool;
    private Map<String,Object> staxInProperties;
    private Map<String,Object> staxOutProperties;
    private boolean delete;
    
    // These will be reset at each run
    private XMLOutputFactory xof;
    private XMLEventFactory xef;
    private XMLEventReader reader;
    private XMLEventWriter writer = null;
    private List<XMLEvent> outputQueue;
    private boolean queueOutput = false;
    private int level= 0;
    private int order = 0;
    
    private Set<String> idValues = null;
    
    private String activeId = null;
    
    private Map<String, List<XMLEvent>> result = new HashMap<String, List<XMLEvent>>();
    
	/**
	 * @param staxInPool
	 * @param staxInProperties
	 * @param staxOutPool
	 * @param staxOutProperties
	 * @param staxEvPool
	 */
	public SmilEventFetcher(StAXInputFactoryPool staxInPool,
			Map<String, Object> staxInProperties,
			StAXOutputFactoryPool staxOutPool,
			Map<String, Object> staxOutProperties,
			StAXEventFactoryPool staxEvPool,
            boolean delete) {
		this.staxInPool = staxInPool;
		this.staxInProperties = staxInProperties;
		this.staxOutPool = staxOutPool;
		this.staxOutProperties = staxOutProperties;
		this.staxEvPool = staxEvPool;
        this.delete = delete;
	}
    
    public Map<String, List<XMLEvent>> fetch(File smil, Set<String> idValues, File output) {
    	
    	queueOutput = false;
    	outputQueue = new LinkedList<XMLEvent>();
        activeId = null;
    	
    	this.idValues = idValues;
    	
    	XMLInputFactory xif = null; 
        OutputStream os = null;
    	try {
            xif = staxInPool.acquire(staxInProperties);
            xof = staxOutPool.acquire(staxOutProperties);
            xef = staxEvPool.acquire();
            
            reader = xif.createXMLEventReader(new FileInputStream(smil));
            os = new FileOutputStream(output);
            
            //System.err.println("Processing: " + smil + " to " + output);
            
            this.run(os);
            
        } catch (FileNotFoundException e) {
            // FIXME
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (XMLStreamException e) {
            // FIXME
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {                
            }
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (XMLStreamException e) {                
            }
            staxInPool.release(xif,staxInProperties);
            staxOutPool.release(xof, staxOutProperties);
            staxEvPool.release(xef);
        }
        // FIXME
    	return result;
    }
    
    private void run(OutputStream stream) throws XMLStreamException {        
        boolean rootElementSeen = false;
        boolean textSeen = false;
        level = 0;
        int skip = 0;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            XMLEvent writeEvent = null;
            switch (event.getEventType()) {            
            case XMLStreamConstants.START_DOCUMENT:
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    writer = xof.createXMLEventWriter(stream, sd.getCharacterEncodingScheme());
                    writeEvent = event;
                } else {
                    writer = xof.createXMLEventWriter(stream, "utf-8");
                    writeEvent = xef.createStartDocument("utf-8", "1.0");
                }
                break;
            case XMLStreamConstants.START_ELEMENT:                
                if (!rootElementSeen && !textSeen) {
                    writer.add(xef.createCharacters("\n"));
                    rootElementSeen = true;
                }
                writeEvent = this.startElement((StartElement)event);
                level++;
                if (writeEvent == null) {
                    skip = level;
                }                
                break;
            case XMLStreamConstants.END_ELEMENT:
                level--;
                writeEvent = this.endElement((EndElement)event);
                break;
            default:
                writeEvent = event;
            }
            if (skip > 0) {
                while (reader.hasNext() && skip != 0) {
                    event = reader.nextEvent();
                    if (event.isStartElement()) {
                        level++;
                    } else if (event.isEndElement()) {
                        level--;
                    }
                    if (level < skip) {
                        skip = 0;
                    }
                }
            } else if (writeEvent != null) {
                if (queueOutput) {
                    //System.err.println("queueing: " + writeEvent);
                    outputQueue.add(writeEvent);
                } else {
                    //System.err.println("normal write" + writeEvent);
                    write(writeEvent);
                }
            }
        }
        //writer.close();
    }
    
    private StartElement startElement(StartElement se) {
        Attribute att = se.getAttributeByName(new QName("id"));
        if (att != null) {
            if (idValues.contains(att.getValue())) {
            	// We have found an ID that we wanted to save. We are at a "par"
            	// or "text" element at this moment, and we want to save the events
            	// from the "par"
                activeId = att.getValue();
            }
        }
        if ("par".equals(se.getName().getLocalPart())) {
            //System.err.println("par start found. queueing");
            queueOutput = true;
        }
        return se;
    }
    
    private EndElement endElement(EndElement ee) throws XMLStreamException {        
        if ("par".equals(ee.getName().getLocalPart())) {
            queueOutput = false;
            //System.err.println("par end found. flushing");
            if (activeId != null) {                
                List<XMLEvent> eventList = new ArrayList<XMLEvent>();
                eventList.addAll(outputQueue);
                eventList.add(ee);
                result.put(activeId, eventList);
                activeId = null;
                if (delete) {
                    outputQueue.clear();
                    //System.err.println("XXX cut XXX");
                    return null;
                } else {
                    //System.err.println("XXX copy XXX");
                    this.flushOutput();
                    outputQueue.clear();
                }
            } else {
                this.flushOutput();
                outputQueue.clear();
            }
        }
        return ee;
    }
    
    private void flushOutput() throws XMLStreamException {
        for (XMLEvent event : outputQueue) {            
            //System.err.println("flush: " + event);
            write(event);
        }
    }
    
    private void write(XMLEvent event) throws XMLStreamException {        
        switch(event.getEventType()) {
        case XMLStreamConstants.START_ELEMENT:
            order++;
            //System.err.print(indent.substring(0, level));
            //System.err.println("<" + event.asStartElement().getName().getLocalPart() + level + "> " + order);
            break;
        case XMLStreamConstants.END_ELEMENT:
            order++;
            //System.err.print(indent.substring(0, level+1));
            //System.err.println("</" + event.asEndElement().getName().getLocalPart() + (level+1) +"> " + order);
            break;
        case XMLStreamConstants.CHARACTERS:
            //System.err.println(event.asCharacters().getData());
            break;
        }
        writer.add(event);
    }

}
