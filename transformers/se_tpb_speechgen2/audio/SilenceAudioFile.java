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
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Linus Ericson
 */
public class SilenceAudioFile {

	public static void writeSilentFile(File outputFile, long durationInMillis, AudioFormat format) throws IOException {
        AudioInputStream ais = new SilenceAudioInputStream(format, durationInMillis);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
    }
	
    public static void writeSilentFile(File outputFile, long durationInMillis, File model) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(model);
        AudioFormat format = aff.getFormat();
        AudioInputStream ais = new SilenceAudioInputStream(format, durationInMillis);
        AudioSystem.write(ais, aff.getType(), outputFile);
    }
    
    public static void writeSilentOutputStream(OutputStream outStream, long durationInMillis, InputStream model) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(model);
        AudioFormat format = aff.getFormat();
        AudioInputStream ais = new SilenceAudioInputStream(format, durationInMillis);
        AudioSystem.write(ais, aff.getType(), outStream);
    }
    
    public static void main(String args[]) throws UnsupportedAudioFileException, IOException {
        File out = new File("d:/test.wav");
        File in = new File("U:/testbok1/fileset/speechgen00009.wav");
        SilenceAudioFile.writeSilentFile(out, 15748, in);
    }
}
