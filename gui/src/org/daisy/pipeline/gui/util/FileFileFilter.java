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
package org.daisy.pipeline.gui.util;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.daisy.util.file.EFile;

/**
 * @author Romain Deltour
 * 
 */
public abstract class FileFileFilter implements FileFilter {

    private boolean acceptDir;
    private Set<String> filteredDirNames;
    private static final String SVN_DIR = ".svn"; //$NON-NLS-1$

    public FileFileFilter(boolean acceptDir) {
        super();
        this.acceptDir = acceptDir;
        filteredDirNames = new HashSet<String>();
        filteredDirNames.add(SVN_DIR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {

        if (file.isDirectory()) {
            return acceptDir && !filteredDirNames.contains(file.getName());
        } else {
            return acceptEFile(new EFile(file));
        }
    }

    protected abstract boolean acceptEFile(EFile file);

}
