package org.daisy.util.file.detect;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.daisy.util.mime.MIMEType;

/**
 * A file signature.
 * @author Markus Gylling
 */
public abstract class Signature {	
	private MIMEType mMIMEType = null;
	private Pattern mNameRegex = null;
	private String[] mImplementors = null;
	private String mNiceName = null;
			
	/**
	 * Constructor.
	 */
	protected Signature(MIMEType mime, String nameRegex, String implementors, String niceName){
		mMIMEType = mime;
		mNameRegex = Pattern.compile(nameRegex);
		if(implementors!=null) {
			mImplementors = implementors.split(" ");
			for (int i = 0; i < mImplementors.length; i++) {
				mImplementors[i] = mImplementors[i].trim(); 
			}
		}
		mNiceName = niceName;
	}
		
	/**
	 * @return the MIMEType in MIMETypeRegistry that maps to this signature. May be null.
	 */
	public MIMEType getMIMEType() {
		return mMIMEType;
	}
	
	/**
	 * @return a regex string of a name pattern for this signature. May be null.
	 */
	public Pattern getNameRegex() {
		return mNameRegex;
	}
	
	/**
	 * Retrieve a collection of qualified names of classes 
	 * representing specialized implementations of the particular 
	 * file type described by this signature. 
	 * <p>May be null when no implementors have been registered.</p>
	 * <p>It is not guaranteed that a qualified name recieved here will 
	 * resolve to an existing class on a given system.</p>
	 */
	public String[] getImplementors() {		
		return mImplementors;
	}

	/**
	 * @return a human readable name of the resource type described by this resource
	 */
	public String getNiceName() {
		return mNiceName;
	}
	
	/*package*/ abstract Set<? extends SignatureToken> getHeaderTokens();
	
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(mNiceName).append("\n");
		sb.append("Mime type: ").append(mMIMEType.getString()).append("\n");
		sb.append("Name regex: ").append(mNameRegex.pattern()).append("\n");
		sb.append("Implementors: ");
		for (String s : mImplementors) {
			sb.append(s).append(' ');
		}
		sb.append("\n");						
		Set<? extends SignatureToken> tokens =  this.getHeaderTokens();
		if(tokens!=null) {
			for (SignatureToken t : tokens) {
				sb.append("Token: ").append(t.toString());
			}
		}else{
			sb.append("Null tokens. ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	/**
	 * Extended method, not used by package detector.
	 * Does the inparam resource match the filename of this Signature?
	 * @throws URISyntaxException 
	 */
	public boolean matchesFileName(URL resource) throws URISyntaxException {
		File f = new File(resource.toURI());
		return getNameRegex().matcher(f.getName()).matches();
	}
	
	/**
	 * Extended method, not used by package detector.
	 * Does the inparam resource match any of the root/header Tokens registered with this Signature?
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws XMLStreamException 
	 */
	public boolean matchesToken(URL resource) throws URISyntaxException, IOException, XMLStreamException {
		if(this instanceof WeakSignature) {
			return false; //no tokens in weak signatures
		}
		
		ResourceProperties rp = ResourceParser.parse(resource);
		
		if (this instanceof ByteHeaderSignature) {
			ByteHeaderToken bht = null;
			if (rp instanceof ResourceByteProperties) {
				ResourceByteProperties rbp = (ResourceByteProperties)rp;
				bht = ((ByteHeaderSignature)this).matchesByteToken(rbp.getByteBuffer()); 
			}
			return bht!=null;
		}
		
		//else XMLSignature
		if (rp instanceof ResourceXMLProperties) {			
			ResourceXMLProperties rxp = (ResourceXMLProperties)rp;
			XMLRootToken xrt = ((XMLSignature)this).matchesRootToken(rxp.getRootElement(), rxp.getPublicId(), rxp.getSystemId());
			return xrt!=null;
		}
		return false;
	}
	
			
}
