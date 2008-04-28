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
package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.Iterator;

import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.HtmlFile;
import org.daisy.util.fileset.Referring;
import org.daisy.util.fileset.XmlFile;
import org.daisy.util.fileset.util.FilesetRegex;
import org.daisy.util.fileset.util.URIStringParser;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;

/**
 * Carries misc generic routines for Fileset validation that
 * an impl may choose to delegate to.
 * @author Markus Gylling
 */
public class ValidatorUtils {

	/**
	 * Check intermembership fragment URI resolvement.
	 * <p>An implementor of the Referring interface may carry URIs with fragments that point to 
	 * destinations within other members of the fileset. This delegate attempts to resolve these 
	 * URIs. An error is reported to the listener only if the fragment
	 * doesnt exist within the destination member; silence will prevail if the destination member itself 
	 * doesnt exist. (This is because missing members are reported natively during Fileset instantiation.)</p> 
	 * @param referer The filesetfile whose links to other members should be checked for resolvement
	 * @param validator The validator using this delegate
	 * @deprecated use InterDocURIFragmentCheckerDelegate instead
	 */
	public static boolean isInterDocFragmentLinkValid(Referring referer, Validator validator) throws Exception {
		FilesetRegex regex = FilesetRegex.getInstance();
		boolean totalResult = true;
		URI cache = null;						
		FilesetFile referencedMember = null;		
		
		Iterator<String> uriator = referer.getUriStrings().iterator();
		while (uriator.hasNext()) {
			boolean currentResult = true;
			String value = uriator.next();
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
					validator.getListener().report(validator, 
							new ValidatorErrorMessage(referencedMember.getFile().toURI(),
									"does not have fragment " + fragment 
									+ " which was referenced by " + referer.getFile().getName()));
				}
			}
		}//while (uriator.hasNext())
		return totalResult;
	}
	
}