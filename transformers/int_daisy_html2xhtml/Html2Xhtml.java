package int_daisy_html2xhtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ccil.cowan.tagsoup.AutoDetector;
import org.ccil.cowan.tagsoup.Parser;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.CharsetDetector;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Chain;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.InputSource;

/**
 * A wrapper around John Cowans Tagsoup (http://mercury.ccil.org/~cowan/XML/tagsoup/)
 * with added charset detection and extensible post-tagsoup processing.
 * @author Markus Gylling
 */
public class Html2Xhtml extends Transformer implements AutoDetector {
	//TODO write the medium and maxed stylesheets
	//TODO bring inline styles to an external stylesheet?
	//TODO bring auxilliary files along somehow (fileset with html manifestmember)
	
	private static final String XSLT_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	EFile inputFile = null;

	public Html2Xhtml(InputListener inListener,
			Set eventListeners,
			Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		

		try {
			//create the input fileset
			inputFile = new EFile((String) parameters.remove("input"));
			InputSource is = new InputSource(new FileInputStream(inputFile));

			//create the xslt chain (always one or more XSLTs, where the first is always echo.xsl)
			Chain chain = new Chain(XSLT_FACTORY,CatalogEntityResolver.getInstance());
			chain.addStylesheet(new StreamSource(Stylesheets.get("echo.xsl").openStream()));

			String xslparam = (String) parameters.remove("xsl");
			if (xslparam != null) {
				//add stylesheets to follow echo.xsl in the user-customized transform chain
				String[] xsls = xslparam.split(",");
				for (int i = 0; i < xsls.length; i++) {
					EFile xslf = new EFile(xsls[i]);
					if (!xslf.exists()) {
						throw new TransformerRunException("Inparameter XSLT file " + xslf.getAbsolutePath()
								+ " does not exist");
					}
					chain.addStylesheet(new StreamSource(xslf));
				}//for													
			} else {
				//add stylesheets to follow echo.xsl in the default transform chain
				String cleanLevel = (String)parameters.remove("cleanLevel");
				if ("NONE".equals(cleanLevel)){
						//add no more xslts, echo.xsl is all we use. This is the default.
				}else if ("MEDIUM".equals(cleanLevel)){
					chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml-clean-medium.xsl").openStream()));					
				}else if ("MAXED".equals(cleanLevel)){
					chain.addStylesheet(new StreamSource(this.getClass().getResource("xhtml-clean-maxed.xsl").openStream()));
				}
			}

			//create the tagsoup instance
			Parser parser = new Parser();
			parser.setEntityResolver(CatalogEntityResolver.getInstance());
			parser.setProperty("http://www.ccil.org/~cowan/tagsoup/properties/auto-detector", this);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/ignore-bogons", true);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/bogons-empty", false);
			parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/default-attributes", false);
			chain.setXMLReader(parser);
			
			//execute
			TempFile tempOutFile = new TempFile();
			SAXSource saxSource = new SAXSource(parser, is);
			StreamResult streamResult = new StreamResult(new FileOutputStream(tempOutFile.getFile()));
			chain.applyChain(Chain.saxSourceToInputSource(saxSource), streamResult);

			//if we came this far,
			//create the output dir and move result there
			EFolder outDir = (EFolder) FileUtils.createDirectory(new EFolder((String) parameters.remove("outDir")));
			File outFile = outDir.addFile(tempOutFile.getFile());
			outFile.renameTo(new java.io.File(outFile.getParentFile(),inputFile.getName()));

		} catch (Exception e) {
			throw new TransformerRunException(e.getMessage(), e);
		}

		return true;
	}

	public Reader autoDetectingReader(InputStream i) {
		//detect charset of current active inFile
		//return a reader with encoding prop set.
		//ignore the inputstream inparam...

		CharsetDetector det = new CharsetDetector();
		String charset = null;
		Charset cs = null;

		try {
			charset = det.detect(inputFile.toURL());
			if (null == charset) {
				charset = det.getProbableCharsetUsingLocale();
			}
			if (null != charset) {
				if (Charset.isSupported(charset)) {
					cs = Charset.forName(charset);
				}
			}
			return new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), cs));
		} catch (Exception e) {

		}
		return null;
	}
}
