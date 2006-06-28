package org.daisy.util.fileset.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.daisy.util.file.EFile;
import org.daisy.util.file.IEFile;
import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.InputSource;

/**
 * <p>Base class for the org.daisy.util.fileset File hierarchy.</p>
 * @author Markus Gylling
 */

abstract class FilesetFileImpl extends EFile implements FilesetFile, IEFile {
	private LinkedHashSet myUriStrings = new LinkedHashSet();	
	private LinkedHashMap myFilesetReferences = new LinkedHashMap();	
	private Map myFilesetReferers = null;	
	protected LinkedHashSet myExceptions = new LinkedHashSet();	
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
		
	public Collection getUriStrings(){
		return this.myUriStrings;
	}
		
	public void putReferencedMember(FilesetFile file) {
		myFilesetReferences.put(file.getFile().toURI(),file);
	}
			
	public FilesetFile getReferencedLocalMember(URI uri) {
		return (FilesetFile)myFilesetReferences.get(uri);
	}
		
	public Collection getReferencedLocalMembers() {
		return myFilesetReferences.values();
	}
	
	public Collection getReferringLocalMembers() throws NullPointerException {
		if (myFilesetReferers==null) throw new NullPointerException("this collection has not been set");
		return myFilesetReferers.values(); 
	}
	
	public FilesetFile getReferringLocalMember(URI uri) throws  NullPointerException {
		if (myFilesetReferers==null) throw new NullPointerException ("this collection has not been set");
		if (!myFilesetReferers.isEmpty()){ 
			return (FilesetFile)myFilesetReferers.get(uri);
		}
		return null;
	}
	
    public boolean isParsed() {
        return isParsed;
    }
	
	void setReferringLocalMembers(Map fileset) {
		//populate the myFilesetReferers HashMap - who points to me?
		myFilesetReferers = new HashMap();
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

    public InputSource asInputSource() throws FileNotFoundException {    	
    	InputSource is = new InputSource(new FileReader(this));
    	is.setSystemId(this.toString());
        return is;
    }
    
    public InputStream asInputStream() throws FileNotFoundException {    	
    	return new FileInputStream(this);    	        
    }
       
	public byte[] asByteArray() throws IOException {
		InputStream is = this.asInputStream();
		long length = this.length();
		byte[] bytes = new byte[(int)length];
		int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("IOException in " + this.getName());
        }
        is.close();
        return bytes;
	}
	
	public ByteBuffer asByteBuffer() throws IOException {
		byte[] bytes = this.asByteArray();
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);				
		return (ByteBuffer)buf.put(bytes).rewind();
	}
    
    public boolean hadErrors() {
    	return !myExceptions.isEmpty();
    }

    public Collection getErrors() {
    	return myExceptions;
    }

	public int compareTo(Object arg0) {
		if(arg0.equals(this)){
			return 0;
		}
		return -1;
	}
}








//void setErrorListener(FilesetErrorHandler listener) {
//this.myErrorListener = listener;
//}

//protected void addError(Exception e) {
//myExceptions.add(e);
//if(this.errorListener!=null) {
//  this.errorListener.error(e);
//}    	
//}

//public Iterator getReferringLocalMembersIterator() throws FilesetException {
//if (myFilesetReferers.isEmpty()) throw new FilesetException("this collection has not been set");
//return myFilesetReferers.keySet().iterator(); 
//}

//public Iterator getUriIterator() {
//return this.myUriStrings.iterator();		
//}

//public boolean hasUris() {		
//return (!this.myUriStrings.isEmpty());
//}

//public Iterator getReferencedLocalMembersURIIterator() {
//return myFilesetReferences.keySet().iterator();    
//}
