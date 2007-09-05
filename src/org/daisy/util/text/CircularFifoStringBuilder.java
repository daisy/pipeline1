package org.daisy.util.text;

/**
 * A circular FIFO string builder.
 * @author Markus Gylling
 */
public class CircularFifoStringBuilder implements CharSequence {

	private StringBuilder mInnerBuilder = null;
	private int mMaxSize = 128;
	
	/**
	 * Constructor.
	 * @param maxSize The fixed sixe of this buffer.
	 */
	public CircularFifoStringBuilder(int maxSize) {
		mInnerBuilder = new StringBuilder(maxSize);
		mMaxSize = maxSize;
	}
	
    public CircularFifoStringBuilder append(char c) {
        mInnerBuilder.append(c);
        checkSize();
        return this;
    }
	
	private void checkSize() {
		while(mInnerBuilder.length()>mMaxSize 
				&& mInnerBuilder.length()> 0) {
			mInnerBuilder.deleteCharAt(0);
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mInnerBuilder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public char charAt(int index) {
		return mInnerBuilder.charAt(index);		
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.CharSequence#length()
	 */
	public int length() {		
		return mInnerBuilder.length();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int start, int end) {		
		return mInnerBuilder.subSequence(start, end);
	}

}
