/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package ca_cnib_rtf2dtbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.xml.sax.EntityResolver;

/**
 * @author Brandon Nelson
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class RTF2DTBook extends Transformer {

	public RTF2DTBook(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		// Get the rtf2xml command
		String rtf2xml = parameters.remove("rtf2xml");
		if (System.getProperty("os.name").startsWith("Windows")) {
			// Windows: returns rtf2xml.exe (compiled from python with py2exe)
			rtf2xml += "-win/rtf2xml.exe";
		} else {
			// Linux, Mac: returns the python command with rtf2xml.py script
			rtf2xml += "-py/rtf2xml.py";
		}
		rtf2xml = FilenameOrFileURI.toFile(rtf2xml).getAbsolutePath();

		// Read parameters
		// Dynamic ones
		String rtfFile = parameters.remove("rtf");
		String dtbookFile = parameters.remove("dtbook");

		// Static ones
		String stylesheet = parameters.remove("stylesheet");
		String xsltFactory = parameters.remove("factory");

		// Output the XML into a temporary file
		TempFile xmlFile;
		try {
			xmlFile = new TempFile();
		} catch (IOException e) {
			String message = i18n("ERROR_ABORTING", e.getMessage());
			throw new TransformerRunException(message, e);
		}

		// Setup rtf2xml args
		List<String> args = new ArrayList<String>();
		args.add(rtf2xml);
		args.add("--headings-to-sections");
		args.add("--lists");
		args.add("--indent=1");
		args.add("--no-empty-para");
		args.add("--output=" + xmlFile.getFile().getAbsolutePath());
		args.add(FilenameOrFileURI.toFile(rtfFile).getAbsolutePath());

		// printArgs(args);

		// Run rtf2xml
		sendMessage(i18n("RUNNING_PYTHON"), MessageEvent.Type.INFO_FINER);
		this.progress(0.05);

		try {
			if (Command.execute(args.toArray(new String[args.size()])) != 0) {
				String message = i18n("ERROR_ABORTING", i18n("PYTHON_FAILED"));
				throw new TransformerRunException(message);
			}
		} catch (ExecutionException e1) {
			String message = i18n("ERROR_ABORTING", e1.getMessage());
			throw new TransformerRunException(message, e1);
		}

		/*
		 * System.out.println("The length of the temp file is: " +
		 * xmlFile.getFile().length()); If the transformer fails, no tempfile is
		 * being created. The length of the file is 0 and causes the
		 * Stylesheet.apply to fail.
		 */

		sendMessage(i18n("APPLYING_XSLT"), MessageEvent.Type.INFO_FINER);
		this.progress(0.70);
		try {
			File outputFile = new File(dtbookFile).getAbsoluteFile();
			outputFile.getParentFile().mkdirs();
			EntityResolver resolver = CatalogEntityResolver.getInstance();
			Stylesheet.apply(xmlFile.getFile().getAbsolutePath(), stylesheet,
					outputFile.getAbsolutePath(), xsltFactory, null, resolver);
			this.progress(0.99);
		} catch (Exception e) {
			String message = i18n("ERROR_ABORTING", e.getLocalizedMessage());
			throw new TransformerRunException(message, e);
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void printArgs(String[] args) {
		System.err.println("arg0: " + args[0]);
		System.err.println("arg1: " + args[1]);
		System.err.println("arg2: " + args[2]);
		System.err.println("arg3: " + args[3]);
		System.err.println("arg4: " + args[4]);
		System.err.println("arg5: " + args[5]);
		System.err.println("arg6: " + args[6]);
	}

}
