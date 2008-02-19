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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * Searches for navigation points in the NCC.
 * @author Linus Ericson
 */
/*package*/ class NccItemFinder {

    private boolean skipPagenum;
    private boolean skipSidebar;
    private boolean skipProdnote;
    private boolean skipFootnote;
    
    private StAXInputFactoryPool staxInPool;
    private Map<String,Object> staxInProperties;
    
    public NccItemFinder(StAXInputFactoryPool staxInPool, Map<String,Object> staxInProperties,
            boolean skipPagenum, boolean skipSidebar, boolean skipProdnote, boolean skipFootnote) {
        this.staxInPool = staxInPool;
        this.staxInProperties = staxInProperties;
        this.skipPagenum = skipPagenum;
        this.skipSidebar = skipSidebar;
        this.skipProdnote = skipProdnote;
        this.skipFootnote = skipFootnote;
    }

    public List<NccItem> getNccItemList(File input) {      
        List<NccItem> nccItems = new ArrayList<NccItem>(); 
        
        XMLInputFactory xif = null;
        XMLEventReader reader = null;
        try {
            xif = staxInPool.acquire(staxInProperties);
            reader = xif.createXMLEventReader(new FileInputStream(input));
            
            this.forwardTo(reader, "body");
            
            NccItem nccItem = NccItem.getNccItem(reader);
            while (nccItem != null) { 
                boolean add = false;
                switch (nccItem.getType()) {
                    case PAGE_FRONT:
                    case PAGE_NORMAL:
                    case PAGE_SPECIAL:
                        add = skipPagenum;
                        break;
                    case SIDEBAR:
                        add = skipSidebar;
                        break;
                    case OPTIONAL_PRODNOTE:
                        add = skipProdnote;
                        break;
                    case NOTEREF:
                        add = skipFootnote;
                        break;
                    default:
                        add = false;
                    break;
                }
                if (add) {
                    nccItems.add(nccItem);            
                }
                nccItem = NccItem.getNccItem(reader);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {                
            }
            staxInPool.release(xif, staxInProperties);
        }
        
        return nccItems;
    }
    
    private void forwardTo(XMLEventReader reader, String localName) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if (localName.equals(se.getName().getLocalPart())) {
                    return;
                }
            }
        }
        throw new XMLStreamException("Start tag " + localName + " not found.");
    }


    
}
