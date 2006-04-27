package org.daisy.util.fileset.interfaces.audio;

import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an MP3 file.
 * @author Markus Gylling
 */
public interface Mp3File extends AudioFile { 
	static String mimeStringConstant = MIMEConstants.MIME_AUDIO_MPEG;
		
	public boolean isMpeg1();
	
	public boolean isMpeg2Lsf();
	
	public boolean isMpeg25Lsf();
	
	public int getLayer();
	
	public int getBitrate();
			
	public boolean isVbr();
	
	public boolean hasID3v2();
	
	public long getCalculatedDurationMillis();
	
}
