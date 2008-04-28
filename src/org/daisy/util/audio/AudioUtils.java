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
package org.daisy.util.audio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class AudioUtils {

	public static long framesToMillis(long frames, AudioFileFormat format) {		
		return Math.round(1000.0 * frames / format.getFormat().getFrameRate());		
	}
	
	public static double framesToSeconds(long frames, AudioFileFormat format) {		
		return frames / format.getFormat().getFrameRate();		
	}
	
	public static long millisToFrames(long ms, AudioFormat format) {
		return millisToFrames(ms, format.getFrameRate());
	}

	public static long millisToFrames(long ms, float frameRate) {
		return (long) (ms*frameRate/1000);
	}
	
	public static long secondsToFrames(double seconds, AudioFormat format) {
		return secondsToFrames(seconds, format.getFrameRate());
	}

	public static long secondsToFrames(double seconds, float frameRate) {
		return (long)(seconds*frameRate);
	}
	
}