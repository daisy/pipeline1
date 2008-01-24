package org.daisy.util.file.detect;

import java.util.List;

import javax.xml.stream.events.XMLEvent;

/**
 * 
 * @author Markus Gylling
 */
/*package*/ class XMLExtendedToken extends SignatureToken {
	private List<XMLEvent> mExtendedToken = null;
	
	/*package*/ XMLExtendedToken(List<XMLEvent> extendedToken) {		
		mExtendedToken = ResourceParser.trim(extendedToken);
		//System.err.println("creating extended token, size: " + mExtendedToken.size());
	}

	/*package*/ List<XMLEvent> getElementList() {
		return mExtendedToken;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("XML Extended:").append("\n");
		for(XMLEvent xe : mExtendedToken) {
			sb.append(xe.toString());	
		}
		sb.append("\n");		
		return sb.toString();
	}

}

