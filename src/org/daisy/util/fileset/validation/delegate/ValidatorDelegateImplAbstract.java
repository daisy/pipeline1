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
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;

/**
 * An abstract base for ValidatorDelegate that a concrete impl may choose to extend for convenience.
 * @author Markus Gylling
 */
public abstract class ValidatorDelegateImplAbstract implements ValidatorDelegate {

	protected Validator mValidator = null; //the registered Validator
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#setValidator(org.daisy.util.fileset.validation.Validator)
	 */
	public void setValidator(Validator validator) {
		mValidator = validator;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.delegate.ValidatorDelegate#execute()
	 */
	public void execute(@SuppressWarnings("unused")Fileset fileset) throws ValidatorNotSupportedException, ValidatorException {
	  //not much generic stuff that can be done here. An overrider will survive without
	  //calling super, but is recommended to do so in case we extend this layer.			
	  if(null==mValidator) throw new ValidatorException("no registered validator");			  
	}
	
	protected final void report(ValidatorMessage message) {
		ValidatorListener listener = mValidator.getListener();
		listener.report(mValidator, message);
	}
		
}
