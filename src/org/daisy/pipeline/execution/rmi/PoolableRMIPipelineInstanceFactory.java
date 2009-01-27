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
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.daisy.pipeline.rmi.LocalClientSocketFactory;
import org.daisy.pipeline.rmi.LocalServerSocketFactory;
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

	private static final Logger logger = LoggerFactory
			.getLogger(PoolableRMIPipelineInstanceFactory.class);

	private static class RegistryHolder {
		static {
			try {
				RMIClientSocketFactory csf = new LocalClientSocketFactory();
				RMIServerSocketFactory ssf = new LocalServerSocketFactory();
				LocateRegistry.createRegistry(1099, csf, ssf);
				logger.info("RMI registry intialized on localhost:1099");
			} catch (Exception e) {
				logger.error("Couldn't initialize the RMI registry", e);
			}
			try {
				registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			} catch (RemoteException e) {
				logger.error("Couldn't get the RMI registry", e);
			}
		}

		private static Registry registry;

		public static Registry getRegistry() {
			return registry;
		}
	}

	private int timeout = 5000;
	private File pipelineDir;
	private File launcher;

	public PoolableRMIPipelineInstanceFactory(File launcher, File pipelineDir) {
		if (launcher == null || pipelineDir == null) {
			throw new NullPointerException();
		}
		this.launcher = launcher;
		this.pipelineDir = pipelineDir;
	}

	private Process launchNewPipelineInstance(final String id)
			throws IOException {
		String[] cmdarray = new String[] { launcher.getAbsolutePath(), id };
		final Process process = Runtime.getRuntime().exec(cmdarray, null,
				pipelineDir);
		// Register a shutdown hook to terminate live RMI Pipeline
		// instances when the JVM quits.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Registry registry = RegistryHolder.getRegistry();
					(((RMIPipelineInstance) registry.lookup(id))).shutdown();
				} catch (Exception e) {}
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
				Registry registry = RegistryHolder.getRegistry();
				pipeline = (RMIPipelineInstance) registry.lookup(uuid);
			} catch (NotBoundException e) {}
		}
		if (pipeline == null) {
			throw new TimeoutException();
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