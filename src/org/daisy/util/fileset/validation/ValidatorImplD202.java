package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.daisy.util.fileset.AudioFile;
import org.daisy.util.fileset.D202MasterSmilFile;
import org.daisy.util.fileset.D202NccFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.Mp3File;
import org.daisy.util.fileset.validation.delegate.impl.FilesetFileTypeRestrictionDelegate;
import org.daisy.util.fileset.validation.delegate.impl.InterDocURICheckerD202Delegate;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.SchematronMessage;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An implementation of {@link org.daisy.util.fileset.validation.Validator} for Daisy 2.02 DTBs.
 * <p>This implementation does not claim to do a complete conformance validation.</p>
 * @author Markus Gylling
 */

//- shape-attributet på a-elementet i ncc.html
//- ordning på smilfiler i ncc:n
//- ordningen på smilreferenser till samma smil i ncc:n
//- ncc:files
//- ncc:maxPageNormal
//- ncc:setInfo
//- ncc:multimediaType correct?
//- http-equiv value case insensitive?
//- ncc:kByteSize
//- samma dc:identifier på alla filer i filesetet
//- samma dc:title i smil och content.html (om de finns) som i ncc.html
//- pekar smil-klippen utanför (tidsmässigt) ljudfilerna?
//- länkar från ncc.html/content.html ska peka på smil par eller text
//- title i smil har rätt värde (samma som motsvarande ncc-item?)
//Hur är det med ordningen på hx i ncc? (Ingen h3 direkt efter h1 etc.).
//ErrorScout är för petig när det gäller viss metadata, t.ex. måste Content-Type ha rätt case.
//ncc:totalElapsedTime och ncc:timeInThisSmil i SMIL måste ha sekundsyntax (inga millisekunder). Det är bara rekommenderat av specifikationen.
//Referenser från SMIL till ncc/content. Om en par har system-required="pagenumber-on", ska man då kontrollera att pekaren till ncc:n (för en ncc-only-bok) går till en <span class="page-*"/>?
//Generellt vore det ju kul om man kunde separera fel och varningar på samma sätt som validatorn gör.
//Det blev en diger lista...

class ValidatorImplD202 extends ValidatorImplAbstract implements Validator, ErrorHandler {
	
	
	/**
	 * Constructor.
	 */
	ValidatorImplD202(){
		super(FilesetType.DAISY_202);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(fileset);
		validate();
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(manifest);
		validate();
	}

	/**
	 * Set validation resources (schemas, delegates) that are hardcoded for this Fileset type.
	 */
	private void setStaticResources() throws ValidatorException{
		try{
			//schemas:
			//these are compound RNG and SCH; super will find that out and create multiple validators per schema
			setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN"),"org.daisy.util.fileset.impl.D202SmilFileImpl");
			setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN"),"org.daisy.util.fileset.impl.D202NccFileImpl");
			
			//delegates:								
			setDelegate(new InterDocURICheckerD202Delegate());									
			setDelegate(new FilesetFileTypeRestrictionDelegate(getAllowedFilesetFileTypes()));
						 
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
		}
	}

	/**
	 * Perform additional validation other than that done through the call to super.validate (which executes any schemas and delegates registered on super)
	 */
	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		FilesetFile mCurrentlyValidatedMember = null;
		for (Iterator iter = mFileset.getLocalMembers().iterator(); iter.hasNext();) {
			try{
				mCurrentlyValidatedMember = (FilesetFile) iter.next();
				
				if(mCurrentlyValidatedMember instanceof D202NccFile) {						
					nccFile((D202NccFile)mCurrentlyValidatedMember);								
				}else if(mCurrentlyValidatedMember instanceof D202TextualContentFile){
					textualContentFile((D202TextualContentFile)mCurrentlyValidatedMember);
				}else if(mCurrentlyValidatedMember instanceof D202MasterSmilFile){
					masterSmilFile((D202MasterSmilFile)mCurrentlyValidatedMember);
				}else if(mCurrentlyValidatedMember instanceof AudioFile) {
					audioFile((AudioFile)mCurrentlyValidatedMember);
				}					
			}catch (Exception e) {
				mValidatorListener.exception(this, e);
				//we had an exception on a specific file in the loop
				//we want to try to continue
				//TODO maybe catch more specifically than Exception 					
				this.mValidatorListener.report(this,new ValidatorErrorMessage(
						mCurrentlyValidatedMember.getFile().toURI(),
						"Exception: " + e.getClass().getSimpleName() +": " + e.getMessage()));
			}	
		}//for mFileset.getLocalMembers()		
		
