package org.daisy.pipeline.gui.util;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class DefaultSelectionEnabler implements ISelectionEnabler {

    public DefaultSelectionEnabler(Mode mode, Class[] classes) {
        super();
        this.mode = mode;
        this.classes = classes;
    }

    public final boolean isEnabledFor(ISelection selection) {
        // Handle undefined selections.
        if (selection == null) {
            selection = StructuredSelection.EMPTY;
        }
        if (selection instanceof IStructuredSelection) {
            return isEnabledFor((IStructuredSelection) selection);
        }
        return false;
    }

    private Mode mode;
    private Class[] classes;

    private boolean isClassCompatible(Object obj) {
        if (classes.length == 0) {
            return true;
        }
        for (Class clazz : classes) {
            if (clazz.isInstance(obj)) {
                return true;
            }
        }
        return false;
    }

    protected boolean checkContent(IStructuredSelection selection) {
        return true;
    }

    private boolean isEnabledFor(IStructuredSelection selection) {
        // Check size
        if (!mode.isSizeCompatible(selection.size())) {
            return false;
        }
        // Check class requirements
        if (classes.length != 0) {
            Iterator iter = selection.iterator();
            while (iter.hasNext()) {
                if (!isClassCompatible(iter.next())) {
                    return false;
                }
            }
        }
        // Check the selection content
        return checkContent(selection);
    }

}
