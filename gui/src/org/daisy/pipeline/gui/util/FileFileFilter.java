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
    private static final String SVN_DIR = ".svn";

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
