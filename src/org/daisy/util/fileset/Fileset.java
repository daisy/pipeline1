/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.fileset;

import java.net.URI;
import java.util.Collection;


/**
 * <p>Build a stateless representation of a fileset.</p>
 * <p>Can represent multiple types of filesets; currently supported types defined in org.daisy.util.fileset.FilesetType.java</p>
 * <p>Usage example:</p>
 * <pre><code>
 * URI opfURI = new File("D:/myPackage.opf").toURI();
 * try{
 *   //instantiate with the manifest URI and this as listener
 *   Fileset fileset = new FilesetImpl(opfURI, this);
 *   Collection members = fileset.getLocalMembers();
 *   //.... 
 * }catch (FilesetFatalException ffe) {
 *   throw ffe;
 * }	
 *
 * //receive notifications from the FilesetErrorHandler callback
 * //All nonthrown Exceptions are reported here, using subtypes		
 * //FilesetFileFatalErrorException, FilesetFileErrorException, FilesetFileWarningException 
 * public void error(FilesetFileException ffe) throws FilesetFileException {
 *  System.err.println(ffe.getCause() + " in " + ffe.getOrigin());
 *  //if one file had a fatal error (such as malformedness) one may want to abort	
 *  if(ffe instanceof FilesetFileFatalErrorException) throw ffe;			
 * } 
 * </code></pre>
 * <p>Uses internal heuristics to determine type of fileset, and types of individual members.</p>
 * <p>Uses interfaces to determine type identity in the FilesetFile hierarchy, therefore requires casting from the type anonymous collections:</p>
 * <pre><code>
 * Z3986OpfFile opf = (Z3986OpfFile) fileset.getLocalMember(aURI);
 * </code></pre>
 * <p>...as well as instanceof tests on interfaces to determine type:</p>
 * <pre><code>
 *   if(aMember instanceof CssFile) ... ;
 * </code></pre>
 * <p>Alternatively the FilesetFile.getMimeType() method can be used, but obviously only
 * after casting to the specific subtype does methods and properties of that particular type become available.</p>
 * <p>The fileset instance exposes an unordered list of all fileset 
 * members. Each member in its turn exposes an ordered list of the members it points to itself, as well as a list of other members that points to itself.</p> 
 * <p>Written to be error tolerant; throws instantiation exceptions only under rare conditions.</p>

 * @author Markus Gylling
 */
public interface Fileset {
	
	/**
	 * @return The object corresponding to the manifest member (ncc, opf, etc)
	 */
	public ManifestFile getManifestMember();
		
	/**
	 *@return the FilesetFile mapping to the input key (absolute URI), null if no key match
	 */
	public FilesetFile getLocalMember(URI absoluteURI);
	
	/**
	 *@return the collection&lt;FilesetFile&gt; of local Fileset members. 
	 *This collection is not ordered, as opposed to the collections
	 *returned by FilesetFile.getReferencedLocalMembers()
	 */
	public Collection<FilesetFile> getLocalMembers();
	
	/**
	* @return a Collection&lt;URI&gt; representing existing local Fileset members
	* @see #getMissingMembersURIs()
	* @see #getRemoteResources()
	*/
	public Collection<URI> getLocalMembersURIs();
	
	/**
	 *@return a collection&lt;String&gt; (unresolved URI or URL strings) of 
	 *remote resources referenced from within the fileset 
	 *(typically http, mailto, etc URIs) 
	 */
	public Collection<String> getRemoteResources();
	
	/**
	*@return true if errors (nontthrown exceptions) were reported during membership population process, false otherwise
	*/
	public boolean hadErrors();
	
	/**
	 *@return a collection&lt;Exception&gt; representing errors (nontthrown exceptions) reported during fileset instantiation.
	 *This contents of this collection equals the sum of all calls to an implementation of the FilesetErrorHandler
	 *interface during one Fileset instantiation process; in other words, the use of getErrors() and FilesetErrorHandler 
	 *are interchangeable and depends on preference. Since the use of FilesetErrorHandler is required, and since callbacks to that interface
	 *are instant and not post-hoc, the use of FilesetErrorHandler instead of this method is recommended.
	 *@see org.daisy.util.fileset.FilesetErrorHandler
	 */
	public Collection<Exception> getErrors();
	
	/**
	 *returns the FilesetType of this Fileset instance 
	 */
	public FilesetType getFilesetType();
			
	/**
	 * Gets an URI relative to the folder of the manifest file
	 * @param filesetFile a member of the fileset
	 * @return a relative URI
	 * @deprecated
	 * @see org.daisy.util.fileset.FilesetFile#getRelativeURI
	 */
	public URI getRelativeURI(FilesetFile filesetFile);
	
	/**  
	 * @return a collection of absolutized local member URIs found in the fileset, 
	 * but where the physical resource was missing.
	 */
	public Collection<URI> getMissingMembersURIs();
	
	/**
	*@return the sum bytesize of all existing local members in this fileset.
	*Nonexisting (referenced but not found) local members and remote (http, ftp etc) members/resources are not included in the count.
	*/
	public long getByteSize();
		
}



//public void setErrorHandler(FilesetErrorHandler errh);

///**
//*@return an iterator allowing iteration over the localMembers HashMap&lt;URI&gt;,&lt;FilesetFile&gt; holding the flat list of the membership
//*/
//public Iterator getLocalMembersURIIterator();	




///**
//*@return an Iterator over the errors HashSet&lt;Exception&gt;
//*/
//public Iterator getErrorsIterator();

///**
// *@return the number of local members in this fileset
// *remote (http, ftp etc) members/resources are not included in the count
// */
//public long getLocalMemberSize();	