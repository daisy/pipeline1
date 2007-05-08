package org.daisy.util.fileset.validation;

import java.io.File;
import java.net.URI;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.zedval.ZedVal;
import org.daisy.zedval.engine.ErrorMessage;
import org.daisy.zedval.engine.FailureMessage;
import org.daisy.zedval.engine.ZedContext;
import org.daisy.zedval.engine.ZedContextException;
import org.daisy.zedval.engine.ZedFileInitializationException;
import org.daisy.zedval.engine.ZedMessage;
import org.daisy.zedval.engine.ZedReporter;
import org.daisy.zedval.engine.ZedReporterException;
import org.daisy.zedval.engine.ZedTest;

/**
 * A bridge for the ZedVal library to realize a Z39.86 {@link org.daisy.util.fileset.validation.Validator}
 * @author Markus Gylling
 */
class ValidatorImplZedVal extends ValidatorImplAbstract implements Validator, ZedReporter  {

	private ZedContext mCurrentZedContext = null;
	private URI mInputOpf = null;

	/**
	 * Constructor.
	 */
	ValidatorImplZedVal(){
		super(FilesetType.Z3986);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		/*
		 * Note - for this bridge, #validate(URI manifest) is the preferred method to call.
		 * Else, a Fileset instance will be created in super, which is not actually needed...
		 */
		mInputOpf = fileset.getManifestMember().getFile().toURI();
		super.validate(fileset);
		validate();	
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		/*
		 * Since this is a bridge wrapper, we really dont need to instantiate the super Fileset - 
		 * ZedVal does this in its own way.
		 */
		//super.validate(manifest);
		mInputOpf = manifest;		
		validate();		
	}

	private void validate() throws ValidatorException, ValidatorNotSupportedException {
		try{
			ZedVal zedval = new ZedVal();
			mValidatorListener.inform(this, "Validating with ZedVal version " + zedval.getVersion());
			try {
				zedval.setReporter(this);
				zedval.validate(new File(mInputOpf));
			} catch (ZedContextException e) {
				throw new ValidatorException(e.getMessage(),e);
			} catch (ZedFileInitializationException e) {
				throw new ValidatorException(e.getMessage(),e);
			}
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.Validator#reset()
	 */
	public void reset() {
		super.reset();
		mCurrentZedContext = null;
		mInputOpf = null;
		//TODO reset local member vars
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.zedval.engine.ZedReporter#addMessage(org.daisy.zedval.engine.ZedMessage)
	 */
	public void addMessage(ZedMessage zm) throws ZedReporterException {
		//find out what particular ZedMessage we are dealing with,
		//and create the most appropriate ValidatorMessage
		if(zm instanceof FailureMessage) {
			//we have an invalidity report
			FailureMessage fm = (FailureMessage)zm;
			
			URI f = fm.getFile().toURI();
			String msg = fm.getText();
			int line = (int)fm.getLine();
			int col = (int)fm.getColumn();
			
			if(fm.getTest()!=null && fm.getTest().getType() == ZedTest.RECOMMENDATION) {
				mValidatorListener.report(this, new ValidatorWarningMessage(f,msg,line,col));
			}else{
				mValidatorListener.report(this, new ValidatorErrorMessage(f,msg,line,col));
			}			
		} else{
			//we have a failure-to-validate (ErrorMessage)
			ErrorMessage em = (ErrorMessage) zm;
			//TODO we may need to cast further down and get precise info
			ValidatorException ve = new ValidatorException(em.getText());
			mValidatorListener.exception(this, ve);			
		}				
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.zedval.engine.ZedReporter#close()
	 */
	public void close() throws ZedReporterException {
		//nothing to see here, please move along
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.zedval.engine.ZedReporter#initialize()
	 */
	public void initialize() throws ZedReporterException {
		//nothing to see here, please move along		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.zedval.engine.ZedReporter#setContext(org.daisy.zedval.engine.ZedContext)
	 */
	public void setContext(ZedContext c) {
		mCurrentZedContext = c;	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.interfaces.FilesetErrorHandler#error(org.daisy.util.fileset.exception.FilesetFileException)
	 */	
	public void error(FilesetFileException ffe) throws FilesetFileException {
		/*
		 * We override the superclass Fileset errorlistener in order
		 * *not* to report anything outwards during Fileset instantiation.
		 * Otherwise we would end up with duplicate messages, since ZedVals
		 * fileset and org.daisy.utils fileset overlap.
		 */
				
	}
}