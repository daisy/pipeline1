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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * Searches the content documents for IDs belonging inside skippable items.
 * @author Linus Ericson
 */
/*package*/ class ContentDocSkippableItemFinder {

    private StAXInputFactoryPool staxPool;
    private Map<String,Object> staxInProperties;
    
    private Set<String> pagenumIds;
    private Set<String> sidebarIds;
    private Set<String> prodnoteIds;
    
    private Stack<Pair<Set<String>,Integer>> skippableStack;
    
    public ContentDocSkippableItemFinder(StAXInputFactoryPool staxPool, Map<String,Object> staxInProperties) {
        this.staxPool = staxPool;
        this.staxInProperties = staxInProperties;
        this.pagenumIds = new HashSet<String>();
        this.sidebarIds = new HashSet<String>();
        this.prodnoteIds = new HashSet<String>();
        this.skippableStack = new Stack<Pair<Set<String>,Integer>>();
    }
    
    
    /**
     * Search the content documents for IDs belonging inside skippable items.
     * If several skippable elements are nested, the outermost one wins
     * (i.e. becomes skippable) unless the inner one is a pagenumber
     * wheich will always have precedence.
     * @param contentDoc the content document to process
     * @param skipPagenum search for pagenum IDs
     * @param skipSidebar search for sidebar IDs
     * @param skipProdnote search for prodnote IDs
     * @return a <code>SkippableContentsIds</code> object containing information about the
     *           skippable IDs.     *  
     * @throws XMLStreamException 
     * @throws FileNotFoundException 
     */
    public SkippableContentIds findSkippableItems(File contentDoc, boolean skipPagenum, boolean skipSidebar, boolean skipProdnote) throws XMLStreamException, FileNotFoundException {        
        XMLInputFactory xif = null;
        XMLEventReader reader = null;
        int level = 0;
        try {
            xif = staxPool.acquire(staxInProperties);            
            reader = xif.createXMLEventReader(new FileInputStream(contentDoc));
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                if (event.isStartElement()) {
                    level++;
                    StartElement se = event.asStartElement();
                    String elemName = se.getName().getLocalPart();
                    if ("span".equals(elemName) || "div".equals(elemName)) {
                        Attribute classAttr = se.getAttributeByName(new QName("class"));
                        if (classAttr != null) {
                            String attrValue = classAttr.getValue();
                            if ("page-front".equals(attrValue) || 
                                    "page-normal".equals(attrValue) || 
                                    "page-special".equals(attrValue)) {
                                // Push to the stack
                                if (skipPagenum) {
                                	skippableStack.push(new Pair<Set<String>,Integer>(pagenumIds, level));
                                }
                            } else if ("sidebar".equals(attrValue)) {
                                // Only push when the stack is empty
                                if (skippableStack.isEmpty() && skipSidebar) {
                                    skippableStack.push(new Pair<Set<String>,Integer>(sidebarIds, level));
                                }
                            } else if ("optional-prodnote".equals(attrValue)) {
                                // Only push when the stack is empty
                                if (skippableStack.isEmpty() && skipProdnote) {
                                    skippableStack.push(new Pair<Set<String>,Integer>(prodnoteIds, level));
                                }
                            }
                        }
                    }
                    // If an ID attribute is found and the stack is not emptey, this ID should be saved.
                    if (!skippableStack.isEmpty()) {
                        Attribute idAttr = se.getAttributeByName(new QName("id"));
                        if (idAttr != null) {
                            skippableStack.peek().getFirst().add(contentDoc.getName() + "#" + idAttr.getValue());
                        }
                    }
                } else if (event.isEndElement()) {
                    level--;
                    if (!skippableStack.isEmpty()) {
                        if (level < skippableStack.peek().getSecond()) {
                            skippableStack.pop();
                        }
                    }
                }
            }
            reader.close();
                        
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
            }
            staxPool.release(xif, staxInProperties);
        }
        return new SkippableContentIds(pagenumIds, sidebarIds, prodnoteIds);
    }
    
    
}
