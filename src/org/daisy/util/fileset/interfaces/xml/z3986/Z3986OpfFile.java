package org.daisy.util.fileset.interfaces.xml.z3986;

import org.daisy.util.fileset.interfaces.xml.OpfFile;
import org.daisy.util.xml.SmilClock;

/**
 * Extends the generic OpfFile interface 
 * with Z3986 DTB specific methods.
 * @see org.daisy.util.fileset.interfaces.xml.z3986.NimasOpfFile
 * @see org.daisy.util.fileset.interfaces.xml.OpfFile
 * @author Markus Gylling
 */
public interface Z3986OpfFile extends OpfFile{
	public SmilClock getStatedDuration();
	public String getMetaDtbMultiMediaType();
}
