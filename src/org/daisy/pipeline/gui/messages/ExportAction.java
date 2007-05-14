package org.daisy.pipeline.gui.messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Romain Deltour
 * 
 */
public class ExportAction extends Action {
    private String lineSeparator = System.getProperty("line.separator");

    public ExportAction() {
        super("Export Messages");
    }

    @Override
    public void run() {
        // TODO persist last location
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell();
        String path = DialogHelper.browseFile(shell, null, SWT.SAVE,
                new String[] { "*.log" });//$NON-NLS-1$
        if (path != null) {
            if (path.indexOf('.') == -1 && !path.endsWith(".log")) //$NON-NLS-1$
                path += ".log"; //$NON-NLS-1$
            File file = new File(path);
            if (file.exists()) {
                // TODO useless on Mac OS X (don't know for other OS)
                String question = "File " + file.toString()
                        + " already exists.  Would you like to overwrite it?";
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
                sb.append(lineSeparator);
                System.out.println("");
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
