package org.daisy.util.fileset.interfaces;

import java.net.URI;
import java.util.Collection;

/**
 * Interface for FilesetFiles that can 
 * refer to other entities (local or remote) via URIs
 * @author Markus Gylling
 */
public interface Referring extends Descendant  {
		
	void putReferencedMember(FilesetFile file);
	
	/**
	 * @return a collection of URI strings, unparsed and unresolved, 
	 * as they barenaked appeared in this referrer.
	 */
	public Collection getUriStrings();
	
	/**
	 * @param uri absolute URI that may or may not be referenced from this referrer, 
	 * and may or may not be represent the location of colleague member of the current Fileset instance. 
	 * @return the corresponding member {@link org.daisy.util.FilesetImpl.FilesetFile} object 
	 * if it is referenced from this member, null otherwise
	 */
	public FilesetFile getReferencedLocalMember(URI uri);
		
	/**
	 * @return a collection&lt;FilesetFile&gt; of the members referenced 
	 * from this member; ordered as appearing in document order 
	 */
	public Collection getReferencedLocalMembers();
	
}
















///**
//* Gives access to methods of java.io.File
//*/
//public File getFile();

///**
//* @return an iterator&lt;URI&gt; for the collection of members referenced from this member; ordered as appearing in document order 
//*/
//public Iterator getReferencedLocalMembersURIIterator();
//public Iterator getUriIterator();
//
//public boolean hasUris();