package org.daisy.util.fileset.interfaces.schema;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * interface for dtd files (*.dtd, *.mod, *.ent)
 * @author Markus Gylling
 */
public interface DtdFile extends FilesetFile  {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XML_DTD;

}
