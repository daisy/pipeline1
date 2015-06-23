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
package org.daisy.util.dtb.ncxonly.model.write.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.daisy.util.audio.AudioUtils;
import org.daisy.util.audio.SequenceAudioInputStream;
import org.daisy.util.dtb.ncxonly.model.AudioClip;
import org.daisy.util.dtb.ncxonly.model.Item;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILBuilder;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILFile;
import org.daisy.util.file.Directory;

/**
 * Create cleaned-up PCM Wave files, one for each SMIL file. 
 * @author Markus Gylling
 */
public class WAVBuilder {

	public WAVBuilder(SMILBuilder smilBuilder, Directory destination) throws IOException {
		for (int i = 0; i < smilBuilder.getSpine().size(); i++) {
			File wavDest= new File(destination,getFileName(i+1));
			SMILFile smilObject = smilBuilder.getSpine().get(i);
			AudioFormat outputAudioFormat = null;	
			
			double elapsedTimeSeconds = 0;
			List<AudioInputStream> inputStreams = new LinkedList<AudioInputStream>();
			
			long prevClipsEndFrame = -1;
			File prevClipsFile = null;

			
			for(Iterator<Item> iter  = smilObject.iterator(); iter.hasNext();) {
				Item item = iter.next();
				for(AudioClip clip :item.getAudioClips()) {
					
					AudioFormat af = clip.getAudioFormat();
					//we use the output audioformat of the first clip
					//and hope for the best if it changes between clips
					if(outputAudioFormat==null)outputAudioFormat=af;
					
					FileInputStream fis = new FileInputStream(clip.getFile());					
					long fileFrameLength = clip.getAudioFileFormat().getFrameLength();
					
					AudioInputStream ais = new AudioInputStream(fis,af,fileFrameLength);
					long clipStartFrame = AudioUtils.secondsToFrames(clip.getStartSeconds(), af); 
					long clipEndFrame = AudioUtils.secondsToFrames(clip.getEndSeconds(), af); 
					
					/*
					 * Adjust for 1 frame overlap if this is the case
					 */					
					if(prevClipsFile !=null && prevClipsFile==clip.getFile()){
						//we are not in the first clip
						if(prevClipsEndFrame==clipStartFrame) {
							clipStartFrame++;
						}
						if(prevClipsEndFrame>clipStartFrame) {
							throw new IllegalStateException("prevClipsEndFrame>clipStartFrame");
						}
					}
					
					long clipFrameLength = clipEndFrame-clipStartFrame; 
					int frameSize = ais.getFormat().getFrameSize();
										
					prevClipsEndFrame = clipEndFrame;
					prevClipsFile = clip.getFile();
					
					/*
					 * Skip to the clips start position in file
					 */
					ais.skip(getHeaderSize(clip.getAudioFileFormat(), clip.getFile()));
					ais.skip(clipStartFrame * frameSize);					
					inputStreams.add(new AudioInputStream(ais,
							ais.getFormat(), clipFrameLength));
										
					/*
					 * Set the clock values and ref to the new united file
					 */
					clip.setFile(wavDest);
					double end = elapsedTimeSeconds + clip.getDurationSeconds();
					clip.setStartSeconds(elapsedTimeSeconds);					
					clip.setEndSeconds(end);
					elapsedTimeSeconds = end;					
				}				
			}
						
			//render all inputstreams for this SMIL file			
			SequenceAudioInputStream seqis = new SequenceAudioInputStream(outputAudioFormat, inputStreams);			
			AudioSystem.write(seqis, AudioFileFormat.Type.WAVE, wavDest);
			//or, use tritonus
//			WaveAudioFileWriter writer = new WaveAudioFileWriter();
//			writer.write(seqis, AudioFileFormat.Type.WAVE, wavDest);
			
			try{
				seqis.close();
			}catch (Exception e) {
				e.printStackTrace();
			}	

									
		} //for (int i = 0; i < smilBuilder.getSpine().size(); i++)
	}

	private long getHeaderSize(AudioFileFormat aff, File file) {
		return file.length() - (aff.getFrameLength() * aff.getFormat().getFrameSize());
	}

	private String getFileName(int i) {
		return "wave" + i + ".wav";		
	}
	
}
