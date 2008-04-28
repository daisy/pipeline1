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

import javax.xml.namespace.QName;

import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * A specific link checker for Daisy 2.02 filesets.
 * @author Markus Gylling
 */
public class InterDocURICheckerD202Delegate extends InterDocURICheckerDelegate {
	
	@Override
	public boolean isFilesetTypeSupported(FilesetType type) {		
		return type == FilesetType.DAISY_202;
	}
	
	public InterDocURICheckerD202Delegate() {
		super();
	}
	
	/**
	 * Check the status of a URI in a Daisy 2.02 DTB.
	 * @param source The FilesetFile in which the URI occurs
	 * @param uriString The URI value as it appears unresolved in the source
	 * @param destination The FilesetFile to which the URI path refers
	 * @param fragment the fragment identifier of the URI, may be null
	 */
	@Override
	protected void checkTarget(FilesetFile source, String uriString, FilesetFile destination, String fragment) {			
		if(destination instanceof D202SmilFile && fragment!=null) {
			D202SmilFile smf = (D202SmilFile)destination;
			if(!(smf.hasIDValueOnQName(fragment, new QName("text"))||smf.hasIDValueOnQName(fragment, new QName("par")))) {
				mValidator.getListener().report(mValidator, new ValidatorErrorMessage(source.getFile().toURI(),
						"The URI " + uriString + " does not resolve correctly"));
			}
		}else if (destination instanceof XmlFile && fragment!=null) {
			if (!((XmlFile) destination).hasIDValue(fragment)) {				
				mValidator.getListener().report(mValidator, new ValidatorErrorMessage(source.getFile().toURI(),
						"The URI " + uriString + " does not resolve"));				
			}			
		}
	}
}


