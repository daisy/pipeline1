package org.daisy.util.fileset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Base class for the org.daisy.util.fileset File hierarchy.</p>
 * @author Markus Gylling
 */

public abstract class AbstractFile extends File {
	private HashSet myLocalURIs = new HashSet();
	private HashSet myRemoteURIs = new HashSet();
	private LinkedHashMap myFilesetReferences = new LinkedHashMap(); 
	private HashMap myFilesetReferers = new HashMap(); 
	
	public AbstractFile(URI uri) {
		super(uri);   		
		if(!this.exists()) {
			FilesetObserver.getInstance().errorEvent(uri, new FileNotFoundException());
		}else if (!this.canRead()) {
			FilesetObserver.getInstance().errorEvent(uri, new IOException());
		}else{
			//basic success, note: important to add it before instantiation (and parse) of extender is done because of the recursion
			FilesetObserver.getInstance().localResourceEvent(this);
		}	
	}
		
	protected void putLocalURI(String uri) {
		myLocalURIs.add(uri);
	}
	
	public long getLocalURIsSize() {
		return myLocalURIs.size();
	}
	
	protected void putRemoteURI(String uri) {
		myRemoteURIs.add(uri);
		FilesetObserver.getInstance().remoteResourceEvent(uri); 
	}
	
	public Iterator getLocalURIsIterator() {
		return myLocalURIs.iterator(); 
	}

	public Iterator getRemoteURIsIterator() {
		return myRemoteURIs.iterator(); 
	}

	public boolean hasLocalURIs() {
		return !myLocalURIs.isEmpty();
	}

	public boolean hasRemoteURIs() {
		return !myLocalURIs.isEmpty();
	}
	
	protected void putReferencedMember(URI uri, Object o) {
		myFilesetReferences.put(uri,o);
	}
	
	/**
	 * @param uri absolute URI of member that may be referenced
	 * @return the corresponding Fileset member object if it is referenced from this member, null otherwise
	 */
	public AbstractFile getReferencedLocalMember(URI uri) {
		return (AbstractFile)myFilesetReferences.get(uri);
	}
	
	/**
	 * @return an iterator for the referenced members collection; ordered as appearing in document order 
	 */
	public Iterator getReferencedLocalMembersIterator() {
		return myFilesetReferences.keySet().iterator();    
	}
	
	/**
	 * @param uri absolute URI of member that may be referring to this member
	 * @return the corresponding Fileset member object if referring to this member, null otherwise
	 */
	public Object getReferringLocalMember(URI uri) {
		return myFilesetReferers.get(uri);
	}
	
	/**
	 * @return an iterator for the referring members collection; not ordered 
	 */
	public Iterator getReferringLocalMembersIterator() {
		return myFilesetReferers.keySet().iterator(); 
	}
	
	protected void setReferringLocalMembers(HashMap fileset) {
		//populate the myFilesetReferers HashMap - who points to me?
		URI myURI = this.toURI();
		Iterator it = fileset.keySet().iterator();
		while (it.hasNext()) {
			AbstractFile member = (AbstractFile) fileset.get(it.next());
			Object o = member.getReferencedLocalMember(myURI);
			if (o!=null){
				myFilesetReferers.put(member.toURI(),member);
			}
		}	    
	}
		
	/**
	 * Resolves a relative URI
	 * @param relativeURI as found in document ('../file.xml' etc)
	 * @return a resolved absolute URI ('file:/E:/folder/file.xml'). Base is always the document where the relative URI occurs.
	 */
	protected URI resolveURI(String relativeURI) {		
		return this.toURI().resolve(relativeURI);								
	}
	
	protected boolean matches(Pattern compiledPattern, String match) {
		 Matcher m = compiledPattern.matcher(match);
		 return m.matches();	
	}
	
//	public void debugPrintReferencedMembers() {    	
//		Iterator it = myFilesetReferences.keySet().iterator();
//		while (it.hasNext()) {
//			Object o = myFilesetReferences.get(it.next());
//			System.err.println(o + " :: " + o.getClass().getSimpleName());    	    
//		}    	
//	}
//	
//	public void debugPrintReferringMembers() { 
//		Iterator it = myFilesetReferers.keySet().iterator();
//		while (it.hasNext()) {
//			Object o = myFilesetReferers.get(it.next());
//			System.err.println(o + " :: " + o.getClass().getSimpleName());    	    
//		}    	
//	}
//	
//	public void debugPrintMyURIs() { 
//		String st = new String();
//        Iterator it = myLocalURIs.iterator();
//		while (it.hasNext()) {			
//			System.err.println(it.next());    	    
//		} 
//		//return st;
//	}
	
}
