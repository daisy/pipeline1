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
import java.util.Collection;

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

    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public static String browseFile(Shell shell, File file, int style,
            String mime) {
        String[] ext = null;
        if (mime != null) {
            try {
                MIMETypeRegistry registry = MIMETypeRegistry.getInstance();
                MIMEType type = registry.getEntryByName(mime);
                Collection patterns = type.getFilenamePatterns();
                patterns.add("*.*"); //$NON-NLS-1$
                ext = (String[]) patterns.toArray(new String[0]);
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
        if (res != null && dialog.getFileNames().length > 1) {
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
