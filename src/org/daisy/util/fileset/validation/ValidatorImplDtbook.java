package org.daisy.util.fileset.validation;

import java.net.URI;
import java.net.URL;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.validation.SchematronMessage;
import org.daisy.zedval.ZedVal;
import org.daisy.zedval.engine.ZedConstants;
import org.daisy.zedval.engine.ZedMap;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An impl of org.daisy.util.fileset.validation.Validator for Dtbook documents.
 * @author Markus Gylling
 */
public class ValidatorImplDtbook extends ValidatorImplAbstract implements Validator {
	private Z3986DtbookFile mDtbookInputFile = null;
	private ZedMap mZedValTestMap = null;
	
	/**
	 * Constructor.
	 */
	ValidatorImplDtbook(){
		super(FilesetType.DTBOOK_DOCUMENT);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources(fileset.getManifestMember().getFile().toURI());		
		super.validate(fileset);
		validate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources(manifest);		
		super.validate(manifest);
		validate();
	}
	
	private void setStaticResources(URI manifest) throws ValidatorException {
		//find out what version of DTBook we are dealing with
		//and then set appropriate canonical schemas on super
		try {
			PeekResult peekResult = PeekerPool.getInstance().acquire(false).peek(manifest);
			String version = peekResult.getRootElementAttributes().getValue("version");
			if(version==null) {
				throw new ValidatorException("version attribute not found on document root");
			}
			
			//these are compound RNG and SCH; super will find that out and create multiple validators per schema
			if(version.equals("2005-1")) {
				setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("-//NISO//RNG dtbook 2005-1//EN"),"org.daisy.util.fileset.impl.Z3986DtbookFileImpl");
			}else if(version.equals("2005-2")) {
				setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("-//NISO//RNG dtbook 2005-2//EN"),"org.daisy.util.fileset.impl.Z3986DtbookFileImpl");
			}else if(version.equals("2005-3")) {
				setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("-//NISO//RNG dtbook 2005-3//EN"),"org.daisy.util.fileset.impl.Z3986DtbookFileImpl");
			}else{
				mValidatorListener.inform(this, "No canonical schema resources were internally associated with this DTBook document version (" + version + ")");
			}
			
		} catch (Exception e) {			
			throw new ValidatorException(e.getMessage(),e);
		} 
		
	}

	/**
	 * Perform additional validation other than that done through the call to super.validate (which executes any schemas and delegates registered on super)
	 */
	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		//nothing to see here, please move along.
	}
		
	/**
	 * We override super class impl of ErrorHandler#Error; Jing
	 * doesnt populate the system id of the SAXParseException, so
	 * we use a overload of ExceptionTransformer that takes current file.
	 * Also, we want to redirect SchematronMessages specific to zed space - 
	 * all sch msgs are reported through #error, but the Schematron schema author 
	 * may have flagged them as having another severity level, using a scheme within the 
	 * SchematronMessage syntax. 
	 */	 
	public void error(SAXParseException exception) throws SAXException {
		if(mDtbookInputFile == null) {
			mDtbookInputFile = (Z3986DtbookFile)this.mFileset.getManifestMember();
		}	
		
		String message = exception.getMessage();
		if(SchematronMessage.isMessage(message)) {
			//it is a schematron message			
			//find out if there is a translator available
			try {				
				SchematronMessage sm = new SchematronMessage(message);				 
				String token;
				if((token = sm.getMessage("zedid"))!=null) {
					//its a zedmap message
					Element msg = (Element) XPathUtils.selectSingleNode (getZedValTestMap().getMapDocument().getDocumentElement(), 
							".//test[@id='" + token + "']/onFalseMsg/msg[@class='long']");
					if(msg!=null) {
						message = msg.getTextContent(); 
					}					
				}
				ValidatorErrorMessage vem = new ValidatorErrorMessage(mDtbookInputFile.getFile().toURI(), message,exception.getLineNumber(),exception.getColumnNumber());
				mValidatorListener.report(this, vem);
				return;
			} catch (Exception e) {
				//inform that the lookup failed (we can still generate a report but with the nontranslated message)
				mValidatorListener.inform(this, "Caught an exception while trying to translate a ZedMap message: " 
						+ e.getClass().getSimpleName() + ": " + e.getMessage() + " on " + exception.getMessage());
			}									
		}		
		//it is not a schematron message, or we caught an exception trying to translate
		mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
					(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR,mDtbookInputFile.getFile().toURI()));
		
	}

	private ZedMap getZedValTestMap() throws Exception {
		if(mZedValTestMap==null) {
			ZedVal zv = new ZedVal();
			//for now, we hardcode to 2005, but this may need to be refined later on
			URL mapURL = zv.getContext().getDefaultTestMap(ZedConstants.Z3986_VERSION_2005);
			mZedValTestMap = new ZedMap(mapURL);	
		}
		return mZedValTestMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();
		mDtbookInputFile = null;
		//we dont reset mZedValTestMap until its version can vary (see #getZedValTestMap())
		//TODO reset local member vars
	}


}
