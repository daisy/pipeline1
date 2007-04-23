package org.daisy.pipeline.gui.messages;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.Category;
import org.daisy.pipeline.gui.util.actions.GroupByAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

public class MessagesView extends ViewPart {
    public static final String ID = "org.daisy.pipeline.gui.views.messages"; //$NON-NLS-1$

    private TreeViewer viewer;
    private MessageFilter filter;
    private List<FilterToggleAction> filterToggleActions;

    @Override
    public void createPartControl(Composite parent) {
        // Create the tree
        Tree messagesTree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        messagesTree.setHeaderVisible(true);
        messagesTree.setLinesVisible(true);

        // Create the viewer
        viewer = new TreeViewer(messagesTree);
        viewer.setContentProvider(new MessagesContentProvider());
        viewer.setLabelProvider(new MessagesLabelProvider());
        viewer.setInput(MessageManager.getDefault());
        getSite().setSelectionProvider(viewer);

        // Create filter
        filter = new MessageFilter();
        viewer.addFilter(filter);

        // Create actions
        createActions();
    }

    private void createActions() {
        IAction filterDialogAction = new FilterDialogAction();
        filterToggleActions = new LinkedList<FilterToggleAction>();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            filterToggleActions.add(new FilterToggleAction(type));
        }
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            filterToggleActions.add(new FilterToggleAction(cause));
        }

        // Configure the drop down menu
        IMenuManager menu = getViewSite().getActionBars().getMenuManager();
        menu.add(filterDialogAction);
        IMenuManager groupByMenu = new MenuManager("Group By");
        menu.add(groupByMenu);
        Map<String, List<Category>> catMap = createCategories();
        for (String key : catMap.keySet()) {
            groupByMenu.add(new GroupByAction(key, viewer, catMap.get(key)));
        }
        // groupByMenu.add(action);

        // Configure the tool bar
        IToolBarManager toolbar = getViewSite().getActionBars()
                .getToolBarManager();
        for (FilterToggleAction action : filterToggleActions) {
            toolbar.add(action);
        }
    }

    private Map<String, List<Category>> createCategories() {
        Map<String, List<Category>> map = new LinkedHashMap<String, List<Category>>();
        // Severity category
        List<Category> typeCat = new LinkedList<Category>();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            typeCat.add(new CategoryType(type));
        }
        map.put("Severity", typeCat);
        // Type category
        List<Category> causeCat = new LinkedList<Category>();
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            causeCat.add(new CategoryCause(cause));
        }
        map.put("Type", causeCat);
        // No category
        map.put("None", null);
        return map;
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    private class FilterDialogAction extends Action {

        public FilterDialogAction() {
            super("Filter...");
        }

        @Override
        public void run() {
            FilterDialog dialog = new FilterDialog(getSite().getShell(), filter);

            if (dialog.open() == IDialogConstants.OK_ID) {
                viewer.refresh();
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
            viewer.refresh();
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

    private class CategoryCause extends Category {

        private MessageEvent.Cause cause;

        public CategoryCause(MessageEvent.Cause cause) {
            // TODO localize
            super(cause.toString());
            this.cause = cause;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof MessageEvent) {
                return ((MessageEvent) obj).getCause() == cause;
            }
            return false;
        }

    }

    private class CategoryType extends Category {

        private MessageEvent.Type type;

        public CategoryType(MessageEvent.Type type) {
            // TODO localize
            super(type.toString());
            this.type = type;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof MessageEvent) {
                return ((MessageEvent) obj).getType() == type;
            }
            return false;
        }

    }
}