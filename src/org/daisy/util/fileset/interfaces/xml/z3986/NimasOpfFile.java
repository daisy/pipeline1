package org.daisy.util.fileset.interfaces.xml.z3986;

import java.net.URI;
import java.util.Collection;

import org.daisy.util.fileset.interfaces.xml.OpfFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents a NIMAS OPF (OpenEbook Package File) file,

 * @see org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile
 * @see org.daisy.util.fileset.interfaces.xml.OpfFile
 * @author Markus Gylling
 */
public interface NimasOpfFile extends OpfFile {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_XML;

	//any methods pertaining particularly to a NIMAS opf file are defined here
	//and are implemented in OpfFileImpl.java
	
}
