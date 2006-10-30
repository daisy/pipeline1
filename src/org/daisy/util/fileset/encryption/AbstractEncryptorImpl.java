package org.daisy.util.fileset.encryption;

import java.io.IOException;
import java.util.Map;

import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;
import org.daisy.util.fileset.interfaces.Fileset;

/**
 * An abstract Encryptor that implementors may choose to extend.
 * @author Markus Gylling
 */
public abstract class AbstractEncryptorImpl implements Encryptor{
	protected Fileset mInputFileset = null;
	protected EFolder mOutputDir = null;
	protected Map mParameters = null;
	
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
	
	public EFolder getOutputDir() {		
		return mOutputDir;
	}

	public void setOutputDir(EFolder outputDir) throws EncryptionException {
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
	

	public Map getParameters() {
		return mParameters;
	}

	public void setParameters(Map parameters) {
		 mParameters = parameters;		
	}

}
