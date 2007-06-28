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
package org.daisy.pipeline.gui.messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.model.MessageManager;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Romain Deltour
 * 
 */
public class ExportAction extends Action {
    private String lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$

    public ExportAction() {
        super(Messages.action_export, GuiPlugin
                .createDescriptor(IIconsKeys.MESSAGE_EXPORT));
    }

    @Override
    public void run() {
        // TODO persist last location
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell();
        String path = DialogHelper.browseFile(shell, null, SWT.SAVE,
                new String[] { "*.log", "*.*" });//$NON-NLS-1$ //$NON-NLS-2$
        if (path != null) {
            if (path.indexOf('.') == -1 && !path.endsWith(".log")) //$NON-NLS-1$
                path += ".log"; //$NON-NLS-1$
            File file = new File(path);
            if (file.exists()) {
                // useless on Mac OS X (don't know for other OS)
                String question = NLS
                        .bind(Messages.action_export_confirm, file);
                if (!MessageDialog.openQuestion(shell, getText(), question)) {
                    return;
                }
            }
            writeMessages(file);
        }
    }

    private void writeMessages(File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (MessageEvent message : MessageManager.getDefault()
                    .getMessages()) {
                StringBuilder sb = new StringBuilder();
                sb.append('[').append(message.getCause()).append(']');
                sb.append(" - ");//$NON-NLS-1$
                sb.append('!').append(message.getType()).append('!');
                sb.append(" - ");//$NON-NLS-1$
                sb.append(message.getMessage());
                Location loc = message.getLocation();
                if (loc != null) {
                    sb.append(" - ");//$NON-NLS-1$
                    String sysId = loc.getSystemId();
                    if (sysId != null && sysId.length() > 0) {
                        if (loc.getLineNumber() > -1) {
                            if (loc.getColumnNumber() > -1) {
                                sb.append(sysId).append('[').append(
                                        loc.getLineNumber()).append(',')
                                        .append(loc.getColumnNumber()).append(
                                                ']');
                            } else {
                                sb.append(sysId).append('[').append(
                                        loc.getLineNumber()).append(']');
                            }
                        } else {
                            sb.append(sysId);
                        }
                    }
                }
                sb.append(lineSeparator);
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            GuiPlugin.get().error("I/O Error while exporting messages", e);//$NON-NLS-1$
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
