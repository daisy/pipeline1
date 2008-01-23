package org.daisy.util.fileset.validation;

import java.io.File;
import java.net.URI;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;

import com.adobe.epubcheck.api.EpubCheck;
import com.adobe.epubcheck.api.Report;

/**
 * A bridge for the EpubCheck library to realize a OPS/EPUB {@link org.daisy.util.fileset.validation.Validator}
 * @author Markus Gylling
 */
class ValidatorImplEpubCheck extends ValidatorImplAbstract implements Validator, Report  {
	
	private URI mInputEpub = null;
	
	/**
	 * Constructor.
	 */
	ValidatorImplEpubCheck(){
		super(FilesetType.OPS_EPUB);
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		mInputEpub = fileset.getManifestMember().getFile().toURI();
		super.validate(fileset);
		validate();	
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI epub) throws ValidatorException, ValidatorNotSupportedException {
		mInputEpub = epub;		
		super.validate(epub);
		validate();		
	}
	
	
	@SuppressWarnings("unused")
	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		try{
			EpubCheck epubCheck = new EpubCheck(new File(mInputEpub), this);
			epubCheck.validate();
		}catch (Exception e){
			this.mValidatorListener.report(this, 
					new ValidatorSevereErrorMessage(
							mInputEpub,e.getMessage(),-1,-1));
			//throw new ValidatorException(e.getMessage(),e);
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();		
		mInputEpub = null;
	}


	/*
	 * (non-Javadoc)
	 * @see com.adobe.epubcheck.api.Report#error(java.lang.String, int, java.lang.String)
	 */
	public void error(String resource, int line, String message) {
		this.mValidatorListener.report(this, 
				new ValidatorErrorMessage(
						FilenameOrFileURI.toURI(resource),message,line,-1));		
	}

	/*
	 * (non-Javadoc)
	 * @see com.adobe.epubcheck.api.Report#warning(java.lang.String, int, java.lang.String)
	 */
	public void warning(String resource, int line, String message) {
		this.mValidatorListener.report(this, 
				new ValidatorWarningMessage(
						FilenameOrFileURI.toURI(resource),message,line,-1));
	}

}