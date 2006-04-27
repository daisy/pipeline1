package org.daisy.util.fileset.interfaces.xml;

import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents a SMIL file in a DTB context.
 * @author Markus Gylling
 */
public interface SmilFile extends XmlFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_SMIL_XML;
	
	/**
	 * @return the calculated duration of this smil file (based solely on audio duration values) 
	 */
	public SmilClock getCalculatedDuration();
	
	/**
	 * @return the calculated duration of this smil file (based solely on audio duration values) 
	 */
	public long getCalculatedDurationMillis();
	
	/**
	 * @return the given duration of this smil file
	 */
	public SmilClock getStatedDuration();
	
	/**
	 * DTB specific SMIL phenomenon.
	 * @return if given, the stated value for TotalElapsedTime (==time prior to onset of this smil file).
	 */
	public SmilClock getStatedTotalElapsedTime();
	

}
