package org.daisy.util.fileset.interfaces.xml;

import org.daisy.util.mime.MIMEConstants;

/**
 * Represents an XHTML 1.0 file
 * @author Markus Gylling 
 */
public interface Xhtml10File extends XmlFile{
	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_XHTML_XML;

	/**
	 * @return true if the xhtml heading sequence is correctly hierarchical, false otherwise
	 */
	public boolean hasHierarchicalHeadingSequence();
	
}
