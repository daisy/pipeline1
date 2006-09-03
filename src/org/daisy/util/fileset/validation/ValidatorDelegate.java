package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.Referring;
import org.daisy.util.fileset.interfaces.sgml.HtmlFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * Carries misc generic routines for Fileset validation that
 * an impl may choose to delegate to.
 * @author Markus Gylling
 */
public class ValidatorDelegate {

	/**
	 * Check intermembership fragment URI resolvement.
	 * <p>An implementor of the Referring interface may carry URIs with fragments that point to 
	 * destinations within other members of the fileset. This delegate attempts to resolve these 
	 * URIs. An error is reported to the listener only if the fragment
	 * doesnt exist within the destination member; silence will prevail if the destination member itself 
	 * doesnt exist. (This is because missing members are reported natively during Fileset instantiation.)</p> 
	 * @param referer The filesetfile whose links to other members should be checked for resolvement
	 * @param validator The validator using this delegate
	 */
	public static boolean isInterDocFragmentLinkValid(Referring referer, Validator validator) throws Exception {
		FilesetRegex regex = FilesetRegex.getInstance();
		boolean totalResult = true;
		URI cache = null;						
		FilesetFile referencedMember = null;		
		
		Iterator uriator = referer.getUriStrings().iterator();
		while (uriator.hasNext()) {
			boolean currentResult = true;
			String value = (String) uriator.next();
			if((!regex.matches(regex.URI_REMOTE,value))
					&&(regex.matches(regex.URI_WITH_FRAGMENT,value))) {
				
				String fragment = URIStringParser.getFragment(value);
				String path = URIStringParser.stripFragment(value);
				//get the full URI of the member to resolve
				URI uri = referer.getFile().toURI().resolve(path);
				if(!uri.equals(cache)) {
					//the referenced member is other than last time
					cache=uri;
					//get the file instance from Fileset via the URI key
					referencedMember=referer.getReferencedLocalMember(uri);
					if (referencedMember==null){
						continue;  //we dont report nonexisting members
					}								
				}
				//check whether this colleague has the id value
				if(referencedMember instanceof XmlFile){
					if(!((XmlFile)referencedMember).hasIDValue(fragment)) {
						currentResult = false;
					}
				}else if(referencedMember instanceof HtmlFile){
					if(!((HtmlFile)referencedMember).hasIDValue(fragment)) {						
						currentResult = false;
					}					
				}else{
					//TODO although inlikely to happen
					currentResult = false;
				}
				if(!currentResult) {
					totalResult = false;
					validator.getValidatorListener().report(validator, 
							new ValidatorErrorMessage(referencedMember.getFile().toURI(),
									"does not have fragment " + fragment 
									+ " which was referenced by " + referer.getFile().getName()));
				}
			}
		}//while (uriator.hasNext())
		return totalResult;
	}
	
}