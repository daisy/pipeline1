package org.daisy.util.fileset;

import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986DtbookFileImpl extends XmlFileImpl implements Z3986DtbookFile {
	
	Z3986DtbookFileImpl(URI uri) throws ParserConfigurationException, SAXException {
		super(uri);
		parse();
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {
			String attrName = attrs.getQName(i).trim().intern();
			String attrValue = attrs.getValue(i).trim().intern();
			
			//check if its an ID and if so add
			if (attrName=="id") {
				myIDValues.add(attrValue);
				//check if uri and if so add	
			}else if (matches(Regex.getInstance().DTBOOK_ATTRIBUTES_WITH_URIS,attrName)) {
				if (!matches(Regex.getInstance().URI_REMOTE,attrValue)) {
					putLocalURI(attrValue);						   							
					URI uri = resolveURI(attrValue);					
					if (!uri.equals(cache)) { //optim only
						cache=uri;						
						Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
						if (o!=null) {
							//already added to listener fileset, so only put to local references collection
							putReferencedMember(uri, o);
						}else{
							try {  
								if (matches(Regex.getInstance().FILE_SMIL,attrValue)) {
									putReferencedMember(uri, new Z3986SmilFileImpl(uri));	
								}else if (matches(Regex.getInstance().FILE_IMAGE,attrValue)) {
									putReferencedMember(uri, new ImageFileImpl(uri));
								}
							} catch (Exception e) {
								throw new SAXException(e);
							} 							
						}
					} //(!uri.equals(cache)) 
				}else{			
					putRemoteURI(attrValue);					  
				}//(!attrValue.matches(RegexPatterns.URI_NONLOCAL))									
			} //if (attrName=="id") 							
		}//for (int i
	}//startElement
}
