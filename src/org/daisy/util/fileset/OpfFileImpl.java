package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.xml.SmilClock;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Markus Gylling
 */

class OpfFileImpl extends XmlFileImpl implements OpfFile {
	private boolean inManifest = false;
	private boolean inSpine = false;
	private HashMap manifestSmilItems = new HashMap();
	private LinkedHashMap spineMap= new LinkedHashMap();
	private boolean finalSpineMapIsBuilt = false;
	private SmilClock myStatedDuration = null;
	
	OpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {		
		super(uri); 
	}
	
	OpfFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {		
		super(uri, errh); 
	}
	
	public String getMimeType() {
		return FilesetConstants.MIMETYPE_OPF;		
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
		
		if (qName=="spine") { 
			inSpine = true;
		} else if (qName=="manifest") {
			inManifest = true;
		}
		
		//assumes that spine always comes after manifest (which is a rule in the DTD)
		//TODO fix spine logic
		
		for (int i = 0; i < attrs.getLength(); i++) {
//			attrName = attrs.getQName(i).intern();
//			attrValue = attrs.getValue(i).intern();
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
			
			try {
				if (sName=="meta") {
					if (attrValue=="dtb:totalTime") {
						myStatedDuration = new SmilClock(attrs.getValue("content"));
					}
				}				
			} catch (Exception nfe) {
				this.listeningErrorHandler.error(new SAXParseException(this.getName()+": exception when calculating " +attrValue,null));
				
			}
			
			if (attrName=="id") {
				this.putIdAndQName(attrValue,qName);
			}
			
			if(inManifest && qName=="item") { 
				if(attrName=="href") {
					putUriValue(attrValue);										
					if (regex.matches(regex.FILE_SMIL,attrValue)) {
						if (!regex.matches(regex.URI_REMOTE,attrValue)) {
							//collect the id and uri for spinelist building
							String idval = attrs.getValue("id");										
							this.manifestSmilItems.put(idval, this.toURI().resolve(attrValue));
						}	
					}
				}	
			}
			
			else if(inSpine && qName=="itemref") {
				//now we've gone through manifest
				//and we have the local manifestSmilItems<idString>,<URI> map to match with 	
				if (attrName=="idref") {
					//get the uri from the local manifestsmilmap
					URI smilkey = (URI)manifestSmilItems.get(attrValue);
					//put it in the spineMap
					spineMap.put(smilkey, null);
				}
			}
		} //for (int i							
	}		
	
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if (qName.equals("spine")){
			inSpine = false;
		}else if (qName.equals("manifest")) {
			inManifest = false;
		}
	}
	
	public Iterator getSpineIterator() { 
		return spineMap.keySet().iterator();		
	}
	
	public Z3986SmilFile getSpineItem(URI uri) throws FilesetException { 
		if (finalSpineMapIsBuilt) {
			return (Z3986SmilFile)spineMap.get(uri);	
		}
		throw new FilesetException("spinemap is not built");	    
	}	
	
//	public Z3986SmilFile getSpineItem(URI uri, Fileset fileset) throws FilesetException { 
//		if (!finalSpineMapIsBuilt) {
//			buildSpineMap(fileset);
//		}
//		return (Z3986SmilFile)spineMap.get(uri);    	    
//	}	
	
	public Collection getSpineItems() throws FilesetException { 
		if (finalSpineMapIsBuilt) {
			return spineMap.values();	
		}
		throw new FilesetException("spinemap is not built");		    	    
	}
	
	public void buildSpineMap (Fileset fileset) {
		//this can be done only when a complete fileset is built
		LinkedHashMap finalSpineMap = new LinkedHashMap();
		Iterator it = spineMap.keySet().iterator();
		while(it.hasNext()) {
			URI smilkey = (URI)it.next();
			Z3986SmilFile smil = (Z3986SmilFile) fileset.getLocalMember(smilkey);
			finalSpineMap.put(smilkey, smil);		  
		}		
		spineMap.clear();
		spineMap.putAll(finalSpineMap);
		finalSpineMap = null;
		finalSpineMapIsBuilt = true;
	}

	public SmilClock getStatedDuration() {		
		return myStatedDuration;
	}


	
//	public Collection getSpineValues(){
//		return this.spineMap.values();
//	}
	
}
