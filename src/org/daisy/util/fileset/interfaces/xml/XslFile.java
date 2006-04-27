package org.daisy.util.fileset.interfaces.xml;

import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an XSL file.
 * @author Markus Gylling
 */
public interface XslFile extends XmlFile{
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XSLT_XML;
}
