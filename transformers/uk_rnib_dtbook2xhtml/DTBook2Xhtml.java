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
package uk_rnib_dtbook2xhtml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.image.ImageFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

public class DTBook2Xhtml extends Transformer implements FilesetErrorHandler {

	public DTBook2Xhtml(InputListener inListener, Boolean isInteractive) {
		super(inListener,  isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		String xml = (String)parameters.remove("xml");
		String factory = (String)parameters.remove("factory");
		String out = (String)parameters.remove("out");
		String copyReferring = (String)parameters.remove("copyReferring");				
		
		URL url = Stylesheets.get("dtbook2xhtml.xsl");
		
		try {	
			File outFile = new File(out);
			File inFile = new File(xml);
			if ("true".equals(copyReferring)) {				
				EFile eInFile = new EFile(inFile);
				String outFileName;
				EFolder folder;
				if (outFile.isDirectory()) {
					folder = new EFolder(outFile);
					outFileName = eInFile.getNameMinusExtension() + ".html";
				} else {
					folder = new EFolder(outFile.getParentFile());
					outFileName = outFile.getName();					
				}
				
				if (inFile.getParentFile().equals(folder)) {
					throw new TransformerRunException("Output directory may not be same as input directory");
				}
				Fileset fileset = this.buildFileSet(new File(xml));								
				if (!parameters.containsKey("css_path")) {
					parameters.put("css_path", "default.css");
				}
				Stylesheet.apply(xml, url, new File(folder, outFileName).toString(), factory, parameters, CatalogEntityResolver.getInstance());
				//folder.addFile(new File(this.getTransformerDirectory(), (String)parameters.get("css_path")));				
				//System.err.println("cl: " + this.getClass().getClassLoader());
				URL url2 = this.getClass().getResource("default.css");
				folder.writeToFile((String)parameters.get("css_path"), url2.openStream());
				
				for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
					FilesetFile fsf = (FilesetFile)it.next();
					if (fsf instanceof ImageFile) {
						folder.addFile(fsf.getFile());
					}
				}
			} else {
				Stylesheet.apply(xml, url, out, factory, parameters, CatalogEntityResolver.getInstance());
			}
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		return true;
	}
	
	private Fileset buildFileSet(File manifest) throws FilesetFatalException {
        return new FilesetImpl(manifest.toURI(), this, false, true);
    }

	public void error(FilesetFileException ffe) throws FilesetFileException {	
		this.sendMessage(ffe);
	}

}
