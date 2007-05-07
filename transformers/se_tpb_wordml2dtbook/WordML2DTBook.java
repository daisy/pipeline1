package se_tpb_wordml2dtbook;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.xslt.Stylesheet;

/**
 * 
 * Transforms a Microsoft Office 2003 WordML document into DTBook.
 * 
 * Version 2007-april-11 
 * Changed a few constructs to reflect changes in the Pipeline core API.
 * 
 * @author  Joel Håkansson
 * @version 2007-april-11
 * @since 1.0
 */
public class WordML2DTBook extends Transformer implements MessageInterface {


	/**
	 * 
	 * @param inListener
	 * @param eventListeners
	 * @param isInteractive
	 */
	public WordML2DTBook(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		String factory = (String)parameters.remove("factory");
		String input = (String)parameters.remove("xml");
        String xslt = (String)parameters.remove("xslt");
        File outdir = new File((String)parameters.remove("out")); // ändra till katalog
        String filename = (String)parameters.remove("filename");
		
//      validate input
		try {
			TempFile t0 = new TempFile();
			Stylesheet.apply(input, new File(this.getTransformerDirectory(), "input-validator.xsl").getAbsolutePath(), t0.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		} catch (Exception e) {
			sendMessage("Input is not a WordML file", MessageEvent.Type.ERROR);
			return false;
		}
		// validate custom and default mapsets		
		

		// 
        if (filename==null || filename.equals(""))
        	filename = new File(input).getName() + ".dtbook.xml";
        String images = (String)parameters.remove("images");
        if (!outdir.exists()) outdir.mkdirs();
        File result = new File(outdir, filename);
		if (images.equals("true")) {
			decodeImages(new File(input), outdir);
			// put parameter for xslt
			parameters.put("forceJPEG", "false");
		}
		progress(0.1);
		try {
			//Stylesheet.apply(input, xslt, result.getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			// New code 
			///*
			TempFile t1 = new TempFile();
			TempFile t2 = new TempFile();
			TempFile t3 = new TempFile();
			TempFile t4 = new TempFile();
			TempFile t5 = new TempFile();
			TempFile t6 = new TempFile();
			
			sendMessage("Tempfolder: " + t1.getFile().getParent());
			
			//factory = "com.icl.saxon.TransformerFactoryImpl"; // temp!
			//xslt = new File(this.getTransformerDirectory(), "wordml2dtbook.xsl").getAbsolutePath(); // temp!
						
			Stylesheet.apply(input, xslt, t1.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.5);
			// Must match the order in wordml2dtbook.xsl
			Stylesheet.apply(t1.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "pagenum-fix.xsl").getAbsolutePath(), t2.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.6);
			Stylesheet.apply(t2.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "defragment-sub.xsl").getAbsolutePath(), t3.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.7);
			Stylesheet.apply(t3.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "defragment-sup.xsl").getAbsolutePath(), t4.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.8);
			Stylesheet.apply(t4.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "defragment-em.xsl").getAbsolutePath(), t5.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.9);
			Stylesheet.apply(t5.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "defragment-strong.xsl").getAbsolutePath(), t6.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.95);
			Stylesheet.apply(t6.getFile().getAbsolutePath(), new File(this.getTransformerDirectory(), "add-author-title.xsl").getAbsolutePath(), result.getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			
			//*/
			// New code
			
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
	  	sendMessage("Time to decode images: " + (new Date().getTime()-start.getTime())+ " ms");
	}

	public void sendMessage(MessageEvent.Type type, String idstr, Object[] params) {
		if (params!=null && params.length>0) {
			super.sendMessage(new MessageFormat(i18n(idstr)).format(params), type);
		} else {
			super.sendMessage(i18n(idstr), type);
		}
	}
}
