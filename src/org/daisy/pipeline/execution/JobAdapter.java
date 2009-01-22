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
package org.daisy.pipeline.execution;

import java.net.URL;
import java.util.Map;

/**
 * An adapter used to extract from a job object all the information required for
 * its execution.
 * 
 * <p>
 * This class is used to adapt any representation of a Pipeline job object to be
 * accepted by an implementation of the {@link AbstractJobExecutionService}
 * handling the corresponding Pipeline job type.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public interface JobAdapter<T> {

	/**
	 * Returns the URL of the Pipeline script the given job is based on.
	 * 
	 * @param job
	 *            the job to adapt.
	 * @return the URL of the Pipeline script the given job is based on.
	 */
	public URL getScriptURL(T job);

	/**
	 * Returns a non-null map of the parameters of the given job.
	 * 
	 * @param job
	 *            the job to adapt.
	 * @return the parameters of the given job.
	 */
	public Map<String, String> getParameters(T job);

	/**
	 * Creates a new execution listener for the given job.
	 * 
	 * @param job
	 *            the job to adapt.
	 * @return a new execution listener for the given job, or <code>null</code>
	 *         if not supported.
	 */
	public JobListener getJobListener(T job);

	/**
	 * Returns a map storing a generic execution context. This can be used to
	 * pass information used at execution time by subclasses of
	 * {@link AbstractJobExecutionService} (such as log files location, etc).
	 * 
	 * @param job
	 *            the job to adapt.
	 * @return a map storing a generic execution context, or <code>null</code>
	 */
	public Map<String, Object> getContext(T job);

}
