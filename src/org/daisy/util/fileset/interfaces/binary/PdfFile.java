package org.daisy.util.fileset.interfaces.binary;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.mime.MIMEConstants;

public interface PdfFile extends FilesetFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_PDF;
}
