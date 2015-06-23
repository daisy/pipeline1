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
	public boolean matchesToken(@SuppressWarnings("unused")URL resource) {
		return false;
	}
	
}
