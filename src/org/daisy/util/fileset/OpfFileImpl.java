package org.daisy.util.fileset;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

class OpfFileImpl extends XmlFileImpl implements OpfFile {
	private boolean inManifest = false;
	private boolean inSpine = false;
	private HashMap manifestSmilItems = new HashMap();
	private LinkedHashMap spineMap= new LinkedHashMap();

	
	OpfFileImpl(URI uri) throws ParserConfigurationException, SAXException {		
		super(uri); 
		parse();
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		qName = qName.intern();
		
		if (qName=="spine") { 
			inSpine = true;
		} else if (qName=="manifest") {
			inManifest = true;
		}
		
		//assumes that spine always comes after manifest (which is a rule in the DTD)
		
		for (int i = 0; i < attrs.getLength(); i++) {
			String attrName = attrs.getQName(i).trim().intern();
			String attrValue = attrs.getValue(i).trim().intern();
			
			//check if its an ID and if so add
			if (attrName=="id") {
				myIDValues.add(attrValue);
			}
			
			if(inManifest && qName=="item") { 
				if(attrName=="href") {
					if (!matches(Regex.getInstance().URI_REMOTE,attrValue)) {
						//manifest href references a local member
						putLocalURI(attrValue);						   							
						URI uri = resolveURI(attrValue);	
						if (!uri.equals(cache)) { //optim							
							cache=uri;
							Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
							if (o!=null) {
								//already added to listener fileset, so only put to local references collection
								putReferencedMember(uri, o);
							}else{
								try {  //dont use mime here since its too spec version dependent
									if (matches(Regex.getInstance().FILE_SMIL,attrValue)) {
										//collect the id and uri for spinelist building
										String idval = attrs.getValue("id");										
										this.manifestSmilItems.put(idval, uri);										  
										//then continue as always
										putReferencedMember(uri, new Z3986SmilFileImpl(uri));	
									}else if (matches(Regex.getInstance().FILE_NCX,attrValue)) {
										putReferencedMember(uri, new Z3986NcxFileImpl(uri));	
									}else if (matches(Regex.getInstance().FILE_CSS,attrValue)) {
										putReferencedMember(uri, new CssFileImpl(uri));
									}else if (matches(Regex.getInstance().FILE_RESOURCE,attrValue)) {
										putReferencedMember(uri, new Z3986ResourceFileImpl(uri));	
									}else if (matches(Regex.getInstance().FILE_IMAGE,attrValue)) {
										putReferencedMember(uri, new ImageFileImpl(uri));
									}else if (matches(Regex.getInstance().FILE_MP3,attrValue)) {										
										putReferencedMember(uri, new Mp3FileImpl(uri));
									//let other files build dtbook during the recursion since the filename is not required to be .xml
									//}else if (matches(RegexPatterns2.getInstance().FILE_DTBOOK,attrValue)) {
									//	putReferencedMember(uri, new DtbookFile(uri));
									}																								
								} catch (Exception e) {
									throw new SAXException(e);
								} 
							}
						}//(!uri.equals(cache)
					}else{			
						putRemoteURI(attrValue);					  
					}//(!attrValue.matches(RegexPatterns.URI_NONLOCAL)) {					
				}				
			}//if(qName=="item")
			else if(inSpine && qName=="itemref") {
			  //now we've gone through manifest, all smil files are in Fileset main list
			  //and we have the local manifestSmilItems<idString>,<URI> map to match with 	
			  if (attrName=="idref") {
			  	//get the uri from the local map
			  	URI smilkey = (URI)manifestSmilItems.get(attrValue);
			  	//retrieve the object from the main fileset map 
			  	Z3986SmilFile smil = (Z3986SmilFile) FilesetObserver.getInstance().getCurrentListener().getLocalMember(smilkey);
			  	//put it in the spineMap
			  	spineMap.put(smilkey, smil);
			  }
			}
		} //for (int i							
	}		
			
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName=="spine") inSpine = false;
		if (qName=="manifest") inManifest = false;
	}

	public Iterator getSpineIterator() {
      return spineMap.keySet().iterator();		
	}

	public Z3986SmilFile getSpineItem(URI uri) {
		return (Z3986SmilFile)spineMap.get(uri);
	}	
}
