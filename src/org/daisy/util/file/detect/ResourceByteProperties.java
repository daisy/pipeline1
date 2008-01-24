package org.daisy.util.file.detect;

/**
 *
 * @author Markus Gylling
 */
/*package*/ class ResourceByteProperties extends ResourceProperties {
	
	private byte[] mByteBuffer = null;
	
	protected ResourceByteProperties(String fileName, byte[] bb) {
		super(fileName);
		mByteBuffer = bb;		
	}

	/*package*/ byte[] getByteBuffer() {
		return mByteBuffer;
	}
}
