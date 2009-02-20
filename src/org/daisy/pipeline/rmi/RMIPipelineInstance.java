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

import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.daisy.pipeline.core.event.UserAbortEvent;

/**
 * Represents a remote Pipeline instance allowing remote job execution.
 * 
 * @author Romain Deltour
 * 
 */
public interface RMIPipelineInstance extends Remote {
	/**
	 * Executes a new job instantiated from the script at the given URL with the
	 * given parameters.
	 * 
	 * @param scriptURL
	 *            the URL of the script the executed job is based on.
	 * @param parameters
	 *            the parameters of the job
	 * @param jobId
	 *            the id of the job (used for logging)
	 * @throws RemoteException
	 */
	public void executeJob(URL scriptURL, Map<String, String> parameters,
			String jobId) throws RemoteException;

	/**
	 * Tries to cancel the currently running job.
	 * <p>
	 * If no job is running, this method will return immediately, otherwise it
	 * will try to cancel the currently running job by sending a
	 * {@link UserAbortEvent}. Because a transformer is not guaranteed to listen
	 * to this event, the caller of this method usually maintains a timeout and
	 * shuts down this Pipeline instance if the job keeps on running.
	 * </p>
	 * 
	 * @throws RemoteException
	 */
	public void cancelCurrentJob() throws RemoteException;

	/**
	 * Registers a (possibly remote) listener to this Pipeline instance.
	 * 
	 * @param listener
	 *            a listener to this Pipeline instance.
	 * @throws RemoteException
	 */
	public void setListener(RMIPipelineListener listener)
			throws RemoteException;

	/**
	 * Whether this instance is ready to execute a new job.
	 * 
	 * @return <code>true</code> iff this instance is ready to execute a new
	 *         job.
	 * @throws RemoteException
	 */
	public boolean isReady() throws RemoteException;

	/**
	 * Tries to gracefully shutdown this remote instance.
	 * 
	 * @throws RemoteException
	 */
	public void shutdown() throws RemoteException;
}
