package org.daisy.util.fileset.interfaces.text;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a Cascading Style Sheet file
 * @author Markus Gylling
 */
public interface CssFile extends FilesetFile, Referring {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_CSS;
	
}
