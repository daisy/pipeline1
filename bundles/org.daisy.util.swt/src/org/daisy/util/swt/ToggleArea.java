package org.daisy.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ToggleArea {

	private Composite control;
	private Shell shell;
	private boolean isControlHidden;

	public ToggleArea(Shell shell) {
		this(shell, shell);
	}

	public ToggleArea(Composite parent, Shell shell) {
		if ((parent == null) || (shell == null)) {
			throw new IllegalArgumentException("Parameter is null"); //$NON-NLS-1$
		}
		this.shell = shell;
		isControlHidden = false;
		control = new Composite(parent, SWT.NONE);
	}

	public Composite getControl() {
		return control;
	}

	public boolean isShown() {
		return !isControlHidden;
	}

	public void toggle() {
		isControlHidden = !isControlHidden;
		// Store old shell size
		Point windowSize = shell.getSize();
		Point oldSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		// Hide control and remove from layout
		GridData data = (GridData) control.getLayoutData();
		data.exclude = isControlHidden;
		control.setVisible(!isControlHidden);
		// Resize shell size
		Point newSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		float ratio = ((float) newSize.y) / ((float) oldSize.y);
		int adjust = (isControlHidden) ? +1 : -1;
		shell.setSize(windowSize.x, (int) (windowSize.y * ratio) + adjust);
	}

	public void show(boolean show) {
		if (show == isControlHidden) {
			toggle();
		}
	}

}
