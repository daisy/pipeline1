package org.daisy.pipeline.gui.util.jface;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileTreeContentProvider implements ITreeContentProvider {

    private FileFilter filter;

    public FileTreeContentProvider(FileFilter filter) {
        super();
        this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    public Object[] getChildren(Object parentElement) {
        if (!(parentElement instanceof File)) {
            throw new IllegalArgumentException("Given element is not a file");
        }
        File file = (File) parentElement;
        return file.listFiles(filter);
    }

    public Object getParent(Object element) {
        if (!(element instanceof File)) {
            throw new IllegalArgumentException("Given element is not a file");
        }
        return ((File) element).getParentFile();
    }

    public boolean hasChildren(Object element) {
        Object[] obj = getChildren(element);
        return obj == null ? false : obj.length > 0;
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public void dispose() {
        // Nothing
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing
    }

}
