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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;

/*package*/ class SmilEventInjector {

	private StAXInputFactoryPool staxInPool;
    private StAXOutputFactoryPool staxOutPool;
    private StAXEventFactoryPool staxEvPool;
    private Map<String,Object> staxInProperties;
    private Map<String,Object> staxOutProperties;
    
    private String session = null;
    private int counter = 1;
    
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
    private boolean idOnText = false;
    private Map<String,Pair<String,List<XMLEvent>>> smilNoterefIdXMLEventMap;
    //private String contentBodyRef;
    private Map<String,String> contentBodyRefSmilBodyIdMap = null;
    //private int injectcount = 0;
    private String smilPrefix = null;
	
	public SmilEventInjector(StAXInputFactoryPool staxInPool,
			Map<String, Object> staxInProperties,
			StAXOutputFactoryPool staxOutPool,
			Map<String, Object> staxOutProperties,
			StAXEventFactoryPool staxEvPool) {
		this.staxInPool = staxInPool;
		this.staxInProperties = staxInProperties;
		this.staxOutPool = staxOutPool;
		this.staxOutProperties = staxOutProperties;
		this.staxEvPool = staxEvPool;
        Random random = new Random(System.currentTimeMillis());
        session = Long.toHexString(random.nextLong());
	}
	
	public Map<String,String> injectSmilEvents(File smil, File output, 
            Map<String,Pair<String,List<XMLEvent>>> smilNoterefIdXMLEventMap/*, String contentBodyRef*/) {
    	
    	queueOutput = false;
    	outputQueue = new LinkedList<XMLEvent>();
        activeId = null;
        this.smilNoterefIdXMLEventMap = smilNoterefIdXMLEventMap;
        //this.contentBodyRef = contentBodyRef;
        this.contentBodyRefSmilBodyIdMap = new HashMap<String,String>();
        this.smilPrefix = output.getName() + "#";
    	
    	this.idValues = smilNoterefIdXMLEventMap.keySet();
    	
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
        return contentBodyRefSmilBodyIdMap;
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
                idOnText = "text".equals(se.getName().getLocalPart());
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
                //System.err.println("Time to inject! " + (++injectcount));
                // So what events do we want to write?
                //  - First we need the <seq> wrapping the note reference and the note body.
                //  - Then we should have the noteref <par>, that one should be waiting in the
                //    output queue right now.
                //  - After that there is the note body par. We get those events from the Map
                //    supplied in the constructor.
                //  - Finally we have the end </seq>
                outputQueue = getInjectedContent(); 
                this.flushOutput();
                outputQueue.clear();
                activeId = null;
                return null;
            }
			this.flushOutput();
			outputQueue.clear();
        }
        return ee;
    }
    
    private List<XMLEvent> getInjectedContent() {
        String contentBodyRef = smilNoterefIdXMLEventMap.get(activeId).getFirst(); 
    	List<XMLEvent> result = new ArrayList<XMLEvent>();
    	result.add(createComment(" Begin nested block "));
    	result.add(createText("\n"));
    	result.add(createStartElement("seq", null));    	
    	result.add(createText("\n"));
    	
    	// Add the noteref
    	result.addAll(outputQueue);
    	// Add missing end par
    	result.add(createEndElement("par"));
        result.add(createText("\n"));
        
        result.add(createComment(" note body "));
        result.add(createText("\n"));
        
        List<Attribute> attrs = new ArrayList<Attribute>();
        attrs.add(createAttribute("endsync", "last"));
        attrs.add(createAttribute("system-required", "footnote-on"));
        String parId = "par_" + session + "_" + counter;
        attrs.add(createAttribute("id", parId));
        counter++;
        if (!idOnText) {            
            contentBodyRefSmilBodyIdMap.put(contentBodyRef, smilPrefix + parId);            
        }
    	result.add(createStartElement("par", attrs));
        result.add(createText("\n"));
        
        String textId = "text_" + session + "_" + counter;
        if (idOnText) {
            contentBodyRefSmilBodyIdMap.put(contentBodyRef, smilPrefix + textId);
        }
        attrs = new ArrayList<Attribute>();
        attrs.add(createAttribute("src", contentBodyRef));
        attrs.add(createAttribute("id", textId));
        counter++;
        result.add(createStartElement("text", attrs));
        result.add(createEndElement("text"));
        result.add(createText("\n"));
        result.add(createStartElement("seq", null));
        result.add(createText("\n"));
        
        // Add all autdio elements
        result.addAll(getAudioEvents());
        
        result.add(createEndElement("seq"));
        result.add(createText("\n"));
        
        
        result.add(createEndElement("par"));
        result.add(createText("\n"));
        
    	result.add(createEndElement("seq"));
        result.add(createText("\n"));
    	result.add(createComment(" End nested block "));
    	return result;
    }
    
    private List<XMLEvent> getAudioEvents() {
        List<XMLEvent> eventList = new ArrayList<XMLEvent>();
        for (XMLEvent event : smilNoterefIdXMLEventMap.get(activeId).getSecond()) {
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("audio".equals(se.getName().getLocalPart())) {
                    List<Attribute> attrs = new ArrayList<Attribute>();
                    attrs.add(se.getAttributeByName(new QName("src")));
                    attrs.add(se.getAttributeByName(new QName("clip-begin")));
                    attrs.add(se.getAttributeByName(new QName("clip-end")));
                    attrs.add(createAttribute("id", "audio_" + session + "_" + counter));
                    counter++;
                    eventList.add(createStartElement("audio", attrs));
                    eventList.add(createEndElement("audio"));
                    eventList.add(createText("\n"));
                }
            }
        }
        return eventList;
    }
    
    private StartElement createStartElement(String name, List<Attribute> attrs) {
    	Iterator<Attribute> attrItr = null;
    	if (attrs != null) {
    		attrItr = attrs.iterator();
    	}
    	return xef.createStartElement(new QName(name), attrItr, null);
    }
    
    private EndElement createEndElement(String name) {
    	return xef.createEndElement(new QName(name), null);
    }
    
    private Characters createText(String text) {
    	return xef.createSpace(text);
    }
    
    private Comment createComment(String text) {
    	return xef.createComment(text);
    }
    
    private Attribute createAttribute(String name, String value) {
        return xef.createAttribute(name, value);
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
