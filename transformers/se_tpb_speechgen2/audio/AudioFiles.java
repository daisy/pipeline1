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
package se_tpb_speechgen2.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 * Audio file functions used by the SpeechGen2 transformer.
 * 
 * @author Martin Blomberg
 *
 */
public class AudioFiles {
	
	/**
	 * Returns the audio file format if the file exists, null
	 * otherwise.
	 * @param file the audio file.
	 * @return the audio file format if the file exists, null
	 * otherwise.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public static AudioFormat getAudioFormat(File file) throws UnsupportedAudioFileException, IOException {
		if (null == file) {
			return null;
		}
		
		if (!file.exists()) {
			return null;
		}
		
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		return aff.getFormat();
	}

	
	/**
	 * Returns the duration of the file, millis.
	 * @param file the audio file.
	 * @return the duration of the file in millis.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public static long getAudioFileDuration(File file) throws UnsupportedAudioFileException, IOException {
		if (null == file) {
			String msg = "null has no duration. file = " + file;
			throw new IllegalArgumentException(msg);
		}
		
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = aff.getFormat();
		// jpritchett@rfbd.org, 14 Nov 2008:  Changed casting to rounding to make more accurate
		//return (long)(1000.0 * aff.getFrameLength() / format.getFrameRate());
		return Math.round(1000.0 * aff.getFrameLength() / format.getFrameRate());
	}
	
	
	/**
	 * Writes <code>millis</code> milliseconds of silience to the file <code>target</code>,
	 * using the audio format <code>format</code>. 
	 * @param target the file to write the silence to.
	 * @param millis the duration of silence.
	 * @param format the audio format to use.
	 * @return the silent file.
	 * @throws IOException
	 */
	public static File getSilentAudio(File target, long millis, AudioFormat format) throws IOException {
		if (null == target) {
			String msg = "Impossible to use " + target + 
				" as destination for audio.";
			throw new IllegalArgumentException(msg);
		}
		
		if (millis < 0) {
			String msg = "Impossible to create audio with " +
					"duration " + millis + " millis.";
			throw new IllegalArgumentException(msg);
		} 
		
		SilenceAudioFile.writeSilentFile(target, millis, format);
		return target;
	}
	
	/**
	 * Writes <code>millis</code> milliseconds of silience to the file <code>target</code>,
	 * using the same audio format as in the file <code>model</code>. 
	 * @param target the file to write the silence to.
	 * @param millis the duration of silence.
	 * @param model the audio format to use.
	 * @return the silent file.
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public static File getSilentAudio(File target, long millis, File model) throws UnsupportedAudioFileException, IOException {
		if (null == model || !model.exists() || model.isDirectory()) {
			String msg = "Impossible to use " + model + 
				" as model for silent audio file.";
			throw new IllegalArgumentException(msg);
		}
		
		if (null == target) {
			String msg = "Impossible to use " + target + 
				" as destination for audio.";
			throw new IllegalArgumentException(msg);
		}
		
		if (millis < 0) {
			String msg = "Impossible to create audio with " +
					"duration " + millis + " millis.";
			throw new IllegalArgumentException(msg);
		} 
		
		// Make a silent audio file, duration: millis
		SilenceAudioFile.writeSilentFile(target, millis, model);		
		return target;
	}
}
