package org.daisy.pipeline.gui.jobs;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.JobParameter;
import org.daisy.pipeline.gui.IActionConstants;
import org.daisy.pipeline.gui.model.JobManager;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;

public class JobsView extends ViewPart {
    public static final String ID = "org.daisy.pipeline.gui.views.jobs"; //$NON-NLS-1$
    public static final int PROP_SEL_JOB_INDEX = 1;

    private static final String[] columnNames = { Messages.heading_jobs, Messages.heading_status };

    private static final int[] columnWeight = { 4, 1 };

    private TreeViewer jobsViewer;

    private Map<Job, Integer> paramNameLength = new HashMap<Job, Integer>();
    private Font paramFont;

    @Override
    public void createPartControl(Composite parent) {
        // Create the tree
        Tree jobsTree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        jobsTree.setHeaderVisible(true);
        // jobsTree.setLinesVisible(true);

        // Configure the columns
        TableLayout layout = new TableLayout();
        jobsTree.setLayout(layout);
        for (int i = 0; i < columnNames.length; i++) {
            layout.addColumnData(new ColumnWeightData(columnWeight[i], true));
            TreeColumn tc = new TreeColumn(jobsTree, SWT.NONE, i);
            tc.setText(columnNames[i]);
            tc.setMoveable(true);
        }

        // Hook painting of parameters to vertically align values
        jobsTree.addListener(SWT.PaintItem, new Listener() {
            public void handleEvent(Event e) {
                if (e.index == 0 && e.item.getData() instanceof JobParameter) {
                    paintParam((JobParameter) e.item.getData(), e);
                }
            }
        });

        // TODO add popup menu to jobInfos table
        // jobsTable.setMenu(createPopUpMenu());

        // Configure the viewer
        jobsViewer = new TreeViewer(jobsTree);
        jobsViewer.setContentProvider(new JobsContentProvider());
        jobsViewer.setLabelProvider(new JobsLabelProvider());
        jobsViewer.setInput(JobManager.getDefault());
        getSite().setSelectionProvider(jobsViewer);

        // add actions
        createActions();
    }

    @Override
    public void dispose() {
        if (paramFont != null) {
            paramFont.dispose();
        }
        super.dispose();
    }

    @Override
    // made public so that action can invoke it
    public void firePropertyChange(int id) {
        super.firePropertyChange(id);
    }

    public TreeViewer getViewer() {
        return jobsViewer;
    }

    /**
     * Passes the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        jobsViewer.getControl().setFocus();
    }

    private void createActions() {
        // Create actions
        IAction moveToTopAction = new MoveToTopAction(this);
        IAction moveUpAction = new MoveUpAction(this);
        IAction moveDownAction = new MoveDownAction(this);
        IAction moveToBottomAction = new MoveToBottomAction(this);
        IAction deleteAction = new DeleteAction(this);

        // Configure the tool bar
        IToolBarManager toolBar = getViewSite().getActionBars()
                .getToolBarManager();
        toolBar.add(moveToTopAction);
        toolBar.add(moveUpAction);
        toolBar.add(moveDownAction);
        toolBar.add(moveToBottomAction);

        // Configure the retargetable actions
        getViewSite().getActionBars().setGlobalActionHandler(
                IActionConstants.MOVE_TO_TOP, moveToTopAction);
        getViewSite().getActionBars().setGlobalActionHandler(
                IActionConstants.MOVE_UP, moveUpAction);
        getViewSite().getActionBars().setGlobalActionHandler(
                IActionConstants.MOVE_DOWN, moveDownAction);
        getViewSite().getActionBars().setGlobalActionHandler(
                IActionConstants.MOVE_TO_BOTTOM, moveToBottomAction);
        getViewSite().getActionBars().setGlobalActionHandler(
                ActionFactory.DELETE.getId(), deleteAction);

        // Hook into Undo/Redo
        IUndoContext undoContext = PlatformUI.getWorkbench()
                .getOperationSupport().getUndoContext();
        getViewSite().getActionBars().setGlobalActionHandler(
                ActionFactory.UNDO.getId(),
                new UndoActionHandler(getSite(), undoContext));
        getViewSite().getActionBars().setGlobalActionHandler(
                ActionFactory.REDO.getId(),
                new RedoActionHandler(getSite(), undoContext));
    }

    private void paintParam(JobParameter param, Event e) {
        if (paramFont == null) {
            FontData[] fd = e.gc.getFont().getFontData();
            for (int i = 0; i < fd.length; i++) {
                fd[i].setHeight(fd[i].height - 1);
            }
            paramFont = new Font(getSite().getShell().getDisplay(), fd);
        }
        e.gc.setFont(paramFont);
        Integer nameLength = paramNameLength.get(param.getJob());
        if (nameLength == null) {
            nameLength = 0;
            for (JobParameter p : param.getJob().getJobParameters().values()) {
                nameLength = Math.max(nameLength, e.gc.textExtent(p
                        .getScriptParameter().getNicename()).x);
            }
            nameLength += e.gc.getCharWidth(':');
            nameLength += e.gc.getCharWidth(' ');
            paramNameLength.put(param.getJob(), nameLength);
        }
        e.gc.drawText(param.getScriptParameter().getNicename() + ':', e.x, e.y);
        e.gc.drawText(param.getValue(), e.x + nameLength, e.y);

    }
}