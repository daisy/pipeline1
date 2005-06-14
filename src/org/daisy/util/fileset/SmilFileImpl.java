package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
abstract class SmilFileImpl extends XmlFileImpl implements SmilFile{
	
	SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);		
	}

	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {        		
		qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {			
			String attrName = attrs.getQName(i).trim().intern();
			String attrValue = attrs.getValue(i).trim().intern();
			
			//check if its an ID and if so add
			if (attrName=="id") {
				myIDValues.add(attrValue);
			}
			//now check if there are references to other files etc
			if(matches(Regex.getInstance().SMIL_ELEMENTS_WITH_URI_ATTRS,qName)){
				if(matches(Regex.getInstance().SMIL_ATTRIBUTES_WITH_URI_ATTRS,attrName)){
					if (!matches(Regex.getInstance().URI_REMOTE,attrValue)) {
						putLocalURI(attrValue);
						if (attrName=="src") { 
							if (qName == "text"){
								attrValue = attrValue.replace(attrValue.substring(attrValue.indexOf("#")),"");
							}                    
							URI uri = resolveURI(attrValue);
							if (!uri.equals(cache)) { //optim only
								cache=uri;
								Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
								if (o!=null) {
									//already added to listener fileset, so only put to local references collection
									putReferencedMember(uri, o);
								}else{    
									try {
										if (qName == "audio") {
											//if (attrValue.matches(RegexPatterns.FILE_MP3)) {
											if (matches(Regex.getInstance().FILE_MP3,attrValue)) {
												putReferencedMember(uri, new Mp3FileImpl(uri));
											}else{
												FilesetObserver.getInstance().errorEvent(this.toURI(), new FilesetException("audio file format not yet supported"));
											}
										}else if (qName == "text") {
											if ((FilesetObserver.getInstance().getCurrentListener().getFileSetType() == FilesetType.DAISY_202)&&(matches(Regex.getInstance().FILE_XHTML,attrValue))) {
												putReferencedMember(uri, new D202TextualContentFileImpl(uri));
											} else if(FilesetObserver.getInstance().getCurrentListener().getFileSetType() == FilesetType.Z3986) {
												putReferencedMember(uri, new Z3986DtbookFileImpl(uri));
											} else {
												FilesetObserver.getInstance().errorEvent(this.toURI(), new FilesetException("textual content file format not recognized"));
											}
										}else if (qName == "img") {
											putReferencedMember(uri, new ImageFileImpl(uri));
										}                         
									} catch (Exception e) {
										throw new SAXException(e);
									}
								}
							} //(!uri.equals(cache))
						}//attrName=="src"
						
						else if ((attrName=="href") && (qName=="a")) {
							//TODO handle smil anchors
						}
						
					}//!attrValue.matches(RegexPatterns.ONLINE_URI
					else {
						putRemoteURI(attrValue);
					}//!attrValue.matches(RegexPatterns.URI_NONLOCAL
				}//matches(Regex.getInstance().SMIL_ATTRIBUTES_WITH_URI_ATTRS
			} //qName.matches(RegexPatterns.SMIL_ELEMENTS_WITH_URI_ATTRS))												
		}//for int i		
	}//startElement
	
}