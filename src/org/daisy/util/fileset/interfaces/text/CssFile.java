package org.daisy.util.fileset.interfaces.text;

import java.io.FileNotFoundException;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a Cascading Style Sheet file
 * @author Markus Gylling
 */
public interface CssFile extends FilesetFile, Referring {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_CSS;
	public org.w3c.css.sac.InputSource asSacInputSource()throws FileNotFoundException;
}
