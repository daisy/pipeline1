/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IActionConstants;
import org.daisy.pipeline.gui.util.DelegatingSelectionProvider;
import org.daisy.pipeline.gui.util.actions.BrowserBackAction;
import org.daisy.pipeline.gui.util.actions.BrowserForwardAction;
import org.daisy.pipeline.gui.util.actions.ToggleBrowserAction;
import org.daisy.pipeline.gui.util.swt.ITabItemProvider;
import org.daisy.pipeline.gui.util.swt.TabFolderTraverseListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

/**
 * The implementation of the Documentation View used to browse the Pipeline
 * documentation files (user guide, scripts & transformers references).
 * 
 * @author Romain Deltour
 * 
 */
public class DocView extends ViewPart {

	/** The ID of this view (as used in plugin.xml) */
	public static final String ID = "org.daisy.pipeline.gui.views.doc"; //$NON-NLS-1$

	/** The browser widget used to display HTML doc files */
	private Browser browser;
	/** The list of ToC pages */
	private List<ITocTab> tocList;
	/** The currently visible ToC page */
	private ITocTab currToc;
	/** The Tab Folder holding the list of ToC page */
	private TabFolder tocFolder;
	/** A selection provider delegating to ToC pages */
	private DelegatingSelectionProvider selectionProvider;
	/** Whether the ToC selection should be sync'ed with the current doc page */
	private boolean shouldSynchronizeToc = false;
	/** Whether the current doc page has been selected from the ToC */
	private boolean selectedFromToc = false;
	/** The action to go backward in the browser history */
	private IAction backAction;
	/** The action to go forward in the browser history */
	private IAction forwardAction;
	/** The action used to synchronize the ToC with the current doc page */
	private IAction syncTocAction;
	/** The action to switch the focus in/out the browser widget */
	private IAction toggleBrowserAction;

	/**
	 * Creates a new instance of this view
	 */
	public DocView() {
		super();
	}

	/**
	 * Creates the {@link IAction}s used in this view
	 */
	protected void createActions() {
		if (browser != null) {
			backAction = new BrowserBackAction(browser);
			forwardAction = new BrowserForwardAction(browser);
			syncTocAction = new SyncTocAction(this);
			toggleBrowserAction = new ToggleBrowserAction(getSite().getShell(),
					browser);

		}
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);

