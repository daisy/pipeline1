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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
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
import org.daisy.util.xml.stax.BookmarkedXMLEventReader;

/**
 * Add the simple skippability (pagenum, sidebar, prodnote) 
 * @author Linus Ericson
 */
/*package*/ class SimpleSkippabilityAdder {
    
    // Basic algorithm description:
    //  - Input contains a Map between a SMIL id and description of a skippable
    //    item. The id resides on the par element or on the text element.
    //  - Start queueing the output whenever a start par tag is found.
    //  - Flush the output queue when the end par tag is found.
    //  - If an id matching a skippable item is found, write the corresponding
    //    system-required attribute on the par when flushing the output.
    //  - The content doc reference for the pars representing notrefs are
    //    also saved since these are needed later on when adding footnote
    //    skippability.

    private StAXInputFactoryPool staxInPool;
    private StAXOutputFactoryPool staxOutPool;
    private StAXEventFactoryPool staxEvPool;
    private Map<String,Object> staxInProperties;
    private Map<String,Object> staxOutProperties;
    
    // These will be reset each time addSimpleSkippabiity is called
    private BookmarkedXMLEventReader reader = null;
    private Map<String,NccItem> nccItemMap;
    private XMLOutputFactory xof;
    private XMLEventFactory xef;
    private List<XMLEvent> outputQueue;
    private boolean queueOutput = false;
    private XMLEventWriter writer = null;
    private Attribute sysReqAttr = null;
    private Map<String, String> idrefContentHrefMap;
    private String getSrcOfNextText = null;
    private SkippableContentIds skippableContentIds = null;
    //private File input = null;
    
    public SimpleSkippabilityAdder(StAXInputFactoryPool staxPool, Map<String,Object> staxInProperties,
            StAXOutputFactoryPool staxOutPool, Map<String,Object> staxOutProperties,
            StAXEventFactoryPool staxEvPool) {
        this.staxInPool = staxPool;
        this.staxInProperties = staxInProperties;
        this.staxOutPool = staxOutPool;
        this.staxOutProperties = staxOutProperties;
        this.staxEvPool = staxEvPool;
    }
    
    /**
     * Add simple skippability (pagenum, siderbar, prodnote).
     * 
     * @param input the SMIL file to add skippability in
     * @param output the outputfile
     * @param map a Map between IDs of skippable items the the corresponding skippable item information
     * @return a map between the IDs of noterefs and the corresponding link to the content doc 
     */
    public Map<String,String> addSimpleSkippability(File input, File output, Map<String,NccItem> map, SkippableContentIds skippableContentIds) {        
        queueOutput = false;
        XMLInputFactory xif = null; 
        OutputStream os = null;
        outputQueue = new LinkedList<XMLEvent>();
        sysReqAttr = null;
        idrefContentHrefMap = new HashMap<String,String>();
        getSrcOfNextText = null;
        this.skippableContentIds = skippableContentIds;
        //this.input = input;
        
        try {
            xif = staxInPool.acquire(staxInProperties);
            xof = staxOutPool.acquire(staxOutProperties);
            xef = staxEvPool.acquire();
            
            reader = new BookmarkedXMLEventReader(xif.createXMLEventReader(new FileInputStream(input)));
            os = new FileOutputStream(output);
            nccItemMap = map;
            
            this.run(os);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
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
            staxInPool.release(xif,staxInProperties);
            staxOutPool.release(xof, staxOutProperties);
        }
        return idrefContentHrefMap;
    }
    
    private void run(OutputStream stream) throws XMLStreamException {        
        boolean rootElementSeen = false;
        boolean textSeen = false;
        int level = 0;
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
                    writer.add(writeEvent);
                }
            }
        }
        writer.close();
    }
    
    private StartElement startElement(StartElement se) {        
        if ("text".equals(se.getName().getLocalPart())) {
            Attribute srcAtt = se.getAttributeByName(new QName("src"));
            if (srcAtt != null) {
                if (skippableContentIds.getPagenumIds().contains(srcAtt.getValue())) {
                    sysReqAttr = xef.createAttribute("system-required", "pagenumber-on");
                } else if (skippableContentIds.getSidebarIds().contains(srcAtt.getValue())) {
                    sysReqAttr = xef.createAttribute("system-required", "sidebar-on");
                } else if (skippableContentIds.getProdnoteIds().contains(srcAtt.getValue())) {
                    sysReqAttr = xef.createAttribute("system-required", "prodnote-on");
                }
            }
        }
        Attribute att = se.getAttributeByName(new QName("id"));
        if (att != null) {
            if (nccItemMap.containsKey(att.getValue())) {
                //System.err.println("ID: " + att.getValue() + " found.");
                switch (nccItemMap.get(att.getValue()).getType()) {
                /*
                case PAGE_FRONT:
                case PAGE_NORMAL:
                case PAGE_SPECIAL:
                    sysReqAttr = xef.createAttribute("system-required", "pagenumber-on");
                    break;
                case SIDEBAR:
                    sysReqAttr = xef.createAttribute("system-required", "sidebar-on");
                    break;
                case OPTIONAL_PRODNOTE:
                    sysReqAttr = xef.createAttribute("system-required", "prodnote-on");
                    break;
                */
                case NOTEREF:
                    if (sysReqAttr != null) {
                        // Another skippable item is active, let's not add any skippability
                        // to this note reference then.
                        break;
                    }
                    if ("text".equals(se.getName().getLocalPart())) {
                        Attribute srcAtt = se.getAttributeByName(new QName("src"));
                        if (srcAtt != null) {
                            idrefContentHrefMap.put(att.getValue(), srcAtt.getValue());
                            //System.err.println("Content href: " + srcAtt.getValue());
                        }
                    } else {
                        getSrcOfNextText = att.getValue();
                    }
                    break;
                default:
                    break;
                }
            }
        }        
        
        if ("par".equals(se.getName().getLocalPart())) {
            //System.err.println("par start found. queueing");
            queueOutput = true;
        } else if ("text".equals(se.getName().getLocalPart()) && getSrcOfNextText != null) {
            Attribute srcAtt = se.getAttributeByName(new QName("src"));
            if (srcAtt != null) {
                idrefContentHrefMap.put(getSrcOfNextText, srcAtt.getValue());
                //System.err.println("Content href: " + srcAtt.getValue());
            }
        }
        return se;
    }
    
    private EndElement endElement(EndElement ee) throws XMLStreamException {
        if ("par".equals(ee.getName().getLocalPart())) {
            //System.err.println("par end found. flushing");
            this.flushOutput();
            queueOutput = false;
        }
        return ee;
    }
    
    private void flushOutput() throws XMLStreamException {
        for (XMLEvent event : outputQueue) {
            if (sysReqAttr != null) {
                StartElement se = (StartElement)event;
                List<Attribute> attrs = new ArrayList<Attribute>();
                for (Iterator<?> attIt = se.getAttributes(); attIt.hasNext(); ) {
                    Attribute att = (Attribute)attIt.next();
                    if (!"system-required".equals(att.getName())) {
                        attrs.add(att);
                    }
                }
                attrs.add(sysReqAttr);
                sysReqAttr = null;
                event = xef.createStartElement(se.getName(), attrs.iterator(), se.getNamespaces());
            }
            //System.err.println("flush: " + event);
            writer.add(event);
        }
        outputQueue.clear();
    }
}
