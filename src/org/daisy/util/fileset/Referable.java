/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Interface for FilesetFiles that can be 
 * referred to by other FilesetFiles
 * @author Markus Gylling
 */
public interface Referable {
	/**
	 * @param uri absolute URI of the member that refers to this member
	 * @return the corresponding member (@link FilesetFile) object if referring to this member, null otherwise
	 * @throws FilesetException if this collection has not been populated
	 * @see instantiators at {@link org.daisy.util.fileset.Fileset}
	 */
	public FilesetFile getReferringLocalMember(URI uri)throws FilesetException;
	
	/**
	 * @return an iterator for the referring members collection; not ordered
 	 * @throws FilesetException if this collection has not been populated 
	 * @see instantiators at {@link org.daisy.util.fileset.Fileset}
	 */
	public Iterator getReferringLocalMembersIterator()throws FilesetException;
	
	/**
	 * @return the referring members collection; not ordered
	 * @throws FilesetException if this collection has not been populated
	 * @see instantiators at {@link org.daisy.util.fileset.Fileset}
	 */
	public Collection getReferringLocalMembers() throws FilesetException;
	
//	
//	//public void setReferringLocalMembers(HashMap fileset);
	
}
