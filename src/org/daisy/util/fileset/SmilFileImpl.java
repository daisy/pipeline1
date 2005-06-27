package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Markus Gylling
 */

public class SmilFileImpl extends XmlFileImpl implements SmilFile {
	private SmilClock myStatedDuration = null;
	private SmilClock myStatedTotalElapsedTime = null;	
	private SmilClock audioClipBegin = null; //does not gather anything outside startelement; put here for optim
	private SmilClock audioClipEnd = null; //does not gather anything outside startelement; put here for optim
	private long myCalculatedDuration;
	//private static boolean doSmilTiming = true;
	
	SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
	}
	
	public SmilFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
		super(uri, errh);		
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		//sName = sName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {
//			attrName = attrs.getQName(i).intern();
//			attrValue = attrs.getValue(i).intern();			
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern();	//for some reason		

			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			}else if(Regex.getInstance().matches(Regex.getInstance().SMIL_ATTRIBUTES_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}	
			
			//if (doSmilTiming) {
				try {
					if (sName=="meta") {
						if (attrValue=="ncc:timeInThisSmil") {
							myStatedDuration = new SmilClock(attrs.getValue("content"));
						}else if (attrValue=="ncc:totalElapsedTime"||attrValue=="dtb:totalElapsedTime") {
							myStatedTotalElapsedTime = new SmilClock(attrs.getValue("content"));
						}
					}				
					if (sName == "audio") {
						//collect the audio element values, and check them later outside for loop						
						if((attrName=="clip-begin"||attrName=="clipBegin")) {
							audioClipBegin = new SmilClock(attrValue);				
						}else if((attrName=="clip-end"||attrName=="clipEnd")) {
							audioClipEnd = new SmilClock(attrValue);		
						}									
					}
				} catch (Exception nfe) {
					this.listeningErrorHandler.error(new SAXParseException(this.getName()+": exception when calculating " +attrValue,null));
					
				}
			//}
		}
		//all attributes of this element have now been looped through
		//test the audio dur stuff
		//if (doSmilTiming) {
			if (sName == "audio") {
				if(audioClipBegin!=null&&audioClipEnd!=null){ //TODO optimize
					//means we had a standard dtb audio element
					myCalculatedDuration += (audioClipEnd.millisecondsValue() - audioClipBegin.millisecondsValue());				
					//reset
					audioClipBegin=null; audioClipEnd=null; 
				}
			}
		//}
	}
	
	
	public SmilClock getCalculatedDuration() {
		return new SmilClock(myCalculatedDuration);
	}
	
	public SmilClock getStatedDuration() {				
		return myStatedDuration;
	}
	
	public SmilClock getStatedTotalElapsedTime() {
		return myStatedTotalElapsedTime;
	}
	
	public long getCalculatedDurationMillis() {
		return myCalculatedDuration;
	}
}