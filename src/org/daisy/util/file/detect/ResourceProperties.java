package org.daisy.util.file.detect;

/**
 * Represent properties of a resource to be detected.
 * @author Markus Gylling
 */

/*package*/ abstract class ResourceProperties {

	private String mFileName = null;
	
	protected ResourceProperties(String fileName) {
		mFileName = fileName;
	}
	
	/*package*/ String getFileName() {
		return mFileName;
	}
}
