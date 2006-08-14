package org.daisy.util.fileset.interfaces.schema;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * interface for RelaxNG files (*.rng, *.rnc)
 * @author Markus Gylling
 */
public interface RelaxngFile extends FilesetFile  {
	//we dont know if this is compact or xml syntax so use an anonymous parent
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_RELAX_NG;

}
