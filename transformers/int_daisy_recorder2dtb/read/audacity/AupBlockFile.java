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

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.audio.AudioUtils;

/**
 * Read the audacity blockfiles with our specialized AuReader (little endian and fixes).
 * @author Markus Gylling
 */
public class AupBlockFile  {
	private File mFile;
	private AudioFileFormat mAudioFileFormat;
	private TransformerDelegateListener mListener;    
         
	public AupBlockFile(File file, String statedLength, String statedRate, TransformerDelegateListener listener) throws IOException, UnsupportedAudioFileException {
		mFile = file;
		this.mListener = listener;
							
		AupBlockFileReader reader = new AupBlockFileReader(statedRate);		
		mAudioFileFormat = reader.getAudioFileFormat(mFile);		
   		
		int stated = Integer.parseInt(statedLength);
		int calced = mAudioFileFormat.getFrameLength();
		if (stated!= calced) {
			try{
				String msg = "Mismatch in stated and calculated file length:";
				msg += file + ": " + mAudioFileFormat.toString();
				msg += " stated length: " + Integer.parseInt(statedLength) + ": " + AudioUtils.framesToMillis(stated, mAudioFileFormat);
				msg += " calcd length: " + mAudioFileFormat.getFrameLength() + ": " + AudioUtils.framesToMillis(calced, mAudioFileFormat);			
				mListener.delegateMessage(this, msg, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT, null);
			}catch (Exception e) {

			}
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
