package org.daisy.util.fileset.interfaces.schema;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * interface for Schematron files (*.sch)
 * @author Markus Gylling
 */
public interface SchematronFile extends FilesetFile  {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_SCHEMATRON_XML;

}
