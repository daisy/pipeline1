package org.daisy.util.fileset.validation.delegate.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;

/**
 * A delegate that will check that all fileset members are of types that match a runtime registered set of types.
 * @author Markus Gylling
 */
public class FilesetFileTypeRestrictionDelegate extends ValidatorDelegateImplAbstract {
	Set<String> mAllowedFilesetFileTypes = null;
	
	 /**
	  * Constructor.
	  * @param allowedTypes A set of fully qualified FilesetFile classnames
	  */
	public FilesetFileTypeRestrictionDelegate(Set<String> allowedTypes) {
		mAllowedFilesetFileTypes = new HashSet<String>();
		if(null!=allowedTypes)mAllowedFilesetFileTypes.addAll(allowedTypes);
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#isFilesetTypeSupported(org.daisy.util.fileset.FilesetType)
	 */
	public boolean isFilesetTypeSupported(FilesetType type) {		
		return true;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	@Override
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {		
		super.execute(fileset);
		for (Iterator iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = (FilesetFile) iter.next();
			if(!mAllowedFilesetFileTypes.contains(ffile.getClass().getName())) {
				mValidator.getListener().report(mValidator, new ValidatorMessage(ffile.getFile().toURI(), 
						ffile.getName() + " is not an allowed file type in " + fileset.getFilesetType().toNiceNameString()));
			}
		}
	}
	
}
