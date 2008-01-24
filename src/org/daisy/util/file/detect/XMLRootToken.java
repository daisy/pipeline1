package org.daisy.util.file.detect;

import javax.xml.stream.events.StartElement;

/**
 * 
 * @author Markus Gylling
 */
/*package*/ class XMLRootToken extends SignatureToken {
	private StartElement mRootElement = null;
	private String mPublicID = null;
	private String mSystemID = null;
	
	/*package*/ XMLRootToken(StartElement root, String publicID, String systemID) {
		mRootElement=root;
		mPublicID = publicID;
		mSystemID = systemID;
	}

	/*package*/ StartElement getRootElement() {
		return mRootElement;
	}

	/*package*/ String getPublicID() {
		return mPublicID;
	}

	/*package*/ String getSystemID() {
		return mSystemID;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("XML").append("\n");
		sb.append("Public ID: ").append(mPublicID).append("\n");
		sb.append("System ID: ").append(mSystemID).append("\n");
		sb.append("Root StartElement: ").append(mRootElement.toString()).append("\n");		
		return sb.toString();
	}

}

