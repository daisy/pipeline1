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
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract generic implementation of a {@link JobExecutionService}, which uses
 * a {@link JobAdapter} to extract the script URL, job parameters, job listener,
 * and an execution context from a job instance handled by this service and then
 * delegates the job execution to the protected
 * {@link #doExecute(URL, Map, JobListener, Map)} method.
 * 
 * @author Romain Deltour
 * 
 */
public abstract class AbstractJobExecutionService<T> implements
		JobExecutionService<T> {

	private class IdleListener implements JobListener {

		public void message(String message, Level level, String path,
				Integer line, Integer column) {}

		public void progress(String taskName, double progress) {}

		public void start() {}

		public void startTransformer(String taskName) {}

		public void stop(Status status, String message) {}

		public void stopTransformer(String taskName) {}

	}

	private JobAdapter<T> jobAdapter;

	/**
	 * Executes the given job. Uses the configured {@link JobAdapter} to extract
	 * execution information from the given job, and then delegates the
	 * execution to {@link #doExecute(URL, Map, JobListener, Map)}.
	 * 
	 * <p>
	 * Calling this method is equivalent to calling
	 * <code>execute(job,null)</code>
	 * </p>
	 * 
	 * @throws IllegalStateException
	 *             if no job adapter has been configured.
	 */
	public void execute(final T job) {
		this.execute(job, null);
	}

	/**
	 * Executes the given job. Uses the configured {@link JobAdapter} to extract
	 * execution information from the given job, and then delegates the
	 * execution to {@link #doExecute(URL, Map, JobListener, Map)}.
	 * 
	 * <p>
	 * If the given listener is <code>null</code>, it uses the job adapter to
	 * get a listener from the given job. If the adapter is unable to create
	 * one, a dummy listener is created.
	 * </p>
	 * 
	 * @throws IllegalStateException
	 *             if no job adapter has been configured.
	 */
	public void execute(final T job, JobListener listener) {
		if (jobAdapter == null) {
			throw new IllegalStateException("Job adapter is null");
		}
		if (job == null) {
			throw new IllegalArgumentException("job is null");
		}
		if (listener == null) {
			listener = jobAdapter.getJobListener(job);
			if (listener == null) {
				listener = new IdleListener();
			}

		}
		URL scriptURL = jobAdapter.getScriptURL(job);
		if (scriptURL == null) {
			listener.stop(Status.SYSTEM_FAILED, "Couldn't get Script URL");
			return;
		}
		Map<String, String> parameters = jobAdapter.getParameters(job);
		if (parameters == null) {
			listener.stop(Status.SYSTEM_FAILED, "Couldn't get Job parameters");
			return;

		}
		Map<String, Object> context = jobAdapter.getContext(job);
		if (context == null) {
			context = new HashMap<String, Object>();
		}
		doExecute(scriptURL, parameters, listener, context);
	}

	/**
	 * Delegates to {@link #doExecute(URL, Map, JobListener, Map)}, with an
	 * empty context and a dummy listener if the given one is <code>null</code>.
	 */
	public void execute(URL scriptURL, Map<String, String> parameters,
			JobListener listener) {
		if (listener == null) {
			listener = new IdleListener();
		}
		doExecute(scriptURL, parameters, listener,
				new HashMap<String, Object>());
	}

	/**
	 * Executes the job represented by the given Script URL and job parameters,
	 * and listens to the execution with the given listener. The context is
	 * usually retrieved from a {@link JobAdapter} and is used to pass execution
	 * information or configuration to the sub-classes of this abstract
	 * implementation.
	 * 
	 * @param scriptURL
	 *            the URL of a Pipeline script
	 * @param parameters
	 *            the parameters of the job to execute
	 * @param listener
	 *            the listener to the job execution
	 * @param context
	 *            the execution context
	 */
	protected abstract void doExecute(URL scriptURL,
			Map<String, String> parameters, JobListener listener,
			Map<String, Object> context);

	/**
	 * Sets the job adapter used to adapt the jobs handled by this execution
	 * service.
	 * 
	 * @param jobAdapter
	 *            the new job adapter that will used by this service.
	 */
	public void setJobAdapter(JobAdapter<T> jobAdapter) {
		this.jobAdapter = jobAdapter;
	}

	/**
	 * Returns the job adapter used to adapt the jobs handled by this execution
	 * service.
	 * 
	 * @return the job adapter used by this service.
	 */
	public JobAdapter<T> getJobAdapter() {
		return jobAdapter;
	}

}
