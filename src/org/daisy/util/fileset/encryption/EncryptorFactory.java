package org.daisy.util.fileset.encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 *  
 * <p>
 * The JAR <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html#Service%20Provider">service provider</a>
 * functionality is also supported. The factory searches for a file named <code>org.daisy.util.fileset.encryption.Encryptor</code>
 * in a <code>META-INF/service</code> directory on the class path. Once found, the factory reads the file line by line and
 * tries to instantiate each Encryptor implementation found in the file until it finds one that supports the required
 * encryption type and fileset type.  
 * </p>
 * @author Markus Gylling
 * @author Linus Ericson
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
		Encryptor enc = this.findFromJarService(encryptionType, filesetType);
		if (enc != null) {
			return enc;
		}
		throw new EncryptorNotSupportedException(encryptionType.toString() + " " + filesetType.toString());		
	}
	
	private Encryptor findFromJarService(EncryptionType encryptionType, FilesetType filesetType) {
		String serviceClass = "META-INF/services/org.daisy.util.fileset.encryption.Encryptor";
		
		InputStream inputStream = null;
		ClassLoader cl = this.getClass().getClassLoader();
        inputStream = cl.getResourceAsStream(serviceClass);
        if (inputStream == null) {
        	inputStream = this.getClass().getResourceAsStream(serviceClass);
        }
        
        if (inputStream == null) {
        	// No services file found. Give up.
            return null;
        }
        
        // Read services file line by line. Try to instantiate an Encryptor and see
		// if the encryptor supports the required encryption type and fileset type
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));		
			String line = reader.readLine();
			while (line != null) {
				if (line.contains("#")) {
					line = line.substring(0, line.indexOf("#"));					
				}
				line = line.trim();
				if (!"".equals(line)) {
					line = line.trim();
					Class klass = null;
					Object o = null;
					try {
						klass = Class.forName(line);
						o = klass.newInstance();
					} catch (ClassNotFoundException e) {
						//e.printStackTrace();
					} catch (InstantiationException e) {
						//e.printStackTrace();
					} catch (IllegalAccessException e) {
						//e.printStackTrace();
					}					
		            if(o instanceof Encryptor) {
		            	Encryptor enc = (Encryptor)o;
		            	if(enc.supportsEncryptionType(encryptionType) && enc.supportsFilesetType(filesetType)) {
		            		// This one looks good
		            		return enc;
		            	}		            			            			            	
		            }
				}
				line = reader.readLine();
			}
		} catch (IOException e) {			
			//e.printStackTrace();
		}
		
		// No suitable implementation found
		return null;
	}
	
}
