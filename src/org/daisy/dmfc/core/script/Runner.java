/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.core.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.transformer.Parameter;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.ScriptAbortException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;

/**
 * Runs a task script. 
 * @author Linus Ericson
 */
public class Runner extends EventSender {

	private boolean mRunning;
	private int mCompletedTasks;
	
	/**
	 * Constructor.
	 * @param listeners a set of EventListeners
	 */
	public Runner(Set<EventListener> listeners) {
		super(listeners);
	}
	
	/**
	 * Execute a task script.
	 * @param scriptRunner the script to execute
	 * @throws ScriptException
	 */
	public void execute(ScriptRunner scriptRunner) throws ScriptException {
		if (!scriptRunner.allRequiredParametersSet()) {
			throw new ScriptException("Not all required parameters have been set");
		}
		try {
			this.mRunning = true;
			this.mCompletedTasks = 0;
			this.sendMessage(Level.CONFIG, i18n("RUNNING_SCRIPT", scriptRunner.getScript().getNicename()));
			for (Task task : scriptRunner.getScript().getTasks()) {
				// Get transformer handler
				TransformerHandler handler = task.getTransformerHandler();
								
				Map<String, String> parameters = new HashMap<String, String>();
				
				// Add parameters specified by task
				this.addTaskParameters(parameters, task, scriptRunner.getParameters());
								
				// Add hard-coded transformer parameters
				this.addTransformerParameters(parameters, handler);				
				
				// Execute transformer
				this.sendMessage(Level.CONFIG, i18n("RUNNING_TASK", handler.getName()));
				boolean success = handler.run(parameters, task.isInteractive());
				if (!success) {
					throw new ScriptException(i18n("TASK_FAILED", handler.getName()));
				}
				
				this.mCompletedTasks++;
				this.sendMessage(Level.CONFIG, i18n("END_OF_SCRIPT"));
			}
		} catch (TransformerAbortException e) {
			throw new ScriptAbortException("Script aborted", e);
		} catch (TransformerRunException e) {
			throw new ScriptException(i18n("ERROR_RUNNING_TASK"), e);
		} finally {
			this.mRunning = false;
			this.mCompletedTasks = 0;
		}
	}
	
	/**
	 * Add hard-coded transformer parameters
	 * @param parameters the script parameters
	 * @param handler a transformer handler
	 */
	private void addTransformerParameters(Map<String,String> parameters, TransformerHandler handler) {
		Collection<Parameter> params = handler.getParameters(); 
		for (Parameter param : params) {
			if (param.getValue() != null) {
				parameters.put(param.getName(), param.getValue());
			}
		}
	}
	
	/**
	 * Add parameters specified by the script task
	 * @param parameters the (to be) script parameters
	 * @param task the task to add parameters from
	 */
	private void addTaskParameters(Map<String,String> parameters, Task task, Map<String,AbstractProperty> runnerProperties) {
		for (TaskParameter param : task.getParameters().values()) {
			parameters.put(param.getName(), param.getValue(runnerProperties));
		}
	}
	
	
	/**
	 * Checks whether the Runner is currently running a script
	 * @return
	 */
	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * @return the number of completed tasks in the current script, if any
	 */
	public int getCompletedTasks() {
		return mCompletedTasks;
	}
}
