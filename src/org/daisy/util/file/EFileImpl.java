package org.daisy.util.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeFactoryException;
import org.daisy.util.mime.MIMETypeFactory;

/**
 * EFile - where E stands for extended.
 * @author Markus Gylling
 */
public class EFileImpl extends java.io.File implements EFile {
	private static MIMETypeFactory mimeFactory = MIMETypeFactory.newInstance();
	
	private MIMEType mimeType = null;
	
	public EFileImpl(String pathname) {
		super(pathname);
	}

	public EFileImpl(String parent, String child) {
		super(parent, child);
	}

	public EFileImpl(java.io.File parent, String child) {
		super(parent, child);
	}

	public EFileImpl(EFolder parent, String child) {
		super(parent, child);
	}
	
	public EFileImpl(URI uri) {
		super(uri);
	}

	public EFileImpl(File file) {
		super(file.toURI());
	}
	
	public boolean isSymLink() throws IOException {
		return this.getCanonicalFile().equals
				(this.getAbsoluteFile());
	}

	public MIMEType getMimeType() {
		return this.mimeType;
	}

	public MIMEType setMimeType(String mime) throws MIMETypeException {
		try{
		  return this.mimeType = mimeFactory.newMimeType(mime); 		  
		} catch  (MIMETypeFactoryException mfe) {
			throw new MIMETypeException(mfe.getMessage(),mfe);
		}				
	}
	
	public MIMEType setMimeType() throws MIMETypeException {
		try{
			return this.mimeType = mimeFactory.newMimeType(this);
		} catch (Exception e) {
			throw new MIMETypeException(e.getMessage(),e);
		}
	}
	
	public void setMimeType(MIMEType mime) {
		  this.mimeType = mime; 
	}
	
	public EFolder getParentFolder() throws IOException{
		return new EFolder(this.getParent());
	}
	
	public String getNameMinusExtension() {
		StringBuilder sb = new StringBuilder();
		String name = this.getName();
		int end = name.lastIndexOf('.');
		if(end > 0) {
			for (int i = 0; i < name.length(); i++) {
				if(i<end){
					sb.append(name.charAt(i));
				}
			}
			return sb.toString();
		}
		return name;
	}

	public String getExtension() {
		StringBuilder sb = new StringBuilder();
		String name = this.getName();
		int start = name.lastIndexOf('.');
		if(start > 0) {
			for (int i = 0; i < name.length(); i++) {
				if(i>start){
					sb.append(name.charAt(i));
				}
			}
			return sb.toString();
		}
		return null;
	}

}
