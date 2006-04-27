package org.daisy.util.fileset.impl;

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

import org.daisy.util.file.EFileImpl;
import org.daisy.util.fileset.FilesetException;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;

/**
 * <p>Base class for the org.daisy.util.fileset File hierarchy.</p>
 * @author Markus Gylling
 */

abstract class FilesetFileImpl extends EFileImpl implements FilesetFile {
	private LinkedHashSet myUriStrings = new LinkedHashSet();	
	private LinkedHashMap myFilesetReferences = new LinkedHashMap();	
	private Map myFilesetReferers = new HashMap();	
	protected FilesetRegex regex = FilesetRegex.getInstance(); 	
	private MIMEType mimeType = null;
	
	FilesetFileImpl(URI uri, String mimeString) throws IOException, FileNotFoundException, MIMETypeException {
		super(uri);			
		if(!this.exists())  {
			throw new FileNotFoundException("File not found: " + this.getName());
		} else if (!this.canRead()){
			throw new IOException("I/O Exception: " + this.getName());
		} else {
		  this.setMimeType(mimeString);
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
	
//	public Iterator getReferencedLocalMembersURIIterator() {
//		return myFilesetReferences.keySet().iterator();    
//	}
	
	public Collection getReferencedLocalMembers() {
		return myFilesetReferences.values();
	}
	
	public FilesetFile getReferringLocalMember(URI uri) throws FilesetException{
		if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
		return (FilesetFile)myFilesetReferers.get(uri);		
	}
	
//	public Iterator getReferringLocalMembersIterator() throws FilesetException {
//		if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
//		return myFilesetReferers.keySet().iterator(); 
//	}

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
	
	public URI getRelativeURI(FilesetFile filesetFile) {	    
	    URI parent = this.getParentFile().toURI();
	    URI filesetFileURI = filesetFile.getFile().toURI();
	    URI relative = parent.relativize(filesetFileURI);
	    return relative;
	}
}
