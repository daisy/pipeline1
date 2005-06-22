package org.daisy.util.fileset;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

/**
 * Reprenents an OPF (OpenEbook Package File) file
 * @author Markus Gylling
 */
public interface OpfFile extends XmlFile, ManifestFile {

	public Iterator getSpineIterator();
	public Z3986SmilFile getSpineItem(URI uri)throws FilesetException;
	//public void buildSpineMap(Fileset fileset);
	public Collection getSpineItems()throws FilesetException;
	
}
