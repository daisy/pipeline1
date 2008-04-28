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

import javax.crypto.SecretKey;

import org.daisy.util.file.Directory;
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
	public void setOutputDir(Directory outputDir) throws EncryptionException;
	public Directory getOutputDir();			
	public void setParameters(Map<String,String> parameters) throws EncryptorNotSupportedException; 
	public Map<String,String> loadParameters(String name) throws IOException;
	public Map<String,String> getParameters(); 
	public void encrypt() throws EncryptionException;
	public SecretKey encrypt(SecretKey secretKey) throws EncryptionException;
	
} 