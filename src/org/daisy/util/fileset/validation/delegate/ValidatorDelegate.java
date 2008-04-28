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
package org.daisy.util.fileset.validation.delegate;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;

/**
 * An inferface for performing delegated fileset validation tasks.
 * @author Markus Gylling
 */
public interface ValidatorDelegate {
	
	/**
	 * Register the Validator that owns this delegate.
	 */
	public void setValidator(Validator validator);
	
	/**
	 * Execute the task delegated by the Validator. The delegate
	 * will use the ValidatorListener registered with the Validator
	 * to report any findings.
	 * @param fileset the fileset to perform validation on 
	 */
	public void execute(Fileset fileset) throws ValidatorNotSupportedException, ValidatorException;
	
	/**
	 * Is inparam fileset supported by this ValidatorDelegate?
	 */
	public boolean isFilesetTypeSupported(FilesetType type);
}
