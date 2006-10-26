package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.validation.SchemaFactory;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.audio.AudioFile;
import org.daisy.util.fileset.interfaces.audio.Mp3File;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202MasterSmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.fileset.interfaces.xml.d202.D202TextualContentFile;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.daisy.util.xml.validation.SchematronMessage;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An impl of org.daisy.util.fileset.validation.Validator for Daisy 2.02 DTBs.
 * <p>This impl does not claim to do a complete conformance validation.</p>
 * @author Markus Gylling
 */
class ValidatorImplD202 extends ValidatorImplAbstract implements Validator, ErrorHandler {
	FilesetFile mCurrentlyValidatedMember = null;
	
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
	 * Validate a Daisy 2.02 DTB. This impl does not claim to do a complete conformance validation.
	 */
	private void validate() throws ValidatorException, ValidatorNotSupportedException {				
		/*
		 * This is basically a replacement routine for what
		 * once was dtb.validation.errorscout.
		 * We are assuming that DTD validation (using subset DTDs) and file existance tests 
		 * has already been run during the Fileset instantiation.
		 *
		 */
		
		//TODO check on FilesetImpl whether DTD val really was on: else report to errorHandler or something
		
		//prepare javax.xml.validation objects for the filetypes
		//that we have rng and/or sch for.
		Map relaxngValidators = new HashMap();		//<FilesetFileSubClass.class,jaxpValidator>
		Map schematronValidators = new HashMap();	//<FilesetFileSubClass.class,jaxpValidator>
		
		
		try{
			SchemaFactory relaxngFactory = SchemaFactory.newInstance(SchemaLanguageConstants.RELAXNG_NS_URI);
			relaxngFactory.setErrorHandler(this);
			relaxngFactory.setResourceResolver(CatalogEntityResolver.getInstance());
			
			SchemaFactory schematronFactory = SchemaFactory.newInstance(SchemaLanguageConstants.SCHEMATRON_NS_URI);
			schematronFactory.setErrorHandler(this);
			schematronFactory.setResourceResolver(CatalogEntityResolver.getInstance());
			
			//populate the validator maps with URLs
			relaxngValidators.put("D202SmilFileImpl", 
					relaxngFactory.newSchema(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN"))
							.newValidator());
			schematronValidators.put("D202SmilFileImpl", 
					schematronFactory.newSchema(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG smil v2.02//EN"))
							.newValidator());

//			relaxngValidators.put("D202MasterSmilFileImpl", 
//					relaxngFactory.newSchema(
//							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG msmil v2.02//EN"))
//							.newValidator());
			
			relaxngValidators.put("D202NccFileImpl", 
					relaxngFactory.newSchema(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN"))
							.newValidator());
			schematronValidators.put("D202NccFileImpl", 
					schematronFactory.newSchema(
							CatalogEntityResolver.getInstance().resolveEntityToURL("-//DAISY//RNG ncc v2.02//EN"))
							.newValidator());
			
			
			//loop through the entire fileset and apply appropriate tests
			
			for (Iterator iter = mFileset.getLocalMembers().iterator(); iter.hasNext();) {
				try{
					mCurrentlyValidatedMember = (FilesetFile) iter.next();
					if(mCurrentlyValidatedMember instanceof XmlFile) {
						XmlFile xmlFile = (XmlFile) mCurrentlyValidatedMember;
						if(xmlFile.isWellformed()){
							//generic schema tests
							if(relaxngValidators.containsKey(mCurrentlyValidatedMember.getClass().getSimpleName())) {
								if(mDebugMode) {
									System.out.println("DEBUG: ValidatorImplD202 validating " 
											+ xmlFile.getFile().getName() + " against RNG");
								}
								javax.xml.validation.Validator relaxngValidator = 
									(javax.xml.validation.Validator)relaxngValidators.get
										(mCurrentlyValidatedMember.getClass().getSimpleName()); 
								relaxngValidator.validate(xmlFile.asStreamSource());								
							}
							if(schematronValidators.containsKey(mCurrentlyValidatedMember.getClass().getSimpleName())) {
								if(mDebugMode) {
									System.out.println("DEBUG: ValidatorImplD202 validating " 
											+ xmlFile.getFile().getName() + " against SCH");
								}
								javax.xml.validation.Validator schematronValidator = 
									(javax.xml.validation.Validator)relaxngValidators.get
										(mCurrentlyValidatedMember.getClass().getSimpleName()); 
								schematronValidator.validate(xmlFile.asStreamSource());	
							}
							
							//interdocument link validity
							ValidatorUtils.isInterDocFragmentLinkValid(xmlFile, this);	
							
							//specific tests on XmlFile subclasses
							if(xmlFile instanceof D202NccFile) {						
								nccFile((D202NccFile)xmlFile);								
							}else if(xmlFile instanceof D202TextualContentFile){
								textualContentFile((D202TextualContentFile)xmlFile);
							}else if(xmlFile instanceof D202MasterSmilFile){
								masterSmilFile((D202MasterSmilFile)xmlFile);
							}
						}//if .isWellformed (else fileset instantiation already reported this)
					}//(currentMember instanceof XmlFile)
					else if(mCurrentlyValidatedMember instanceof AudioFile) {
						audioFile((AudioFile)mCurrentlyValidatedMember);
					}
				}catch (Exception e) {
					//we had an exception on a specific file in the loop
					//we want to try to continue
					//TODO maybe catch more specifically than Exception 					
					this.mValidatorListener.report(this,new ValidatorErrorMessage(
							mCurrentlyValidatedMember.getFile().toURI(),
							"Exception: " + e.getClass().getSimpleName() +": " + e.getMessage()));
				}	
			}//mFileset.getLocalMembers()
			
			
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
			if(calculatedTotalTime.secondsValueRounded() != ncc.getStatedDuration().secondsValueRounded()) {					
				this.mValidatorListener.report(this,new ValidatorErrorMessage(ncc.getFile().toURI(),
						"expected total time " + calculatedTotalTime.toString() 
						+" but found "+ ncc.getStatedDuration().toString()));						
			}
			
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
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
					this.mValidatorListener.report(this,new ValidatorWarningMessage(mp3file.getFile().toURI(),"file has ID3 tag"));
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
		mCurrentlyValidatedMember = null;
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
		
		URI uri = mCurrentlyValidatedMember.getFile().toURI();
		
		if(SchematronMessage.isMessage(exception.getMessage())) {
			try {
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
			} catch (ValidationException e) {
				if(mDebugMode) {
					System.out.println("DEBUG: Exception in ValidatorImplD202#error: " + e.getMessage());
				}
			}			
		}
		super.error(exception);		
	}

	
//	Hur är det med ordningen på hx i ncc? (Ingen h3 direkt efter h1 etc.).
//
//			ErrorScout är för petig när det gäller viss metadata, t.ex. måste Content-Type ha rätt case.
//
//			ncc:totalElapsedTime och ncc:timeInThisSmil i SMIL måste ha sekundsyntax (inga millisekunder). Det är bara rekommenderat av specifikationen.
//
//			Referenser från SMIL till ncc/content. Om en par har system-required="pagenumber-on", ska man då kontrollera att pekaren till ncc:n (för en ncc-only-bok) går till en <span class="page-*"/>?
//
//			Generellt vore det ju kul om man kunde separera fel och varningar på samma sätt som validatorn gör.
//
//			Det blev en diger lista...

}
