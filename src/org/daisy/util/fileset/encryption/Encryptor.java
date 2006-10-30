package org.daisy.util.fileset.encryption;

import java.util.Map;

import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;
import org.daisy.util.fileset.interfaces.Fileset;

/**
 * Base interface for any implementation of a fileset encryptor. 
 * @author Markus Gylling
 */
public interface Encryptor {

	public boolean supportsFilesetType(FilesetType filesetType);
	public boolean supportsEncryptionType(EncryptionType encryptionType);
	public void setInputFileset(Fileset fileset) throws EncryptorNotSupportedException;
	public Fileset getInputFileset();
	public void setOutputDir(EFolder outputDir) throws EncryptionException;
	public EFolder getOutputDir();			
	public void setParameters(Map parameters) throws EncryptorNotSupportedException; 
	public Map getParameters(); 
	public void encrypt() throws EncryptionException;
	
}
