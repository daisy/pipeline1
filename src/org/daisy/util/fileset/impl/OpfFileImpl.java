package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.xml.OpfFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This singular implementation of the different Opf interfaces (zed, nimas, etc)
 * encapsulates all data and methods that the interfaces define.
 * @author Markus Gylling
 */

class OpfFileImpl extends XmlFileImpl implements OpfFile, Z3986OpfFile {
	private boolean inManifest = false;
	private boolean inSpine = false;
	private boolean inDcFormat = false;
	private boolean inDcTitle = false;
	private HashMap manifestSmilItems = new HashMap();
	private LinkedHashMap spineMap= new LinkedHashMap();
	private boolean finalSpineMapIsBuilt = false;
	private SmilClock statedDuration = null;
	private	String statedMultiMediaType = null;
	private	String statedDcFormat = null;
	private	String statedDcTitle = null;
	
	OpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {		
		super(uri,OpfFile.mimeStringConstant); 
	}
	
	OpfFileImpl(URI uri, String mimeStringConstant) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {		
		super(uri,mimeStringConstant); 
	}
	
	OpfFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {		
		super(uri, errh, OpfFile.mimeStringConstant); 
	}

	OpfFileImpl(URI uri, ErrorHandler errh, String mimeStringConstant) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {		
		super(uri, errh, mimeStringConstant); 
	}

	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//qName = qName.intern();
						
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
//			attrName = attrs.getQName(i).intern();
//			attrValue = attrs.getValue(i).intern();
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();
			
			try {
				if (sName=="meta") {
					if (attrValue=="dtb:totalTime") {
						statedDuration = new SmilClock(attrs.getValue("content"));
					}else if(attrValue=="dtb:multimediaType") {
						statedMultiMediaType = attrs.getValue("content");
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
		}else if (qName.toLowerCase().equals("dc:format")) {
			inDcFormat = false;
		}else if (qName.toLowerCase().equals("dc:title")) {
			inDcTitle = false;
		}

	}
	
    public void characters(char[] ch, int start, int length) throws SAXException {
    	if(inDcFormat){    	
    		statedDcFormat = String.copyValueOf(ch,start,length);
    	}else if(inDcTitle){    	
    		statedDcTitle = statedDcTitle + String.copyValueOf(ch,start,length);
    	}    	
    }
	
//	public Iterator getSpineIterator() { 
//		return spineMap.keySet().iterator();		
//	}
	
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
		return statedDuration;
	}

	public String getMetaDtbMultiMediaType() {
		return statedMultiMediaType;
	}
	
	public String getMetaDcFormat() {
		return statedDcFormat;		
	}	
	
	public String getMetaDcTitle() {
		return statedDcTitle;		
	}
	
//	public Collection getSpineValues(){
//		return this.spineMap.values();
//	}
	
}
