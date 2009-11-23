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
package org_pef_dtbook2pef.system;

import java.io.File;

import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Abstract base for tasks.
 * InternalTask is an interface designed for a transformer internal 
 * conversion pipe line. Tasks are chained by file exchange.
 * 
 * Based on se_tpb_dtbookFix.Executor by Markus Gylling
 * @author Joel Håkansson
 */
public abstract class InternalTask {
	protected String name = null;

	/**
	 * Constructor.
	 * @param parameters
	 * @param niceName
	 */
	protected InternalTask(String name) {
		this.name = name;
	}

	/**
	 * The name of the internal task.
	 * @return returns the name of this internal task
	 */
	public String getName() {
		return name;
	}

	/**
	 * Apply the task to <code>input</code> and place the result in <code>output</code>.
	 * @param input
	 * @param output
	 * @param options
	 * @throws TransformerRunException
	 */
	public abstract void execute(File input, File output) throws TransformerRunException;

}
