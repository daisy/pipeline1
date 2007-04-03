package org.daisy.pipeline.gui.tasks;

import org.daisy.pipeline.gui.util.jface.CompositeListViewer;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Romain Deltour
 * 
 */
public class TaskListViewer extends CompositeListViewer<TaskItem> {
    /**
     * Creates a task list viewer on a newly-created composite list control
     * under the given parent. The list control is created using the SWT style
     * bits <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>.
     * The viewer has no input, no content provider, a default label provider,
     * no sorter, and no filters.
     * 
     * @param parent the parent control
     */
    public TaskListViewer(Composite parent) {
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    }

    /**
     * Creates a task list viewer on a newly-created composite list control
     * under the given parent. The list control is created using the given style
     * bits. The viewer has no input, no content provider, a default label
     * provider, no sorter, and no filters.
     * 
     * @param parent the parent control
     * @param style SWT style bits
     */
    public TaskListViewer(Composite parent, int style) {
        this(new CompositeList<TaskItem>(parent, style));
    }

    /**
     * Creates a task list viewer on the given composite list control. The
     * viewer has no input, no content provider, a default label provider, no
     * sorter, and no filters.
     * 
     * @param table the table control
     */
    public TaskListViewer(CompositeList<TaskItem> list) {
        super(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobdetails.CompositeListViewer#createItem(org.daisy.pipeline.gui.jobdetails.CompositeList,
     *      int)
     */
    @Override
    protected TaskItem createItem(CompositeList<TaskItem> parent, int index) {
        return new TaskItem(parent, SWT.NONE, index);
    }

}