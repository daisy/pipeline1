/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2007  Daisy Consortium
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
package se_tpb_charsetSwitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.exception.FilesetFatalException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetFileFactory;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.XmlFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.daisy.util.xml.xslt.XSLTException;
import org.xml.sax.SAXException;

/**
 * Switches character set on all XML files in a fileset.
 * @author Linus Ericson
 */
public class CharsetSwitcher extends Transformer implements FilesetErrorHandler {
	
	//mg 20070530: using saxon8 causes file locks
	private static final String XSLT_FACTORY = TransformerFactoryConstants.SAXON8;
	//private static final String XSLT_FACTORY = TransformerFactoryConstants.XALAN_XSLTC_INTERNAL;	
	private static final double FILESET_DONE = 0.05;
	private static final double TRANSFORM_DONE = 0.97;
	
	
	private File stylesheet = null;
	private Map mXifProperties = null;
	
	/**
	 * Constructor.
	 * @param inListener
	 * @param eventListeners
	 * @param isInteractive
	 */
	public CharsetSwitcher(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
		
		mXifProperties = new HashMap();
		mXifProperties.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
		mXifProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		mXifProperties.put(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.pipeline.core.transformer.Transformer#execute(java.util.Map)
	 */
	protected boolean execute(Map parameters) throws TransformerRunException {
		String input = (String)parameters.remove("input");
		String output = (String)parameters.remove("output");
		String encoding = (String)parameters.remove("encoding");
		String breaks = (String)parameters.remove("breaks");
		
		File inputFile = FilenameOrFileURI.toFile(input);		
		File outputFile = new File(FilenameOrFileURI.toFile(output), inputFile.getName());
		
		if (inputFile.equals(outputFile)) {
			return false;
		}
		
		// Set the desired line break symbol(s)
		if ("unix".equals(breaks)) {
			breaks = "\n";
		} else if ("dos".equals(breaks)) {
			breaks = "\r\n";
		} else if ("mac".equals(breaks)) {
			breaks = "\r";
		} else {
			breaks = System.getProperty("line.separator", "\n");
		}
		
		try {
			EFolder outputFolder = new EFolder(FilenameOrFileURI.toFile(output));
			// Compile stylesheet to perform switch
			stylesheet = this.createStylesheet(encoding);
			
			Collection filesInFileset = new HashSet();
			FileUtils.createDirectory(FilenameOrFileURI.toFile(output));
			FilesetFile filesetFile = FilesetFileFactory.newInstance().newFilesetFile(inputFile);
			filesetFile.parse();
			filesInFileset.add(filesetFile);
			
			if (filesetFile instanceof ManifestFile) {
				Fileset fileset = this.buildFileSet(filesetFile.getFile());				
				filesInFileset.addAll(fileset.getLocalMembers());
			}
			
			this.progress(FILESET_DONE);
			this.checkAbort();
						
			int count = 0;
			for (Iterator it = filesInFileset.iterator(); it.hasNext(); ) {
				FilesetFile fsf = (FilesetFile)it.next();
				if (fsf instanceof XmlFile) {
					// Handle XML files
					TempFile tempFile = new TempFile();
					this.switchCharset(fsf, tempFile.getFile());
					//outputFolder.addFile(tempFile.getFile(),true,"fromRtf.html");
					this.switchLineBreak(tempFile.getFile(), new File(FilenameOrFileURI.toFile(output), fsf.getName()), breaks);
					tempFile.delete();
				} else {
					// Just copy everything else
					outputFolder.addFile(fsf.getFile());
				}
				count++;
				this.progress(FILESET_DONE + (double)count/filesInFileset.size()*(TRANSFORM_DONE-FILESET_DONE));
				this.checkAbort();
			}
			
			
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);			
		} catch (FilesetFatalException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (BitstreamException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XSLTException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (PoolException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException(e.getMessage(), e);
		}		
		return true;
	}
	
	private void switchCharset(FilesetFile inputFile, File outputFile) throws CatalogExceptionNotRecoverable, XSLTException, PoolException, XMLStreamException, IOException {
		
		// Detect public ID, system ID and root element name
		String publicId = null;
		String systemId = null;
		String internal = null;
		String root = null;
		StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();		
		XMLInputFactory factory = pool.acquire(mXifProperties);
		factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		
		StreamSource ss = new StreamSource(inputFile.getFile());
		ss.setSystemId(inputFile.getFile());		
		XMLEventReader reader = factory.createXMLEventReader(ss);		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.getEventType() == XMLStreamConstants.DTD) {
				String doctype = ((DTD)event).getDocumentTypeDeclaration();
				DoctypeParser doctypeParser = new DoctypeParser(doctype);
				publicId = doctypeParser.getPublicId();
				systemId = doctypeParser.getSystemId();
				internal = doctypeParser.getInternalSubset();
			}
			if (event.isStartElement()) {
				root = event.asStartElement().getName().getLocalPart();
				break;
			}
		}		
		if(ss.getInputStream()!=null) ss.getInputStream().close();
		if(ss.getReader()!=null) ss.getReader().close();
		reader.close();
		pool.release(factory, mXifProperties);
		
		// Fill in properties
		Map properties = new HashMap();
		if (publicId != null) {
			properties.put("public", publicId);
		}
		if (systemId != null) {
			properties.put("system", systemId);
		}
		if (internal != null) {
			properties.put("internal", internal);
		}
		if (root != null) {
			properties.put("root", root);
		}
				
		// Transform
		// mg20070530: this is where the filelock occurs on inputFile when using saxon8
		Stylesheet.apply(inputFile.toString(), stylesheet.toString(), outputFile.toString(), XSLT_FACTORY, properties, CatalogEntityResolver.getInstance());
	}
	
	/**
	 * Compile the stylesheet that is to perform the charset switch.
	 * @param encoding the desired encoding
	 * @return a File containing a stylesheet
	 * @throws IOException
	 * @throws CatalogExceptionNotRecoverable
	 * @throws XSLTException
	 */
	private File createStylesheet(String encoding) throws IOException, CatalogExceptionNotRecoverable, XSLTException {
		File compile = new File(this.getTransformerDirectory(), "compile.xsl");
		File template = new File(this.getTransformerDirectory(), "template.xsl");
		TempFile stylesheet = new TempFile();	
		Map parameters = new HashMap();
		parameters.put("encoding", encoding);
		Stylesheet.apply(template.toString(), compile.toString(), stylesheet.getFile().toString(), XSLT_FACTORY, parameters, CatalogEntityResolver.getInstance());
		return stylesheet.getFile();
	}
	
	/**
	 * Switch line break symbol(s).
	 * @param inputFile the input file
	 * @param outputFile the output file
	 * @param eol the desired end of line character sequence
	 * @throws IOException
	 */
	private void switchLineBreak(File inputFile, File outputFile, String eol) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		InputStream is = new UnixInputSteam(fis);
		OutputStream os = new FileOutputStream(outputFile);
		int b = is.read();
		while (b != -1) {						
			if (b == '\n') {
				os.write(eol.getBytes());
			} else {
				os.write(b);
			}
			b = is.read();
		}
		os.flush();
		os.close();
		is.close();
		fis.close();
		
	}
	
	/**
	 * Build a fileset.
	 * @param manifest the manifest file
	 * @return a fileset
	 * @throws FilesetFatalException
	 */
	private Fileset buildFileSet(File manifest) throws FilesetFatalException {
        return new FilesetImpl(manifest.toURI(), this, false, true);
    }

	public void error(FilesetFileException ffe) throws FilesetFileException {		
		this.sendMessage(ffe);		
	}

}
