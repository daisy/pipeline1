/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package se_tpb_speechgen2.tts;

import java.io.File;


/**
 * The general view of the tts output.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSOutput {
	private File file;
	private int number = -1;
	private long duration = -1;
	
	/**
	 * @param f the file in which this object's audio is stored.
	 * @param nr the sequence number from the document.
	 * @param dur the duration of the generated audio.
	 */
	public TTSOutput(File f, int nr, long dur) {
		file = f;
		number = nr;
		duration = dur;
	}
	
	/**
	 * Returns the file in which the output represented by this object is stored.
	 * @return the file in which the output represented by this object is stored.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the duration of the generated audio.
	 * @return the duration of the generated audio.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Returns the number from the sequence in which this output is supposed to
	 * be inserted into the document.
	 * @return the number from the sequence in which this output is supposed to
	 * be inserted into the document.
	 */
	public int getNumber() {
		return number;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TTSOutput: #" + number + ":" + duration + " @" + file.getAbsolutePath();
	}
}
