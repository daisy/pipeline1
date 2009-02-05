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

import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.rmi.RMIPipelineInstance;
import org.daisy.pipeline.rmi.RMIPipelineListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simply wraps an {@link RMIPipelineInstance} and the {@link Process} used to
 * launch it, to be able to force quit (using {@link Process#destroy()} if the
 * RMI instance throws an exception while shutting down.
 * 
 * <p>
 * All the methods except {@link #shutdown()} simply delegate to the underlying
 * {@link RMIPipelineInstance}.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class RMIPipelineInstanceWrapper implements RMIPipelineInstance {
	private final Logger logger = LoggerFactory
			.getLogger(RMIPipelineInstanceWrapper.class);
	private RMIPipelineInstance pipeline;
	private Process process;

	public RMIPipelineInstanceWrapper(RMIPipelineInstance pipeline,
			Process process) {
		this.pipeline = pipeline;
		this.process = process;
	}

	public void executeJob(URL scriptURL, Map<String, String> parameters)
			throws RemoteException {
		pipeline.executeJob(scriptURL, parameters);
	}

	public void cancelCurrentJob() throws RemoteException {
		pipeline.cancelCurrentJob();
	}

	public boolean isReady() throws RemoteException {
		return pipeline.isReady();
	}

	public void setListener(RMIPipelineListener listener)
			throws RemoteException {
		pipeline.setListener(listener);
	}

	public void shutdown() {
		ExecutorService timeoutExecutor = Executors
				.newSingleThreadExecutor();
		Future<?> future = timeoutExecutor.submit(new Callable<Object>() {
			public Object call() throws Exception {
				pipeline.shutdown();
				return null;
			}
		});
		try {
			future.get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger
					.warn("Couldn't gracefully shutdown RMI Pipeline instance",
							e);
			process.destroy();
		} finally {
			timeoutExecutor.shutdownNow(); 
		}
	}

	public InputStream getInputStream() {
		return process.getInputStream();
	}

	public InputStream getErroStream() {
		return process.getErrorStream();
	}
}
