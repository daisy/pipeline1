/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ca_cnib_rtf2dtbook;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.EntityResolver;


/**
 * @author Linus Ericson
 */
public class RTF2DTBook extends Transformer {

	private static String pythonCommand = System.getProperty("dmfc.python.path");

	public RTF2DTBook(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);        
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		// Read parameters
		String rtfFile = (String)parameters.remove("rtf");
		String dtbookFile = (String)parameters.remove("dtbook");
		String python = (String)parameters.remove("python");
		String stylesheet = (String)parameters.remove("stylesheet");
		String xsltFactory = (String)parameters.remove("factory"); 

		// Output the XML into a temporary file
		TempFile xmlFile;
		try {
			xmlFile = new TempFile();
		} catch (IOException e) {
			throw new TransformerRunException(i18n("CANNOT_CREATE_TEMP_FILE"), e);
		}

		// Setup python args
		String[] args = new String[8];
		args[0] = pythonCommand;
		args[1] = FilenameOrFileURI.toFile(python).getAbsolutePath();
		args[2] = "--headings-to-sections";
		args[3] = "--lists";
		args[4] = "--indent=1";
		args[5] = "--no-empty-para";
		args[6] = "--output=" + xmlFile.getFile().getAbsolutePath();
		args[7] = FilenameOrFileURI.toFile(rtfFile).getAbsolutePath();;

		/*
		System.err.println("arg0: " + args[0]);
		System.err.println("arg1: " + args[1]);
		System.err.println("arg2: " + args[2]);
		System.err.println("arg3: " + args[3]);
		System.err.println("arg4: " + args[4]);
		System.err.println("arg5: " + args[5]);
		System.err.println("arg6: " + args[6]);
		 */

		// Run python
		sendMessage(Level.FINER, i18n("RUNNING_PYTHON"));
		this.progress(0.05);


		try {
			if (Command.execute(args) != 0) {
				throw new TransformerRunException(i18n("PYTHON_FAILED"));
			}
		} catch (ExecutionException e1) {
			throw new TransformerRunException(i18n("CANNOT_RUN_PYTHON"), e1);
		} 


		/*
		System.out.println("The length of the temp file is: " + xmlFile.getFile().length());
		If the transformer fails, no tempfile is being created.  The length of the file
		is 0 and causes the Stylesheet.apply to fail.
		 */
		
			// Finish up with some XSLT
			sendMessage(Level.FINER, i18n("APPLYING_XSLT"));
			this.progress(0.70);
			try {
				EntityResolver resolver = CatalogEntityResolver.getInstance();
				Stylesheet.apply(xmlFile.getFile().getAbsolutePath(), stylesheet, dtbookFile, xsltFactory, null, resolver);
				this.progress(0.99);
			} catch (XSLTException e) {
				throw new TransformerRunException(i18n("CANNOT_CREATE_TEMP_FILE"), e);

			} catch (CatalogExceptionNotRecoverable e) {
				throw new TransformerRunException(i18n("ENTITY_RESOLVER_ERROR"), e);
			}

			return true;
		

		
	}

}
