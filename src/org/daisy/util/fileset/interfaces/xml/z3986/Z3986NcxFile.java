package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an NCX file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986NcxFile extends XmlFile{
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBNCX_XML;
}
