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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.daisy.pipeline.rmi.RMIPipelineApp;
import org.daisy.pipeline.rmi.RMIPipelineInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory of {@link RMIPipelineInstanceWrapper} to be used with pools from
 * the Commons Pool library.
 * 
 * <p>
 * This factory is configured with the path to the Pipeline home directory, the
 * RMI registry used by the RMI Pipeline instances, and optionally a timeout
 * value to stop waiting for a newly created instance to appear in the registry.
 * </p>
 * <p>
 * The remote Pipeline instances will be based on the {@link RMIPipelineApp}
 * application in the confiured pipeline directory. The java command used to
 * launch these instances can be configured with the file
 * <code>$PIPELINE_DIR/rmi/vmargs.conf</code>, where each line wil be added as a
 * parameter of the command.
 * </p>
 * <p>
 * It uses an internal static holder to initialize an RMI registry on the port
 * 1099 of the localhost.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class PoolableRMIPipelineInstanceFactory extends
		BasePoolableObjectFactory {

	private final Logger logger = LoggerFactory
			.getLogger(PoolableRMIPipelineInstanceFactory.class);

	private String javaPath = "java";
	private int timeout = 10000;
	private File pipelineDir;
	private Registry rmiRegistry;

	/**
	 * Sets the path to the JVM launcher used to launch the remote Pipeline
	 * instances. THe default value is <code>java</code>.
	 * 
	 * @param javaPath
	 *            the path to the JVM launcher.
	 */
	public void setJavaPath(String javaPath) {
		File file = new File(javaPath);
		if (!file.exists()) {
			this.javaPath = javaPath;
		} else {
			logger.info("Invalid java command: {}", javaPath);
		}
	}

	/**
	 * Sets the path to the pipeline installation directory used to launch the
	 * remote Pipeline instances.
	 * 
	 * @param pipelinePath
	 *            the path to the pipeline installation directory
	 */
	public void setPipelinePath(String pipelinePath) {
		this.pipelineDir = new File(pipelinePath);
	}

	/**
	 * Sets the RMI registry used to bind the remote pipeline instances
	 * 
	 * @param rmiRegistry
	 *            an RMI registry
	 */
	public void setRmiRegistry(Registry rmiRegistry) {
		this.rmiRegistry = rmiRegistry;
	}

	/**
	 * Sets the timeout value (in milliseconds) to stop waiting for a newly
	 * created instance to appear in the registry
	 * 
	 * @param timeout
	 *            the creation timeout value (in milliseconds)
	 */
	public void setTimeout(String timeout) {
		try {
			this.timeout = Integer.valueOf(timeout);
		} catch (NumberFormatException e) {
			logger.info("Invalid timeout value: {}", timeout);
		}
	}

	/**
	 * Checks that the pool has been correctly configured.
	 */
	public void init() {
		if (!pipelineDir.exists()) {
			throw new IllegalStateException("Pipeline dir does not exist: "
					+ pipelineDir.getPath());
		}
		if (!pipelineDir.isDirectory()) {
			throw new IllegalStateException("Pipeline dir is not a directory: "
					+ pipelineDir.getPath());
		}
		if (rmiRegistry == null) {
			throw new IllegalStateException("RMI registry is null");
		}
	}

	private Process launchNewPipelineInstance(final String id)
			throws IOException {
		List<String> cmd = new ArrayList<String>();
		cmd.add(javaPath);

		// Add VM arguments
		BufferedReader br = null;
		try {
			File argFile = new File(pipelineDir, "rmi/vmargs.conf");
			if (argFile.exists()) {
				br = new BufferedReader(new FileReader(argFile));
				String line;
				while ((line = br.readLine()) != null) {
					cmd.add(line);
				}
			}
		} catch (Exception e) {
			logger.warn("Couldn't read VM arguments", e);
		} finally {
			if (br != null) {
				br.close();
			}
		}

		// Add class path
		cmd.add("-cp");
		cmd.add(buildClassPath());

		// Add application class and arguments
		cmd.add(RMIPipelineApp.class.getName());
		cmd.add(id);
		String[] cmdarray = (String[]) cmd.toArray(new String[cmd.size()]);

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (String item : cmdarray) {
				sb.append(item).append(' ');
			}
			logger.debug("Launching {} with cmd \"{}\"", id, sb.toString());
		}
		final Process process = Runtime.getRuntime().exec(cmdarray, null,
				pipelineDir);

		// Register a shutdown hook to terminate live RMI Pipeline
		// instances when the JVM quits.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				process.destroy();
			}
		});
		return process;
	}

	private String buildClassPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(".").append(File.pathSeparatorChar);
		if (new File(pipelineDir, "bin").exists()) {
			sb.append("bin").append(File.separatorChar).append(
					File.pathSeparatorChar);
		} else {
			sb.append("pipeline.jar").append(File.pathSeparatorChar);
		}
		for (File file : new File(pipelineDir, "lib")
				.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.isFile()
								&& pathname.getName().endsWith(".jar");
					}
				})) {
			sb.append(pipelineDir.toURI().relativize(file.toURI()).getPath())
					.append(File.pathSeparatorChar);
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * Launch a new RMI Pipeline instance from the launcher script, and wait for
	 * this instance to be registered in the RMI Registry to return it.
	 * 
	 * @throws TimeoutException
	 *             if the RMI Pipeline instance was not registered before a
	 *             fixed 5000ms timeout.
	 */
	public Object makeObject() throws Exception {
		String uuid = UUID.randomUUID().toString();
		Process process = launchNewPipelineInstance(uuid);
		long starttime = System.currentTimeMillis();
		long endtime = starttime + timeout;
		RMIPipelineInstance pipeline = null;
		while (pipeline == null && System.currentTimeMillis() <= endtime) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			try {
				pipeline = (RMIPipelineInstance) rmiRegistry.lookup(uuid);
			} catch (NotBoundException e) {
			}
		}
		if (pipeline == null) {
			throw new TimeoutException("Couldn't load the Pipeline instance");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Launched instance {}", uuid);
		}
		return new RMIPipelineInstanceWrapper(pipeline, process);
	}

	/**
	 * Destroys the RMI Pipeline instance by calling
	 * {@link RMIPipelineInstanceWrapper#shutdown()}.
	 */
	@Override
	public void destroyObject(Object obj) throws Exception {
		((RMIPipelineInstanceWrapper) obj).shutdown();
	}

	/**
	 * Validates the RMI Pipeline instance by calling
	 * {@link RMIPipelineInstanceWrapper#isReady()}.
	 */
	@Override
	public boolean validateObject(Object obj) {
		try {
			return ((RMIPipelineInstanceWrapper) obj).isReady();
		} catch (Exception e) {
			logger.warn("invalid instance");
			return false;
		}
	}
}
