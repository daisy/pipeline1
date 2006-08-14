package org.daisy.util.fileset.interfaces.schema;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * interface for W3C Schema files (*.xsd)
 * @author Markus Gylling
 */
public interface XsdFile extends FilesetFile  {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_XSD_XML;

	
}
