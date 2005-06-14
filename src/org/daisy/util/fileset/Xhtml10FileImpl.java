package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

abstract class Xhtml10FileImpl extends XmlFileImpl implements TextualContentFile, Xhtml10File {
	
	Xhtml10FileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri); 
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {		
		
		for (int i = 0; i < attrs.getLength(); i++) {
			String attrName = attrs.getQName(i).trim().intern();
			String attrValue = attrs.getValue(i).trim().intern();	
			
			//check if its an ID and if so add
			if (attrName=="id") {
				myIDValues.add(attrValue);
			}
					
			if (matches(Regex.getInstance().XHTML_ELEMENTS_WITH_URI_ATTRS,qName)) {
				if (matches(Regex.getInstance().XHTML_ATTRS_WITH_URIS,attrName)) {
					if (!matches(Regex.getInstance().URI_REMOTE,attrValue)) {
						putLocalURI(attrValue);
						if (matches(Regex.getInstance().URI_WITH_FRAGMENT,attrValue)) {
							attrValue = attrValue.replace(attrValue.substring(attrValue.indexOf("#")),"");
						}
						URI uri = resolveURI(attrValue);
						if (!uri.equals(cache)) {
							cache=uri;								
							Object o = FilesetObserver.getInstance().getCurrentListener().getLocalMember(uri); 
							if (o!=null) {
								//already added to listener fileset, so only put to local references collection
								putReferencedMember(uri, o);
							}else{
								try {
									if (matches(Regex.getInstance().FILE_SMIL,attrValue)) {
										if(FilesetObserver.getInstance().getCurrentListener().getFileSetType()==FilesetType.DAISY_202) {
										  putReferencedMember(uri, new D202SmilFileImpl(uri));
										}else{
										  //is abstract;	
                                          //putReferencedMember(uri, new SmilFileImpl(uri));	
										}
									} else if (matches(Regex.getInstance().FILE_CSS,attrValue)) {
										putReferencedMember(uri, new CssFileImpl(uri));
									} else if ((attrName=="src")&&(qName.equals("img"))) {
										putReferencedMember(uri, new ImageFileImpl(uri));	
									} else {
										FilesetObserver.getInstance().errorEvent(new FilesetExceptionRecoverable("ungroked pattern: " +qName+"::"+attrs.getQName(i)+"::"+attrs.getValue(i)+ " in " + this.getName()));										
									}
								} catch (Exception e) {
									throw new SAXException(e);
								} 
							}
						}//if (!uri.equals(cache))
					}else{ 			
						putRemoteURI(attrValue);					  
					}//(!attrValue.matches(RegexPatterns.URI_NONLOCAL)) {
				}//(attrName=="href"||attrName=="src") {
			}//qName.matches (RegexPatterns.XHTML_ELEMENTS_WITH_URI_ATTRS))  									
		}//for (int i 		
	} //public void startElement		
}
