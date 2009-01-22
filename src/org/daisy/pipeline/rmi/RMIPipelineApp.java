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

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.EventObject;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.TaskProgressChangeEvent;
import org.daisy.pipeline.core.event.TaskStateChangeEvent;
import org.daisy.pipeline.core.event.UserAbortEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.JobAbortedException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.pipeline.rmi.RMIPipelineListener.ExitCode;
import org.daisy.util.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default command line implementation of a remote Pipeline instance. This main
 * application expects a string ID as its unique argument and binds a new
 * Pipeline instance in the local RMI registry under the ID name.
 * 
 * @author Romain Deltour
 * 
 */
public class RMIPipelineApp extends UnicastRemoteObject implements
		RMIPipelineInstance, BusListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(RMIPipelineApp.class);

	public static void main(String[] args) {
		try {
			new RMIPipelineApp(args[0]);
			logger.info("Started RMI Pipeline instance {}", args[0]);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private final I18n messages = new I18n();
	private String name;
	private PipelineCore pipeline;
	private RMIPipelineListener listener;
	private boolean isRunning = false;
	private boolean isValid = true;
	private long lastProgressDate;
	private long progressInterval = 1000L;

	/**
	 * @throws RemoteException
	 */
	protected RMIPipelineApp(String name) throws RemoteException {
		super(0, new LocalClientSocketFactory(), new LocalServerSocketFactory());
		this.name = name;

		// Subscribe this to the Event Listener
		EventBus.getInstance().subscribe(this, MessageEvent.class);
		EventBus.getInstance().subscribe(this, TaskStateChangeEvent.class);
		EventBus.getInstance().subscribe(this, TaskProgressChangeEvent.class);

		// Create the Pipeline Core
		try {
			pipeline = new PipelineCore();
		} catch (DMFCConfigurationException e) {
			logger.error("Couldn't initialize the Pipeline Core", e);
			throw new RemoteException(e.getMessage(), e);
		}

		// Bind this to the registry
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
		registry.rebind(name, this);
	}

	public void executeJob(URL scriptURL, Map<String, String> parameters)
			throws RemoteException {
		synchronized (this) {
			if (isRunning) {
				throw new IllegalStateException("A job is already running");
			}
			isRunning = true;
		}

		// Create Script and Job
		Script script = null;
		try {
			script = pipeline.newScript(scriptURL);
		} catch (ScriptValidationException e) {
			logger.error("Invalid Script :" + e.getMessage(), e);
			throw new RemoteException(messages.format("SCRIPT_NOT_VALID"), e);
		}
		Job job = new Job(script);

		// Set parameters
		for (String name : parameters.keySet()) {
			ScriptParameter scriptParam = job.getScriptParameter(name);
			if (scriptParam == null) {
				logger.warn("Unknown script parameter: {}", name);
			}
			try {
				job.setParameterValue(name, parameters.get(name));
			} catch (DatatypeException e) {
				logger.error("Invalid parameter '" + name + "' :"
						+ e.getMessage(), e);
				throw new RemoteException(
						messages.format("ILLEGAL_PARAM_MESSAGE", name, e
								.getLocalizedMessage()), e);
			}
		}

		// Execute script and catch the different possible outcomes
		if (listener != null)
			listener.started();
		try {
			pipeline.execute(job);
			logger.info("Job done");
			if (listener != null)
				listener.stopped(ExitCode.FINISHED, null);
		} catch (JobAbortedException e) {
			logger.warn("Job aborted");
			if (listener != null)
				listener.stopped(ExitCode.ABORTED, null);
		} catch (JobFailedException e) {
			logger.error("Job failed: " + e.getMessage(), e);
			if (listener != null)
				listener.stopped(ExitCode.FAILED, e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error("Unexpected Exception: " + e.getMessage(), e);
			if (listener != null)
				listener.stopped(ExitCode.SYSTEM_FAILED, e
						.getLocalizedMessage());
		} catch (Error e) {// catch and re-throw to invalidate this instance
			logger.error("Unexpected Error: " + e.getMessage(), e);
			if (listener != null)
				listener.stopped(ExitCode.SYSTEM_FAILED, e
						.getLocalizedMessage());
			isValid = false;
			throw e;
		} finally {
			isRunning = false;
		}
	}

	public void cancelCurrentJob() throws RemoteException {
		if(!isRunning) {
			return;
		}
		EventBus.getInstance().publish(new UserAbortEvent(this));
	}

	public void setListener(RMIPipelineListener listener) {
		this.listener = listener;
	}

	public boolean isReady() throws RemoteException {
		return isRunning && isValid;
	}

	public void shutdown() throws RemoteException {
		try {
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			registry.unbind(name);
		} catch (Exception e) {
			logger.error("Couldn't unbind the RMI Pipeline instance", e);
			System.exit(1);
		} finally {
			System.exit(0);
		}
	}

	public synchronized void received(EventObject event) {
		if (event instanceof TaskProgressChangeEvent) {
			receivedProgress((TaskProgressChangeEvent) event);
		} else if (event instanceof MessageEvent) {
			receivedMessage((MessageEvent) event);
		} else if (event instanceof TaskStateChangeEvent) {
			receivedTaskChange((TaskStateChangeEvent) event);
		} else {
			logger.debug("Unknown Event: " + event.getClass().getName());
		}
	}

	/**
	 * Only update progress if a certain amount of time has passed
	 * 
	 * @param event
	 */
	private void receivedProgress(TaskProgressChangeEvent event) {
		if (listener == null)
			return;
		try {
			long now = System.currentTimeMillis();
			if (now - lastProgressDate > progressInterval
					|| event.getProgress() == 1) {
				lastProgressDate = now;
				listener.progress(((Task) event.getSource()).getName(), event
						.getProgress());
			}
		} catch (RemoteException e) {
			logger.error("Couldn't send remote progress: " + e.getMessage(), e);
		}
	}

	/**
	 * @param event
	 */
	private void receivedMessage(MessageEvent event) {
		logger.info(event.toString());
		if (listener == null)
			return;
		String path = null;
		Integer line = null;
		Integer column = null;
		if (event.getLocation() != null) {
			Location loc = event.getLocation();
			String sysId = loc.getSystemId();
			if (sysId != null && sysId.length() > 0) {
				File file = new File(sysId);
				path = file.getPath();
				if (loc.getLineNumber() > -1) {
					line = Integer.valueOf(loc.getLineNumber());
					if (loc.getColumnNumber() > -1) {
						column = Integer.valueOf(loc.getColumnNumber());
					}
				}
			}
		}
		try {
			listener.message(event.getMessage(), event.getType().toString(),
					path, line, column);
		} catch (RemoteException e) {
			logger.error("Couldn't send remote message: " + e.getMessage(), e);
		}
	}

	private void receivedTaskChange(TaskStateChangeEvent event) {
		String taskName = ((Task) event.getSource()).getName();
		String state = event.getState().toString();
		logger.info("Transformer {} {}", taskName, state.toLowerCase());
		if (listener == null)
			return;
		try {
			switch (event.getState()) {
			case STARTED:
				listener.taskStarted(taskName);
				break;
			case STOPPED:
				listener.taskStopped(taskName);
				break;
			}
		} catch (RemoteException e) {
			logger.error(
					"Couldn't send remote state change: " + e.getMessage(), e);
		}
	}
}
