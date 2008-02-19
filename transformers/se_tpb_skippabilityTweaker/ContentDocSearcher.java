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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXInputFactoryPool;

/**
 * Find the SMIL references in note bodies.
 * @author Linus Ericson
 */
/*package*/ class ContentDocSearcher {

    private StAXInputFactoryPool staxPool;
    private Map<String,Object> staxInProperties;
    
    public ContentDocSearcher(StAXInputFactoryPool staxPool, Map<String,Object> staxInProperties) {
        this.staxPool = staxPool;
        this.staxInProperties = staxInProperties;
    }
    
    /**
     * Find the SMIL references in note bodies.
     * 
     * This is just a wrapper for the method below in order to support multiple content
     * documents. This function will just apply the search funtion below on each content
     * doc.
     * 
     * @param contentNoterefIds
     * @return
     */
    public Map<File, Map<String,NoterefNotebodySmilInfo>> search(Map<File, Set<String>> contentNoterefIds) {
        Map<File, Map<String,NoterefNotebodySmilInfo>> result = new HashMap<File, Map<String, NoterefNotebodySmilInfo>>();
              
        for (File file : contentNoterefIds.keySet()) {
            //Map<String, List<String>> contentNoterefIdMap = this.search(file, contentNoterefIds.get(file));
            //result.put(file, contentNoterefIdMap);
            
            // Find the bodyrefs from the noterefs IDs
            Map<FileAndFragment,List<String>> bodyrefNoterefMap = this.findBodyrefs(file, contentNoterefIds.get(file));
            
            Map<String, Map<FileAndFragment,List<String>>> fileBodyrefNoterefMap = new HashMap<String, Map<FileAndFragment,List<String>>>();
            for (FileAndFragment faf : bodyrefNoterefMap.keySet()) {
                if (!fileBodyrefNoterefMap.containsKey(faf.getFile())) {
                    fileBodyrefNoterefMap.put(faf.getFile(), new HashMap<FileAndFragment,List<String>>());
                }                             
                fileBodyrefNoterefMap.get(faf.getFile()).put(faf, bodyrefNoterefMap.get(faf));
            }
            
            for (String contentDoc : fileBodyrefNoterefMap.keySet()) {
                File contentFile = new File(file.getParentFile(), contentDoc);
                if (!result.containsKey(contentFile)) {
                    result.put(file, new HashMap<String,NoterefNotebodySmilInfo>());
                }                
                Map<FileAndFragment,List<String>> brefNrefMap = fileBodyrefNoterefMap.get(contentDoc);
                result.get(contentFile).putAll(this.findNotebodySmilRef(contentFile, brefNrefMap));
                
            }
            
            
        }
        
        return result;
    }
    
    /**
     * Search for notebody SMIL references. 
     * @param contentDoc the content document to search for notebody SMIL references in
     * @param bodyrefNoterefMap a Map between a bodyref and a noteref ID
     * @return a Map between a noteref ID and the corresponding <code>NoterefNotebodySmilInfo</code>
     */
    private Map<String, NoterefNotebodySmilInfo> findNotebodySmilRef(File contentDoc, Map<FileAndFragment,List<String>> bodyrefNoterefMap) {
        Map<String, NoterefNotebodySmilInfo> result = new HashMap<String, NoterefNotebodySmilInfo>();
        
        XMLInputFactory xif = null;
        XMLEventReader reader = null;
        NoterefNotebodySmilInfo notebodySmilInfo = null;
        int recordLevel = 0;
        try {
            xif = staxPool.acquire(staxInProperties);            
            reader = xif.createXMLEventReader(new FileInputStream(contentDoc));
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                if (event.isStartElement()) {
                    recordLevel++;
                    StartElement se = (StartElement)event;
                    
                    Attribute idAttr = se.getAttributeByName(new QName("id"));
                    if (idAttr != null) {
                        FileAndFragment faf = new FileAndFragment(contentDoc.getName(), idAttr.getValue(), contentDoc.getName());
                        if (bodyrefNoterefMap.containsKey(faf)) {
                            //System.err.println("note body " + idAttr.getValue() + " found.");
                            // We have found a note body. Time to start recording all smil references we see.
                            notebodySmilInfo = new NoterefNotebodySmilInfo(bodyrefNoterefMap.get(faf), idAttr.getValue());
                            recordLevel = 0;
                        }
                    }
                    
                    if (notebodySmilInfo != null && "a".equals(se.getName().getLocalPart())) {
                        // We are in recording mode and just saw an 'a' element. Interseting...
                        Attribute hrefAttr = se.getAttributeByName(new QName("href"));
                        if (hrefAttr != null && hrefAttr.getValue().toLowerCase().contains(".smil")) {
                            // We have found a SMIL link. Yay!
                        	FileAndFragment faf = new FileAndFragment(hrefAttr.getValue(), null);
                            //notebodySmilInfo.addSmilRef(hrefAttr.getValue());
                            notebodySmilInfo.addSmilRef(faf);
                        }
                    }
                } else if (event.isEndElement()) {
                    recordLevel--;
                    if (notebodySmilInfo != null && recordLevel == -1) {
                        // The note body just closed
                        //result.put(notebodySmilInfo.getNoterefId(), notebodySmilInfo);
                        for (String noterefId : notebodySmilInfo.getNoterefIds()) {
                        	result.put(noterefId, notebodySmilInfo);
                        }
                        notebodySmilInfo = null;
                    }
                }
            }
            reader.close();
                        
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
            }
            staxPool.release(xif, staxInProperties);
        }
        
        return result;
    }
    
    private Map<FileAndFragment,List<String>> findBodyrefs(File contentDoc, Set<String> noterefIds) {
        Map<FileAndFragment,List<String>> bodyrefNoterefMap = new HashMap<FileAndFragment,List<String>>();
        XMLInputFactory xif = null;
        XMLEventReader reader = null;
        try {
            xif = staxPool.acquire(staxInProperties);            
            reader = xif.createXMLEventReader(new FileInputStream(contentDoc));
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                
                if (event.isStartElement()) {
                    StartElement se = (StartElement)event;
                    Attribute idAttr = se.getAttributeByName(new QName("id"));
                    if (idAttr != null && noterefIds.contains(idAttr.getValue())) {
                        // Id is matching. Do we have a bodyref?
                        Attribute bodyrefAttr = se.getAttributeByName(new QName("bodyref"));
                        if (bodyrefAttr != null) {
                            FileAndFragment faf = new FileAndFragment(bodyrefAttr.getValue(), contentDoc.getName());
                            if (!bodyrefNoterefMap.containsKey(faf)) {
                            	bodyrefNoterefMap.put(faf, new ArrayList<String>());
                            }
                            bodyrefNoterefMap.get(faf).add(idAttr.getValue());
                            
                            noterefIds.remove(idAttr.getValue());
                            //System.err.println("Bodyref " + bodyrefAttr.getValue() + " found.");
                        } else {
                            System.err.println("WARNING: Missing bodyref on id=" + idAttr.getValue() + " " + contentDoc);
                        }
                    }
                }
            }
            reader.close();
            
            if (!noterefIds.isEmpty()) {
                System.err.println("WARNING: There are " + noterefIds.size() + " content doc IDs missing.");
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
            staxPool.release(xif, staxInProperties);
        }
        return bodyrefNoterefMap;
    }
    
}
