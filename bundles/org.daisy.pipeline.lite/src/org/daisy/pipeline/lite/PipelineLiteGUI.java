package org.daisy.pipeline.lite;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.pipeline.lite.internal.Images;
import org.daisy.util.jface.ExceptionErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PipelineLiteGUI {
	public static final int OK = Window.OK;

	private Display display;
	private Shell shell;

	public PipelineLiteGUI() {
		this.display = new Display();
		this.shell = new Shell(display);
	}

	public int openJobConfigDialog(Job job) {
		return new JobConfigDialog(shell, job).open();
	}

	public int openProgressDialogAndExecute(Job job, PipelineCore pipeline,
			MessageManager messMan, boolean monitorSubtasks, Type severity) {
		JobRunner runner = new JobRunner(job, pipeline);
		runner.monitorSubtasks(monitorSubtasks);
		try {
			ProgressMonitorDialog progDialog = new ProgressMonitorDialog(null) {
				@Override
				protected void configureShell(Shell shell) {
					super.configureShell(shell);
					shell.setText(Messages.getString("JobProgressDialog.title")); //$NON-NLS-1$
					shell.setImage(Images.getImage(Images.PIPELINE_LOGO));
				}
			};
			progDialog.run(true, true, runner);
			return progDialog.getReturnCode();
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof JobFailedException) {
				new JobResultDialog(null, messMan.getMessages(severity), false)
						.open();
			} else {
				new ExceptionErrorDialog(null, e.getCause()).open();
			}
		} catch (InterruptedException e) {
			// Nothing to do
		}
		return -1;
	}

	public int openResultDialog(MessageManager messMan, Type severity) {
		return new JobResultDialog(null, messMan.getMessages(severity), true)
				.open();
	}

	public File openScriptSelectionDialog(File scriptDir) {
		ScriptSelectionDialog dialog = new ScriptSelectionDialog(shell,
				scriptDir);
		if (dialog.open() == OK) {
			return dialog.getScriptFile();
		}
		return null;
	}

	public void openErrorDialog(Exception e) {
		new ExceptionErrorDialog(null, e).open();
	}
}
