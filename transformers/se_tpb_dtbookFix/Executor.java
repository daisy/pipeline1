/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package se_tpb_dtbookFix;

import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Abstract base for all executors.
 * Any executor is a member of 1.-n ExecutorCategory
 * @author Markus Gylling
 */
abstract class Executor {
	protected Map<String, String> mParameters = null;
	protected String mNiceName = null;
		
	/**
	 * Constructor.
	 * @param parameters
	 * @param niceName
	 */
	Executor(Map<String, String> parameters, String niceName) {
		mParameters = parameters;
		mNiceName = niceName;
	}
		
	/**
	 * Does this Executor support inparam DTBook version?
	 */
	abstract boolean supportsVersion(String version);
	
	/**
	 * A user friendly name of this Executor.
	 */
	String getNiceName() {
		return mNiceName;
	}
	
	/**
	 * Apply the executor to <code>source</code> and place the output in <code>result</code>.
	 */
	abstract void execute(Source source, Result result) throws TransformerRunException;
	
}
