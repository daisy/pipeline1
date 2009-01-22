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
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.daisy.pipeline.execution.AbstractJobExecutionService;
import org.daisy.pipeline.execution.JobAdapter;
import org.daisy.pipeline.execution.JobListener;
import org.daisy.pipeline.execution.Status;
import org.daisy.pipeline.rmi.RMIPipelineInstance;
import org.daisy.pipeline.rmi.RMIPipelineListener;
import org.daisy.util.execution.TimeoutMonitor;
import org.daisy.util.file.StreamRedirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A implementation of an {@link AbstractJobExecutionService} that uses a pool
 * of {@link RMIPipelineInstance} to execute Pipeline jobs.
 * 
 * <p>
 * This execution service must be configured via properties set in the
 * {@link #setConfig(Map)} method. Allowed properties are defined in the
 * embedded {@link Config} enumeration.
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

	/**
	 * Enumerates configuration properties for the RMI execution service.
	 */
	public enum Config {
		/**
		 * The maximum number of active RMI Pipeline instance used by this
		 * service (default to 10)
		 */
		MAX_ACTIVE("execution.maxpoolsize", "10"),
		/**
		 * The timeout after which a non-responding RMI Pipeline instance will
		 * be shut down (defaults to 1000000000)
		 */
		TIMEOUT("execution.timeout", "1000000000"),
		/**
		 * The time interval to check the timeout value (default to 2000)
		 */
		TIMEOUT_MONITORING_INTERVAL("execution.timeout.interval", "2000"),
		/**
		 * The path to the directory of the Pipeline installation used by the
		 * underlying RMI Pipeline instances (no default value)
		 */
		PIPELINE_DIR("execution.pipeline.dir", ""),
		/**
		 * The path to the script used to launch the underlying RMI Pipeline
		 * instances (no default value)
		 */
		PIPELINE_LAUNCHER("execution.pipeline.launcher", "");
		private String key;
		private String defaultValue;

		private Config(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}

		public String getKey() {
			return key;
		}

		public String getValue(Map<String, String> config) {
			String value = config.get(getKey());
			return (value != null) ? value : defaultValue;
		}
	}

	private class RMIPipelineInstanceKiller extends TimeoutMonitor {
		private RMIPipelineInstanceWrapper pipeline;
		private JobListener updater;

		public RMIPipelineInstanceKiller(RMIPipelineInstanceWrapper pipeline,
				JobListener updater) {
			super(Long.valueOf(Config.TIMEOUT.getValue(config)), Long
					.valueOf(Config.TIMEOUT_MONITORING_INTERVAL
							.getValue(config)));
			this.pipeline = pipeline;
			this.updater = updater;
		}

		@Override
		protected void timeout() {
			pipeline.shutdown();
			updater.stop(Status.SYSTEM_FAILED, "Job not responding");
		}

	}

	private final Logger logger = LoggerFactory
			.getLogger(RMIJobExecutionService.class);

	private GenericObjectPool pipelinePool;
	private ExecutorService executor;
	private Map<String, String> config;

	/**
	 * Initializes the pool and Thread executor with the configuration
	 * properties.
	 */
	public void init() {
		int maxActive = Integer.valueOf(Config.MAX_ACTIVE.getValue(config));
		File pipelineDir = new File(Config.PIPELINE_DIR.getValue(config));
		File launcher = new File(Config.PIPELINE_LAUNCHER.getValue(config));
		pipelinePool = new GenericObjectPool(
				new PoolableRMIPipelineInstanceFactory(launcher, pipelineDir),
				maxActive);
		executor = Executors.newFixedThreadPool(maxActive);
	}

	/**
	 * Sets the configuration properties used for pools, RMI Pipeline launcher
	 * path, etc.
	 * 
	 * @param config
	 *            a map containing the configuration properties used by this
	 *            service.
	 * @see Config
	 */
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	/**
	 * Executes the job represented by the given parameters in a new
	 * {@link RMIPipelineInstance} retrieved from an internal pool.
	 * 
	 * <p>
	 * Two {@link File} objects are possibly retrieved from the context under
	 * the keys {@link #STDERR_FILE_KEY} and {@link #STDOUT_FILE_KEY} to log the
	 * RMI Pipline instance standard out and error streams.
	 * </p>
	 */
	@Override
	protected void doExecute(final URL scriptURL,
			final Map<String, String> parameters, final JobListener listener,
			final Map<String, Object> context) {
		executor.execute(new Runnable() {
			public void run() {
				listener.start();

				RMIPipelineInstanceWrapper pipeline = null;
				StreamRedirector outRedirector = null;
				StreamRedirector errRedirector = null;
				TimeoutMonitor timeoutMonitor = null;
				try {
					// Get a RMI Pipeline from the pool
					// TODO catch interrupted exception
					pipeline = (RMIPipelineInstanceWrapper) pipelinePool
							.borrowObject();

					// Setup log files
					Object stdoutFile = context.get(STDOUT_FILE_KEY);
					if (stdoutFile != null && stdoutFile instanceof File) {
						outRedirector = new StreamRedirector(pipeline
								.getInputStream(), new FileOutputStream(
								(File) stdoutFile), true);
						outRedirector.start();

					}
					Object stderrFile = context.get(STDERR_FILE_KEY);
					if (stderrFile != null && stderrFile instanceof File) {
						errRedirector = new StreamRedirector(pipeline
								.getErroStream(), new FileOutputStream(
								(File) stderrFile), true);
						errRedirector.start();

					}
					// Setup the timeout monitor
					timeoutMonitor = new RMIPipelineInstanceKiller(pipeline,
							listener);
					timeoutMonitor.start();

					// Finally execute the job
					RMIPipelineListener rmiListener = new RMIPipelineListenerImpl(
							listener, timeoutMonitor);
					pipeline.setListener(rmiListener);
					pipeline.executeJob(scriptURL, parameters);

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

					// Terminate the logging and timeout threads
					if (outRedirector != null && outRedirector.isAlive()) {
						outRedirector.cancel();
					}
					if (errRedirector != null && errRedirector.isAlive()) {
						errRedirector.cancel();
					}
					if (timeoutMonitor != null && timeoutMonitor.isAlive()) {
						timeoutMonitor.cancel();
					}
				}
			}
		});

	}
}
