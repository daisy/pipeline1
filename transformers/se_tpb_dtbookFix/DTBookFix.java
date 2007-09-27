package se_tpb_dtbookFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
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
 *  Limitations:
 *   - Cannot handle reversed structure: e.g. level3/level2/level1
 *   - Does not structure frontmatter
 *   
 *   
 * @author  Joel Hakansson, TPB
 * @version 17 sep 2007
 * @since 1.0
 */
public class DTBookFix extends Transformer {
	private static final float PROGRESS_INCS = 12;
	private int currentInc = 0;

	public DTBookFix(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		progress(0);
		String factory = (String)parameters.remove("factory");
		File input = new File((String)parameters.get("input"));
		File output = new File((String)parameters.get("output"));
		String clean = (String)parameters.get("clean");

		// tomma p i table läggs till om hx pga... pagenumfix?
		// ändra hx i tabell till p
		
		try {
			FileJuggler files = new FileJuggler(input, output);
			
			// Complete structure
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/levelnormalizer.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/levelsplitter.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level1.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level2.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level3.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
						
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level4.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
						
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level5.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level6.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();
			/*
			completeStructure(files.getInput(), files.getOutput());
			files.swap();
			next();*/
			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/remove-illegal-headings.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			files.swap();

			Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/list-fix.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
			next();
			
			if ("true".equals(clean)) {
				files.swap();
				Stylesheet.apply(files.getInput().getAbsolutePath(), getPath("./xslt/level-cleaner.xsl"), files.getOutput().getAbsolutePath(), factory, parameters, CatalogEntityResolver.getInstance());
				next();	
			}
			files.close();
			next();
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
	
	private String getPath(String path) {
		return new File(this.getTransformerDirectory(), path).getAbsolutePath();
	}

	private void next() {
		currentInc++;
		progress(currentInc/PROGRESS_INCS<=1?currentInc/PROGRESS_INCS:1);
	}

}
