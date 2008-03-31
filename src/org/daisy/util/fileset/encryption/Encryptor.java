package org.daisy.util.fileset.encryption;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.encryption.exception.EncryptionException;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;

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
	public Map loadParameters(String name) throws IOException;
	public Map getParameters(); 
	public void encrypt() throws EncryptionException;
	public SecretKey encrypt(SecretKey secretKey) throws EncryptionException;
	
} 