/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
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

package org.daisy.util.execution;

/**
 * Observer of progress.
 * This interface is typically implemented by subclasses of the
 * <code>org.daisy.pipeline.core.transformer.Transformer</code> class and used by
 * other classes to report progress back to the transformer.
 * @author Linus Ericson
 */
public interface ProgressObserver {
    
    /**
     * Report a progress event.
     * @param progress the current progress in the interval [0,1]
     */
    public void reportProgress(double progress);
}
