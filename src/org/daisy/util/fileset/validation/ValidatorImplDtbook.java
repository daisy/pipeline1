package org.daisy.util.fileset.validation;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.xml.z3986.Z3986DtbookFile;
import org.daisy.util.fileset.util.FilesetConstants;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.daisy.util.xml.validation.SchematronMessage;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An impl of org.daisy.util.fileset.validation.Validator for Dtbook documents.
 * @author Markus Gylling
 */
class ValidatorImplDtbook extends ValidatorImplAbstract implements Validator {
	private Z3986DtbookFile mDtbookInputFile = null;
	
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
		super.validate(fileset);
		validate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		super.validate(manifest);
		validate();
	}
	
	/**
	 * Identify, locate and run any canonical validation resources
	 * for the specific version of the input Dtbook file.  
	 * @throws ValidatorNotSupportedException if no validation resources are identified for the particular dtbook version
	 */
	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		String versionValue = "___";
		String publicId = "___";
		String systemId = "___";
		Map schemaSources = new HashMap();
		
		try{
			//get identifiers to find out what version we are dealing with
			//this can be pre-2005, which means its non-namespaced
			mDtbookInputFile = (Z3986DtbookFile)this.mFileset.getManifestMember();
			Attributes rootAttrs = mDtbookInputFile.getRootElementAttributes();
			if(rootAttrs!=null){
				versionValue = null;
				versionValue = rootAttrs.getValue("version");
				if(versionValue == null){
					versionValue = rootAttrs.getValue(FilesetConstants.NAMESPACEURI_DTBOOK_Z2005,"version");
					if(versionValue == null){
						versionValue = rootAttrs.getValue("","version");
					}
				}
			}		
			publicId = mDtbookInputFile.getPrologPublicId();
			systemId = mDtbookInputFile.getPrologSystemId();
			
			//based on version identifiers, gather URLs of canonical schemas
			if((versionValue.equals("2005-1"))
					||(publicId.equals(FilesetConstants.PUBLIC_ID_DTBOOK_Z2005_1))
					||(systemId.equals(FilesetConstants.SYSTEM_ID_DTBOOK_Z2005_1))) {
								
				URL url = CatalogEntityResolver.getInstance().resolveEntityToURL
					("-//NISO//RNG dtbook 2005-1//EN","./z39862005/dtbook-2005-1.rng");
				//todo handle catalog exc
				StreamSource ssrng = new StreamSource(url.openStream());
				ssrng.setSystemId(url.toExternalForm());				
				schemaSources.put(ssrng, SchemaLanguageConstants.RELAXNG_NS_URI);
				
				StreamSource sssch = new StreamSource(url.openStream());
				sssch.setSystemId(url.toExternalForm());				
				schemaSources.put(sssch, SchemaLanguageConstants.SCHEMATRON_NS_URI);
				
								
			} else if((versionValue.equals("2005-2"))
					||(publicId.equals(FilesetConstants.PUBLIC_ID_DTBOOK_Z2005_2))
					||(systemId.equals(FilesetConstants.SYSTEM_ID_DTBOOK_Z2005_2))) {
								
				URL url = CatalogEntityResolver.getInstance().resolveEntityToURL
					("-//NISO//RNG dtbook 2005-2//EN","./z39862005/dtbook-2005-2.rng");
				//todo handle catalog exc
				StreamSource ssrng = new StreamSource(url.openStream());
				ssrng.setSystemId(url.toExternalForm());				
				schemaSources.put(ssrng, SchemaLanguageConstants.RELAXNG_NS_URI);
				
				StreamSource sssch = new StreamSource(url.openStream());
				sssch.setSystemId(url.toExternalForm());				
				schemaSources.put(sssch, SchemaLanguageConstants.SCHEMATRON_NS_URI);
			}else{
				//heres the spot for other dtbook versions
			}
			
						
			//create a JAXP validator and run the schema(s) against the input file
			if(!schemaSources.isEmpty()) {
				
				for (Iterator iter = schemaSources.keySet().iterator(); iter.hasNext();) {
					Source schemaSource = (Source)iter.next();
					String schemaNsURI = (String)schemaSources.get(schemaSource);
					SchemaFactory factory = SchemaFactory.newInstance(schemaNsURI); 
					factory.setErrorHandler(this);
					factory.setResourceResolver(CatalogEntityResolver.getInstance());
					Schema schema = factory.newSchema(schemaSource);													
					javax.xml.validation.Validator jaxpValidator = schema.newValidator();	
					String filename = schemaSource.getSystemId();
					if (filename != null && filename.lastIndexOf("/") > 0) {
						filename = filename.substring(filename.lastIndexOf("/"));
					}
					mValidatorListener.inform(this, "Validating using the " 
							+ SchemaLanguageConstants.toNiceNameString(schemaNsURI) 
							+ " " + filename +".");
					jaxpValidator.validate(new StreamSource(mDtbookInputFile.getFile().toURI().toURL().openStream()));					
				}
			}else{
				throw new ValidatorNotSupportedException("dtbook version");
			}
			
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
		}finally{
			for (Iterator iter = schemaSources.keySet().iterator(); iter.hasNext();) {
				try{
					Source s = (Source)iter.next();
					if(s instanceof StreamSource) {
						StreamSource ss = (StreamSource) s;
						if(ss.getReader()!=null) {
							ss.getReader().close();
						}
						if(ss.getInputStream()!=null) {
							ss.getInputStream().close();
						}
					}
				}catch (Exception e) {
					
				}
			}
		}		
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
		//TODO this method fixed when ZV jar is available
		if(SchematronMessage.isMessage(exception.getMessage())) {
			//TODO it is a schematron message
			mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
					(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR,mDtbookInputFile.getFile().toURI()));			
		}else{		
			//it is not a schematron message
			mValidatorListener.report(this,ExceptionTransformer.newValidatorMessage
					(exception, ExceptionTransformer.SAX_ERRHANDLER_TYPE_ERROR,mDtbookInputFile.getFile().toURI()));
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();
		mDtbookInputFile = null;
		//TODO reset local member vars
	}


}
