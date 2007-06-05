/*
 * Daisy Pipeline Copyright (C) 2007 Daisy Consortium
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
package org.daisy.pipeline.core.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.JobStateChangeEvent;
import org.daisy.pipeline.core.event.StateChangeEvent;
import org.daisy.pipeline.core.transformer.Parameter;
import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.exception.JobAbortedException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.pipeline.exception.TransformerAbortException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;

/**
 * Runs a task script.
 * 
 * @author Linus Ericson
 */
public class Runner {

    private boolean mRunning;
    private int mCompletedTasks;
    private I18n mInternationalization;

    /**
     * Constructor.
     */
    public Runner() {
        mInternationalization = new I18n();
    }

    /**
     * Execute a task script.
     * 
     * @param job the script to execute
     * @throws JobFailedException
     */
    public void execute(Job job) throws JobFailedException {
        if (!job.allRequiredParametersSet()) {
            throw new JobFailedException(
                    "Not all required parameters have been set");
        }
        try {
            this.mRunning = true;
            this.mCompletedTasks = 0;

            EventBus.getInstance().publish(
                    new JobStateChangeEvent(job,
                            StateChangeEvent.Status.STARTED));

            for (Task task : job.getScript().getTasks()) {
                // Get transformer handler
                TransformerHandler handler = task.getTransformerHandler();

                Map<String, String> parameters = new HashMap<String, String>();

                // Add parameters specified by task
                this.addTaskParameters(parameters, task, job.getJobParameters());

                // Add hard-coded transformer parameters
                this.addTransformerParameters(parameters, handler);

                boolean success = handler.run(parameters, task.isInteractive(), task);
                if (!success) {
                    throw new JobFailedException(i18n("TASK_FAILED", handler.getName()));
                }
                this.mCompletedTasks++;

            }
            EventBus.getInstance().publish(
                    new JobStateChangeEvent(job,
                            StateChangeEvent.Status.STOPPED));
        } catch (TransformerAbortException e) {
            throw new JobAbortedException("Task aborted", e);
        } catch (TransformerRunException e) {
            throw new JobFailedException(i18n("ERROR_RUNNING_TASK"), e);
        } finally {
            this.mRunning = false;
            this.mCompletedTasks = 0;
        }
    }

    /**
     * Add hard-coded transformer parameters
     * 
     * @param parameters the script parameters
     * @param handler a transformer handler
     */
    private void addTransformerParameters(Map<String, String> parameters,
            TransformerHandler handler) {
        Collection<Parameter> params = handler.getParameters();
        for (Parameter param : params) {
            if (param.getValue() != null) {
                parameters.put(param.getName(), param.getValue());
            } else if (param.isRequired() == false
                    && !parameters.containsKey(param.getName())) {
                parameters.put(param.getName(), param.getDefaultValue());
            }
        }
    }

    /**
     * Add parameters specified by the script task
     * 
     * @param parameters the (to be) script parameters
     * @param task the task to add parameters from
     */
    private void addTaskParameters(Map<String, String> parameters, Task task,
            Map<String, JobParameter> runnerProperties) {
        for (TaskParameter param : task.getParameters().values()) {
            parameters.put(param.getName(), param.getValue(runnerProperties));
        }
    }

    /**
     * Checks whether the Runner is currently running a script
     * 
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

    /*
     * i18n convenience methods
     */
    private String i18n(String msgId) {
        return mInternationalization.format(msgId);
    }

    private String i18n(String msgId, Object[] params) {
        return mInternationalization.format(msgId, params);
    }

    private String i18n(String msgId, Object param) {
        return i18n(msgId, new Object[] { param });
    }

    private String i18n(String msgId, Object param1, Object param2) {
        return i18n(msgId, new Object[] { param1, param2 });
    }

}
