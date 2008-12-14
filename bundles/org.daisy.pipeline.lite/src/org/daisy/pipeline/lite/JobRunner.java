package org.daisy.pipeline.lite;

import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.ProgressChangeEvent;
import org.daisy.pipeline.core.event.StateChangeEvent;
import org.daisy.pipeline.core.event.SystemEvent;
import org.daisy.pipeline.core.event.TaskProgressChangeEvent;
import org.daisy.pipeline.core.event.TaskStateChangeEvent;
import org.daisy.pipeline.core.event.UserAbortEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.JobAbortedException;
import org.daisy.pipeline.exception.JobFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class JobRunner implements IRunnableWithProgress, BusListener {
	private PipelineCore pipeline;
	private Job job;
	private IProgressMonitor monitor;
	private int currTaskIndex = 1;
	private int taskSize;
	private int taskWork = 1000;
	private int lastWork = 0;
	private boolean monitorSubtasks = true;

	public JobRunner(Job job, PipelineCore pipeline) {
		this.job = job;
		this.pipeline = pipeline;
		this.taskSize = job.getScript().getTasks().size();
		EventBus.getInstance().subscribe(this, SystemEvent.class);
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		this.monitor = monitor;
		monitor.beginTask("Running " + job.getScript().getNicename() + "...",
				taskWork * taskSize);
		try {
			pipeline.execute(job);
		} catch (JobFailedException e) {
			if (e instanceof JobAbortedException) {
				throw new InterruptedException();
			} else {
				throw new InvocationTargetException(e);
			}
		} finally {
			monitor.done();
		}

	}

	public void received(EventObject event) {
		if (event instanceof TaskProgressChangeEvent) {
			if (monitor.isCanceled()) {
				EventBus.getInstance().publish(new UserAbortEvent(this));
			} else {
				ProgressChangeEvent pce = (ProgressChangeEvent) event;
				int prog = (int) (pce.getProgress() * taskWork);
				monitor.worked(prog - lastWork);
				lastWork = prog;
			}
		}
		if (event instanceof TaskStateChangeEvent) {
			TaskStateChangeEvent tsce = (TaskStateChangeEvent) event;
			if (monitorSubtasks
					&& (tsce.getState() == StateChangeEvent.Status.STARTED)) {
				monitor.subTask(((Task) tsce.getSource()).getTransformerInfo()
						.getNiceName()
						+ " [" + currTaskIndex + "/" + taskSize + "]");
				currTaskIndex++;
			}
		}
	}

	public void monitorSubtasks(boolean monitorSubtasks) {
		this.monitorSubtasks = monitorSubtasks;
	}
}
