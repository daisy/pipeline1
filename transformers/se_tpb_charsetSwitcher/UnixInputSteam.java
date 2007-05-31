/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2007  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_charsetSwitcher;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream that will automatically convert mac or dos line breaks
 * to unix line breaks.
 * @author Linus Ericson
 */
class UnixInputSteam extends InputStream {

	private int mCurrent = -1;
	private int mNext = -1;
	private InputStream mIs;
	
	public UnixInputSteam(InputStream is) {
		mIs = is;
	}
	
	public int read() throws IOException {
		if (mNext != -1) {       // a buffered int was read last iteration
	         mCurrent = mNext;
	         mNext = -1;
	      } else {                // else read an int from the stream
	         mCurrent = mIs.read();
	      }

	      if (mCurrent == 13) {
	         mNext = mIs.read();
	         if (mNext == 10) {    // encounter "\r\n" transform to "\n"
	            mNext = -1; 
	         }
	         mCurrent = 10;  
	      } 

	      return mCurrent;
	}

	@Override
	public void close() throws IOException {
		mIs.close();
		super.close();
	}

}
