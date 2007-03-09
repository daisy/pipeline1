package org.daisy.pipeline.gui.util;

import java.io.File;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public final class DialogHelper {
    private DialogHelper() {
        // Nothing. This class is a static utility.
    }

    public static String browseFile(Shell shell, File file, int style,
            String[] extensions) {
        // TODO core integration : use EFile instead of File
        FileDialog dialog = new FileDialog(shell, style);
        if (file != null) {
            if (file.isDirectory()) {
                dialog.setFilterPath(file.getPath());
            } else {
                dialog.setFileName(file.getName());
                dialog.setFilterPath(file.getParent());
            }
        }
        dialog.setFilterExtensions(extensions);
        return dialog.open();
    }
}
