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
