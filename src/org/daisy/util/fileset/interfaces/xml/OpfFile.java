package org.daisy.util.fileset.interfaces.xml;

import java.util.Collection;

import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.mime.MIMEConstants;

/**
 * Represents a OEB OPF (OpenEbook Package File) file,
 * versions 1.01 or 1.2. For specific subtypes of OPF see:
 * @see org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile
 * @see org.daisy.util.fileset.interfaces.xml.z3986.NimasOpfFile
 * @author Markus Gylling
 */
public interface OpfFile extends XmlFile, ManifestFile {
	static String mimeStringConstant = MIMEConstants.MIME_TEXT_XML;
	
	/**
	 * @return an ordered collection of FilesetFile subclasses listed in spine
	 * The particular subclass(es) of FilesetFile returned will vary depending on
	 * whether this class has been subclassed (NimasOpfFile, Z3986OpfFile).
	 */
	public Collection getSpineItems()throws IllegalStateException;

	/**
	 * @return the value of the Dc:Format metadata item; null if not existing
	 */
	public String getMetaDcFormat();
	
	/**
	 * @return the value of the Dc:Title metadata item; null if not existing
	 */
	public String getMetaDcTitle();	
}




//public Iterator getSpineIterator();

///**
// * @return a FilesetFile subclass corresponding to inparam URI.
// * The particular subclass of FilesetFile returned will vary depending on
// * whether this class has been subclassed (NimasOpfFile, Z3986OpfFile).
// */
//public FilesetFile getSpineItem(URI uri) throws FilesetException;
