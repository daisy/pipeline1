package org.daisy.util.fileset.interfaces;

import java.net.URI;
import java.util.Collection;

/**
 * Interface for FilesetFiles that can be 
 * referred to by other FilesetFiles
 * @author Markus Gylling
 */
public interface Referable extends Descendant {
	/**
	 * @param uri absolute URI of a FilesetFile Fileset member that may or may not refer to this member
	 * @return the corresponding member (@link FilesetFile) object if referring to this member, null otherwise
	 * @throws FilesetFatalException if this collection has not been populated
	 * @see instantiators at {@link org.daisy.util.fileset.interfaces.Fileset}
	 */
	public FilesetFile getReferringLocalMember(URI uri) throws NullPointerException;
		
	/**
	 * @return the referring members collection; not ordered
	 * @throws FilesetFatalException if this collection has not been populated
	 * @see instantiators at {@link org.daisy.util.fileset.interfaces.Fileset}
	 */
	public Collection getReferringLocalMembers() throws NullPointerException;
		
}