/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.util.viewers;

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
