package org.daisy.util.fileset.interfaces;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.daisy.util.fileset.FilesetType;


/**
 * @author Markus Gylling
 */
public interface Fileset {
	
	/**
	 *@the object corresponding to the manifest member (ncc, opf, etc)
	 */
	public ManifestFile getManifestMember();
		
//	/**
//	 *@return an iterator allowing iteration over the localMembers HashMap&lt;URI&gt;,&lt;FilesetFile&gt; holding the flat list of the membership
//	 */
//	public Iterator getLocalMembersURIIterator();	

	/**
	 * @return the set&lt;URI&gt; representing local Fileset members
	 */
	public Set getLocalMembersURIs();
	
	/**
	 *@return the FilesetFile mapping to the input key (absolute URI), null if no key match
	 */
	public FilesetFile getLocalMember(URI absoluteURI);
	
	/**
	 *@return the collection&lt;FilesetFile&gt; of local Fileset members 
	 */
	public Collection getLocalMembers();
	
	/**
	 *@return the collection&lt;String&gt; (unresolved URI or URL strings) of remote resources referenced from within the fileset (typically http, mailto, etc URIs) 
	 */
	public Collection getRemoteResources();
	
	/**
	 *@return true if errors were encountered during membership population process, false otherwise
	 */
	public boolean hadErrors();
	
//	/**
//	 *@return an Iterator over the errors HashSet&lt;Exception&gt;
//	 */
//	public Iterator getErrorsIterator();

	/**
	 *@return the collection of errors &lt;Exception&gt;
	 */
	public Collection getErrors();
	
	/**
	 *returns the FilesetType of this Fileset instance 
	 *@see {@link org.daisy.util.fileset.FilesetType}
	 */
	public FilesetType getFilesetType();
	
	/**
	 *@return the number of local members in this fileset
	 *remote (http, ftp etc) members/resources are not included in the count
	 */
	public long getLocalMemberSize();	
	
	/**
	 *@return the sum bytesize of all local members in this fileset
	 *remote (http, ftp etc) members/resources are not included in the count
	 */
	public long getByteSize();
	
	/**
	 * Gets an URI relative to the folder of the manifest file
	 * @param filesetFile a member of the fileset
	 * @return a relative URI
	 * @deprecated
	 * @see org.daisy.util.FilesetFile#getRelativeURI
	 */
	public URI getRelativeURI(FilesetFile filesetFile);
	
	/**
	 * Gets a collection of URIs found in the fileset, but where the file was missing. 
	 * @return a collection of URIs
	 */
	public Collection getMissingURIs();

}
