package org.daisy.pipeline.lite;

import java.util.Iterator;
import java.util.List;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.lite.internal.Images;
import org.daisy.pipeline.lite.internal.MessageLabelProvider;
import org.daisy.util.jface.AbstractDetailsDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class JobResultDialog extends AbstractDetailsDialog {
	protected Image image;
	protected List<MessageEvent> messages;
	private Clipboard clipboard;
	private TableViewer msgViewer;

	public JobResultDialog(Shell shell, List<MessageEvent> messages, boolean ok) {
		this(shell, messages, ok ? "Pipeline Job Completed"
				: "Pipeline Job Failed", ok ? "Job completed." : messages.get(
				messages.size() - 1).getMessage(),
				ok ? MessageDialog.INFORMATION : MessageDialog.ERROR);
	}

	public JobResultDialog(Shell parentShell, List<MessageEvent> messages,
			String title, String message, int imageType) {
		super(parentShell, title, message);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.messages = messages;
		switch (imageType) {
		case MessageDialog.ERROR:
			image = getErrorImage();
			break;
		case MessageDialog.INFORMATION:
			image = getInfoImage();
			break;
		}
		this.clipboard = new Clipboard(Display.getCurrent());

	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setImage(Images.getImage(Images.PIPELINE_LOGO));
	}

	@Override
	protected void adjustDetailsLayout(GridData data) {
		super.adjustDetailsLayout(data);
		data.heightHint = msgViewer.getTable().getItemHeight()
				* Math.min(messages.size() + 1, 10);
	}

	@Override
	protected Control createDetailsArea(Composite parent) {
		msgViewer = new TableViewer(parent, SWT.MULTI | SWT.BORDER);
		msgViewer.setContentProvider(new ArrayContentProvider());
		msgViewer.setLabelProvider(new MessageLabelProvider());
		msgViewer.setInput(messages.toArray());
		hookListeners();
		return msgViewer.getControl();
	}

	private void hookListeners() {
		// Key listener for ctrl+C / ctrl+A
		msgViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// Select all for ctrl+A
				if ((((e.stateMask & SWT.COMMAND) > 0) || ((e.stateMask & SWT.CTRL) > 0))
						&& ((e.keyCode == 'a') || (e.keyCode == 'A'))) {
					selectAll();
				}
				// Paste for ctrl+C
				if ((((e.stateMask & SWT.COMMAND) > 0) || ((e.stateMask & SWT.CTRL) > 0))
						&& ((e.keyCode == 'c') || (e.keyCode == 'C'))) {
					copyToClipboard();
				}
			}
		});
		// Pop-up menu for select all and paste
		Menu menu = new Menu(msgViewer.getControl().getShell(), SWT.POP_UP);
		MenuItem itemSelect = new MenuItem(menu, SWT.PUSH);
		itemSelect.setText("Select All");
		itemSelect.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				selectAll();
			}
		});
		MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
		itemCopy.setText("Copy");
		itemCopy.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				copyToClipboard();
			}
		});
		msgViewer.getControl().setMenu(menu);
	}

	private void selectAll() {
		msgViewer.getTable().selectAll();
	}

	private void copyToClipboard() {
		StringBuilder sb = new StringBuilder();
		ISelection sel = msgViewer.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			for (Iterator<?> iterator = ssel.iterator(); iterator.hasNext();) {
				sb.append(iterator.next()).append("\n");
			}

		}
		String textData = sb.toString();
		if (textData.length() > 0) {

			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { textData },
					new Transfer[] { textTransfer });
		}
	}

	@Override
	protected Image getImage() {
		return image;
	}

}
