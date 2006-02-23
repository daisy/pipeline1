package org.daisy.util.fileset;

/**
 * Represents an MP3 file.
 * @author Markus Gylling
 */
public interface Mp2File extends AudioFile { 
	
		
	public boolean isMpeg1();
	
	public boolean isMpeg2Lsf();
	
	public boolean isMpeg25Lsf();
	
	public int getLayer();
	
	public int getBitrate();
			
	public boolean isVbr();
	
	public boolean hasID3v2();
	
	public long getCalculatedDurationMillis();
	
}
