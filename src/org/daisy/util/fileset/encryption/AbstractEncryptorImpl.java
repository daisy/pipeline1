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
package org.daisy.util.fileset.encryption;

import java.io.IOException;
import java.util.Map;

import org.daisy.util.file.Directory;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;

/**
 * An abstract Encryptor that implementors may choose to extend.
 * @author Markus Gylling
 */
public abstract class AbstractEncryptorImpl implements Encryptor{
	protected Fileset mInputFileset = null;
	protected Directory mOutputDir = null;
	protected Map<String,String> mParameters = null;
	
	protected AbstractEncryptorImpl() {
		
	}
	
	public void encrypt() throws EncryptionException {
		//utility state checker for subs
		if(	mInputFileset == null || !this.supportsFilesetType(mInputFileset.getFilesetType()) 
				|| mOutputDir == null || !mOutputDir.exists()) {
			throw new EncryptionException("invalid state");
		}		
	}
	
	public Fileset getInputFileset() {		
		return mInputFileset;
	}
	
	public void setInputFileset(Fileset fileset) throws EncryptorNotSupportedException {
		if(fileset==null) {
			throw new EncryptorNotSupportedException("fileset is null");
		}
		
		if(!this.supportsFilesetType(fileset.getFilesetType())) {
			throw new EncryptorNotSupportedException(fileset.toString());
		}
		
		mInputFileset = fileset;		
	}
	
	public Directory getOutputDir() {		
		return mOutputDir;
	}

	public void setOutputDir(Directory outputDir) throws EncryptionException {
		if(outputDir==null) {
			throw new EncryptorNotSupportedException("outputDir is null");			
		}
		
		if(!outputDir.exists()) {
			try {
				FileUtils.createDirectory(outputDir);
			} catch (IOException e) {
				throw new EncryptorNotSupportedException(e.getMessage(),e);
			}
		}
		
		mOutputDir = outputDir;
	}
	

	public Map<String,String> getParameters() {
		return mParameters;
	}

	public void setParameters(Map<String,String> parameters) {
		 mParameters = parameters;		
	}

}
