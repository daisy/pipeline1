package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.ValidatorFactory;
import org.daisy.util.fileset.validation.ValidatorListener;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * 
 * Fixes dtbook conversion errors/problems
 * 
 * Features:
 * 	- Adds levels where missing
 *  - Adds list items around lists in lists
 *  - Simplifies structure if possible 
 *  
 * Steps: 
 *  1. Removes levelx if it has descendant headings of x-1
 *  2. Splits a level into several levels on every additional heading on the same level
 *  3. Adds headings where needed
 *  4. Changes hx to p where parent isn't a levely
 *  
 *  5. Add li around lists that has a list as parent
 *   
 *  6. Moves structure upwards where possible
 *  
 *  (7. Tidy description todo)
 *  
 *  Limitations:
 *   - Cannot handle reversed structure: e.g. level3/level2/level1
 *   - Does not structure frontmatter
 *   - Validation not implemented yet!
 *   
 *  Note:
 *   - The current version of post-pagenum-fix.xsl contains one repairing operation
 *     that will never be used since the program will stop if the file is invalid at
 *     that point. If needed, it should be moved to the repairing section of dtbook-fix.
 *     
 *  Further development:
 *   - Tidy:
 *   	<levelx><h1>Heading</h1><p/></levelx><levelx><p>text</p>...
 *      to
 *      <levelx><h1>Heading</h1><p>text</p>...
 *   - Implement input and intermediate validation
 *   - Option to skip repairing and fail if input is invalid
 *   - Add documentation
 *   
 * @author  Joel Hakansson, TPB
 * @version 19 October 2007
 * @since 1.0
 */
public class DTBookFix extends Transformer implements ValidatorListener {
	private static final float PROGRESS_INCS = 14;
	private int currentInc = 0;
	private boolean mHadValidationErrors = false;

	public DTBookFix(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		String factory = (String)parameters.remove("factory");
		File input = new File((String)parameters.get("input"));
		File output = new File((String)parameters.get("output"));
		String forceRepair = (String)parameters.get("forceRepair");
		String tidy = (String)parameters.get("tidy");
		String indent = (String)parameters.get("indent");

		// tomma p i table läggs till om hx pga... pagenumfix?
		// ändra hx i tabell till p
		
		try {
			FileJuggler files = new FileJuggler(input, output);
		    if ("true".equals(forceRepair) || /*!*/ isValid(files.getInput())) {
				EventBus.getInstance().publish(new MessageEvent(this, "Reparing...", MessageEvent.Type.INFO));
				repair(files, factory, parameters);
			} else { advance(10); }
		    if (!isValid(files.getInput())) {
		    	EventBus.getInstance().publish(new MessageEvent(this, "Reparing failed!", MessageEvent.Type.ERROR));
		    	return false;
		    }
		    if ("true".equals(tidy)) {
				EventBus.getInstance().publish(new MessageEvent(this, "Tidying...", MessageEvent.Type.INFO));
				tidy(files, factory, parameters);
			} else { advance(3); }
			
		    if ("true".equals(indent)) {
				Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/tidy-indent.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
				files.swap();
		    }
			next();

			files.close();
			EventBus.getInstance().publish(new MessageEvent(this, "Done!", MessageEvent.Type.INFO));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (CatalogExceptionNotRecoverable e) {
			e.printStackTrace();
			return false;
		} catch (XSLTException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	private boolean isValid(File f) {
		ValidatorFactory vfac = ValidatorFactory.newInstance();		
		mHadValidationErrors = false;
		try{
			Validator validator = vfac.newValidator(FilesetType.DTBOOK_DOCUMENT);
			validator.setListener(this);
			
			/*
			 * If adding extra schemas beyond the canonical ones, do:
			 * URL url = CatalogEntityResolver.getInstance().resolveEntityToURL(catalogID);
			 * String type = "org.daisy.util.fileset.impl.Z3986DtbookFileImpl";
			 * validator.setSchema(url,type);
			 */

			validator.validate(f.toURI());
		}catch (ValidatorNotSupportedException  e) {
			// TODO: inform or throw
			e.printStackTrace();
		} catch (ValidatorException e) {
			// TODO: inform or throw
			e.printStackTrace();
		}	
		return !mHadValidationErrors;
	}
	
	private String getPath(String path) {
		return new File(this.getTransformerDirectory(), path).getAbsolutePath();
	}
	
	private void next() {
		currentInc++;
		progress(currentInc/PROGRESS_INCS<=1?currentInc/PROGRESS_INCS:1);
	}
	
	private void advance(int steps) {
		for (int i=0;i<steps;i++) {
			next();
		}
	}
	
	private void repair(FileJuggler files, String factory, Map parameters) throws CatalogExceptionNotRecoverable, XSLTException, FileNotFoundException {
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-levelnormalizer.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-levelsplitter.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level1.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level2.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level3.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
					
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level4.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
					
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level5.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-level6.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();

		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-remove-illegal-headings.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();

		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/repair-lists.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
	}
	
	private void tidy(FileJuggler files, String factory, Map parameters) throws FileNotFoundException, CatalogExceptionNotRecoverable, XSLTException {
		String simplifyHeadingLayout = (String)parameters.get("simplifyHeadingLayout");
		if ("true".equals(simplifyHeadingLayout)) {
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/tidy-level-cleaner.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			files.swap();
		}
		next();

		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/tidy-pagenum-fix.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();
		
		Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/tidy-add-author-title.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
		files.swap();
		next();

	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#exception(org.daisy.util.fileset.validation.Validator, java.lang.Exception)
	 */
	public void exception(Validator validator, Exception e) {
		// TODO: inform? See Transformer#sendMessage(FilesetFileException ffe)
		mHadValidationErrors = true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#report(org.daisy.util.fileset.validation.Validator, org.daisy.util.fileset.validation.message.ValidatorMessage)
	 */
	public void report(Validator validator, ValidatorMessage message) {
		if(!(message instanceof ValidatorWarningMessage)) {			
			mHadValidationErrors = true;
		}
		// TODO: inform? See int_daisy_validator.ValidatorDriver
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#inform(org.daisy.util.fileset.validation.Validator, java.lang.String)
	 */
	public void inform(Validator validator, String information) {
		// TODO: inform? See int_daisy_validator.ValidatorDriver
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorListener#progress(org.daisy.util.fileset.validation.Validator, double)
	 */
	public void progress(Validator validator, double progress) {
		// TODO Auto-generated method stub		
	}



}
