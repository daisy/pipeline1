package org.daisy.pipeline.gui.messages;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.ITableField;
import org.daisy.pipeline.gui.util.TableView;
import org.daisy.pipeline.gui.util.actions.CollapseAllAction;
import org.daisy.pipeline.gui.util.actions.ExpandAllAction;
import org.daisy.pipeline.gui.util.actions.GroupByAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public class MessagesView extends TableView {
    public static final String ID = "org.daisy.pipeline.gui.views.messages"; //$NON-NLS-1$
    private MessageFilter filter;
    private List<FilterToggleAction> filterToggleActions;
    private List<IAction> groupByActions;
    private IAction filterDialogAction;
    private IAction expandAllAction;
    private IAction collapseAllAction;
    private IAction clearAction;
    private IAction exportAction;
    private static IMemento memento;

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        MessagesView.memento = memento;
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        filter.saveState(memento);
    }

    private class ClearAction extends Action {

        public ClearAction() {
            super("Clear Messages");
        }

        @Override
        public void run() {
            final Display display = getViewer().getTree().getDisplay();
            BusyIndicator.showWhile(display, new Runnable() {
                public void run() {
                    MessageManager.getDefault().clear();
                    display.asyncExec(new Runnable() {
                        public void run() {
                            getViewer().refresh();
                        }
                    });
                }
            });
        }

    }

    private class FilterDialogAction extends Action {

        public FilterDialogAction() {
            super("Filter...");
        }

        @Override
        public void run() {
            FilterDialog dialog = new FilterDialog(getSite().getShell(), filter);

            if (dialog.open() == IDialogConstants.OK_ID) {
                getViewer().refresh();
                for (FilterToggleAction action : filterToggleActions) {
                    action.refresh();
                }
            }
        }

    }

    private class FilterToggleAction extends Action {

        private boolean checked;
        private MessageEvent.Cause cause;
        private MessageEvent.Type type;

        public FilterToggleAction(MessageEvent.Cause cause) {
            // TODO localize
            super(cause.toString(), IAction.AS_CHECK_BOX);
            this.cause = cause;
            refresh();
        }

        public FilterToggleAction(MessageEvent.Type type) {
            // TODO localize
            super(type.toString(), IAction.AS_CHECK_BOX);
            this.type = type;
            refresh();
        }

        @Override
        public void run() {
            checked = !checked;
            if (cause != null) {
                filter.configure(cause, checked);
            } else if (type != null) {
                filter.configure(type, checked);
            }
            getViewer().refresh();
        }

        public void refresh() {
            if (cause != null) {
                setChecked(filter.isAccepted(cause));
            } else if (type != null) {
                setChecked(filter.isAccepted(type));
            }
            this.checked = isChecked();
        }
    }

    @Override
    protected void createActions() {
        super.createActions();
        // Filter actions
        filterDialogAction = new FilterDialogAction();
        filterToggleActions = new LinkedList<FilterToggleAction>();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            filterToggleActions.add(new FilterToggleAction(type));
        }
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            filterToggleActions.add(new FilterToggleAction(cause));
        }
        // Group by actions
        List<CategorySet> cats = createCategorySets();
        groupByActions = new LinkedList<IAction>();
        for (CategorySet cat : cats) {
            groupByActions.add(new GroupByAction(cat, getViewer()));
        }
        // Expand/Collpase Action
        expandAllAction = new ExpandAllAction(getViewer());
        collapseAllAction = new CollapseAllAction(getViewer());
        // Clear/Export Action
        clearAction = new ClearAction();
        exportAction = new ExportAction();
    }

    @Override
    protected ITreeContentProvider createContentProvider() {
        return new MessagesContentProvider();
    }

    @Override
    protected ViewerFilter[] createFilters() {
        ViewerFilter[] superFilters = super.createFilters();
        filter = new MessageFilter();
        filter.init(memento);
        ViewerFilter[] filters = new ViewerFilter[superFilters.length + 1];
        System.arraycopy(superFilters, 0, filters, 0, superFilters.length);
        filters[filters.length - 1] = filter;
        return filters;
    }

    @Override
    protected Object createViewerInput() {
        return MessageManager.getDefault();
    }

    @Override
    protected ITableField[] getFields() {
        return new ITableField[] { new MessageField(), new TypeField() };
    }

    @Override
    protected void initMenu(IMenuManager menu) {
        super.initMenu(menu);
        menu.add(filterDialogAction);
        IMenuManager groupByMenu = new MenuManager("Group By");
        menu.add(groupByMenu);
        for (IAction action : groupByActions) {
            groupByMenu.add(action);
        }
    }

    @Override
    protected void initToolBar(IToolBarManager toolbar) {
        super.initToolBar(toolbar);
        for (FilterToggleAction action : filterToggleActions) {
            toolbar.add(action);
        }
        toolbar.add(expandAllAction);
        toolbar.add(collapseAllAction);
        toolbar.add(clearAction);
        toolbar.add(exportAction);
    }

    private List<CategorySet> createCategorySets() {
        List<CategorySet> cats = new ArrayList<CategorySet>();
        cats.add(new CauseCategorySet());
        cats.add(new TypeCategorySet());
        cats.add(new JobCategorySet());
        cats.add(CategorySet.NONE);
        return cats;
    }
}