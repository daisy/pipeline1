package org.daisy.util.fileset;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Markus Gylling
 */
public interface FilesetFile extends JavaFile {
	public Iterator getLocalURIsIterator() ;
	public Iterator getRemoteURIsIterator();
	public boolean hasLocalURIs(); 
	public boolean hasRemoteURIs();
	public FilesetFile getReferencedLocalMember(URI uri);
	public Iterator getReferencedLocalMembersIterator();
	public FilesetFile getReferringLocalMember(URI uri);
	public Iterator getReferringLocalMembersIterator(); 
}
