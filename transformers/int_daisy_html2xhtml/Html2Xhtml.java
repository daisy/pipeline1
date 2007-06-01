package int_daisy_html2xhtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ccil.cowan.tagsoup.AutoDetector;
import org.ccil.cowan.tagsoup.Parser;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.impl.FilesetImpl;
import org.daisy.util.fileset.interfaces.Fileset;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.i18n.CharsetDetector;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;
import org.daisy.util.xml.xslt.Chain;
import org.daisy.util.xml.xslt.TransformerFactoryConstants;
import org.xml.sax.InputSource;

/**
 * A wrapper around John Cowans Tagsoup
 * (http://mercury.ccil.org/~cowan/XML/tagsoup/) with added charset detection
 * and extensible post-tagsoup processing.
 * 
 * @author Markus Gylling
 */
public class Html2Xhtml extends Transformer implements AutoDetector, FilesetErrorHandler {
	// TODO write the medium and maxed stylesheets
	// TODO bring inline styles to an external stylesheet?
	// TODO make doctype user settable (XHTML10, XHTML11) 
	// TODO xml:lang as inparam

	EFile mInputFile = null;
	String mUserSetEncoding = null;

	public Html2Xhtml(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map parameters)
			throws TransformerRunException {

		try {

			mInputFile = new EFile(FilenameOrFileURI.toFile((String) parameters.remove("input")));
			InputSource is = new InputSource(new FileInputStream(mInputFile));

			String enc = (String) parameters.remove("encoding");
			if (enc != null && enc.length() > 0) {
				mUserSetEncoding = enc;
			}
			
			// create the xslt chain (always one or more XSLTs, where the first
			// is always echo.xsl)
			Chain chain = new Chain(TransformerFactoryConstants.SAXON8, CatalogEntityResolver.getInstance());
			chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml-clean-minimum.xsl").openStream()));

			String xslparam = (String) parameters.remove("xsl");
			if (xslparam != null
					&& xslparam.length() > 0) {
				// add stylesheets to follow echo.xsl in the user-customized
				// transform chain
				String[] xsls = xslparam.split(File.pathSeparator);
				for (int i = 0; i < xsls.length; i++) {
					EFile xslf = new EFile(xsls[i]);
					if (!xslf.exists()) {
						throw new TransformerRunException("Inparameter XSLT file "
								+ xslf.getAbsolutePath()
								+ " does not exist");
					}
					chain.addStylesheet(new StreamSource(xslf));
				}// for
			} else {
				// add stylesheets to follow echo.xsl in the default transform
				// chain
				String cleanLevel = (String) parameters.remove("cleanLevel");
				if ("NONE".equals(cleanLevel)) {
					// add no more xslts, echo.xsl is all we use. This is the
					// default.
				} else if ("MEDIUM".equals(cleanLevel)) {
					chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml-clean-medium.xsl").openStream()));
				} else if ("MAXED".equals(cleanLevel)) {
					chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml-clean-maxed.xsl").openStream()));
				}
			}

			// create the tagsoup instance
			String param = (String)parameters.remove("stripUnknownElems");
			boolean ignoreBogons = param.equals("true");
			
			Parser parser = new Parser();
			parser.setEntityResolver(CatalogEntityResolver.getInstance());
			parser.setProperty("http://www.ccil.org/~cowan/tagsoup/properties/auto-detector", this);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/ignore-bogons", ignoreBogons);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/bogons-empty", false);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/default-attributes", false);
			chain.setXMLReader(parser);

			// execute
			TempFile tempOutFile = new TempFile();
			SAXSource saxSource = new SAXSource(parser, is);
			StreamResult streamResult = new StreamResult(new FileOutputStream(tempOutFile.getFile()));
			chain.applyChain(Chain.saxSourceToInputSource(saxSource), streamResult);

			//add doctype
			TempFile tempOutFile2 = new TempFile();
			System.gc();
			assertDoctype(new EFile(tempOutFile.getFile()),new EFile(tempOutFile2.getFile()));
			
			// if we came this far,
			// create the output dir and move result there
			EFolder outDir = (EFolder) FileUtils.createDirectory(new EFolder((String) parameters.remove("outDir")));			
			File outFile = outDir.addFile(tempOutFile2.getFile());
			File finalFile = new java.io.File(outFile.getParentFile(), mInputFile.getName());
			outFile.renameTo(finalFile);

			
			
			
			//now parse the result and move over any referred files that were in input dir
			try{
				Fileset fileset = new FilesetImpl(finalFile.toURI(),this,false,false);
				Collection<String> c = fileset.getManifestMember().getUriStrings();
				for(String uriString : c) {
					if(!uriString.startsWith("..")) {
						try{
							URI uri = mInputFile.toURI().resolve(URI.create(uriString));																
							File file = new File(uri);
							if(file.exists() && file.canRead()) {
								File dest = null;
								if(file.getParentFile().equals(mInputFile.getParentFile())) {
									dest = new File(finalFile.getParentFile(), file.getName());
								}else{
									URI relative = mInputFile.getParentFile().toURI().relativize(file.getParentFile().toURI());								
									dest = new File(new File(finalFile.getParentFile(),relative.getPath()),file.getName());																
								}
								FileUtils.createDirectory(dest.getParentFile());
								FileUtils.copyFile(file, dest);
							}
						}catch (Exception e) {
							this.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
						}
					}
				}
				
			}catch (Exception e) {
				this.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
			}
		} catch (Exception e) {
			this.sendMessage(e.getMessage(), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM);
			throw new TransformerRunException(e.getMessage(), e);
		}

