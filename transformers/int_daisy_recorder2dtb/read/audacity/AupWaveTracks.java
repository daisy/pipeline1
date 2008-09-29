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


import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.util.dtb.ncxonly.model.AudioClip;

/**
 * An AupWaveTrack consists of a list of AupAudioFile
 * @author Markus Gylling
 */
public class AupWaveTracks extends LinkedList<AupWaveTrack> {
		
	private TransformerDelegateListener mListener;

	public AupWaveTracks(TransformerDelegateListener listener) {
		super();
		this.mListener = listener;		
	}

	/**
	 * Get the duration of all wave tracks, expressed in seconds
	 */
	public double getDuration() {
		double s = 0;
		for(AupWaveTrack track : this) {
			s += track.getDurationSeconds();
		}		
		return s; 
	}
		
	/**
	 * Retrieve the start time of inparam track vis-a-vis the
	 * presentation as a whole, expressed in seconds
	 */
	public double getStartTime(AupWaveTrack waveTrack) {
		double s = 0;
		for(AupWaveTrack track : this) {
			if(track == waveTrack) {
				break;
			}
			s += track.getDurationSeconds();			
		}		
		return s;
	}
		
	/**
	 * Retrieve the end time of inparam track vis-a-vis the
	 * presentation as a whole, expressed in seconds
	 */
	public double getEndTime(AupWaveTrack waveTrack) {
		double s = 0;
		for(AupWaveTrack track : this) {
			s += track.getDurationSeconds();
			if(track == waveTrack) {
				break;
			}
		}		
		return s;
	}
	
	
	/**
	 * Retrieve the time in seconds that has elapsed until the onset of 
	 * inparam track, vis-a-vis the presentation as a whole
	 */
	public double getElapsedTime(AupWaveTrack waveTrack) {
		double s = 0;
		for(AupWaveTrack track : this) {
			if(track == waveTrack) {
				break;
			}
			s += track.getDurationSeconds();			
		}		
		return s;
	}
	
	
	/**
	 * Retrieve the time in seconds that has elapsed until the onset of 
	 * inparam audioFile, vis-a-vis the presentation as a whole
	 */
	public double getElapsedTime(AupBlockFile file) {
		double s = 0;
		boolean foundFile = false;
		for(AupWaveTrack track : this) {
			for(AupBlockFile aaf : track) {
				if(aaf==file) {
					foundFile = true;
					break;
				}
				s += aaf.getDurationSeconds(); 
			}
			if(foundFile)break;			
		}		
		return s;
	}
	
	/**
	 * Get the wavetrack in which inparam time exists, 
	 * vis-a-vis the presentation as a whole. Return null if the
	 * inparam time is beyond the duration of the presentation.
	 */
	public AupWaveTrack getWaveTrack(double seconds) {
		double s =  0;
		for(AupWaveTrack track : this) {
			s += track.getDurationSeconds();
			if(seconds<=s) return track;
		}	
		String msg = "Warning: did not find a AupWaveTrack that includes " 
				+ Double.toString(seconds) + "; total presentation time is " + Double.toString(this.getDuration()) +". Returning last wavetrack.";
		mListener.delegateMessage(this,msg, MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, null);
		return this.getLast();
	}
	
	/**
	 * Retrieve the one or several AudioClips needed to represent the incoming
	 * time interval in the presentation as a whole. If the inparam interval spans several wavefiles and/or
	 * wavetracks, then several clips are returned.
	 * @param label The AupLabel to be associated with AudioClips
	 * @param labelStart start value in seconds
	 * @param labelEnd end value in seconds
	 */
	public List<AudioClip> getClips( double labelStart,double labelEnd) {
		//System.err.println("AupWaveTracks.getClips: " + labelStart.toString() + "->" + labelEnd.toString());
		
		//start and end may be in different audiofiles, in different wavetracks
		//the incoming values represent times in the whole presentation
						
		/*
		 * Get the tracks where start and end appear
		 */
		AupWaveTrack startTrack = getWaveTrack(labelStart);		
		AupWaveTrack endTrack = getWaveTrack(labelEnd);		

		
		/*
		 * Get the blockfiles where start and end appear
		 * AupWaveTrack.getAudioFile(Clock) are relative to the track
		 */
		AupBlockFile startFile = null;		
		AupBlockFile endFile = null;		
		
		if(startTrack == this.getFirst()) {
			startFile = startTrack.getAudioFile(labelStart);
		}else{
			//remove previous time
			double elapsedTime = getElapsedTime(startTrack);
			startFile = startTrack.getAudioFile(labelStart-elapsedTime);
		}
		
		if(endTrack == this.getFirst()) {
			endFile = endTrack.getAudioFile(labelEnd);
		}else{
			//remove previous time
			double elapsedTime = getElapsedTime(endTrack);
			endFile = endTrack.getAudioFile(labelEnd-elapsedTime);
		}
		assert(startFile!=null && endFile!=null);
		
		
		//gather all files that are part of this interval
		List<AupBlockFile> blockFiles = new LinkedList<AupBlockFile>();
		
		if(startFile!=endFile) {
			//note: there may be entire audio files, even tracks,
			//inbetween start and end.
			//line up all included files
			int currentTrack = this.indexOf(startTrack);
			boolean passedStartFile = false;
			boolean passedEndFile = false;
			
			for (int i = currentTrack; i < this.size(); i++) {
				AupWaveTrack track = this.get(i);
				for (int j = 0; j < track.size(); j++) {
					AupBlockFile af = track.get(j);
					if(af==startFile) passedStartFile = true;
					if(passedStartFile) blockFiles.add(af);
					if(af==endFile) {
						passedEndFile = true;	
						break;										
					}
				}//for
				if(passedEndFile) break;				
			}//for			
		}else{
			//start and end in same file
			blockFiles.add(startFile);
		}
				
		//finally, create the AudioClips
		List<AudioClip> clips = new LinkedList<AudioClip>();
		//the time values in an AudioClip are relative to the file itself
		if(blockFiles.size()==1) {
			double start = labelStart - getElapsedTime(startFile);
			double end = labelEnd - getElapsedTime(endFile);
			AupBlockFile abf = blockFiles.get(0);
			AudioClip clip = new AudioClip(abf.getFile(),abf.getAudioFileFormat(),start,end);
			clips.add(clip);
		} else {
			//first audiofile in list
			double start = labelStart - getElapsedTime(startFile);			
			double end = startFile.getDurationSeconds();
			AudioClip clip = new AudioClip(startFile.getFile(),startFile.getAudioFileFormat(),start,end);
			clips.add(clip);
			
			if(blockFiles.size()>2) {
				for (int i = 0; i <  blockFiles.size(); i++) {
					//skip the first and last
					if(i>0 && i<blockFiles.size()-1) {
						AupBlockFile aaf = blockFiles.get(i);
						start = 0.0;
						end = aaf.getDurationSeconds();
						AudioClip cl = new AudioClip(aaf.getFile(),aaf.getAudioFileFormat(),start,end);
						clips.add(cl);
					}
				}				
			}
			
			//last audiofile in list
			start = 0;
			end = labelEnd - getElapsedTime(endFile);
			clip = new AudioClip(endFile.getFile(),endFile.getAudioFileFormat(),start,end);
			clips.add(clip);
		}
		
		return clips;
	}	
	
	private static final long serialVersionUID = 8913929084062469916L;
}