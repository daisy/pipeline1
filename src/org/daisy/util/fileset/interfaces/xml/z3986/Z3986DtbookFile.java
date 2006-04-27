package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a Dtbook file in a Z3986 fileset, irrespective of Z3986 subversion
 * @author Markus Gylling
 */
public interface Z3986DtbookFile extends TextualContentFile{

	static String mimeStringConstant = MIMEConstants.MIME_APPLICATION_X_DTBOOK_XML;
	
	public String getDcIdentifier();

	public String getDcTitle();
	
	public String getDcCreator();
	
	public String getDcPublisher();

	public String getDocauthor();

	public String getDoctitle();

	public String getDtbUid();
}
