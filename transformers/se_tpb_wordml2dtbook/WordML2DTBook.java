package se_tpb_wordml2dtbook;

import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
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
	 * @param isInteractive
	 */
	public WordML2DTBook(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		// program parameters
		String factory = (String)parameters.remove("factory");
		String input = (String)parameters.remove("xml");
        String xslt = (String)parameters.remove("xslt");
        File outdir = new File((String)parameters.remove("out"));
        String filename = (String)parameters.remove("filename");
        String images = (String)parameters.remove("images");
        String overwrite = (String)parameters.remove("overwrite");
        
        // xslt parameters
        String uid = (String)parameters.get("uid");
        String stylesheet = (String)parameters.get("stylesheet");
        
        final File dtbook2xthml = new File(this.getTransformerDirectory(), "./lib/dtbook2xhtml.xsl");
        final File inputValidator = new File(this.getTransformerDirectory(), "./xslt/pre-input-validator.xsl");
        final File countCharsWml = new File(this.getTransformerDirectory(), "./xslt/post-count-characters-wml.xsl");
        final File countCharsDTBook = new File(this.getTransformerDirectory(), "./xslt/post-count-characters-dtbook.xsl");
        final File pagenumFix = new File(this.getTransformerDirectory(), "./xslt/post-pagenum-fix.xsl");
        final File defragmentSub = new File(this.getTransformerDirectory(), "./xslt/post-defragment-sub.xsl");
        final File defragmentSup = new File(this.getTransformerDirectory(), "./xslt/post-defragment-sup.xsl");
        final File defragmentEm = new File(this.getTransformerDirectory(), "./xslt/post-defragment-em.xsl");
        final File defragmentStrong = new File(this.getTransformerDirectory(), "./xslt/post-defragment-strong.xsl");
        final File addAuthorTitle = new File(this.getTransformerDirectory(), "./xslt/post-add-author-title.xsl");
        
//      validate input
		try {
			TempFile t0 = new TempFile();
			Stylesheet.apply(input, inputValidator.getAbsolutePath(), t0.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		} catch (Exception e) {
			sendMessage("Input is not a WordML file", MessageEvent.Type.ERROR);
			return false;
		}
		// validate custom and default mapsets		
		// new SimpleValidator();

		// 
        if (filename==null || filename.equals(""))
        	filename = new File(input).getName() + ".dtbook.xml";
        
        if (!outdir.exists()) outdir.mkdirs();
        else if (outdir.list(new FilenameFilter() {
        	public boolean accept(File f, String s) {
        		return !(new File(f, s).isDirectory());
        	}
        }).length>0) {
        	if ("true".equals(overwrite)) sendMessage("Directory is not empty. Files could be overwritten!", MessageEvent.Type.WARNING);
        	else {
        		sendMessage("Directory is not empty. Aborting process.", MessageEvent.Type.ERROR);
        		return false;
        	}
        }
        File result = new File(outdir, filename);
		if ("true".equals(images)) {
			decodeImages(new File(input), outdir);
			// put parameter for xslt
			parameters.put("forceJPEG", "false");
		}
		if (uid==null || "".equals(uid)) {
			String s = (new Long((Math.round(Math.random() * 1000000000)))).toString();
			char[] chars = s.toCharArray();
			char[] dest = new char[] {'0','0','0','0','0','0','0','0','0'};
			System.arraycopy(chars, 0, dest, 9-chars.length, chars.length);
			parameters.put("uid", "AUTO-UID-" + new String(dest));
		}
		progress(0.1);
		try {
			if ("dtbook2xhtml.xsl".equals(stylesheet)) {
				FileUtils.copy(dtbook2xthml, new File(outdir, "dtbook2xhtml.xsl"));
			}
			TempFile t1 = new TempFile();
			TempFile t2 = new TempFile();
			TempFile t3 = new TempFile();
			TempFile t4 = new TempFile();
			TempFile t5 = new TempFile();
			TempFile t6 = new TempFile();
			
			sendMessage("Tempfolder: " + t1.getFile().getParent(), MessageEvent.Type.DEBUG);
			
			progress(0.4);
			Stylesheet.apply(input, xslt, t1.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.5);
			
			// check character count
			TempFile tc1 = new TempFile();
			TempFile tc2 = new TempFile();
			Stylesheet.apply(input, countCharsWml.getAbsolutePath(), tc1.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			Stylesheet.apply(t1.getFile().getAbsolutePath(), countCharsDTBook.getAbsolutePath(), tc2.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			
			if (tc1.getFile().length()!=tc2.getFile().length()) {
				sendMessage("The text size has changed (" + tc1.getFile().length() + "/" + tc2.getFile().length() + "). Check the result for errors.", MessageEvent.Type.WARNING);
			} else {
				sendMessage("Text size ok.");
			}
			
			// Must match the order in wordml2dtbook.xsl
			Stylesheet.apply(t1.getFile().getAbsolutePath(), pagenumFix.getAbsolutePath(), t2.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.6);
			Stylesheet.apply(t2.getFile().getAbsolutePath(), defragmentSub.getAbsolutePath(), t3.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.7);
			Stylesheet.apply(t3.getFile().getAbsolutePath(), defragmentSup.getAbsolutePath(), t4.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.8);
			Stylesheet.apply(t4.getFile().getAbsolutePath(), defragmentEm.getAbsolutePath(), t5.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.9);
			Stylesheet.apply(t5.getFile().getAbsolutePath(), defragmentStrong.getAbsolutePath(), t6.getFile().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			progress(0.95);
			Stylesheet.apply(t6.getFile().getAbsolutePath(), addAuthorTitle.getAbsolutePath(), result.getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());

			
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
