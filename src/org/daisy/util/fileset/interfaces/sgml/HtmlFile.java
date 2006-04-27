package org.daisy.util.fileset.interfaces.sgml;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.mime.MIMEConstants;

public interface HtmlFile extends FilesetFile, Referring {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_HTML;
}
