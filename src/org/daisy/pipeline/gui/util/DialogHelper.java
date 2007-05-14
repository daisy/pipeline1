package org.daisy.pipeline.gui.util;

import java.io.File;

import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public final class DialogHelper {
    private DialogHelper() {
        // Nothing. This class is a static utility.
    }

    public static String browseDir(Shell shell, File dir) {
        return browseDir(shell, dir, SWT.NONE);
    }

    public static String browseDir(Shell shell, File dir, int style) {
        DirectoryDialog dialog = new DirectoryDialog(shell, style);
        if (dir != null) {
            dialog.setFilterPath(dir.getPath());
        }
        return dialog.open();
    }

    public static String browseFile(Shell shell, File file) {
        return browseFile(shell, file, SWT.NONE);
    }

    public static String browseFile(Shell shell, File file, int style) {
        return browseFile(shell, file, style, (String[]) null);
    }

    @SuppressWarnings("unchecked")
    public static String browseFile(Shell shell, File file, int style,
            String mime) {
        String[] ext = null;
        if (mime != null) {
            try {
                MIMETypeRegistry registry = MIMETypeRegistry.getInstance();
                MIMEType type = registry.getEntryByName(mime);
                ext = (String[]) type.getFilenamePatterns().toArray(
                        new String[0]);
            } catch (MIMETypeRegistryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MIMETypeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return browseFile(shell, file, style, (String[]) ext);
    }

    public static String browseFile(Shell shell, File file, int style,
            String[] extensions) {
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
        String res = dialog.open();
        if (res != null && dialog.getFileNames().length>1) {
            String path = dialog.getFilterPath();
            StringBuilder sb = new StringBuilder();
            for (String name : dialog.getFileNames()) {
                sb.append(path).append(File.separatorChar).append(name).append(
                        File.pathSeparatorChar);
            }
            sb.deleteCharAt(sb.length() - 1);
            res = sb.toString();
        }
        return res;
    }
}
