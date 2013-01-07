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

import java.io.File;
import java.net.URI;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;

import com.adobe.epubcheck.api.EpubCheck;
import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.util.FeatureEnum;

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
	public void error(String resource, int line, int column, String message) {
		this.mValidatorListener.report(this, 
				new ValidatorErrorMessage(
						FilenameOrFileURI.toURI(resource),message,line,column));		
	}

	/*
	 * (non-Javadoc)
	 * @see com.adobe.epubcheck.api.Report#warning(java.lang.String, int, java.lang.String)
	 */
	public void warning(String resource, int line, int column, String message) {
		this.mValidatorListener.report(this, 
				new ValidatorWarningMessage(
						FilenameOrFileURI.toURI(resource),message,line,column));
	}

	public void exception(String arg0, Exception arg1) {
		// TODO Auto-generated method stub

	}

	public int getErrorCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getExceptionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getWarningCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void info(String resource, FeatureEnum feature, String value) {
		// TODO Auto-generated method stub

	}

}