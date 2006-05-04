package org.daisy.util.fileset.interfaces.xml.d202;

import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents a SMIL file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202SmilFile extends SmilFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBD202SMIL_XML;
	/**
	 * DTB specific SMIL phenomenon.
	 * @return if given, the stated value for TotalElapsedTime (==time prior to onset of this smil file).
	 */
	public SmilClock getStatedTotalElapsedTime();
}
