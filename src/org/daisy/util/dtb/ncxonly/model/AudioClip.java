package org.daisy.util.dtb.ncxonly.model;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An audio clip of a Model item. An AudioClip is always contained within a single physical file.
 * @author Markus Gylling
 */
public class AudioClip {
	
	private File mFile = null;
	private double mStartSeconds = -1;
	private double mEndSeconds = -1;
	private AudioFileFormat mAudioFileFormat = null;
	private Nature mNature = null;
	private boolean fileChangedEvent = false;
	
	public AudioClip(File file, AudioFileFormat format, double startSeconds, double endSeconds) {
		this(file,format,startSeconds,endSeconds,Nature.NEUTRAL);
	}

	/**
	 * Constructor.
	 * @param file the physical file backing this clip
	 * @param format the AudioFileFormat of the physical file backing this clip
	 * @param startSeconds the start time of the clip within the physical file backing it 
	 * @param endSeconds the end time of the clip within the physical file backing it
	 * @param nature Whether this clip should be maintained in the output Daisy DTB. This can be used for example for page number announcements or other items that benefit from a start and endtime.	 
	 */
	public AudioClip(File file, AudioFileFormat format, double startSeconds, double endSeconds, Nature transcience) {
		mFile = file;
		mAudioFileFormat = format;
		mStartSeconds= startSeconds;
		mEndSeconds = endSeconds;
		mNature = transcience;
	}
	
	/**
	 * Get the AudioFormat of the data in this clip
	 */
	public AudioFormat getAudioFormat() {
		return getAudioFileFormat().getFormat();
	}

	/**
	 * Get the AudioFileFormat of the file backing this clip
	 */
	public AudioFileFormat getAudioFileFormat() {
		if(fileChangedEvent) {
			//redo mAudioFileFormat			
			try {
				mAudioFileFormat = AudioSystem.getAudioFileFormat(mFile);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		fileChangedEvent = false;
		return mAudioFileFormat;
	}
	
	public File getFile() {
		return mFile;
	}

	/**
	 * Set the file that backs this clip.
	 * <p>The user of this class is responsible
	 * to use {@link AudioClip#setStartSeconds(double)} and {@link AudioClip#setEndSeconds(double)}
	 * to reflect any changes in the time interval of this clip.</p>  
	 */
	public void setFile(File file) {
		fileChangedEvent = true;
		mFile = file;
	}
		
	public double getDurationSeconds() {
		return mEndSeconds - mStartSeconds;
	}
	
	
	/**
	 *  Get this clips end time within the underlying file.
	 */		
	public double getEndSeconds() {
		return mEndSeconds;	
	}
	
	/**
	 * Get this clips start time within the underlying file.
	 */		
	public double getStartSeconds() {
		return mStartSeconds;		
	}
		
	public Nature getNature() {
		return mNature;
	}
	
	public void setNature(Nature nature) {
		mNature = nature; 
	}
	
	/**
	 * Set this clips start time (seconds) in the underlying file
	 */
	public void setStartSeconds(double start) {
		mStartSeconds = start;
	}
	
	/**
	 * Set this clips end time (seconds) in the underlying file
	 */
	public void setEndSeconds(double end) {
		mEndSeconds = end;
	}
	
	/**
	 * Persistence nature of this AudioClip.
	 * <ul>
	 *   <li>TRANSIENT: this clip must be merged with a predecessor when creating the SMIL presentation</li>
	 *   <li>NONTRANSIENT: this clip must be maintained when creating the SMIL presentation</li>
	 *   <li>NEUTRAL: unspecified nature</li>
	 * </ul>
	 * @author Markus Gylling
	 */
	public enum Nature {
		TRANSIENT,		
		NONTRANSIENT,   
		NEUTRAL;		
	}

}
