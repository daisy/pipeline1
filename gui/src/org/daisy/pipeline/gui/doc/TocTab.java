package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;

import org.daisy.pipeline.gui.util.HtmlFileFilter;
import org.daisy.pipeline.gui.util.swt.TreeTabItemProvider;
import org.daisy.pipeline.gui.util.viewers.ExpandTreeDoubleClickListener;
import org.daisy.pipeline.gui.util.viewers.FileTreeContentProvider;
import org.daisy.util.file.EFolder;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Romain Deltour
 * 
 */
public abstract class TocTab extends TreeTabItemProvider implements ITocTab {
    private FileFilter fileFilter;

    public TocTab() {
        super();
        fileFilter = createFileFilter();
    }

    public URI getURI() {
        IStructuredSelection sel = (IStructuredSelection) getViewer()
                .getSelection();
        File file = (File) sel.getFirstElement();
        if (file != null && file.isFile()) {
            // Note: the file has already been filtered
            return file.toURI();
        }
        return null;
    }

    public boolean select(Object element) {
        // try to get a file object
        File file = null;
        if (element instanceof File) {
            file = (File) element;
        } else {
            file = convertToFile(element);
        }
        if (file != null && contains(file)) {
            getViewer().setSelection(
                    new StructuredSelection(convertToTocFile(file)), true);
            return true;
        }
        return false;
    }

    protected boolean contains(File file) {
        return find(file, new File[] { getRootDir() });
    }

    protected File convertToFile(Object object) {
        return null;
    }

    protected File convertToTocFile(File file) {
        return file;
    }

    @Override
    protected IContentProvider createContentProvider() {
        return new FileTreeContentProvider(fileFilter);
    }

    @Override
    protected Control createControl(TabFolder parent) {
        Control control = super.createControl(parent);
        getViewer().addDoubleClickListener(new ExpandTreeDoubleClickListener());
        return control;
    }

    protected FileFilter createFileFilter() {
        return new HtmlFileFilter(true);
    }

    @Override
    protected IBaseLabelProvider createLabelProvider() {
        return new DocFileLabelProvider(new TocImageProvider(getRootDir()));
    }

    protected boolean find(File file, File[] files) {
        if (file == null) {
            return false;
        }
        for (File tested : files) {
            if (file.equals(tested)) {
                return true;
            }
            if (tested.isDirectory()
                    && find(file, tested.listFiles(fileFilter))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object getInput() {
        return getRootDir();
    }

    protected abstract EFolder getRootDir();

}
