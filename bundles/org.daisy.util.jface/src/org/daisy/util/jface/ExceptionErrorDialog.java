package org.daisy.util.jface;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExceptionErrorDialog extends AbstractDetailsDialog {
	private Throwable throwable;
	private Text text;
	private Clipboard clipboard;

	public ExceptionErrorDialog(Shell parentShell, Throwable t) {
		this(parentShell, t, null, null);
	}

	public ExceptionErrorDialog(Shell parentShell, Throwable t, String title,
			String message) {
		super(parentShell, title == null ? Messages
				.getString("ExceptionErrorDialog.title") : title, //$NON-NLS-1$
				message == null ? Messages
						.getString("ExceptionErrorDialog.text") //$NON-NLS-1$
						: message);
		throwable = t;
		clipboard = new Clipboard(Display.getCurrent());
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDetailsArea(Composite parent) {
		text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL
				| SWT.BORDER);
		text.setText(getStackTrace(throwable));
		hookListeners();
		return text;
	}

	private String getStackTrace(Throwable t) {
		if (t == null) {
			return null;
		}
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	@Override
	protected Image getImage() {
		return getErrorImage();
	}

	private void hookListeners() {
		// Key listener for ctrl+C / ctrl+A
		text.addKeyListener(new KeyAdapter() {

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
		Menu menu = new Menu(text.getShell(), SWT.POP_UP);
		MenuItem itemCopy = new MenuItem(menu, SWT.PUSH);
		itemCopy.setText(Messages.getString("common.action.copy")); //$NON-NLS-1$
		itemCopy.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				copyToClipboard();
			}
		});
		text.setMenu(menu);
	}

	private void copyToClipboard() {
		String textData = text.getSelectionText();
		if (textData.length() > 0) {
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { textData },
					new Transfer[] { textTransfer });
		}
	}

	private void selectAll() {
		text.selectAll();
	}
}