		//do a loop through spine and check timing					
		D202NccFile ncc = (D202NccFile) mFileset.getManifestMember();			
		long calculatedTotalTimeMillis = 0;						
		for (Iterator spineIterator = ncc.getSpineItems().iterator(); spineIterator.hasNext();) {
			D202SmilFile smil = (D202SmilFile) spineIterator.next();

			//test totalElapsedTime
			if (smil.getStatedTotalElapsedTime()!=null) {
				//recommended syntax: hh:mm:ss, so round to whole seconds
				SmilClock calculatedTotalElapsedTime = new SmilClock(calculatedTotalTimeMillis);
				if(calculatedTotalElapsedTime.secondsValueRounded() != smil.getStatedTotalElapsedTime().secondsValueRounded()) {					
					this.mValidatorListener.report(this,new ValidatorErrorMessage(smil.getFile().toURI(),
							"expected total elapsed time " + calculatedTotalElapsedTime .toString() 
							+" but found "+ smil.getStatedTotalElapsedTime().toString()));						
				}
				
			}		
			
			//up the totaltime counter
			calculatedTotalTimeMillis += smil.getCalculatedDuration().millisecondsValue();
			
			//test timeInThisSmil
			if (smil.getStatedDuration()!=null) {	
				//recommended syntax: hh:mm:ss, so round to whole seconds
				if(smil.getCalculatedDuration().secondsValueRounded() != smil.getStatedDuration().secondsValueRounded()) {					
					this.mValidatorListener.report(this,new ValidatorErrorMessage(smil.getFile().toURI(),
							"expected duration "+ smil.getCalculatedDuration().toString()
							+" but found "+ smil.getStatedDuration().toString()));						
				}
			}	
		}//spineIterator		
		
		//test ncc stated totaltime against calculated spine totaltime
		SmilClock calculatedTotalTime = new SmilClock(calculatedTotalTimeMillis);
		//recommended syntax: hh:mm:ss, so round to whole seconds

