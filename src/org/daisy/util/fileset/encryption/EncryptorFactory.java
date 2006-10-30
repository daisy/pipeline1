package org.daisy.util.fileset.encryption;

import java.util.Iterator;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.encryption.exception.EncryptorNotSupportedException;

/**
 * Abstract factory for production of implementations of {@link org.daisy.util.fileset.encryption.Encryptor}.
 * <p>This factory relies on system properties to produce implementations. To register an implementation so that this factory can find it, 
 * set a system property following the below specs:</p>
 * <ul>
 * <li>The first part of the key must be 'org.daisy.util.fileset.encryption';
 * <li>Following that is a specifier of supported protection scheme (e.g. <code>PTDB_1_0_0</code>, <code>PTDB_2_0_0</code>, <code>MyScheme</code>, see org.daisy.util.fileset.encryption.EncryptionType for enums)</li>
 * <li>Following that is a specifier of supported fileset type(s) (e.g. <code>DAISY_202</code> or <code>Z3986</code>, see org.daisy.util.fileset.FilesetType for constant enums)</li>
 * <li>The property value is the fully qualified path to an implementation of org.daisy.util.fileset.encryption.Encryptor.</li>
 * </ul>
 * 
 * <p>Example:</p>
 * <code>org.daisy.util.fileset.encryption.PTDB_1_0_0.DAISY_202 = com.acme.MyEncryptorImpl</code>
 * 
 * <p>Multiple implementations may coexist on the system, sharing the initial (non-specifying) part of the key string.
 * This factory will query each found implementation (using non-specifying part of the key) to find out if it supports the requested services, and return the first match.
 * If no match is found, an EncryptorNotSupportedException is thrown.
 * </p> 
 * @author Markus Gylling
 */

public class EncryptorFactory {

	private static String mSystemPropertyKeyConstant = "org.daisy.util.fileset.encryption"; 
	private boolean mDebugState = false;
		
	private EncryptorFactory(){
		if(System.getProperty("org.daisy.debug")!=null) {
			mDebugState = true;
		}
	}
	
	public static EncryptorFactory newInstance() {
		return new EncryptorFactory();
	}

	
	public Encryptor newEncryptor(EncryptionType encryptionType, FilesetType filesetType) throws EncryptorNotSupportedException {
		//go through system properties and see if we find a matching registered impl						
		for (Iterator iter = System.getProperties().keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(key.startsWith(mSystemPropertyKeyConstant)) {		
				String implName = System.getProperty(key);				
				if(mDebugState) System.out.println("DEBUG: EncryptorFactory.newEncryptor trying impl " + implName);
				try {
					Class klass = Class.forName(implName);
					Object o = klass.newInstance();
		            if(o instanceof Encryptor) {
		            	Encryptor enc = (Encryptor)o;
		            	if(enc.supportsEncryptionType(encryptionType) && enc.supportsFilesetType(filesetType)) {
		            		return enc;
		            	}		            			            			            	
		            }   				
				} catch (Throwable t) {
					if(mDebugState) System.out.println("DEBUG: EncryptorFactory.newEncryptor Exception");					
				}  								
			} 			
		} 		
		throw new EncryptorNotSupportedException(encryptionType.toString() + " " + filesetType.toString());		
	}
	
}
