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