		//#1770791 NPE if ncc.getStatedDuration() is null
		if(ncc.getStatedDuration()!=null) {
			if(calculatedTotalTime.secondsValueRounded() != ncc.getStatedDuration().secondsValueRounded()) {					
				this.mValidatorListener.report(this,new ValidatorErrorMessage(ncc.getFile().toURI(),
					"expected total time " + calculatedTotalTime.toString() 
					+" but found "+ ncc.getStatedDuration().toString()));						
			}
		}else{
			//we couldnt compare calculated to stated since ncc.getStatedDuration() is null
			//the absence of this meta element is reported elsewhere so be silent on that
			this.mValidatorListener.report(this,new ValidatorWarningMessage(ncc.getFile().toURI(), 
					"Could not compare calculated duration to stated duration since this information is missing in the NCC"));									
		}
		
	}
	
	private void masterSmilFile(D202MasterSmilFile file) {
		// TODO 
		
	}

	/**
	 * Performs non-rng/sch tests pertaining specifically to content docs
	 */
	private void textualContentFile(D202TextualContentFile contentFile) {
		if (!contentFile.hasHierarchicalHeadingSequence()){
			this.mValidatorListener.report(this,new ValidatorErrorMessage(contentFile.getFile().toURI(),"incorrect heading hierarchy"));
		}				
	}

	/**
	 * Performs non-rng/sch tests pertaining specifically to NCC files
	 */
	private void nccFile(D202NccFile ncc) {
		//check the heading hirearchy
		if (!ncc.hasHierarchicalHeadingSequence()){
			this.mValidatorListener.report(this,new ValidatorErrorMessage(ncc.getFile().toURI(),"incorrect heading hierarchy"));
		}
	}
	
	/**
	 * Performs non-rng/sch tests pertaining specifically to audiofiles
	 */
	private void audioFile(AudioFile member) {
		if (member instanceof Mp3File){
			Mp3File mp3file = (Mp3File) member;
			try {
				if(!mp3file.isParsed())mp3file.parse();
				if (mp3file.hasID3v2()) {
					this.mValidatorListener.inform(this,mp3file.getFile().toURI()+ " has ID3 tag");
				}
				if (!mp3file.isMono()) {
					this.mValidatorListener.report(this,new ValidatorWarningMessage(mp3file.getFile().toURI(),"file is not single channel"));
				}
				if (mp3file.isVbr()) {
					this.mValidatorListener.report(this,new ValidatorWarningMessage(mp3file.getFile().toURI(),"file uses variable bit rate (VBR)"));				
				}
			} catch (Exception e) {
				this.mValidatorListener.report(this,new ValidatorErrorMessage(mp3file.getFile().toURI(),"Exception: " + e.getClass().getSimpleName() +": " +  e.getMessage()));
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();
		//mCurrentlyValidatedMember = null;
		//TODO reset local member vars
	}

	
	/**
	 * We override the superclass impl of ErrorHandler#Error in
	 * order to redirect SchematronMessages specific to 2.02 - all sch msgs are 
	 * reported through #error, but the Schematron schema author may have flagged
	 * them as having another severity level, using a scheme within the 
	 * SchematronMessage syntax. 
	 */
	public void error(SAXParseException exception) throws SAXException {
		
		if(SchematronMessage.isMessage(exception.getMessage())) {
			try {
				URI uri = new URI(exception.getSystemId());
				SchematronMessage sm = new SchematronMessage(exception.getMessage());
				if(sm.getMessage("dtb").equals("d202")){		
					//we recognize the scheme from the 202 schemas in catalog					
					String msg = sm.getMessage("msg");
					int line = exception.getLineNumber();
					int col = exception.getColumnNumber();										
					if(sm.getMessage("type").equals("warning")) {
						mValidatorListener.report(this,new ValidatorWarningMessage(uri,msg,line,col));
					}else{
						mValidatorListener.report(this,new ValidatorErrorMessage(uri,msg,line,col));
					}	
					return;
				}
				//else, we dont know the scheme of this schematron message, so fall back to super				
			} catch (Exception e) {
				if(mDebugMode) {
					System.out.println("DEBUG: Exception in ValidatorImplD202#error: " + e.getMessage());
				}
			}			
		}
		super.error(exception);		
	}
	
	private Set<String> getAllowedFilesetFileTypes() {
		Set<String> allowedTypes = new HashSet<String>();
		allowedTypes.add("org.daisy.util.fileset.impl.D202NccFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.D202SmilFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.D202MasterSmilFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.D202TextualContentFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.Mp3FileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.Mp2FileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.WavFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.JpgFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.GifFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.PngFileImpl");
		allowedTypes.add("org.daisy.util.fileset.impl.CssFileImpl");
		return allowedTypes;
	}
}


///**
//* Validate a Daisy 2.02 DTB. This impl does not claim to do a complete conformance validation.
//*/
//private void validateOld() throws ValidatorException, ValidatorNotSupportedException {				
//	/*
//	 * We are assuming that DTD validation (using subset DTDs) and file existance tests 
//	 * has already been run during the Fileset instantiation.
//	 *
//	 */
//	
//	//TODO check on FilesetImpl whether DTD val really was on: else report to errorHandler or something
//	
//	//prepare javax.xml.validation objects for the filetypes
//	//that we have rng and/or sch for.
//	Map<String,javax.xml.validation.Validator> relaxngValidators = new HashMap<String,javax.xml.validation.Validator>();		//<FilesetFileSubClass.class,jaxpValidator>
//	Map<String,javax.xml.validation.Validator> schematronValidators = new HashMap<String,javax.xml.validation.Validator>();	    //<FilesetFileSubClass.class,jaxpValidator>
//	
//	
//	try{
//		SchemaFactory relaxngFactory = SchemaFactory.newInstance(SchemaLanguageConstants.RELAXNG_NS_URI);
//		relaxngFactory.setErrorHandler(this);
//		relaxngFactory.setResourceResolver(CatalogEntityResolver.getInstance());
//		
//		SchemaFactory schematronFactory = SchemaFactory.newInstance(SchemaLanguageConstants.SCHEMATRON_NS_URI);
//		schematronFactory.setErrorHandler(this);
//		schematronFactory.setResourceResolver(CatalogEntityResolver.getInstance());
//		
//		//populate the validator maps with URLs
//		relaxngValidators.put("D202SmilFileImpl", 
//				relaxngFactory.newSchema(
//						CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN"))
//						.newValidator());
//		schematronValidators.put("D202SmilFileImpl", 
//				schematronFactory.newSchema(
//						CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN"))
//						.newValidator());
//
////		relaxngValidators.put("D202MasterSmilFileImpl", 
////				relaxngFactory.newSchema(
////						CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG msmil v2.02//EN"))
////						.newValidator());
//		
//		relaxngValidators.put("D202NccFileImpl", 
//				relaxngFactory.newSchema(
//						CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN"))
//						.newValidator());
//		schematronValidators.put("D202NccFileImpl", 
//				schematronFactory.newSchema(
//						CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN"))
//						.newValidator());
//		
//		
//		//loop through the entire fileset and apply appropriate tests
//		
//		for (Iterator iter = mFileset.getLocalMembers().iterator(); iter.hasNext();) {
//			try{
//				mCurrentlyValidatedMember = (FilesetFile) iter.next();
//				if(mCurrentlyValidatedMember instanceof XmlFile) {
//					XmlFile xmlFile = (XmlFile) mCurrentlyValidatedMember;
//					if(xmlFile.isWellformed()){
//						//generic schema tests
//						if(relaxngValidators.containsKey(mCurrentlyValidatedMember.getClass().getSimpleName())) {
//							if(mDebugMode) {
//								System.out.println("DEBUG: ValidatorImplD202 validating " 
//										+ xmlFile.getFile().getName() + " against RNG");
//							}
//							javax.xml.validation.Validator relaxngValidator = 
//								relaxngValidators.get
//									(mCurrentlyValidatedMember.getClass().getSimpleName()); 
//							relaxngValidator.validate(xmlFile.asStreamSource());								
//						}
//						if(schematronValidators.containsKey(mCurrentlyValidatedMember.getClass().getSimpleName())) {
//							if(mDebugMode) {
//								System.out.println("DEBUG: ValidatorImplD202 validating " 
//										+ xmlFile.getFile().getName() + " against SCH");
//							}
//							javax.xml.validation.Validator schematronValidator = 
//								relaxngValidators.get //because of compoundness, schval requires clean sch?
//									(mCurrentlyValidatedMember.getClass().getSimpleName()); 
//							schematronValidator.validate(xmlFile.asStreamSource());	
//						}
//						
//						//interdocument link validity
//						ValidatorUtils.isInterDocFragmentLinkValid(xmlFile, this);	
//						
//						//specific tests on XmlFile subclasses
//						if(xmlFile instanceof D202NccFile) {						
//							nccFile((D202NccFile)xmlFile);								
//						}else if(xmlFile instanceof D202TextualContentFile){
//							textualContentFile((D202TextualContentFile)xmlFile);
//						}else if(xmlFile instanceof D202MasterSmilFile){
//							masterSmilFile((D202MasterSmilFile)xmlFile);
//						}
//					}//if .isWellformed (else fileset instantiation already reported this)
//				}//(currentMember instanceof XmlFile)
//				else if(mCurrentlyValidatedMember instanceof AudioFile) {
//					audioFile((AudioFile)mCurrentlyValidatedMember);
//				}
//			}catch (Exception e) {
//				//we had an exception on a specific file in the loop
//				//we want to try to continue
//				//TODO maybe catch more specifically than Exception 					
//				this.mValidatorListener.report(this,new ValidatorErrorMessage(
//						mCurrentlyValidatedMember.getFile().toURI(),
//						"Exception: " + e.getClass().getSimpleName() +": " + e.getMessage()));
//			}	
//		}//mFileset.getLocalMembers()
//		
//		
//		//do a loop through spine and check timing					
//		D202NccFile ncc = (D202NccFile) mFileset.getManifestMember();			
//		long calculatedTotalTimeMillis = 0;						
//		for (Iterator spineIterator = ncc.getSpineItems().iterator(); spineIterator.hasNext();) {
//			D202SmilFile smil = (D202SmilFile) spineIterator.next();
//
//			//test totalElapsedTime
//			if (smil.getStatedTotalElapsedTime()!=null) {
//				//recommended syntax: hh:mm:ss, so round to whole seconds
//				SmilClock calculatedTotalElapsedTime = new SmilClock(calculatedTotalTimeMillis);
//				if(calculatedTotalElapsedTime.secondsValueRounded() != smil.getStatedTotalElapsedTime().secondsValueRounded()) {					
//					this.mValidatorListener.report(this,new ValidatorErrorMessage(smil.getFile().toURI(),
//							"expected total elapsed time " + calculatedTotalElapsedTime .toString() 
//							+" but found "+ smil.getStatedTotalElapsedTime().toString()));						
//				}
//				
//			}		
//			
//			//up the totaltime counter
//			calculatedTotalTimeMillis += smil.getCalculatedDuration().millisecondsValue();
//			
//			//test timeInThisSmil
//			if (smil.getStatedDuration()!=null) {	
//				//recommended syntax: hh:mm:ss, so round to whole seconds
//				if(smil.getCalculatedDuration().secondsValueRounded() != smil.getStatedDuration().secondsValueRounded()) {					
//					this.mValidatorListener.report(this,new ValidatorErrorMessage(smil.getFile().toURI(),
//							"expected duration "+ smil.getCalculatedDuration().toString()
//							+" but found "+ smil.getStatedDuration().toString()));						
//				}
//			}	
//		}//spineIterator		
//		
//		//test ncc stated totaltime against calculated spine totaltime
//		SmilClock calculatedTotalTime = new SmilClock(calculatedTotalTimeMillis);
//		//recommended syntax: hh:mm:ss, so round to whole seconds
//		if(calculatedTotalTime.secondsValueRounded() != ncc.getStatedDuration().secondsValueRounded()) {					
//			this.mValidatorListener.report(this,new ValidatorErrorMessage(ncc.getFile().toURI(),
//					"expected total time " + calculatedTotalTime.toString() 
//					+" but found "+ ncc.getStatedDuration().toString()));						
//		}
//		
//	}catch (Exception e) {
//		throw new ValidatorException(e.getMessage(),e);
//	}
//					
//}