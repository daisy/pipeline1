/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;

/**
 * @author Romain Deltour
 * 
 */
public abstract class EFileFilter implements FileFilter {

    private Set<String> filteredDirNames;
    private static final String SVN_DIR = ".svn"; //$NON-NLS-1$

    public EFileFilter() {
        super();
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
            try {
                return acceptEFolder(new EFolder(file));
            } catch (IOException e) {
                GuiPlugin.get().error(
                        "Couldn't create EFolder from file " + file, e);
                return false;
            }
        } else {
            return acceptEFile(new EFile(file));
        }
    }

    protected boolean acceptEFile(EFile file) {
        return true;
    }

    protected boolean acceptEFolder(EFolder dir) {
        return !filteredDirNames.contains(dir.getName());
    }

    protected void rejectDir(String name) {
        filteredDirNames.add(name);
    }

}
