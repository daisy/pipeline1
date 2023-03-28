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
package org.daisy.pipeline.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.daisy.pipeline.core.event.MessageEvent;

/**
 * Listener to a remote Pipeline instance.
 * 
 * @author Romain Deltour
 * @see RMIPipelineInstance
 * 
 */
public interface RMIPipelineListener extends Remote {
	/**
	 * Exit codes of a job execution.
	 */
	public enum ExitCode {
		FINISHED, FAILED, SYSTEM_FAILED, ABORTED
	}

	/**
	 * Notifies that a Pipeline message was received.
	 * 
	 * @param message
	 *            the message text.
	 * @param type
	 *            the message type (see {@link MessageEvent.Type})
	 * @param path
	 *            the path to the cause (possibly null).
	 * @param line
	 *            the line number of the cause (possibly null)
	 * @param column
	 *            the column number of the cause (possibly null)
	 */
	public void message(String message, String type, String path, Integer line,
			Integer column) throws RemoteException;

	/**
	 * Notifies that the Pipeline execution progressed.
	 * 
	 * @param taskName
	 *            The name of the task that progressed.
	 * @param progress
	 *            The new progress value.
	 */
	public void progress(String taskName, double progress)
			throws RemoteException;

	/**
	 * Notifies that a subtask started.
	 * 
	 * @param taskName
	 *            the task name.
	 */
	public void taskStarted(String taskName) throws RemoteException;

	/**
	 * Notifies that a subtask stopped.
	 * 
	 * @param taskName
	 *            the taks name.
	 */
	public void taskStopped(String taskName) throws RemoteException;

	/**
	 * Notifies that the job execution started.
	 */
	public void started() throws RemoteException;

	/**
	 * Notifies that the job execution stopped.
	 * 
	 * @param code
	 *            the exit code
	 * @param message
	 *            an optional termination message
	 */
	public void stopped(ExitCode code, String message) throws RemoteException;
}
