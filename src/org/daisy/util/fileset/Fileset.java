package org.daisy.util.fileset;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.daisy.util.fileset.FilesetFileImpl;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * <p>Fileset retriever for miscellaneous filesets.</p>
 * <p>Exposes a flat unordered list of FilesetFile extenders through {@link #getLocalMember(URI)} and {@link #getLocalMembersIterator()}.</p>
 * <p>Non-local resources (typically online URIs) are exposed as Strings (URIs as they occured in instance) through {@link #getRemoteMembersIterator()}.</p>
 * 
 * <p>Each local member in its turn exposes two main collections:</p>
 * <ul>
 * <li>{@link org.daisy.util.fileset.FilesetFileImpl#getReferencedLocalMember(URI)}<br/> Referenced members - collection of other members that this member references -<strong>sequentially sorted as they appear in document order</strong></li>
 * <li>{@link org.daisy.util.fileset.FilesetFileImpl#getReferringLocalMember(URI)} <br/>Referring members - collection of other members that references this member -<strong>not sorted</strong></li>
 * </ul> 
 * 
 * <p>Usage example:</p>
 * <pre><code>
 *		try {
 *			Fileset fileset = new Fileset(new URI("file:/E:/dtbs/hauy/ncc.html"));
 *			if (fileset.hadErrors()) { //errors that didnt abort population process				
 *				// use the &lt;Exception&gt;,&lt;URI&gt; hashmap to traverse errors
 *				Iterator it = fileset.getErrorsIterator();
 *				while(it.hasNext()) {
 *					Exception e = (Exception)it.next();
 *					URI uri = fileset.getError(e);					
 *					System.err.println("error " + e.getMessage() + " occurred in " + uri.getPath());
 *				}
 *			}
 *			
 *			//use the &lt;URI&gt;,&lt;AbstractFile&gt; hashmap for local members collection traversal 
 *			Iterator it = fileset.getLocalMembersIterator();
 *			while (it.hasNext()) {
 *				FilesetFile ob = fileset.getLocalMember((URI)it.next());
 *				if(ob instanceof SMILFile) {
 *					SMILFile smil = (SMILFile)ob;
 *					//etc
 *				}
 *			}
 *									
 *		}catch(FilesetException fse) {
 *			//errors that aborted the population process 
 *		}
 * </code></pre>
 * @author Markus Gylling
 */

public class Fileset {
	private HashMap localMembers = new HashMap();	//collects AbstractFile extenders, via FilesetObserver; here are all local files instantiated through the recursive process started by build()
	private HashSet remoteMembers = new HashSet();	//collects Strings typically consisting of online URIs
	//private HashMap errors = new HashMap();			//collects errors (URI, Exception) that occured during build process
	private HashSet errors = new HashSet();			//collects recoverable errors <Exception> that occured during build process
	private FilesetFile manifestMember = null;		//a convenience pointer to the main member (ncc, opf, etc)
	private boolean dtdValidate;
	private FilesetType filesetType = null;
	
	/**
	 * Class constructor. 
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 * @param doDTDValidation sets DTD validation on or off. Default value is off.
	 */
	public Fileset(URI manifestURI, boolean doDTDValidation) throws FilesetException {
		initialize(manifestURI, doDTDValidation);
	}
	
	/**
	 * <p>Class constructor.</p>
	 * <p>Populates the fileset with DTD validation (if applicable) turned off, see {@link #Fileset(URI, boolean)} 
	 * @param manifestURI the URI of the object being input port for fileset retrieval (ncc, opf, playlist, etc)
	 */
	public Fileset(URI manifestURI) throws FilesetException {
      initialize(manifestURI, false);
	}
	
	private void initialize(URI manifestURI, boolean doDTDValidation) throws FilesetException  {
		File f = new File(manifestURI);
		if(!f.exists()){
			throw new FilesetException(new FileNotFoundException("manifest not found"));
		}else if(!f.canRead()) {
			throw new FilesetException(new IOException("cant read manifest"));
		}else{
			try {
				this.dtdValidate = doDTDValidation;
				build(f);
			} catch (Exception e) {
				throw new FilesetException(e);		
			} 
		}    		
	}
	
	/**
	 * Adds a local resource ('member')  to the members collection
	 * @param ob Object of a type extending AbstractFile
	 */
	protected void addLocalMember (Object ob) {		
		JavaFile tmp = (JavaFile)ob; //cast to be able to use .toURI for the key        		
		localMembers.put(tmp.toURI(),ob); //but dont cast what is set as object 	    
	}
	
	
	/**
	 * @return false if the local member collection is empty, true otherwise 
	 */
	public boolean hadLocalMembers() {
		return (localMembers.isEmpty()) ? false : true;
	}
	
	/**
	 * Note: faster than getLocalMember(String)
	 * @param uri absolute URI of requested member
	 * @return the member object corresponding to the URI key 
	 * @see #getLocalMember(String)
	 */
	public FilesetFile getLocalMember (URI uri) {
		return (FilesetFile)localMembers.get(uri);	    	    
	}	
	
	/**
	 * Note: slower than getLocalMember(URI)
	 * @param localName local name of requested member
	 * @return the member object corresponding to the name 
	 * @see #getLocalMember(URI)
	 */
	public FilesetFile getLocalMember(String localName) {    	
		Iterator it = getLocalMembersIterator();    	
		while(it.hasNext()) {
			URI key = (URI)it.next();
			if (key.toString().endsWith(localName)) {
				return (FilesetFile)localMembers.get(key);
			}
		}  
		return null;    	
	}
	
	/**
	 * @return an iterator allowing traversal over entire Fileset membership. Not ordered.
	 */
	public Iterator getLocalMembersIterator() {
		return localMembers.keySet().iterator();	  
	}	
	
	public long getLocalMemberCount() {
		return localMembers.size();
	}
	
	/**
	 * Adds an as-it-occured String representation of a remote resource (typically online URI)  to the remoteResources collection
	 * @param uri 
	 */
	protected void addRemoteMember (String uri) {
		remoteMembers.add(uri);		
	}
	
	/**
	 * @return true if non-local resources (typically online URIs) were encountered during membership population, false otherwise
	 * @see #getRemoteMembersIterator()
	 */
	public boolean hadRemoteMembers() {
		return (remoteMembers.isEmpty()) ? false : true;
	}
	
	/**
	 * @return an iterator allowing traversal over a collection of non-local resources (online URIs) that were encountered during population process
	 * @see #hadRemoteMembers()
	 */
	public Iterator getRemoteMembersIterator() {
		return remoteMembers.iterator();
	}
	
	public long getRemoteMemberCount() {
		return remoteMembers.size();
	}
	
	/**
	 * Adds an object to the errors collection 
	 * @param e Exception that occured
	 */

	protected void addError(Exception e) {
	  errors.add(e);
    }
	
//	protected void addError(Exception e,URI uri) {
//		errors.put(e,uri);
//	}
	
	/**
	 * @return true if errors occured during membership population, false otherwise
	 * @see #getErrorsIterator()
	 */
	public boolean hadErrors() {
		return (errors.isEmpty()) ? false : true;
	}
	
	/**
	 * @return an iterator allowing traversal over a collection of errors that occured during population process
	 * <p>Collection structure is key:URI, object: Exception</p>
	 * @see #hadErrors()
	 */
	public Iterator getErrorsIterator() {
		//return errors.keySet().iterator();
		return errors.iterator();
	}
		
	public HashSet getErrorSet() {
		return (HashSet)errors.clone();
	}
		
	/**
	 * @return the manifest member; ie the one used as input base for subsequent population of fileset
	 */
	public FilesetFileImpl getManifestMember() {
		return (FilesetFileImpl)manifestMember;
	}
	
	public FilesetType getFileSetType() {
		return filesetType;
	}
	
	protected boolean doDTDValidation() {
		return dtdValidate;
	}
	
	/**
	 * analyzes the input manifest file; starts the retrieval process
	 * @param manifest the File being input port for fileset retrieval (ncc, opf, playlist, etc) 
	 */
	
	private void build (File manifest) throws ParserConfigurationException, SAXException, IOException, FilesetException {           
		//speed up JAXP
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","com.sun.org.apache.xerces.internal.parsers.XML11Configuration");
		
		//add to observer
		FilesetObserver.getInstance().addListener(this);  
		
		//use filename as base form for determination    	 
		if (matches(Regex.getInstance().FILE_NCC,manifest.getName())) {		
			filesetType = FilesetType.DAISY_202;
			//populate the whole fileset recursively
			manifestMember = new D202NccFileImpl(manifest.toURI()); 
			//...and then get the mastersmil which is not referenced by any colleague
			File test = new File(manifestMember.getParentFile(), "master.smil");
			if (test.exists()){
				D202MasterSmilFile msmil = new D202MasterSmilFileImpl(test.toURI()); 
			}
			
		} else if (matches(Regex.getInstance().FILE_OPF,manifest.getName())){ 
			filesetType = FilesetType.Z3986;
			//populate the whole fileset recursively
			manifestMember = new OpfFileImpl(manifest.toURI());			
		}else{
			throw new FilesetException("Input manifest "+ manifest.getName() +" not recognized");
		}    	
		
		//now that the whole fileset is represented in this.members,
		//set the myFilesetReferers in each member
		// == set of other fileset members referring to the member
		Iterator it = getLocalMembersIterator();
		while(it.hasNext()){
			FilesetFileImpl mem = (FilesetFileImpl) localMembers.get(it.next());
			mem.setReferringLocalMembers(localMembers);    	    
		}
		
		//remove from observer
		FilesetObserver.getInstance().removeListener(this);    	    	
	}
	
	private boolean matches(Pattern compiledPattern, String match) {
		Matcher m = compiledPattern.matcher(match);
		return m.matches();	
	}
	
}
