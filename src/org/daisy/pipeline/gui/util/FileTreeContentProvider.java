package org.daisy.pipeline.gui.util;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileTreeContentProvider implements ITreeContentProvider {
    
    private File file;

    public FileTreeContentProvider(File file) {
        super();
        this.file = file;
    }

    public Object[] getChildren(Object parentElement) {
        return ((File) parentElement).listFiles();
    }

    public Object getParent(Object element) {
        return ((File) element).getParentFile();
    }

    public boolean hasChildren(Object element) {
        Object[] obj = getChildren(element);
        return obj == null ? false : obj.length > 0;
    }

    public Object[] getElements(Object inputElement) {
        return file.listFiles();
    }

    public void dispose() {
        // Nothing
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing
    }

}
