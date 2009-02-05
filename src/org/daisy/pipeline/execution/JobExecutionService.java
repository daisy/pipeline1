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
import java.util.concurrent.Future;

/**
 * 
 * A service to execute Pipeline jobs.
 * 
 * <p>
 * A job is represented either as an instance of the generic type handled by
 * this service or as an association of a Script URL with a map of parameters.
 * </p>
 * 
 * <p>
 * Additionally, callers can provide a {@link JobListener} to be notified of
 * execution events and messages.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public interface JobExecutionService<T> {

	/**
	 * Executes the Pipeline job represented by the argument.
	 * 
	 * @param job
	 *            an object representing a Pipeline job.
	 * @return a cancelable future returning <code>null</code> when the job is
	 *         done
	 */
	public Future<?> execute(T job);

	/**
	 * Executes the Pipeline job represented by the argument, and listens to the
	 * execution.
	 * 
	 * @param job
	 *            an object representing a Pipeline job.
	 * @param listener
	 *            a listener to the job execution.
	 * @return a cancelable future returning <code>null</code> when the job is
	 *         done
	 */
	public Future<?> execute(T job, JobListener listener);

	/**
	 * Executes the Pipeline job defined by the given Script URL and parameters,
	 * and listens to the execution.
	 * 
	 * @param scriptURL
	 *            the URL to a Pipeline Script file.
	 * @param parameters
	 *            the parameters to configure the Pipeline Job.
	 * @param listener
	 *            a listener to the job execution.
	 * @return a cancelable future returning <code>null</code> when the job is
	 *         done
	 */
	public Future<?> execute(URL scriptURL,
			Map<String, String> parameters, JobListener listener);
}
