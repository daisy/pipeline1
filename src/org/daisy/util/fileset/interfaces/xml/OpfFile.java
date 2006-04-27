package org.daisy.util.fileset.interfaces.xml;

import java.net.URI;
import java.util.Collection;

import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.xml.SmilClock;

/**
 * Represents a generic OPF (OpenEbook Package File) file,
 * versions 1.01 or 1.2. For specific subtypes of OPF see:
 * @see org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile
 * @see org.daisy.util.fileset.interfaces.xml.z3986.NimasOpfFile
 * @author Markus Gylling
 */
public interface OpfFile extends XmlFile, ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_XML;
	//public Iterator getSpineIterator();
	public Z3986SmilFile getSpineItem(URI uri)throws FilesetException;
	public Collection getSpineItems()throws FilesetException;
	public String getMetaDcFormat();
	public String getMetaDcTitle();
	
}
