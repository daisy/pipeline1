package org.daisy.pipeline.gui.util;

import org.eclipse.jface.viewers.ISelection;

public interface ISelectionEnabler {
    public enum Mode {
        ANY_NUMBER, NONE, NONE_OR_ONE, ONE_OR_MORE, SINGLE, MULTIPLE, ;
        public boolean isSizeCompatible(int size) {
            switch (this) {
            case ANY_NUMBER:
                return true;
            case NONE:
                return size == 0;
            case NONE_OR_ONE:
                return size <= 1;
            case ONE_OR_MORE:
                return size != 0;
            case SINGLE:
                return size == 1;
            case MULTIPLE:
                return size > 2;
            default:
                return false;
            }
        }
    }

    public boolean isEnabledFor(ISelection selection);
}
