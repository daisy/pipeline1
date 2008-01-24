package org.daisy.util.file.detect;

import java.net.URL;
import java.util.Set;

import org.daisy.util.mime.MIMEType;

/**
 * Weak file signature. Contains a MIMEType and a name regex.
 * @author Markus Gylling
 */
public class WeakSignature extends Signature {
		
	/*package*/ WeakSignature(MIMEType mime, String nameRegex, String implementors, String niceName) {
		super(mime, nameRegex, implementors, niceName);			
	}
	
	@Override
	/*package*/ Set<SignatureToken> getHeaderTokens() {
		return null;
	}
		
	/*package*/ SignatureMatchResult matches(String fileName) {
		boolean matchesFilename = getNameRegex().matcher(fileName).matches();
		return new SignatureMatchResult(false,null,matchesFilename);									
	}

	@Override
	public boolean matchesToken(URL resource) {
		return false;
	}
	
}
