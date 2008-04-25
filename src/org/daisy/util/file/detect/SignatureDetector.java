package org.daisy.util.file.detect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.events.XMLEvent;



/**
 * Main class for usage of the signature detection package.
 * <p>Usage example:</p>
 * <pre><code>
 * //create instance, optionally load the default library
 * SignatureDetector detector = new SignatureDetector(true);
 * //load a user library
 * detector.loadLibrary(myLibrary.toURL);
 * //detect a resource
 * List&lt;Signature&gt; list = detector.detect(myResourceURL);
 * </code></pre>
 * @author Markus Gylling
 */
public final class SignatureDetector {
	private SignatureRegistry mSignatureRegistry = null;
	private boolean mUsingLooseHeuristics = false;
	
	public enum Feature {
		UseLooseHeuristics;
	}
	
	/**
	 * Default constructor.
	 * @throws SignatureLibraryException 
	 */
	public SignatureDetector() throws SignatureLibraryException {
		this(true);
	}
	
	/**
	 * Extended constructor.
	 * @param useDefaultLibrary whether to automatically load the bundled default signature library. 
	 * @throws SignatureLibraryException 
	 */
	public SignatureDetector(boolean useDefaultLibrary) throws SignatureLibraryException {			
		mSignatureRegistry = new SignatureRegistry();
		if(useDefaultLibrary) {
			URL doc = DefaultSignatureLibrary.getInstance().getURL();
			mSignatureRegistry.addLibrary(doc, DefaultSignatureLibrary.getInstance());
		}
	}	
	
	/**
	 * Load a library of signature tokens.
	 * <p>The library is an XML document conforming to SignatureLibrary.rng</p>
	 * <p>This method can be called several times to load multiple libraries.</p>
	 * @throws SignatureLibraryException 
	 */
	public void loadLibrary(URL doc) throws SignatureLibraryException {		
		mSignatureRegistry.addLibrary(doc, new UserSignatureLibrary(doc));
	}

