/*
 * Created on 2005-jun-19
 */
package org.daisy.util.fileset;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Markus Gylling
 */
public interface Fileset {
	public ManifestFile getManifestMember();
	public Iterator getLocalMembersURIIterator();	
	public FilesetFile getLocalMember(URI absoluteURI);
	public Collection getLocalMembers();
	public boolean hadErrors();
	public Iterator getErrorsIterator();
	public Collection getErrors();
	public FilesetType getFilesetType();
}
