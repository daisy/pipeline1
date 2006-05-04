package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents an SMIL file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986SmilFile extends SmilFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_SMIL;
	/**
	 * DTB specific SMIL phenomenon.
	 * @return if given, the stated value for TotalElapsedTime (==time prior to onset of this smil file).
	 */
	public SmilClock getStatedTotalElapsedTime();
}
