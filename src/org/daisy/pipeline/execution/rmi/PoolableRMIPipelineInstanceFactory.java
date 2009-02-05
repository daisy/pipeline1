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
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.daisy.pipeline.rmi.RMIPipelineInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory of {@link RMIPipelineInstanceWrapper} to be used with pools from
 * the Commons Pool library.
 * 
 * <p>
 * This factory is configured with the path to the Pipeline home directory, and
 * the path to the Pipeline RMI launcher script used to create new instances.
 * </p>
 * 
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

	private int timeout = 5000;
	private File pipelineDir;
	private File launcher;
	private Registry rmiRegistry;

	public PoolableRMIPipelineInstanceFactory(File launcher, File pipelineDir,
			Registry rmiRegistry) {
		if (launcher == null || pipelineDir == null || rmiRegistry == null) {
			// TODO throw IAE instead
			throw new NullPointerException();
		}
		this.launcher = launcher;
		this.pipelineDir = pipelineDir;
		this.rmiRegistry = rmiRegistry;
	}

	private Process launchNewPipelineInstance(final String id)
			throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Launching {} with laucher {}", id, launcher
					.getAbsolutePath());
		}
		String[] cmdarray = new String[] {launcher.getAbsolutePath(), id };
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
			} catch (InterruptedException e) {}
			try {
				pipeline = (RMIPipelineInstance) rmiRegistry.lookup(uuid);
			} catch (NotBoundException e) {}
		}
		if (pipeline == null) {
			throw new TimeoutException("Couldn't load the Pipeline instance");
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
			return false;
		}
	}
}