		// Create the sash form
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create the ToC tab folder
		tocFolder = new TabFolder(sashForm, SWT.TOP);
		tocFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tocFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currToc = tocList.get(tocFolder.getSelectionIndex());
				if (selectionProvider != null) {
					selectionProvider.setSelectionProviderDelegate(currToc
							.getViewer());
				}
			}

		});
		tocList = createTocTabs();
		for (ITabItemProvider tab : tocList) {
			final TabItem item = tab.createTabItem(tocFolder);
			item.getControl().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					updateBrowserFromToc();
				}

			});
			item.getControl().addTraverseListener(new TraverseListener() {

				public void keyTraversed(TraverseEvent e) {
					if (e.detail == SWT.TRAVERSE_RETURN) {
						updateBrowserFromToc();
					}
				}

			});
		}
		TabFolderTraverseListener.addNewTo(tocFolder);

		// Create the browser
		try {
			browser = new Browser(sashForm, SWT.NONE);
			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
			browser.addLocationListener(new LocationAdapter() {

				@Override
				public void changed(LocationEvent event) {
					if (selectedFromToc) {// avoid round-trips
						selectedFromToc = false;// reset the flag
						return;
					}
					if (shouldSynchronizeToc) {
						updateTocFromBrowser(event.location);
					}
				}

			});
		} catch (SWTError e) {
			GuiPlugin.get().error("Couldn't instantiate browser widget", e); //$NON-NLS-1$
			Label label = new Label(sashForm, SWT.NONE);
			label.setLayoutData(new GridData(GridData.FILL_BOTH));
			label.setText(Messages.error_noBrowser + e.getLocalizedMessage());
		}

		// Create the actions
		createActions();
		IActionBars actionBars = getViewSite().getActionBars();
		registerGlobalActions(actionBars);
		initMenu(actionBars.getMenuManager());
		initToolBar(actionBars.getToolBarManager());

		// Finalize
		sashForm.setWeights(new int[] { 1, 3 });
		selectionProvider = new DelegatingSelectionProvider();
		currToc = tocList.get(tocFolder.getSelectionIndex());
		selectionProvider.setSelectionProviderDelegate(currToc.getViewer());
		getSite().setSelectionProvider(selectionProvider);
		currToc.getViewer().getTree().setSelection(
				currToc.getViewer().getTree().getItem(0));
		updateBrowserFromToc();
		// Remove browser from the tablist
		List<Control> newTabList = new ArrayList<Control>();
		Control[] oldTabList = sashForm.getTabList();
		for (Control control : oldTabList) {
			if (!(control instanceof Browser)) {
				newTabList.add(control);
			}
		}
		sashForm.setTabList(newTabList.toArray(new Control[0]));
	}

	/**
	 * Creates the list of ToC pages appearing in the ToC tab folder
	 * 
	 * @return the list of ToC pages available in the doc view
	 */
	protected List<ITocTab> createTocTabs() {
		List<ITocTab> tocList = new ArrayList<ITocTab>();
		tocList.add(new HelpTocTab());
		tocList.add(new ScriptTocTab());
		tocList.add(new TransformersTocTab());
		return tocList;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	/**
	 * Initializes the contextual menu.
	 * 
	 * @return The menu manager for the context menu
	 */
	protected MenuManager initContextMenu() {
		return new MenuManager();
	}

	/**
	 * Initializes the view menu.
	 * 
	 * @param menu
	 *            The menu manager for the view menu
	 */
	protected void initMenu(IMenuManager menu) {
	}

	/**
	 * Initializes the tool bar.
	 * 
	 * @param toolbar
	 *            The tool bar manager of this view
	 */
	protected void initToolBar(IToolBarManager toolbar) {
		if (browser != null) {
			toolbar.add(syncTocAction);
			toolbar.add(backAction);
			toolbar.add(forwardAction);
		}
	}

	/**
	 * Registers the action handlers for the global actions declared in this
	 * view.
	 * 
	 * @param actionBars
	 *            The action bars of this view
	 */
	protected void registerGlobalActions(IActionBars actionBars) {
		if (browser != null) {
			actionBars.setGlobalActionHandler(ActionFactory.BACK.getId(),
					backAction);
			actionBars.setGlobalActionHandler(ActionFactory.FORWARD.getId(),
					forwardAction);
			actionBars.setGlobalActionHandler(IActionConstants.TOGGLE_BROWSER,
					toggleBrowserAction);
		}
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
	}

	@Override
	public void setFocus() {
		tocFolder.setFocus();
	}

	/**
	 * Toggle the ToC / Browser synchronization status
	 * 
	 * @param shouldSynchronizeToc
	 *            whether the ToC and Browser should become synchronized
	 */
	void setTocSynchronization(boolean shouldSynchronizeToc) {
		this.shouldSynchronizeToc = shouldSynchronizeToc;
		if (shouldSynchronizeToc && (browser != null)) {
			updateTocFromBrowser(browser.getUrl());
		}
	}

	/**
	 * Whether the ToC should be synchronized with the Browser content
	 * 
	 * @return <code>true</code> if and only if the ToC selection should be
	 *         synchronized with the current doc page
	 */
	boolean shouldSynchronizeToc() {
		return shouldSynchronizeToc;
	}

	/**
	 * Updates the documentation page showed in the Browser widget according to
	 * the ToC selection
	 */
	private void updateBrowserFromToc() {
		selectedFromToc = true;
		URI uri = currToc.getURI();
		if ((uri != null) && (browser != null)) {
			browser.setUrl(uri.toString());
		}
	}

	/**
	 * Updates the ToC selection to the document page represented by the given
	 * <code>location</code> URI.
	 * 
	 * @param location
	 *            a URI of a documentation page
	 */
	private void updateTocFromBrowser(String location) {
		File file;
		try {
			URI uri = new URI(location);
			if (!"file".equals(uri.getScheme())) {//$NON-NLS-1$
				// Can't be the URI of a ToC item
				return;
			}
			if ((uri.getFragment() != null) || (uri.getQuery() != null)) {
				// remove the fragment and query
				uri = new URI(uri.getScheme(), uri.getHost(), uri.getPath(),
						null);
			}
			file = new File(uri);
		} catch (Exception e) {
			GuiPlugin.get()
					.error("Couldn't retrieve File from Toc URI spec", e); //$NON-NLS-1$
			return;
		}
		for (ITocTab toc : tocList) {
			if (toc.select(file)) {
				tocFolder.setSelection(toc.getTabItem());
				return;
			}
		}
	}
}
