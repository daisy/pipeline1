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
package org.daisy.util.fileset.validation.delegate.impl;

import java.util.Iterator;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.HtmlFile;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * A delegate that will check that innerdoc fragments (URIs within an XML file) resolve correctly.
 * Note - only checks URIs with syntax '#id'; ie URIs with syntax 'self#id' are not checked.
 * @author Markus Gylling
 */
public class InnerDocURICheckerDelegate extends ValidatorDelegateImplAbstract {

	public boolean isFilesetTypeSupported(@SuppressWarnings("unused")FilesetType type) {
		return true;
	}

	public InnerDocURICheckerDelegate() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	@Override
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
		super.execute(fileset);

		for (Iterator<FilesetFile> iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = iter.next();
			if (ffile instanceof XmlFile || ffile instanceof HtmlFile) {				
				XmlFile referer = (XmlFile) ffile;
				Iterator<String> uriator = referer.getUriStrings().iterator();
				while (uriator.hasNext()) {
					String uriString = uriator.next();
					if (uriString.startsWith("#")) {						
						String fragment = URIStringParser.getFragment(uriString);
						if (fragment.length()<1)fragment=null;						
						checkTarget(referer, uriString, fragment);
					}
				}// while (uriator.hasNext())
			}
		}
	}
	
	/**
	 * Check the status of a URI.
	 * <p>This primitive check will only report an error if a fragment exists in the URI, and this fragment does not resolve. Subclasses may override and refine the check.</p>
	 * @param source The FilesetFile in which the URI occurs
	 * @param uriString The URI value as it appears unresolved in the source
	 * @param destination The FilesetFile to which the URI path refers
	 * @param fragment The fragment identifier of the URI, may be null
	 */
	protected void checkTarget(XmlFile source, String uriString, String fragment) {		
		if(fragment!=null){
			if(!source.hasIDValue(fragment)) {
				mValidator.getListener().report(mValidator, new ValidatorErrorMessage(source.getFile().toURI(),
						"The URI " + uriString + " does not resolve"));
			}	
		}		
	}
}
