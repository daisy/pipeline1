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

import org.daisy.util.file.EFile;
import org.daisy.util.file.IEFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.mime.MIMETypeException;

/**
 * <p>Base class for the org.daisy.util.fileset File hierarchy.</p>
 * @author Markus Gylling
 */

abstract class FilesetFileImpl extends EFile implements FilesetFile, IEFile {
	private LinkedHashSet<String> myUriStrings = new LinkedHashSet<String>();	
	private LinkedHashMap<URI,FilesetFile> myFilesetReferences = new LinkedHashMap<URI,FilesetFile>();	
	private Map<URI,FilesetFile> myFilesetReferers = null;	
	protected LinkedHashSet<Exception> myExceptions = new LinkedHashSet<Exception>();	
	protected boolean isParsed = false;
	protected FilesetRegex regex = FilesetRegex.getInstance();
	
		
	FilesetFileImpl(URI uri, String mimeString) throws IOException, FileNotFoundException {		
		super(uri);				
		if(!this.exists())  {
			throw new FileNotFoundException("File not found: " + this.getName());
		} else if (!this.canRead()){
			throw new IOException("I/O Exception: " + this.getName());
		} else {		  
			try{
				super.setMimeType(mimeString);
			}catch(MIMETypeException mte){
				myExceptions.add(new FilesetFileErrorException(this,mte)); //we dont throw if this fails			
			}
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
		
	public Collection<String> getUriStrings(){
		return this.myUriStrings;
	}
		
	public void putReferencedMember(FilesetFile file) {
		myFilesetReferences.put(file.getFile().toURI(),file);
	}
			
	public FilesetFile getReferencedLocalMember(URI uri) {
		return myFilesetReferences.get(uri);
	}
		
	public Collection<FilesetFile> getReferencedLocalMembers() {
		return myFilesetReferences.values();
	}
	
	public Collection<FilesetFile> getReferringLocalMembers() throws NullPointerException {
		if (myFilesetReferers==null) throw new NullPointerException("this collection has not been set");
		return myFilesetReferers.values(); 
	}
	
	public FilesetFile getReferringLocalMember(URI uri) throws  NullPointerException {
		if (myFilesetReferers==null) throw new NullPointerException ("this collection has not been set");
		if (!myFilesetReferers.isEmpty()){ 
			return myFilesetReferers.get(uri);
		}
		return null;
	}
	
    public boolean isParsed() {
        return isParsed;
    }
	
	void setReferringLocalMembers(Map<URI,FilesetFile> fileset) {
		//populate the myFilesetReferers HashMap - who points to me?
		myFilesetReferers = new HashMap<URI,FilesetFile>();
		URI myURI = this.toURI();
		Iterator<URI> it = fileset.keySet().iterator();
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
	
	public URI getAbsolutizedURI(String relativeURI) {
		URI parent = this.getParentFile().toURI();
		return parent.resolve(relativeURI);		
	}
	
    public boolean hadErrors() {
    	return !myExceptions.isEmpty();
    }

    public Collection<Exception> getErrors() {
    	return myExceptions;
    }

	public int compareTo(Comparable<Object> arg0) {
		if(arg0.equals(this)){
			return 0;
		}
		return -1;
	}
}