package org.daisy.util.fileset.validation.delegate.impl;

import javax.xml.namespace.QName;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
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


