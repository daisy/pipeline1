package org.daisy.util.fileset.interfaces.xml.d202;

import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.Xhtml10File;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents the tuxtual content file (XHTML 1.0) in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
public interface D202TextualContentFile extends TextualContentFile, Xhtml10File{
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBD202XHTML_XML;
}
