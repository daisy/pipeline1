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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.image.ImageFile;
import org.daisy.util.xml.LocusTransformer;
import org.daisy.util.xml.NamespaceReporter;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SimpleNamespaceContext;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.dom.Serializer;
import org.daisy.util.xml.pool.LSParserPool;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSParser;

/**
 * Main transformer class.
 * <p>Note: this transformer uses the XSLT dtbook2xhtml.xsl placed in org/daisy/util/xml/xslt/stylesheets</p>
 * @author Linus Ericson
 */
public class DTBook2Xhtml extends Transformer implements FilesetErrorHandler,  DOMErrorHandler {

	public DTBook2Xhtml(InputListener inListener, Boolean isInteractive) {
		super(inListener,  isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		String inputXML = (String)parameters.remove("xml");
		String factory = (String)parameters.remove("factory");
		String out = (String)parameters.remove("out");
		String copyReferring = (String)parameters.remove("copyReferring");				
		
		URL xsltURL = Stylesheets.get("dtbook2xhtml.xsl");		
		File inFile = FilenameOrFileURI.toFile(inputXML);
		
		/*
		 * Check if the doc is compound
		 */
		try {			
			NamespaceReporter nsr = new NamespaceReporter(inFile.toURI().toURL());
			if(nsr.getNamespaceURIs().contains(Namespaces.MATHML_NS_URI)) {
				this.sendMessage(i18n("CONTAINS_MATHML"), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
				parameters.put("svg_mathml", "true");
			}
			
			if(nsr.getNamespaceURIs().contains(Namespaces.SVG_NS_URI)) {
				this.sendMessage(i18n("CONTAINS_SVG"), MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM);
				parameters.put("svg_mathml", "true");
			}
		} catch (Exception e) {
			this.sendMessage(i18n("ERROR", e.getMessage()), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
		} 
		
		
		try {	
			File outFile = FilenameOrFileURI.toFile(out);		
			FileUtils.createDirectory(outFile.getParentFile());
			
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
				Fileset fileset = this.buildFileSet(new File(inputXML));								
				if (!parameters.containsKey("css_path")) {
					parameters.put("css_path", "default.css");
				}
				Stylesheet.apply(inputXML, xsltURL, new File(folder, outFileName).toString(), factory, parameters, CatalogEntityResolver.getInstance());

				URL url2 = this.getClass().getResource("default.css");
				folder.writeToFile((String)parameters.get("css_path"), url2.openStream());
				
				for (Iterator it = fileset.getLocalMembers().iterator(); it.hasNext(); ) {
					FilesetFile fsf = (FilesetFile)it.next();
					if (fsf instanceof ImageFile) {
						folder.addFile(fsf.getFile());
					}
				}
			} else {
				Stylesheet.apply(inputXML, xsltURL, out, factory, parameters, CatalogEntityResolver.getInstance());
			}
			
			
			if(parameters.containsKey("svg_mathml")) {
				//some post-xslt namespace cleanup.
				Map<String, Object>domConfigMap = LSParserPool.getInstance().getDefaultPropertyMap(Boolean.FALSE);
				LSParser parser = LSParserPool.getInstance().acquire(domConfigMap);
				DOMConfiguration domConfig = parser.getDomConfig();						
				domConfig.setParameter("error-handler", this);										
				Document doc = parser.parseURI(outFile.toURI().toString());

				SimpleNamespaceContext snc = new SimpleNamespaceContext();
				snc.declareNamespace("m", Namespaces.MATHML_NS_URI);
				NodeList math = XPathUtils.selectNodes(doc.getDocumentElement(), "//m:*", snc);
				for (int i = 0; i < math.getLength(); i++) {
					try{
						Node m = math.item(i);
						m.setPrefix("");
						if(m.getLocalName().equals("math")) {
							Node a = m.getAttributes().removeNamedItem("xmlns:dtbook");
							Node b = m.getAttributes().removeNamedItem("xmlns:m");
							Node c = m.getAttributes().getNamedItem("xmlns");
							c.setNodeValue(Namespaces.MATHML_NS_URI);
						}
					} catch (Exception e) {
						this.sendMessage(e.getMessage(), MessageEvent.Type.ERROR);
					}
				}
				
				Map<String,Object> props = new HashMap<String,Object>();
				props.put("namespaces", Boolean.FALSE); //temp because of attributeNS bug(?) in Xerces DOM3LS					
				props.put("error-handler", this);	
				//props.put("format-pretty-print", Boolean.TRUE);					
				Serializer.serialize(doc, outFile, "utf-8", props);					
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
