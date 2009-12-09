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
package fr_braillenet_dtbook2daisy3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.fileset.ImageFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

/**
 * Main transformer class.
 * <p>Note: this transformer uses the XSLT dtbook2daisy3textonly.xsl placed in org/daisy/util/xml/xslt/stylesheets</p>
 * @author Romain Deltour
 */
public class DTBookToDaisy3 extends Transformer implements FilesetErrorHandler,  DOMErrorHandler {

	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	
	public DTBookToDaisy3(InputListener inListener, Boolean isInteractive) {
		super(inListener,  isInteractive);
	}

	protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
		String inputXML = parameters.remove("input");
		String outPath = parameters.remove("output");
		String copyReferring = parameters.remove("copyReferring");				
		
		URL xsltURL = Stylesheets.get("dtbook2daisy3textonly.xsl");		
		File inFile = FilenameOrFileURI.toFile(inputXML);
		
		
		/*
		 * Check if the doc is compound
		 */
		try {			
			NamespaceReporter nsr = new NamespaceReporter(inFile.toURI().toURL());
			if(nsr.getNamespaceURIs().contains(Namespaces.MATHML_NS_URI)) {
				this.sendMessage(i18n("CONTAINS_MATHML"), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
				parameters.put("svg_mathml", "true");
				//FIXME abort
			}
			
			if(nsr.getNamespaceURIs().contains(Namespaces.SVG_NS_URI)) {
				this.sendMessage(i18n("CONTAINS_SVG"), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
				parameters.put("svg_mathml", "true");
				//FIXME abort
			}
		} catch (Exception e) {
			this.sendMessage(i18n("ERROR", e.getMessage()), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
		} 
		
		
		try {	
			File outFile = FilenameOrFileURI.toFile(outPath);	
			File outDir = outFile.getParentFile();
			FileUtils.createDirectory(outDir);
			
			// Copy possible referring files
			if ("true".equals(copyReferring)) {				
				if (inFile.getParentFile().getCanonicalPath().equals(outDir.getCanonicalPath())) {
					throw new TransformerRunException(i18n("INPUT_OUTPUT_SAME"));
				}
				Fileset fileset = this.buildFileSet(new File(inputXML));								
				for (Iterator<FilesetFile> it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
					FilesetFile fsf = it.next();
					if (fsf instanceof ImageFile) {
						FileUtils.copyChild(fsf.getFile(), outDir, inFile.getParentFile());
					}
				}
			}
			
			// Set Missing parameters
			parameters.put("outputname", outFile.getName().substring(0, outFile.getName().lastIndexOf('.')));
			
			// Apply the XSLT
			Map<String,Object> xslParams = new HashMap<String,Object>();
			xslParams.putAll(parameters);
			Stylesheet.apply(inputXML, xsltURL, outFile.getAbsolutePath(), XSLT_FACTORY, xslParams, CatalogEntityResolver.getInstance());
			
			
			//Some post-xslt namespace cleanup.
			//FIXME add MathML doctype
			
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

	
	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.DOMErrorHandler#handleError(org.w3c.dom.DOMError)
	 */
	public boolean handleError(DOMError error) {
		Location loc = LocusTransformer.newLocation(error.getLocation());		
		MessageEvent.Type type = null;
		if(error.getSeverity()==DOMError.SEVERITY_WARNING) {
			type = MessageEvent.Type.WARNING;
		}else{
			type = MessageEvent.Type.ERROR;
		}		
		this.sendMessage(error.getMessage(), type, MessageEvent.Cause.INPUT, loc);	    		
		if(error.getSeverity()==DOMError.SEVERITY_WARNING) {
		   return true;
	    }
		return false; 
		
	}

}
