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
package org.daisy.pipeline.execution.rmi;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.daisy.pipeline.execution.AbstractJobExecutionService;
import org.daisy.pipeline.execution.JobAdapter;
import org.daisy.pipeline.execution.JobListener;
import org.daisy.pipeline.execution.Status;
import org.daisy.pipeline.rmi.RMIPipelineInstance;
import org.daisy.pipeline.rmi.RMIPipelineListener;
import org.daisy.util.execution.TimeoutMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A implementation of an {@link AbstractJobExecutionService} that uses a pool
 * of {@link RMIPipelineInstance} to execute Pipeline jobs.
 * 
 * <p>
 * This execution service must be configured with a {@link GenericObjectPool}
 * creating {@link RMIPipelineInstanceWrapper}, and possibly with timeout and
 * timeout monitoring interval values.
 * </p>
 * <p>
 * Additionally, as all subclasses of {@link AbstractJobExecutionService}, this
 * service must be configured with a {@link JobAdapter}.
 * </p>
 * 
 * @author Romain Deltour
 */
public class RMIJobExecutionService<T> extends AbstractJobExecutionService<T> {

	/**
	 * The key to the File object in the execution context where the standard
	 * out stream of the RMI Pipeline instance can be logged.
	 */
	public static final String STDOUT_FILE_KEY = "stdoutFile";
	/**
	 * The key to the File object in the execution context where the standard
	 * error stream of the RMI Pipeline instance can be logged.
	 */
	public static final String STDERR_FILE_KEY = "stderrFile";

	private class JobExecutionTask extends Thread {

		private RMIPipelineInstanceWrapper pipeline = null;
		private URL scriptURL;
		private Map<String, String> parameters;
		private JobListener listener;
		private Map<String, Object> context;

		private JobExecutionTask(URL scriptURL, Map<String, String> parameters,
				JobListener listener, Map<String, Object> context) {
			super();
			this.scriptURL = scriptURL;
			this.parameters = parameters;
			this.listener = listener;
			this.context = context;
		}

		public void run() {
			listener.start();
			TimeoutMonitor timeoutMonitor = null;
			try {
				// Get a RMI Pipeline from the pool
				pipeline = (RMIPipelineInstanceWrapper) pipelinePool
						.borrowObject();

				// Setup the timeout monitor
				timeoutMonitor = new TimeoutMonitor(timeout,
						timeoutMonitoringInterval) {
					@Override
					protected void timeout() {
						if (logger.isDebugEnabled()) {
							logger.debug("Pipeline timeout");
						}
						pipeline.shutdown();
						listener.stop(Status.SYSTEM_FAILED,
								"Job not responding");
					}
				};
				timeoutMonitor.start();

				// Get the job ID from context
				Object jobId = context.get("jobId");

				// Finally execute the job
				RMIPipelineListener rmiListener = new RMIPipelineListenerImpl(
						listener, timeoutMonitor);
				pipeline.setListener(rmiListener);
				pipeline.executeJob(scriptURL, parameters,
						(jobId != null) ? jobId.toString() : null);

			} catch (InterruptedException e) {
				logger.warn(e.getLocalizedMessage(), e);
				listener.stop(Status.ABORTED, "Aborted.");
			} catch (Exception e) {
				logger.warn(e.getLocalizedMessage(), e);
				listener.stop(Status.SYSTEM_FAILED, "Unexpected Error: "
						+ e.getLocalizedMessage());
			} finally {

				// Clean the pool
				if (pipeline != null) {
					try {
						pipelinePool.returnObject(pipeline);
					} catch (Exception e) {
						logger.warn("Couldn't return Pipeline to pool", e);
					}
				}

				// Terminate the timeout thread
				if (timeoutMonitor != null && timeoutMonitor.isAlive()) {
					timeoutMonitor.cancel();
				}
			}
		}

		@Override
		public void interrupt() {
			if (pipeline != null) {
				ExecutorService timeoutExecutor = Executors
						.newSingleThreadExecutor();
				Future<?> task = timeoutExecutor.submit(new Runnable() {
					public void run() {
						try {
							pipeline.cancelCurrentJob();
						} catch (RemoteException e) {
							logger.error("Abortion failed", e);
							pipeline.shutdown();

						}
					}
				});
				try {
					task.get(timeout, TimeUnit.MILLISECONDS);
				} catch (TimeoutException e) {
					logger
							.warn("Abortion request timed out, shutting down Pipeline instance");
					pipeline.shutdown();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error("Abortion failed ", e);
				} finally {
					timeoutExecutor.shutdownNow();
				}
			}
			super.interrupt();
			listener.stop(Status.ABORTED, "Aborted.");
		}

	}

	private final Logger logger = LoggerFactory
			.getLogger(RMIJobExecutionService.class);

	private GenericObjectPool pipelinePool;
	private ExecutorService executor;
	private int timeout = 60000;
	private int timeoutMonitoringInterval = 2000;

	/**
	 * Initializes the pool and Thread executor with the configuration
	 * properties.
	 */
	public void init() {
		if (pipelinePool == null) {
			throw new IllegalStateException("Pipeline pool is null");
		}
		executor = Executors.newFixedThreadPool(pipelinePool.getMaxActive());
	}

	/**
	 * Sets the pool of {@link RMIPipelineInstance} used to execute the jobs.
	 * 
	 * @param pipelinePool
	 *            the pool of RMI Pipeline instances to set
	 */
	public void setPipelinePool(GenericObjectPool pipelinePool) {
		this.pipelinePool = pipelinePool;
	}

	/**
	 * Sets the value of the timeout (in milliseconds) after which a non
	 * responding job execution must be interrupted.
	 * 
	 * @param timeout
	 *            the timeout value (in milliseconds)
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Sets the value of the time interval (in milliseconds) at which the
	 * responsiveness of the job is checked.
	 * 
	 * @param timeoutMonitoringInterval
	 *            the timeout monitoring interval value (in milliseconds)
	 */
	public void setTimeoutMonitoringInterval(int timeoutMonitoringInterval) {
		this.timeoutMonitoringInterval = timeoutMonitoringInterval;
	}

	/**
	 * Executes the job represented by the given parameters in a new
	 * {@link RMIPipelineInstance} retrieved from an internal pool.
	 * 
	 * <p>
	 * Two {@link File} objects are possibly retrieved from the context under
	 * the keys {@link #STDERR_FILE_KEY} and {@link #STDOUT_FILE_KEY} to log the
	 * RMI Pipeline instance standard out and error streams.
	 * </p>
	 */
	@Override
	protected Future<?> doExecute(final URL scriptURL,
			final Map<String, String> parameters, final JobListener listener,
			final Map<String, Object> context) {
		return executor.submit(new JobExecutionTask(scriptURL, parameters,
				listener, context));

	}

	/**
	 * Closes the pool and shut down the executor.
	 */
	public void destroy() {
		try {
			pipelinePool.close();
			executor.shutdownNow();
		} catch (Exception e) {
			logger.warn("Couldn't close Pipeline pool");
		}
	}
}
