package org.daisy.util.fileset.validation.delegate.impl;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.sgml.HtmlFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.util.FilesetRegex;
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

	public boolean isFilesetTypeSupported(FilesetType type) {
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

		for (Iterator iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = (FilesetFile) iter.next();
			if (ffile instanceof XmlFile || ffile instanceof HtmlFile) {				
				XmlFile referer = (XmlFile) ffile;
				Iterator uriator = referer.getUriStrings().iterator();
				while (uriator.hasNext()) {
					String uriString = (String) uriator.next();
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
