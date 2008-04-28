package org.daisy.pipeline.gui.language;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Switches the language in RCP based products. Works only if the product is
 * deployed to the local file system since the <product>.ini file is altered.
 * 
 * <p>
 * This code has been inspired from the <a
 * href="http://max-server.myftp.org/trac/mp3m">MP3 Manager RCP demo</a>
 * written by Kai Tödter.
 * </p>
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class LanguageSwitchHandler extends AbstractHandler {

	/** The command parameter for the locale to switch to */
	public static final String LOCALE_PARAMETER = "org.daisy.gui.language.locale"; //$NON-NLS-1$

	private static boolean changeLocale(String locale) {
		Location configArea = Platform.getConfigurationLocation();
		if (configArea == null) {
			return false;
		}

		URL location = null;
		try {
			location = new URL(configArea.getURL().toExternalForm()
					+ "config.ini"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			// This should never happen
		}

		try {
			String fileName = location.getFile();
			File file = new File(fileName);
			fileName += ".bak"; //$NON-NLS-1$
			file.renameTo(new File(fileName));
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			BufferedWriter out = new BufferedWriter(new FileWriter(location
					.getFile()));
			try {
				boolean isNlWritten = false;
				String line = in.readLine();
				while (line != null) {
					if (line.startsWith("osgi.nl")) { //$NON-NLS-1$
						if (locale != null) {
							out.write("osgi.nl=" + locale); //$NON-NLS-1$
							out.newLine();
						}
						isNlWritten = true;
					} else {
						out.write(line);
						out.newLine();
					}
					line = in.readLine();
				}
				if (!isNlWritten) {
					if (locale != null) {
						out.write("osgi.nl=" + locale); //$NON-NLS-1$
						out.newLine();
					}
				}
				out.flush();
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		} catch (Exception e) {
			GuiPlugin.get().error(e.getMessage(), e);
			return false;
		}
		return true;

	}

	/**
	 * The constructor.
	 */
	public LanguageSwitchHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String locale = event.getParameter(LOCALE_PARAMETER);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		// Does not work:
		// System.getProperties().setProperty("eclipse.exitdata", "-nl " +
		// locale);
		if (!changeLocale(locale)) {
			MessageDialog.openError(shell, Messages.langswitch_error_title,
					Messages.langswitch_error_message);
			return null;
		}
		if (MessageDialog.openQuestion(shell,
				Messages.langswitch_restart_title,
				Messages.langswitch_restart_message)) {
			PlatformUI.getWorkbench().restart();
		}
		return null;

	}
}
