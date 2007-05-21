package org.daisy.pipeline.gui.messages;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.event.MessageEvent.Cause;
import org.daisy.dmfc.core.event.MessageEvent.Type;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public class MessagesView extends TableView {
    private class ClearAction extends Action {

        public ClearAction() {
            super("Clear Messages", GuiPlugin
                    .createDescriptor(IIconsKeys.MESSAGE_CLEAR));
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
            super("Filter...", GuiPlugin
                    .createDescriptor(IIconsKeys.TREE_FILTER));
        }

        @Override
        public void run() {
            FilterDialog dialog = new FilterDialog(getSite().getShell(), filter);

            if (dialog.open() == IDialogConstants.OK_ID) {
                getViewer().refresh();
                for (FilterToggleAction action : filterTypeActions) {
                    action.refresh();
                }
                for (FilterToggleAction action : filterCauseActions) {
                    action.refresh();
                }
            }
        }

    }

    private class FilterToggleAction extends Action {

        private boolean checked;
        private MessageEvent.Cause cause;
        private MessageEvent.Type type;

        public FilterToggleAction(MessageEvent.Cause cause, ImageDescriptor icon) {
            // TODO localize
            super(cause.toString(), IAction.AS_CHECK_BOX);
            setImageDescriptor(icon);
            this.cause = cause;
            refresh();
        }

        public FilterToggleAction(MessageEvent.Type type, ImageDescriptor icon) {
            // TODO localize
            super(type.toString(), IAction.AS_CHECK_BOX);
            setImageDescriptor(icon);
            this.type = type;
            refresh();
        }

        public void refresh() {
            if (cause != null) {
                setChecked(!filter.isAccepted(cause));
            } else if (type != null) {
                setChecked(!filter.isAccepted(type));
            }
            this.checked = isChecked();
        }

        @Override
        public void run() {
            checked = !checked;
            if (cause != null) {
                filter.configure(cause, !checked);
            } else if (type != null) {
                filter.configure(type, !checked);
            }
            getViewer().refresh();
        }
    }

    private class ScrollLockAction extends Action {

        public ScrollLockAction() {
            super("Scroll Lock", GuiPlugin
                    .createDescriptor(IIconsKeys.MESSAGE_SCROLL_LOCK));
        }

        @Override
        public void run() {
            locked = !locked;
        }
    }

    public static final String ID = "org.daisy.pipeline.gui.views.messages"; //$NON-NLS-1$
    private static IMemento memento;
    private MessageFilter filter;
    private List<FilterToggleAction> filterTypeActions;
    private List<FilterToggleAction> filterCauseActions;
    private List<IAction> groupByActions;
    private IAction filterDialogAction;
    private IAction expandAllAction;
    private IAction scrollLockAction;

    private IAction collapseAllAction;

    private IAction clearAction;

    private IAction exportAction;
    private boolean locked;

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        MessagesView.memento = memento;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        filter.saveState(memento);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    private List<CategorySet> createCategorySets() {
        List<CategorySet> cats = new ArrayList<CategorySet>();
        cats.add(new CauseCategorySet());
        cats.add(new TypeCategorySet());
        cats.add(new JobCategorySet());
        cats.add(CategorySet.NONE);
        return cats;
    }

    private ImageDescriptor getIcon(Cause cause) {
        switch (cause) {
        case INPUT:
            return GuiPlugin.createDescriptor(IIconsKeys.MESSAGE_FILTER_INPUT);
        case SYSTEM:
            return GuiPlugin.createDescriptor(IIconsKeys.MESSAGE_FILTER_SYSTEM);
        default:
            return null;
        }
    }

    private ImageDescriptor getIcon(Type type) {
        switch (type) {
        case DEBUG:
            return GuiPlugin.createDescriptor(IIconsKeys.MESSAGE_FILTER_DEBUG);
        case ERROR:
            return GuiPlugin.createDescriptor(IIconsKeys.MESSAGE_FILTER_ERROR);
        case INFO:
            return GuiPlugin.createDescriptor(IIconsKeys.MESSAGE_FILTER_INFO);
        case WARNING:
            return GuiPlugin
                    .createDescriptor(IIconsKeys.MESSAGE_FILTER_WARNING);
        default:
            return null;
        }
    }

    @Override
    protected void createActions() {
        super.createActions();
        // Filter actions
        filterDialogAction = new FilterDialogAction();
        filterTypeActions = new LinkedList<FilterToggleAction>();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            filterTypeActions.add(new FilterToggleAction(type, getIcon(type)));
        }
        filterCauseActions = new LinkedList<FilterToggleAction>();
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            filterCauseActions
                    .add(new FilterToggleAction(cause, getIcon(cause)));
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
        scrollLockAction = new ScrollLockAction();
    }

    @Override
    protected ITreeContentProvider createContentProvider() {
        return new MessagesContentProvider(this);
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
        toolbar.add(expandAllAction);
        toolbar.add(collapseAllAction);
        toolbar.add(new Separator());
        for (FilterToggleAction action : filterTypeActions) {
            toolbar.add(action);
        }
        for (FilterToggleAction action : filterCauseActions) {
            toolbar.add(action);
        }
        toolbar.add(new Separator());
        toolbar.add(exportAction);
        toolbar.add(clearAction);
        toolbar.add(scrollLockAction);
    }
}