package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.DelegatingSelectionProvider;
import org.daisy.pipeline.gui.util.actions.BrowserBackAction;
import org.daisy.pipeline.gui.util.actions.BrowserForwardAction;
import org.daisy.pipeline.gui.util.swt.ITabItemProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
public class DocView extends ViewPart implements ISelectionListener,
        LocationListener {

    public static final String ID = "org.daisy.pipeline.gui.views.doc";

    private Browser browser;
    private List<ITocTab> tocList;
    private ITocTab visibleToc;
    private TabFolder tocFolder;
    private DelegatingSelectionProvider selectionProvider;
    private boolean syncToc = false;
    private boolean selectedFromToc = false;
    private boolean isSync = false;

    private IAction backAction;
    private IAction forwardAction;
    private IAction syncTocAction;

    public DocView() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.browser.LocationListener#changed(org.eclipse.swt.browser.LocationEvent)
     */
    public void changed(LocationEvent event) {
        if (selectedFromToc) {// avoid round tripping
            selectedFromToc = false;
            isSync = true;
            return;
        }
        isSync = false;
        if (syncToc) {
            syncToc(event.location);
            isSync = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.browser.LocationListener#changing(org.eclipse.swt.browser.LocationEvent)
     */
    public void changing(LocationEvent event) {
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

        
        try {
        	// Create the browser
        	browser = new Browser(sashForm, SWT.NONE);
        	browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        	browser.addLocationListener(this);
        	
        	// Create the actions
        	createActions();
        	IActionBars actionBars = getViewSite().getActionBars();
        	registerGlobalActions(actionBars);
        	initMenu(actionBars.getMenuManager());
        	initToolBar(actionBars.getToolBarManager());
        } catch (SWTError e) {
        	GuiPlugin.get().error("Couldn't instantiate browser widget", e);
        	Label label = new Label(sashForm, SWT.NONE);
        	label.setLayoutData(new GridData(GridData.FILL_BOTH));
        	label.setText("Couldn't create browser widget:\n\n" + e.getLocalizedMessage());
        }

        // Finalize
        sashForm.setWeights(new int[] { 1, 3 });
        selectionProvider = new DelegatingSelectionProvider();
        getSite().setSelectionProvider(selectionProvider);
        tocSelected(visibleToc);
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
    }

    protected void tocSelected(ITocTab toc) {
        if (selectionProvider != null) {
            selectionProvider.setSelectionProviderDelegate(toc.getViewer());
        }
    }

    boolean shouldSyncToc() {
        return syncToc;
    }

    void setSyncToc(boolean syncToc) {
        this.syncToc = syncToc;
        if (syncToc && browser != null) {
            syncToc(browser.getUrl());
        }
    }

    private void syncToc(String location) {
        if (isSync) {
            return;
        }
        File file;
        try {
            URI uri = new URI(location);
            file = new File(uri);
        } catch (Exception e) {
            GuiPlugin.get()
                    .error("Couldn't retrieve File from toc URI spec", e);
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
