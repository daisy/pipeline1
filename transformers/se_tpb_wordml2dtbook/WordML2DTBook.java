package se_tpb_wordml2dtbook;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.SAXParserFactory;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;

/**
 * 
 * Transforms a Microsoft Office 2003 WordML document into DTBook.
 * 
 * @author  Joel Håkansson
 * @version 2006-aug-25
 * @since 1.0
 */
public class WordML2DTBook extends Transformer implements MessageInterface {


	/**
	 * 
	 * @param inListener
	 * @param eventListeners
	 * @param isInteractive
	 */
	public WordML2DTBook(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
		// TODO Auto-generated constructor stub
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		String factory = (String)parameters.remove("factory");
		String input = (String)parameters.remove("xml");
        String xslt = (String)parameters.remove("xslt");
        File outdir = new File((String)parameters.remove("out")); // ändra till katalog
        String filename = (String)parameters.remove("filename");
        if (filename==null || filename.equals(""))
        	filename = new File(input).getName() + ".dtbook.xml";
        String images = (String)parameters.remove("images");
        if (!outdir.exists()) outdir.mkdirs();
        progress(0.05);
        File result = new File(outdir, filename);
		if (images.equals("true")) {
			decodeImages(new File(input), outdir);
			// put parameter for xslt
			parameters.put("forceJPEG", "false");
		}
		progress(0.6);
		try {
		    Stylesheet.apply(input, xslt, result.getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
        } catch (Exception e) {
            throw new TransformerRunException(e.getMessage(), e);
		}
        progress(1);
		return true;
	}

	private void decodeImages(File input, File outdir) throws TransformerRunException {
		Date start = new Date();
	  	try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			ImageDecodeHandler handler = new ImageDecodeHandler(outdir, this);
			spf.newSAXParser().parse(input, handler);
	  	} catch (Exception e) {
	  		throw new TransformerRunException(e.getMessage(), e);
	  	}
	  	System.out.println();
	  	System.out.println("Completion time: " + (new Date().getTime()-start.getTime())+ " ms");
	}
	
	public void sendMessage(Level level, String idstr, Object[] params) {
		if (params!=null && params.length>0) {
			super.sendMessage(level, new MessageFormat(i18n(idstr)).format(params));
		} else {
			super.sendMessage(level, i18n(idstr));
		}
	}
}
