package org.daisy.util.fileset;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Markus Gylling
 */
public interface ImageFile extends FilesetFile { //TODO minimize
	public FilesetFile getReferringLocalMember(URI uri);
	public Iterator getReferringLocalMembersIterator(); 
}