		return true;
	}

	private void assertDoctype(EFile in, EFile out) throws IOException, XMLStreamException {
		//add only if not existing
		boolean hasDoctype = true;
		Peeker peeker = null;
		try{
			peeker= PeekerPool.getInstance().acquire();
			PeekResult pr = peeker.peek(in);
			if(pr.getPrologPublicId()==null && pr.getPrologSystemId()==null){
				hasDoctype=false;
			}
		}catch (Exception e) {

		} finally{
			PeekerPool.getInstance().release(peeker);
		}
		
		if(hasDoctype){
			FileUtils.copy(in, out);
		}else{
			XMLInputFactory xif = null;
			Map xifPropertes = StAXInputFactoryPool.getInstance().getDefaultPropertyMap(false);
			XMLOutputFactory xof = null;
			Map xofPropertes = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			XMLEventFactory xef = null;
			
			try{
				xif = StAXInputFactoryPool.getInstance().acquire(xifPropertes);
				xof = StAXOutputFactoryPool.getInstance().acquire(xofPropertes);
				xef = StAXEventFactoryPool.getInstance().acquire();
												
				XMLEventReader xsr = xif.createXMLEventReader(in.asInputStream());
				FileOutputStream fos = new FileOutputStream(out);				
				XMLEventWriter xew = xof.createXMLEventWriter(fos);
				
				while(xsr.hasNext()) {
					XMLEvent e = xsr.nextEvent();					
					if(e.getEventType() != XMLEvent.DTD) {
						xew.add(e);	
					}										
					if(e.isStartDocument()) {
						xew.add(xef.createDTD("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"));
					}
				}
				xew.flush();
				xew.close();
			}finally{
				StAXInputFactoryPool.getInstance().release(xif,xifPropertes);
				StAXOutputFactoryPool.getInstance().release(xof,xofPropertes);
				StAXEventFactoryPool.getInstance().release(xef);
			}
		}
	}
	
	

	public Reader autoDetectingReader(InputStream i) {
		// detect charset of current active inFile
		// return a reader with encoding prop set.
		// ignore the inputstream inparam...
		Charset cs = null;
		try {
			if (mUserSetEncoding != null && Charset.isSupported(mUserSetEncoding)) {
				cs = Charset.forName(mUserSetEncoding);
			} else {
				CharsetDetector det = new CharsetDetector();
				String charset = det.detect(mInputFile.toURL());
				if (null == charset) {
					charset = det.getProbableCharsetUsingLocale();
				}
				if (null != charset) {
					if (Charset.isSupported(charset)) {
						cs = Charset.forName(charset);
					}
				}
			}
			if (cs != null) {
				this.sendMessage(i18n("USING_CHARSET",cs.displayName()),MessageEvent.Type.INFO,MessageEvent.Cause.INPUT);
				return new BufferedReader(new InputStreamReader(new FileInputStream(mInputFile), cs));
			}	
		} catch (Exception e) {
			this.sendMessage(e.getMessage(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM);
		}
		this.sendMessage(i18n("NO_CHARSET",mInputFile.getName()),MessageEvent.Type.WARNING,MessageEvent.Cause.INPUT);
		return null;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		//run when parsing the xhtml output, dont report out
		//complaining has no place here
	}
}
