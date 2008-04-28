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

import java.util.HashSet;
import java.util.Set;

import org.daisy.util.mime.MIMEType;

/**
 * Leading byte-based file signature. Contains a MIMEType, a name regex, and a ByteSignatureToken.
 * @author Markus Gylling
 */
public class ByteHeaderSignature extends Signature {

	private Set<ByteHeaderToken> mByteSignatureTokens = null;
	
	/*package*/ ByteHeaderSignature(MIMEType mime, String nameRegex, String implementors, String niceName) {
		super(mime, nameRegex, implementors,niceName);	
		mByteSignatureTokens = new HashSet<ByteHeaderToken>();		
	}

	/*package*/ void addSignatureToken(ByteHeaderToken bst) {
		mByteSignatureTokens.add(bst);
	}
	
	@Override
	/*package*/ Set<ByteHeaderToken> getHeaderTokens() {
		return mByteSignatureTokens;
	}
	
	
	/*package*/ SignatureMatchResult matches(byte[] bb, String fileName) {
		boolean matchesFilename = getNameRegex().matcher(fileName).matches();
		ByteHeaderToken matchingToken = matchesByteToken(bb);
		boolean matchesToken = matchingToken!=null;		
		return new SignatureMatchResult(matchesToken, matchingToken,matchesFilename);
		
	}
	
	/**
	 * Return a matching token or null if no match. 
	 */
	/*package*/ ByteHeaderToken matchesByteToken(byte[] bb) {
		for (ByteHeaderToken t : mByteSignatureTokens) {
			/*
			 * Compare the token array to the equal amount of bytes 
			 * in inparam (resource) array.
			 * We dont require that array length is equal.
			 */
			boolean matchesToken = true;			
			byte[] token = t.getByteArray();	
			for (int i = 0; i < token.length; i++) {
				if(i>bb.length-1) {
					//not enough data in inparam array
					throw new IllegalArgumentException("Byte array is shorter than token array: " 
							+ bb.length + " , expecting " + token.length);
				}
				
				if(token[i]!= bb[i]) {
					matchesToken = false;
					break;
				}
			}//for (int i = 0; i < token.length; i++)
									
			if(matchesToken)return t;
			
		} //for (ByteSignatureToken t : mByteSignatureTokens) 
						
		return null;
	}
}
