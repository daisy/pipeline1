package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a resource file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986ResourceFile extends XmlFile, ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBRESOURCE_XML;
}
