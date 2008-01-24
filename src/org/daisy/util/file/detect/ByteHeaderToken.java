package org.daisy.util.file.detect;


/**
 * @author Markus Gylling
 */
/*package*/ class ByteHeaderToken extends SignatureToken  {
	private byte[] mByteArray = null;
	
	/*package*/ ByteHeaderToken(byte[] bb) {
		mByteArray=bb;
	}

	/*package*/ byte[] getByteArray() {
		return mByteArray;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Byte array: ");
		for (int i = 0; i < mByteArray.length; i++) {
			sb.append(Integer.toHexString(Byte.valueOf((mByteArray[i])).intValue()));
			sb.append(" ");
		} 		
		sb.append("\n");
		return sb.toString();
	}
	
}
