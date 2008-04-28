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
package int_daisy_recorder2dtb.read.audacity;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.util.audio.AudioUtils;

/**
 * 
 * @author Markus Gylling
 */
public class AupBlockFile  {
	private File mFile;
	private AudioFileFormat mAudioFileFormat;    
         
	public AupBlockFile(File file, String statedLength) throws IOException, UnsupportedAudioFileException {
		mFile = file;
		
		/*
		 * Read the audacity blockfiles with our specialized AuReader (little endian and fixes).
		 */						
		AupBlockFileReader reader = new AupBlockFileReader();		
		mAudioFileFormat = reader.getAudioFileFormat(mFile);				
		//AudioFormat format = mAudioFileFormat.getFormat();   
		
		int stated = Integer.parseInt(statedLength);
		int calced = mAudioFileFormat.getFrameLength();
		if (stated!= calced) {
			System.err.println("Mismatch in stated and calculated file length:");
			System.err.println(file + ": " + mAudioFileFormat.toString());
			System.err.println("stated length: " + Integer.parseInt(statedLength) + ": " + AudioUtils.framesToMillis(stated, mAudioFileFormat));
			System.err.println("calcd length: " + mAudioFileFormat.getFrameLength() + ": " + AudioUtils.framesToMillis(calced, mAudioFileFormat));
			
		}
		//debug
//		WaveAudioFileWriter writer = new WaveAudioFileWriter();
//		File wavDest = new File("D:\\audacity-out\\block-convert-"+ file.getName() +".wav");
//		FileInputStream fis = new FileInputStream(file);				
//		long fileFrameLength = mAudioFileFormat.getFrameLength();		
//		AudioInputStream ais = new AudioInputStream(fis,format,fileFrameLength);
//		ais.skip(24716);
//		writer.write(ais, AudioFileFormat.Type.WAVE, wavDest);
		//end debug
		
	}
	
	
	public double getDurationSeconds() {
		return mAudioFileFormat.getFrameLength() / mAudioFileFormat.getFormat().getFrameRate();		
	}
	
	public File getFile() {
		return mFile;
	}
	
	public AudioFileFormat getAudioFileFormat() {
		return mAudioFileFormat;
	}
	
	@Override
	public String toString() {
		return mFile.getName() + ": " + mAudioFileFormat.toString();
	}


}
