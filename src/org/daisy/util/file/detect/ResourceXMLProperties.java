package org.daisy.util.file.detect;

import javax.xml.stream.events.StartElement;

/**
 *
 * @author Markus Gylling
 */
/*package*/ class ResourceXMLProperties extends ResourceProperties {

	private String mPublicId;
	private String mSystemId;
	private StartElement mStartElement;

	protected ResourceXMLProperties(String fileName, String pid, String sid, StartElement se) {
		super(fileName);
		mPublicId = pid;
		mSystemId = sid;
		mStartElement = se;
	}

	/*package*/ String getPublicId() {
		return mPublicId;
	}
	
	/*package*/ String getSystemId() {
		return mSystemId;
	}
	
	/*package*/ StartElement getRootElement() {
		return mStartElement;
	}
}
