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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.daisy.pipeline.execution.JobListener;
import org.daisy.pipeline.execution.Level;
import org.daisy.pipeline.execution.Status;
import org.daisy.pipeline.rmi.LocalSocketFactory;
import org.daisy.pipeline.rmi.RMIPipelineListener;
import org.daisy.util.execution.TimeoutMonitor;

/**
 * An RMI Pipeline instance listener that dispatches notifications to a
 * {@link JobListener} and resets a {@link TimeoutMonitor} on each notification
 * (used to check non-responding executions).
 * 
 * @author Romain Deltour
 * 
 */
public class RMIPipelineListenerImpl extends UnicastRemoteObject implements
		RMIPipelineListener {

	private static final long serialVersionUID = 1L;

	private JobListener jobListener;
	private TimeoutMonitor timeoutMonitor;

	/**
	 * Creates a new RMI instance listener.
	 * 
	 * @param jobListener
	 *            the job listener that will be dispatched the execution events.
	 * @param timeoutMonitor
	 *            a possibly <code>null</code> timeout monitor that is reset
	 *            after all notifications
	 * @throws RemoteException
	 */
	public RMIPipelineListenerImpl(JobListener jobListener,
			TimeoutMonitor timeoutMonitor) throws RemoteException {
		super(0, new LocalSocketFactory(), new LocalSocketFactory());
		if (jobListener == null)
			throw new IllegalArgumentException("Job updater is null");
		this.jobListener = jobListener;
		this.timeoutMonitor = timeoutMonitor;
	}

	public void message(String message, String type, String path, Integer line,
			Integer column) throws RemoteException {
		try {
			jobListener.message(message, toLevel(type), path, line, column);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		} finally {
			if (timeoutMonitor != null) {
				timeoutMonitor.reset();
			}
		}
	}

	public void progress(String taskName, double progress)
			throws RemoteException {
		jobListener.progress(taskName, progress);
		if (timeoutMonitor != null) {
			timeoutMonitor.reset();
		}
	}

	public void started() throws RemoteException {
		// Nothing to do, the job should already be started
		if (timeoutMonitor != null) {
			timeoutMonitor.reset();
		}
	}

	public void stopped(ExitCode code, String message) throws RemoteException {
		try {
			jobListener.stop(toStatus(code), message);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		} finally {
			if (timeoutMonitor != null) {
				timeoutMonitor.reset();
			}
		}
	}

	public void taskStarted(String taskName) throws RemoteException {
		jobListener.startTransformer(taskName);
		if (timeoutMonitor != null) {
			timeoutMonitor.reset();
		}
	}

	public void taskStopped(String taskName) throws RemoteException {
		jobListener.stopTransformer(taskName);
		if (timeoutMonitor != null) {
			timeoutMonitor.reset();
		}
	}

	private Status toStatus(ExitCode code) {
		return Status.valueOf(code.toString());
	}

	private Level toLevel(String type) {
		return Level.valueOf(type);
	}
}
