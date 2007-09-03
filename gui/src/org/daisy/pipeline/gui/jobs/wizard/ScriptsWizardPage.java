/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
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
package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.model.ScriptManager;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.daisy.pipeline.gui.util.viewers.ExpandTreeDoubleClickListener;
import org.daisy.pipeline.gui.util.viewers.FileTreeContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Wizard page used to select the script on which the created job will be based.
 * 
 * @author Romain Deltour
 * 
 */
public class ScriptsWizardPage extends WizardPage {
 
	/** The name used to identify this wizard page */
	public static final String NAME = "selectScript"; //$NON-NLS-1$
	/** The name of the setting used to persist the last selected script */
	public static final String SETTINGS_LAST_SCRIPT_URI = "lastScriptURI"; //$NON-NLS-1$
	/** A reference to the script manager */
	private ScriptManager scriptMan;
	/** The current selection (in the script tree) */
	private IStructuredSelection selection;
	/** The TreeViewer used for the script tree */
	private TreeViewer scriptTreeViewer;

	/**
	 * Creates this wizard page.
	 */
	protected ScriptsWizardPage() {
		super(NAME);
		setTitle(Messages.page_script_title);
		setDescription(Messages.page_script_description);
		scriptMan = ScriptManager.getDefault();
	}

	@Override
	public boolean canFlipToNextPage() {
		Job job = ((NewJobWizard) getWizard()).getJob();
		return ((job != null) && !job.getScript().getParameters().isEmpty());
	}

	public void createControl(Composite parent) {
		// Tree of script files
		scriptTreeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		scriptTreeViewer.getTree().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		scriptTreeViewer.setContentProvider(new FileTreeContentProvider(
				new ScriptFileFilter(true)));
		scriptTreeViewer.setLabelProvider(new ScriptsLabelProvider());
		scriptTreeViewer.setInput(PipelineUtil.getDir(PipelineUtil.SCRIPT_DIR));
		scriptTreeViewer.getTree().deselectAll();
		scriptTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						if ((event.getSelection() instanceof IStructuredSelection)
								&& (event.getSelection() != null)) {
							selection = (IStructuredSelection) event
									.getSelection();
							updatePageComplete(true);
						}
					}
				});
		// Go to next page when double-clicking a script
		scriptTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (isPageComplete()) {
					IWizardPage page = getNextPage();
					if (page != null) {
						getContainer().showPage(page);
					}
				}
			}
		});
		scriptTreeViewer
				.addDoubleClickListener(new ExpandTreeDoubleClickListener());
		setControl(scriptTreeViewer.getControl());
		initContent();
	}

	/**
	 * Initialize the page contents from the persisted settings,restoring the
	 * last selected script.
	 */
	private void initContent() {
		if (!((NewJobWizard) getWizard()).isFirstInSession()) {
			String lastScriptURI = getDialogSettings().get(
					SETTINGS_LAST_SCRIPT_URI);
			if (lastScriptURI != null) {
				File scriptFile;
				try {
					scriptFile = new File(new URI(lastScriptURI));
					scriptTreeViewer.setSelection(new StructuredSelection(
							scriptFile), true);
				} catch (URISyntaxException e) {
					GuiPlugin.get().error(
							"Couldn't create script URI from wizard settings", //$NON-NLS-1$
							e);
				}
			}
		}
	}

	@Override
	public void performHelp() {
		((NewJobWizard) getWizard()).performHelp();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			updatePageComplete(false);
		}
		super.setVisible(visible);
	}

	/**
	 * Check whether this wizard page is complete, i.e. whether the user has
	 * selected a script.
	 * 
	 * @param showError
	 */
	void updatePageComplete(boolean showError) {
		((NewJobWizard) getWizard()).scriptSelected(null);
		setPageComplete(false);

		// Check selection
		File file = (selection == null) ? null : (File) selection
				.getFirstElement();
		if ((file == null) || file.isDirectory()) {
			setMessage(null);
			return;
		}
		Script script = scriptMan.getScript(file.toURI());
		if (script == null) {
			setMessage(null);
			setErrorMessage((showError) ? Messages.page_script_error_unhandledScript
					: null);
			return;
		}

		// Update script description
		setErrorMessage(null);
		String description = script.getDescription();
		if ((description != null) && (description.length() > 0)) {
			setMessage(description, INFORMATION);
		}

		// Store the script URI in wizard settings
		getDialogSettings().put(SETTINGS_LAST_SCRIPT_URI,
				file.toURI().toString());

		// Send script to the wizard
		((NewJobWizard) getWizard()).scriptSelected(script);
		setPageComplete(true);
	}

}
