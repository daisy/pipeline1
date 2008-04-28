/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.OpfFile;
import org.daisy.util.fileset.UIDCarrier;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This base implementation backing the different Opf interfaces (opf, zed, nimas, etc)
 * encapsulates all data collection that the interfaces require. Only getters appear
 * in subclasses.
 * @author Markus Gylling
 */

class OpfFileImpl extends XmlFileImpl implements OpfFile, UIDCarrier {
	private boolean inManifest = false;
	private boolean inSpine = false;
	private boolean inDcFormat = false;
	private boolean inDcTitle = false;
	private boolean inUidDcIdentifier = false;
	private HashMap<String,URI> manifestItems = new HashMap<String,URI>();
	private LinkedHashMap<URI,FilesetFile> spineMap= new LinkedHashMap<URI,FilesetFile>();
	private boolean finalSpineMapIsBuilt = false;
	protected SmilClock statedDuration = null;
	protected String statedMultiMediaType = null;
	private	String statedDcFormat = null;
	private	String statedDcTitle = null;
	private String uidRootIdRef = null;  //the idref at root element
	private String statedUid = null;		//the actul uid
	
	OpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {		
		super(uri,OpfFile.mimeStringConstant); 
	}
	
	OpfFileImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {		
		super(uri,mimeStringConstant); 
	}
		
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		super.startElement(namespaceURI, sName, qName, attrs);						
		if (qName=="spine") { 
			inSpine = true;
		} else if (qName=="manifest") {
			inManifest = true;
		}
		
		if (qName.toLowerCase().equals("dc:format")) {
			inDcFormat = true;
		}else if (qName.toLowerCase().equals("dc:title")){
			inDcTitle = true;
		}
		
		//assumes that spine always comes after manifest (which is a rule in the DTD)
		
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
			
			try {
				if(sName=="package"){
					if (attrName=="unique-identifier") {
						//catch the root idref to a certain dc:identifier
						uidRootIdRef = attrValue;
					}
				} else if (sName=="meta") {
					if (attrValue=="dtb:totalTime") {
						statedDuration = new SmilClock(attrs.getValue("content"));
					}else if(attrValue=="dtb:multimediaType") {
						statedMultiMediaType = attrs.getValue("content");
					}										
				}
			} catch (Exception nfe) {				
				myExceptions.add(new FilesetFileErrorException(this,nfe));						
			}
			
			if (attrName=="id") {
				QName q = new QName(namespaceURI,sName);
				this.putIdAndQName(attrValue,q);	
				if(qName.toLowerCase().equals("dc:identifier") && attrValue == uidRootIdRef){
					inUidDcIdentifier = true;
				}
			}
			
			if(inManifest && qName=="item") { 
				if(attrName=="href") {
					putUriValue(attrValue);										
					if (!regex.matches(regex.URI_REMOTE,attrValue)) {
						//collect the id and uri for spinelist building
						String idval = attrs.getValue("id");										
						this.manifestItems.put(idval, this.toURI().resolve(attrValue));
					}						
				}	
			}
			
			else if(inSpine && qName=="itemref") {
				//now we've gone through manifest
				//and we have the local manifestSmilItems<idString>,<URI> map to match with 	
				if (attrName=="idref") {
					//get the uri from the local manifestsmilmap
					URI key = manifestItems.get(attrValue);
					//put it in the spineMap
					spineMap.put(key, null);
				}
			}
		} //for (int i							
	}		
	
	@SuppressWarnings("unused")
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if (qName=="spine"){
			inSpine = false;
		}else if (qName=="manifest") {
			inManifest = false;
		}else if (qName.toLowerCase().equals("dc:format")) {
			inDcFormat = false;
		}else if (qName.toLowerCase().equals("dc:title")) {
			inDcTitle = false;
		}else if(qName.toLowerCase().equals("dc:identifier") && (inUidDcIdentifier) ){
			inUidDcIdentifier = false;
		}	

	}
	
	@SuppressWarnings("unused")
    public void characters(char[] ch, int start, int length) throws SAXException {
    	if(inDcFormat){    	
    		statedDcFormat = String.copyValueOf(ch,start,length);
    	}else if(inDcTitle){    	
    		statedDcTitle += String.copyValueOf(ch,start,length);
    	}else if(inUidDcIdentifier){
    		statedUid += String.copyValueOf(ch,start,length);
    	}
    }
		
	public Collection<FilesetFile> getSpineItems() throws IllegalStateException { 
		if (finalSpineMapIsBuilt) {
			return spineMap.values();	
		}
		throw new IllegalStateException("spinemap is not built");		    	    
	}
	
	/*package*/ void buildSpineMap (Fileset fileset) {
		//this can be done only when a complete fileset is built
		LinkedHashMap<URI,FilesetFile> tempSpineMap = new LinkedHashMap<URI,FilesetFile>();
		Iterator<URI> it = spineMap.keySet().iterator();
		while(it.hasNext()) {
			URI key = it.next();
			FilesetFile ff = fileset.getLocalMember(key);
			if (ff!=null){
				tempSpineMap.put(key,fileset.getLocalMember(key));
			}	
		}		
		spineMap.clear();
		spineMap.putAll(tempSpineMap);
		tempSpineMap = null;
		finalSpineMapIsBuilt = true;
	}
	
	public String getMetaDcFormat() {
		return statedDcFormat;		
	}	
	
	public String getMetaDcTitle() {
		return statedDcTitle;		
	}
		
	private static final long serialVersionUID = 5561960060601196098L;

	public String getUID() {
		return statedUid;
	}
}