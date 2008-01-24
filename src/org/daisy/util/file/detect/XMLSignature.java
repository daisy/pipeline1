package org.daisy.util.file.detect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.daisy.util.mime.MIMEType;

/**
 * XML file signature. Contains a MIMEType, a name regex, and an XMLSignatureToken.
 * @author Markus Gylling
 */
public class XMLSignature extends Signature {

	private Set<XMLRootToken> mRootTokens = null;
	private Set<XMLExtendedToken> mExtendedTokens = null;
	
	/*package*/ XMLSignature(MIMEType mime, String nameRegex, String implementors, String niceName) {
		super(mime, nameRegex, implementors, niceName);
		mRootTokens = new HashSet<XMLRootToken>();				
		mExtendedTokens = new HashSet<XMLExtendedToken>();
	}
	
	
	/*package*/ void addHeaderToken(XMLRootToken xst) {
		mRootTokens.add(xst);
	}
		
	@Override
	/*package*/ Set<XMLRootToken> getHeaderTokens() {
		return mRootTokens;
	}

	/*package*/ void addExtendedToken(XMLExtendedToken xst) {
		mExtendedTokens.add(xst);
	}
			
	/*package*/ Set<XMLExtendedToken> getExtendedTokens() {
		return mExtendedTokens;
	}
	
	/**
	 * Match the inparams against this signatures name pattern and XML Root token(s).
	 */
	/*package*/ SignatureMatchResult matches(StartElement se, String publicID, String systemID, String fileName) {		
		boolean matchesFilename = getNameRegex().matcher(fileName).matches();
		XMLRootToken matchedToken = matchesRootToken(se, publicID, systemID);
		boolean matchesToken = matchedToken!=null; 		
		return new SignatureMatchResult(matchesToken,matchedToken,matchesFilename);
	}

	
	/**
	 * Return the token that matches or null if no match
	 */
	/*package*/ XMLRootToken matchesRootToken(StartElement se, String publicID, String systemID) {
		/**
		 * The inparams to this method are those of a resource.
		 * The tested resource may contain more phenomena than the token, but not less.
		 */
				
		for(XMLRootToken t : mRootTokens) {
			boolean matchesToken = true;
			if(t.getPublicID()!=null ) {
				if(publicID==null||!publicID.equals(t.getPublicID())) {
					matchesToken = false;
				}
			}	
			if(t.getSystemID()!=null ) {
				if(systemID==null||!systemID.equals(t.getSystemID())) {
					matchesToken = false;
				}
			}
			
			if(t.getRootElement()!=null ) {
				StartElement token = t.getRootElement(); 
				/*
				 * Compare all properties of the token StartElement against resource StartElement
				 */
				if(!token.getName().equals(se.getName())) {
					matchesToken = false;
				}
				
				for (Iterator iter = token.getAttributes(); iter.hasNext();) {
					Attribute tokenAttr = (Attribute) iter.next();
					Attribute test = se.getAttributeByName(tokenAttr.getName());
					if(test==null) {
						matchesToken = false;					
					}else{
						if(!test.getValue().equals(tokenAttr.getValue())) {
							matchesToken = false;						
						}
					}				
				}			
			}	
			if (matchesToken) return t;
		}//for		
		return null;
	}
	
}
