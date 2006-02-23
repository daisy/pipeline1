/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.daisy.util.fileset.FilesetConstants;

/**
 * <p>Base class for the org.daisy.util.fileset File hierarchy.</p>
 * @author Markus Gylling
 */

abstract class FilesetFileImpl extends File implements FilesetFile {
	private LinkedHashSet myUriStrings = new LinkedHashSet();	
	private LinkedHashMap myFilesetReferences = new LinkedHashMap();	
	private Map myFilesetReferers = new HashMap();	
	protected Regex regex = Regex.getInstance(); 
	
	FilesetFileImpl(URI uri) throws IOException, FileNotFoundException {
		super(uri);				
		if(!this.exists())  {
			throw new FileNotFoundException(this.getName());
		} else if (!this.canRead()){
			throw new IOException(this.getName());
		} else {
	      //ok to bubble up		
		}
	}
	
	public File getFile() {
	    return this;
	}
	
	/**
	 * Adds a URI string to the local ordered uri string collection
	 */
	protected void putUriValue(String uri) {
		myUriStrings.add(uri);
	}
		
	public Iterator getUriIterator() {
		return this.myUriStrings.iterator();		
	}

	public boolean hasUris() {		
		return (!this.myUriStrings.isEmpty());
	}
	
	public void putReferencedMember(URI uri, FilesetFile file) {
		myFilesetReferences.put(uri,file);
	}
			
	public FilesetFile getReferencedLocalMember(URI uri) {
		return (FilesetFile)myFilesetReferences.get(uri);
	}
	
	public Iterator getReferencedLocalMembersURIIterator() {
		return myFilesetReferences.keySet().iterator();    
	}
	
	public Collection getReferencedLocalMembers() {
		return myFilesetReferences.values();
	}
	
	public FilesetFile getReferringLocalMember(URI uri) throws FilesetException{
		if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
		return (FilesetFile)myFilesetReferers.get(uri);		
	}
	
	public Iterator getReferringLocalMembersIterator() throws FilesetException {
		if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
		return myFilesetReferers.keySet().iterator(); 
	}

	public Collection getReferringLocalMembers() throws FilesetException {
		if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
		return myFilesetReferers.values(); 
	}
	
	void setReferringLocalMembers(Map fileset) {
		//populate the myFilesetReferers HashMap - who points to me?
		URI myURI = this.toURI();
		Iterator it = fileset.keySet().iterator();
		while (it.hasNext()) {
			FilesetFileImpl member = (FilesetFileImpl) fileset.get(it.next());
			Object o = member.getReferencedLocalMember(myURI);
			if (o!=null){
				myFilesetReferers.put(member.toURI(),member);
			}
		}	    
	}
}
