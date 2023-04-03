package org.daisy.pipeline.lite;

import java.io.File;

import org.daisy.pipeline.lite.internal.Images;
import org.daisy.pipeline.scripts.ui.ScriptFileFilter;
import org.daisy.pipeline.scripts.ui.ScriptsLabelProvider;
import org.daisy.util.jface.ExpandTreeDoubleClickListener;
import org.daisy.util.jface.FileTreeContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

public class ScriptSelectionDialog extends Dialog {

	private TreeViewer treeViewer;
	private File scriptFile;
	private File scriptDir;

	protected ScriptSelectionDialog(Shell parentShell, File scriptDir) {
		super(parentShell);
		this.scriptDir = scriptDir;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("ScriptSelectionDialog.title")); //$NON-NLS-1$
		shell.setImage(Images.getImage(Images.PIPELINE_LOGO));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		// Adjust the area margins
		initializeDialogUnits(parent);
		Point defaultMargins = LayoutConstants.getMargins();
		Point defaultSpacing = LayoutConstants.getSpacing();
		GridLayoutFactory.fillDefaults().margins(defaultMargins.x * 3 / 2,
				defaultMargins.y * 3 / 2).spacing(defaultSpacing.x * 2,
				defaultSpacing.y * 2).numColumns(1).applyTo(area);
		// Create the message label
		Label message = new Label(area, SWT.LEFT);
		message.setText(Messages.getString("ScriptSelectionDialog.text")); //$NON-NLS-1$
		// Set width hint
		GridData messageGD = new GridData(GridData.FILL_BOTH);
		messageGD.widthHint = 300;
		message.setLayoutData(messageGD);
		// Create the tree of script files
		treeViewer = new TreeViewer(area, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new FileTreeContentProvider(
				new ScriptFileFilter(true)));
		treeViewer.setLabelProvider(new ScriptsLabelProvider());
		treeViewer.setInput(scriptDir);
		treeViewer.getTree().deselectAll();
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if ((event.getSelection() instanceof IStructuredSelection)) {

					updatePageComplete((IStructuredSelection) event
							.getSelection());
				}
			}

		});
		treeViewer.addDoubleClickListener(new ExpandTreeDoubleClickListener());
		// Set height hint
		Tree tree = treeViewer.getTree();
		GridData treeGD = new GridData(GridData.FILL_BOTH);
		treeGD.heightHint = tree.getItemCount() * tree.getItemHeight() * 2;
		tree.setLayoutData(treeGD);
		return treeViewer.getControl();
	}

	public File getScriptFile() {
		return scriptFile;
	}

	private void updatePageComplete(IStructuredSelection selection) {

		File file = (selection == null) ? null : (File) selection
				.getFirstElement();
		if ((file == null) || file.isDirectory()) {
			scriptFile = null;
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		} else {
			scriptFile = file;
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
	}

}
