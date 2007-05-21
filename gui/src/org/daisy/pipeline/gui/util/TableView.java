package org.daisy.pipeline.gui.util;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Romain Deltour
 * 
 */
public abstract class TableView extends ViewPart {

    private TreeViewer viewer;
    private ITreeContentProvider contentProvider;

    @Override
    public void createPartControl(Composite parent) {
         parent.setLayout(new FillLayout());

         // Create the tree viewer
        viewer = new TreeViewer(createTree(parent));
        createColumns(viewer.getTree());
        contentProvider = createContentProvider();
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(createLabelProvider());

        // Create sorter and filters
         viewer.setSorter(createSorter());
         ViewerFilter[] filters = createFilters();
         for (ViewerFilter filter : filters) {
         viewer.addFilter(filter);
         }
        
         // Create actions after sorter and filters
         // so that they can be correctly initialized
         createActions();

         // Set the viewer input
        viewer.setInput(createViewerInput());
        getSite().setSelectionProvider(viewer);

        // Init the context menu and action bars
        // MenuManager mgr = initContextMenu();
        // Menu menu = mgr.createContextMenu(viewer.getControl());
        // viewer.getControl().setMenu(menu);
        // getSite().registerContextMenu(mgr, viewer);
         IActionBars actionBars = getViewSite().getActionBars();
        registerGlobalActions(actionBars);
        initMenu(actionBars.getMenuManager());
        initToolBar(actionBars.getToolBarManager());

    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
    }

    @Override
    public void setFocus() {
        Viewer viewer = getViewer();
        if ((viewer != null) && !viewer.getControl().isDisposed()) {

            viewer.getControl().setFocus();
        }
    }

    protected void createActions() {
    }

    protected void createColumns(final Tree tree) {
        TableLayout layout = new TableLayout();
        tree.setLayout(layout);
        tree.setHeaderVisible(true);
        // TODO save and restore column data from memento
        final ITableField[] fields = getFields();
        for (int i = 0; i < fields.length; i++) {
            layout.addColumnData(new ColumnWeightData(fields[i].getWeight()));
            TreeColumn tc = new TreeColumn(tree, SWT.NONE, i);
            tc.setText(fields[i].getHeaderText());
            // tc.setImage(fields[i].getHeaderImage());
            // tc.setResizable(true);
            // tc.setMoveable(true);
            // tc.addSelectionListener(getHeaderListener());
            // tc.setData(fields[i]);
        }
    }

    protected abstract ITreeContentProvider createContentProvider();

    protected ViewerFilter[] createFilters() {
        return new ViewerFilter[0];
    }

    protected IBaseLabelProvider createLabelProvider() {
        return new TableViewLabelProvider(getFields());
    }

    protected ViewerSorter createSorter() {
        return null;
    }

    protected Tree createTree(Composite parent) {
        Tree tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION);
        tree.setLinesVisible(true);
        return tree;
    }

    protected abstract Object createViewerInput();

    protected abstract ITableField[] getFields();

    protected SelectionListener getHeaderListener() {
        return new SelectionAdapter() {
        };
    }

    protected TreeViewer getViewer() {
        return viewer;
    }

    protected MenuManager initContextMenu() {
        return new MenuManager();
    }

    protected void initMenu(IMenuManager menu) {
    }

    protected void initToolBar(IToolBarManager toolbar) {
    }

    protected void registerGlobalActions(IActionBars actionBars) {
    }

}
