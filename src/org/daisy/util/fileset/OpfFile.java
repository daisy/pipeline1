package org.daisy.util.fileset;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Markus Gylling
 */
public interface OpfFile extends XmlFile {

	public Iterator getSpineIterator();
	public Z3986SmilFile getSpineItem(URI uri);
}
