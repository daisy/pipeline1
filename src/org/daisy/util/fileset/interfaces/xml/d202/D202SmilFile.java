package org.daisy.util.fileset.interfaces.xml.d202;

import org.daisy.util.fileset.interfaces.xml.SmilFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a SMIL file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202SmilFile extends SmilFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBD202SMIL_XML;
}
