package org.daisy.util.file.detect;

/**
 * The result of matching a resource against a Signature
 * @author Markus Gylling
 */
/*package*/ class SignatureMatchResult {
	private boolean mMatchesFilename;
	private boolean mMatchesToken;
	private SignatureToken mMatchedToken;
	
	/*package*/SignatureMatchResult(boolean matchesToken, SignatureToken matchedToken, boolean matchesFilename) {
		mMatchesFilename = matchesFilename;
		mMatchesToken = matchesToken;
		mMatchedToken = matchedToken;
	}
	
	/*package*/ boolean matchesFilename() {
		return mMatchesFilename;
	}
	
	/*package*/ boolean matchesToken() {
		return mMatchesToken;
	}
	
	/**
	 * Retrieve the token that was basis for a positive token match,
	 * or null if this result did not match on the token.
	 */
	/*package*/ SignatureToken getMatchedToken() {
		return mMatchedToken;
	}
	
}
