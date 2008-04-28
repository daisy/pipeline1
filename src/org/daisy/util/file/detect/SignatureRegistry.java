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
 */package org.daisy.util.file.detect;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * File signature registry. Holds a collection of SignatureLibrary, 
 * whereof one may be the native library (./DefaultSignatureLibrary.xml).
 * @author Markus Gylling
 */
/*package*/ class SignatureRegistry  {
		
	private static Map<URL, SignatureLibrary> mSignatureLibraries = null;
	private static URL cachedSignaturesURL = null;
	private static SignatureCache cachedSignatures = null;
		
	/*package*/ SignatureRegistry() {
		mSignatureLibraries = new LinkedHashMap<URL, SignatureLibrary>();
		try {
			/*
			 * Note - at this time, the cache makes no sense,
			 * since the detector does not abort on first hit.
			 */
			cachedSignaturesURL = new URL("http://cache");
			cachedSignatures = new SignatureCache(new URL("http://cache"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.addLibrary(cachedSignatures);
	}

	/**
	 * Add a library to the set of libraries.
	 * @throws SignatureLibraryException 
	 */
	/*package*/ void addLibrary(URL key, SignatureLibrary library) {
		mSignatureLibraries.put(key, library);
	}
	
	/*package*/ void addLibrary(SignatureLibrary lib) {
		mSignatureLibraries.put(lib.getURL(), lib);
	}

	
	/**
	 * Remove a library from the set of registered libraries
	 */
	/*package*/ void removeLibrary(URL doc) {
		mSignatureLibraries.remove(doc);
	}

	/**
	 * Remove a library from the set of registered libraries
	 */
	/*package*/ void removeLibrary(SignatureLibrary library) {
		mSignatureLibraries.remove(library.getURL());
	}

	
	/**
	 * Clear all registered libraries.
	 */
	/*package*/ void clearLibraries() {
		mSignatureLibraries.clear();
	}
	
	/**
	 * Get all registered libraries.
	 */
	/*package*/ Map<URL, SignatureLibrary> getLoadedLibraries() {
		//exclude the cache entry
		Map<URL, SignatureLibrary> ret = new HashMap<URL, SignatureLibrary>();
		for(URL url : mSignatureLibraries.keySet()) {
			if(!url.equals(cachedSignaturesURL)) {
				ret.put(url,mSignatureLibraries.get(url));
			}			
		}		
		return ret;
	}
		
	/**
	 * Find entries in the signature registry that matches the inparam URL.
	 * @return Unsorted matching Signature(s) if detection succeeded, else null.
	 */
	/*package*/ Map<Signature, SignatureMatchResult> detect(URL url) throws SignatureDetectionException {
								
		try{
			
			ResourceProperties rp = ResourceParser.parse(url);
			ResourceXMLProperties rxp = null;
			ResourceByteProperties rbp = null;
			if(rp instanceof ResourceXMLProperties) {
				rxp = (ResourceXMLProperties) rp;
			}else{	
				rbp = (ResourceByteProperties) rp;
			}
			
			/*
			 * return all signatures that match without any sorting or filtering
			 */
						
			Map<Signature, SignatureMatchResult> returnSet = new HashMap<Signature, SignatureMatchResult>();
			
			for(SignatureLibrary lib : mSignatureLibraries.values()) {
				Set<Signature> set = lib.getSignatures();
				for(Signature sig : set) {
					try{
						SignatureMatchResult matchResult = null;												
						if(rxp != null) {
							//resource is an xml file
							if(sig instanceof XMLSignature){
								XMLSignature xs = (XMLSignature)sig;							
								matchResult = xs.matches(rxp.getRootElement(), rxp.getPublicId(), rxp.getSystemId(), rxp.getFileName());
							}else{
								//this signature is weak or byte
								if(sig instanceof ByteHeaderSignature) {
									continue;
								}
							}
						}else if (rbp !=null) {
							//resource is not an xml file
							if(sig instanceof ByteHeaderSignature) {
								ByteHeaderSignature bs = (ByteHeaderSignature)sig;
								matchResult = bs.matches(rbp.getByteBuffer(), rbp.getFileName());
							}else{
								//this signature is weak or xml
								if(sig instanceof XMLSignature) {
									continue;
								}
							}
						}
						
						if(matchResult == null) {
							WeakSignature ws = (WeakSignature)sig;
							matchResult = ws.matches(rp.getFileName());	
						}
																		
						if(matchResult.matchesToken() || matchResult.matchesFilename()) {
							cachedSignatures.addSignature(sig);
							returnSet.put(sig,matchResult);
						}
					}catch (Exception e) {
						System.err.println("Exception in SignatureRegistry#detect: " + e.getClass().getSimpleName() + ": " + e.getMessage());						
					}
				}				
			}
						
			if(!returnSet.isEmpty()) return returnSet;			
		}catch (Exception e) {
			throw new SignatureDetectionException(e.getMessage(),e);
		}
		return null;
	}




	
		
		
		
	


	

}
