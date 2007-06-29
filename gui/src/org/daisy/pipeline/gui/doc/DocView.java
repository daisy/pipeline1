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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Romain Deltour
 * 
 */
public class DocView extends ViewPart implements ISelectionListener {

    public static final String ID = "org.daisy.pipeline.gui.views.doc"; //$NON-NLS-1$

    private Browser browser;
    private List<ITocTab> tocList;
    private ITocTab visibleToc;
    private TabFolder tocFolder;
    private DelegatingSelectionProvider selectionProvider;
    private boolean shouldSynchronizeToc = false;
    private boolean selectedFromToc = false;
    private boolean tocIsSynchronized = false;

    private IAction backAction;
    private IAction forwardAction;
    private IAction syncTocAction;
    private IAction toggleBrowserAction;

    public DocView() {
        super();
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

        // Create the toc tab folder
        tocFolder = new TabFolder(sashForm, SWT.TOP);
        tocFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        tocFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                visibleToc = tocList.get(tocFolder.getSelectionIndex());
                tocSelected(visibleToc);
            }

        });
        tocList = createTocTabs();
        for (ITabItemProvider tab : tocList) {
            tab.createTabItem(tocFolder);
        }
        TabFolderTraverseListener.addNewTo(tocFolder);

        try {
            // Create the browser
            browser = new Browser(sashForm, SWT.NONE);
            browser.setLayoutData(new GridData(GridData.FILL_BOTH));
            browser.addLocationListener(new LocationAdapter() {

                @Override
                public void changed(LocationEvent event) {
                    if (selectedFromToc) {// avoid round tripping
                        selectedFromToc = false;// reset the flag
                        tocIsSynchronized = true;// we are obviously in sync
                        return;
                    }
                    tocIsSynchronized = false;
                    if (shouldSynchronizeToc) {
                        synchronizeToc(event.location);
                        tocIsSynchronized = true;
                    }
                }

            });

            // Create the actions
            createActions();
            IActionBars actionBars = getViewSite().getActionBars();
            registerGlobalActions(actionBars);
            initMenu(actionBars.getMenuManager());
            initToolBar(actionBars.getToolBarManager());
        } catch (SWTError e) {
            GuiPlugin.get().error("Couldn't instantiate browser widget", e); //$NON-NLS-1$
            Label label = new Label(sashForm, SWT.NONE);
            label.setLayoutData(new GridData(GridData.FILL_BOTH));
            label.setText(Messages.error_noBrowser + e.getLocalizedMessage());
        }

        // Finalize
        sashForm.setWeights(new int[] { 1, 3 });
        selectionProvider = new DelegatingSelectionProvider();
        getSite().setSelectionProvider(selectionProvider);
        tocSelected(visibleToc);
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

    @Override
    public void dispose() {
        super.dispose();
        getSite().getPage().removePostSelectionListener(this);
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        getSite().getPage().addPostSelectionListener(this);
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part == getSite().getPart()) {
            selectedFromToc = true;
            // get the selected URI from the toc
            URI uri = visibleToc.getURI();
            if (uri != null && browser != null) {
                browser.setUrl(uri.toString());
            }
            return;
        }
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
            return;
        }
        Object element = ((IStructuredSelection) selection).getFirstElement();
        for (ITocTab toc : tocList) {
            // try to propagate the selection
            // update the browser if required
            if (toc.select(element)) {
                URI uri = toc.getURI();
                if (uri != null && browser != null) {
                    browser.setUrl(toc.toString());
                }
            }
        }
    }

    @Override
    public void setFocus() {
        visibleToc.getViewer().getControl().setFocus();
    }

    protected void createActions() {
        backAction = new BrowserBackAction(browser);
        forwardAction = new BrowserForwardAction(browser);
        syncTocAction = new SyncTocAction(this);
        toggleBrowserAction = new ToggleBrowserAction(getSite().getShell(),
                browser);
    }

    protected List<ITocTab> createTocTabs() {
        List<ITocTab> tocList = new ArrayList<ITocTab>();
        tocList.add(new HelpTocTab());
        tocList.add(new ScriptTocTab());
        tocList.add(new TransformersTocTab());
        return tocList;
    }

    protected MenuManager initContextMenu() {
        return new MenuManager();
    }

    protected void initMenu(IMenuManager menu) {
    }

    protected void initToolBar(IToolBarManager toolbar) {
        toolbar.add(syncTocAction);
        toolbar.add(backAction);
        toolbar.add(forwardAction);
    }

    protected void registerGlobalActions(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(ActionFactory.BACK.getId(),
                backAction);
        actionBars.setGlobalActionHandler(ActionFactory.FORWARD.getId(),
                forwardAction);
        actionBars.setGlobalActionHandler(IActionConstants.TOGGLE_BROWSER,
                toggleBrowserAction);
    }

    protected void tocSelected(ITocTab toc) {
        if (selectionProvider != null) {
            selectionProvider.setSelectionProviderDelegate(toc.getViewer());
        }
    }

    boolean shouldSynchronizeToc() {
        return shouldSynchronizeToc;
    }

    void setTocSynchronization(boolean shouldSynchronizeToc) {
        this.shouldSynchronizeToc = shouldSynchronizeToc;
        if (shouldSynchronizeToc && browser != null) {
            synchronizeToc(browser.getUrl());
        }
    }

    private void synchronizeToc(String location) {
        if (tocIsSynchronized) {
            return;
        }
        File file;
        try {
            URI uri = new URI(location);
            if (!"file".equals(uri.getScheme())) {//$NON-NLS-1$
                // Can't be the URI of a toc item
                return;
            }
            if (uri.getFragment() != null || uri.getQuery() != null) {
                // remove the fragment and query
                uri = new URI(uri.getScheme(), uri.getHost(), uri.getPath(),
                        null);
            }
            file = new File(uri);
        } catch (Exception e) {
            GuiPlugin.get()
                    .error("Couldn't retrieve File from toc URI spec", e); //$NON-NLS-1$
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
