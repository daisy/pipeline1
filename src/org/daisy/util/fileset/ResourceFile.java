package org.daisy.util.fileset;

import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class ResourceFile extends XMLFile {

	public ResourceFile(URI uri) throws ParserConfigurationException, SAXException {
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
			
			if(matches(Regex.getInstance().RESOURCE_ELEMENTS_WITH_URI_ATTRS,qName)) { 
				if(attrName=="src") {
					if (!matches(Regex.getInstance().URI_REMOTE,attrValue)) {
						//content src references a local member
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
									if (matches(Regex.getInstance().FILE_MP3,attrValue)) { 
										putReferencedMember(uri, new MP3File(uri));
									}else if (matches(Regex.getInstance().FILE_IMAGE,attrValue)) {
										putReferencedMember(uri, new ImageFile(uri));
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
		} //for (int i							
	}
}
