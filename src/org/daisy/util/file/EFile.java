/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeFactory;
import org.daisy.util.mime.MIMETypeFactoryException;
import org.xml.sax.InputSource;

/**
 * EFile - where E stands for extended.
 * @author Markus Gylling
 */
public class EFile extends java.io.File  {

	private static MIMETypeFactory mimeFactory = MIMETypeFactory.newInstance();
	
	private MIMEType mimeType = null;
	
	public EFile(String pathname) {
		super(pathname);
	}

	public EFile(String parent, String child) {
		super(parent, child);
	}

	public EFile(java.io.File parent, String child) {
		super(parent, child);
	}

	public EFile(EFolder parent, String child) {
		super(parent, child);
	}
	
	public EFile(URI uri) {
		super(uri);
	}

	public EFile(URI uri, String mimeString) throws MIMETypeException {
		super(uri);
		this.setMimeType(mimeString);
	}
			
	public EFile(File file) {
		super(file.toURI());
	}
	
	public boolean isSymLink() throws IOException {
		boolean isEqual = this.getAbsolutePath().equals(this.getCanonicalPath());
		if(!isEqual) {
			return true;
		}
		return false;
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
	
	public String getName(){
		return super.getName();
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

    public InputSource asInputSource() throws FileNotFoundException {    	
    	//InputSource is = new InputSource(new FileReader(this));
    	//use this instead to avoid charset decoding probs
    	InputSource is = new InputSource(this.toString());
    	is.setSystemId(this.toURI().toString());
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
	
	private static final long serialVersionUID = 11152264979926847L;
	
}