	/**
	 * Attempt to detect and retrieve signatures matching the inparam file.
	 * @param resource The file to be detected
	 * @return A list of Signature objects if detection succeeded. Map is sorted by closest match(es) first.
	 * If detection failed, an empty List is returned.
	 * @throws SignatureDetectionException if an unexpected error occurred
	 */
	public List<Signature> detect(File resource) throws SignatureDetectionException {
		try {
			return this.detect(resource.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new SignatureDetectionException(e.getMessage(),e);
		}
	}
	
	/**
	 * Attempt to detect and retrieve signatures matching the inparam resource.
	 * @param resource The resource to be detected
	 * @return A list of Signature objects if detection succeeded. Map is sorted by closest match(es) first.
	 * If detection failed, an empty List is returned.
	 * @throws SignatureDetectionException if an unexpected error occurred
	 */
	public List<Signature> detect(URL resource) throws SignatureDetectionException {		
		
		/*
		 * Query the registry, get all matches.
		 */		
		Map<Signature, SignatureMatchResult> signatures = mSignatureRegistry.detect(resource);
		
		if(signatures==null) return new ArrayList<Signature>(0);
		
		/*
		 * By design a returnset contains 0-n XMLSignature or 0-n  ByteSignature
		 */		
		ReturnType returnType = getReturnType(signatures.keySet().iterator());
		
		/*
		 * Sort and filter per features (mUsingLooseHeuristics, etc)
		 */			
		List<Signature> retList = new LinkedList<Signature>();
		
		//first loop: basic filtering
		for(Signature sig : signatures.keySet()) {
			SignatureMatchResult smr = signatures.get(sig);
			if(!mUsingLooseHeuristics) {
				//remove all signatures that dont match on all fields in the matchresult
				if(smr.matchesFilename() && smr.matchesToken()) {					
					retList.add(sig);
				}	
			}else{
				//dont remove; just sort.
				if(smr.matchesFilename() && smr.matchesToken()) {
					//matches both, place first 
					retList.add(0,sig);
				}else{
					retList.add(sig);
				}	
			}
		}

		/*
		 * If we have several XML signatures in return set,  and at least 
		 * one of these signatures has an extended identifier registered, 
		 * check those out and sort early any signature that matches.
		 */
		boolean matchedExtendedXMLToken = false;
		if(returnType == ReturnType.XML && retList.size()>1 ) {
			List<Signature> tempList = new LinkedList<Signature>();			
			for(Signature s : retList) {
				//if(s instanceof XMLSignature) { 
				//mg200804 added filename match when getting a false hit that a 202 contentdoc was an ncc
				//may be a better way to deal with this
				if(s instanceof XMLSignature && signatures.get(s).matchesFilename()) {
					XMLSignature xs = (XMLSignature)s;
					Set<XMLExtendedToken> extendedTokens = xs.getExtendedTokens();
					if(!extendedTokens.isEmpty()) {
						for(XMLExtendedToken xet : extendedTokens) {
							List<XMLEvent> list = xet.getElementList();							
							try{
								if(ResourceParser.matches(resource,list)) {
									tempList.add(s);								
									//if actual move of members
									matchedExtendedXMLToken = true;
									break;
								}	
							}catch (Exception e) {
								throw new SignatureDetectionException(e.getMessage(),e);
							}
						}
					}					
				}				
			}
			if(!tempList.isEmpty()) {
				//add any non matched sigs to end
				for(Signature s : retList) {
					if(!tempList.contains(s)) {
						tempList.add(s);
					}
				}
				retList = tempList;
			}
		}
						
		return retList;		
	}
	
	public void setFeature(Feature feature, boolean value) throws FeatureNotSupportedException {
		switch(feature) {
			case UseLooseHeuristics:					
				if(value) {
					mUsingLooseHeuristics = true;
				}else{
					mUsingLooseHeuristics = false;
				}
				break;
			default:
				throw new FeatureNotSupportedException(feature.toString());
		}		
	}
	
	public boolean getFeature(Feature feature) throws FeatureNotSupportedException {
		switch(feature) {
			case UseLooseHeuristics:
				return mUsingLooseHeuristics;								
			default:
				throw new FeatureNotSupportedException(feature.toString());
		}		
	}
	
	/**
	 * @param args Any number of space separated file paths
	 */
	public static void main(String [] args) {
		try {
			SignatureDetector detector = new SignatureDetector(true);
			for (int i = 0; i < args.length; i++) {
				URL u = toURI(args[i]).toURL();
				List<Signature> list = detector.detect(u);
				System.out.println(list.size() + " signatures detected for " + u.getPath());
				for(Signature s : list) {
					System.out.println(s.toString());
				}	
			}						
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Print information on loaded signatures to standard out.
	 */
	public void printSignatures() {
		Map<URL, SignatureLibrary> libs = mSignatureRegistry.getLoadedLibraries();
		System.out.println((libs.size())  +" libraries loaded.");	
		for(URL url : libs.keySet()) {
			SignatureLibrary lib = libs.get(url);
			Set<Signature> sigs = lib.getSignatures();
			System.out.println("Signatures in " + url.toString() + ": " + sigs.size()  + "\n");
			for(Signature s : sigs) {
				System.out.println(s.toString());
			}
		}							
	}

	private ReturnType getReturnType (Iterator<Signature> signatures) {
		while (signatures.hasNext()) {
			Signature sig = signatures.next();
			if(sig instanceof XMLSignature)
				return ReturnType.XML;			
		}
		return ReturnType.BYTE;
	}

	private enum ReturnType {
		XML,
		BYTE;
	}	
	
    private static Pattern schemePattern = Pattern.compile("[a-z]{2,}:.*");
    
    /**
     * Convert a filename or a file URI to a <code>File</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>File</code> object
     */
    private static File toFile(String filenameOrFileURI) {
        try {
            if (hasScheme(filenameOrFileURI)) {
                try {                    
                    File f = new File(new URI(filenameOrFileURI));
                    return f;
                } catch (URISyntaxException e) {
                    e.printStackTrace();                   
                }
                return null;
            } 
            return new File(filenameOrFileURI);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Convert a filename or a file URI to a <code>URI</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>URI</code> object
     */
    private static URI toURI(String filenameOrFileURI) {
        File file = toFile(filenameOrFileURI);
        return file==null?null:file.toURI();
    }
    
    /**
     * Checks if a path starts with  scheme identifier. If it
     * does, it is assumed to be a URI.
     * @param test the string to test.
     * @return <code>true</code> if the specified string starts with a scheme
     * identitier, <code>false</code> otherwise. 
     */
    private static boolean hasScheme(String test) {
        return schemePattern.matcher(test).matches();        
    }  
}
