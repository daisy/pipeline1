package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class D202MasterSmilFileImpl extends SmilFileImpl implements D202MasterSmilFile {
	private boolean inBody = false;
	
	public D202MasterSmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		parse();
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {        		
		if(inBody){
			for (int i = 0; i < attrs.getLength(); i++) {			
				String attrName = attrs.getQName(i).trim().intern();
				String attrValue = attrs.getValue(i).trim().intern();
				
				//check if its an ID and if so add
				if (attrName=="id") {
					myIDValues.add(attrValue);
				}else if (attrName=="src") {
					putLocalURI(attrValue);
					URI uri = resolveURI(attrValue);
					Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
					if (o!=null) {	
						//already added to listener fileset, so only put to local references collection
						putReferencedMember(uri, o);
					}else{    
						try {
							putReferencedMember(uri, new D202SmilFileImpl(uri));                         
						} catch (Exception e) {
							throw new SAXException(e);
						}
					}
				}			
			}//for int i
		}
	}//startElement
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("head")) inBody = true;
	}
}
