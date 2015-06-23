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
package org.daisy.util.fileset.validation.delegate.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.FilesetType;
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
	public boolean isFilesetTypeSupported(@SuppressWarnings("unused")FilesetType type) {		
		return true;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegateImplAbstract#execute(org.daisy.util.fileset.interfaces.Fileset)
	 */
	@Override
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {		
		super.execute(fileset);
		for (Iterator<FilesetFile> iter = fileset.getLocalMembers().iterator(); iter.hasNext();) {
			FilesetFile ffile = iter.next();
			if(!mAllowedFilesetFileTypes.contains(ffile.getClass().getName())) {
				mValidator.getListener().report(mValidator, new ValidatorMessage(ffile.getFile().toURI(), 
						ffile.getName() + " is not an allowed file type in " + fileset.getFilesetType().toNiceNameString()));
			}
		}
	}
	
}
