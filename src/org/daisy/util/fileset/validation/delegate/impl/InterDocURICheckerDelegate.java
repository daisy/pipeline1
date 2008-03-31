package org.daisy.util.fileset.validation.delegate.impl;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.HtmlFile;
import org.daisy.util.fileset.Referring;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * A delegate that will check that fragment identifiers in interdocument URIS (URIs between fileset
 * members) resolve correctly.
 * <p>The fragment of any occuring URI is checked to correspond to the value of an attribute named 'id' in the destination member.</p>
 * <p>This delegate is written to support subclassing in order to refine the check behavior.</p>
 * @author Markus Gylling
 */
public class InterDocURICheckerDelegate extends ValidatorDelegateImplAbstract {

	public boolean isFilesetTypeSupported(FilesetType type) {
		return true;
	}

	public InterDocURICheckerDelegate() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	@Override
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
		super.execute(fileset);
		FilesetRegex regex = FilesetRegex.getInstance();
		URI cache = null;
		FilesetFile referencedMember = null;

		for (Iterator iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = (FilesetFile) iter.next();
			if (ffile instanceof Referring) {				
				Referring referer = (Referring) ffile;
				Iterator uriator = referer.getUriStrings().iterator();
				while (uriator.hasNext()) {
					String uriString = (String) uriator.next();
					if (!regex.matches(regex.URI_REMOTE, uriString) && !uriString.startsWith("#")) {
						String path = URIStringParser.stripFragment(uriString);
						String fragment = URIStringParser.getFragment(uriString);
						if (fragment.length()<1)fragment=null;
						URI uri = referer.getFile().toURI().resolve(path);
						if (!uri.equals(cache)) {
							// the referenced member is other than last time
							cache = uri;
							// get the file instance from Fileset via the URI key
							referencedMember = referer.getReferencedLocalMember(uri);						
							if (referencedMember == null) continue; // we dont report nonexisting members							
						}						
						checkTarget(ffile, uriString, referencedMember, fragment);
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
	protected void checkTarget(FilesetFile source, String uriString, FilesetFile destination, String fragment) {		
		if(fragment!=null){
			boolean result = false;
			if (destination instanceof XmlFile) {
				if (((XmlFile) destination).hasIDValue(fragment)) result = true;			
			}			
			else if (destination instanceof HtmlFile) {
				if (((HtmlFile) destination).hasIDValue(fragment)) result = true;							
			}			
			if(!result) {
				mValidator.getListener().report(mValidator, new ValidatorErrorMessage(source.getFile().toURI(),
						"The URI " + uriString + " does not resolve"));
			}	
		}		
	}
}
