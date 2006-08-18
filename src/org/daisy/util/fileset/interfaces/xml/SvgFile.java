package org.daisy.util.fileset.interfaces.xml;

import org.daisy.util.mime.MIMEConstants;

/**
 * @author jpritchett
 *
 * Interface for SVG image files
 */
public interface SvgFile extends XmlFile {
	static String mimeStringConstant = MIMEConstants.MIME_IMAGE_SVG_XML;
}
