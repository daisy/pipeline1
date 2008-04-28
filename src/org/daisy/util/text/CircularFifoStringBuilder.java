/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
