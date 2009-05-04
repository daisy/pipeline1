package org_pef_dtbook2pef;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileJuggler;
import org_pef_dtbook2pef.filters.CaseFilter;
import org_pef_dtbook2pef.filters.CharFilter;
import org_pef_dtbook2pef.filters.InternalTask;
import org_pef_dtbook2pef.filters.RegexFilter;
import org_pef_dtbook2pef.filters.StringFilter;
import org_pef_dtbook2pef.filters.TextNodeTask;
import org_pef_dtbook2pef.filters.XslFoTask;
import org_pef_dtbook2pef.filters.XsltTask;

/**
 * <p>Transforms DTBook 2005-3 into PEF 2008-1</p>
 * 
 * <p>The script process should consist of these steps:</p>
 * <ol>
 * <li>Hyphenator</li>
 * <li>Braille code translator, with soft hyphens and space at breakpoints.</li>
 * <li>Layout definer. Defines the layout.</li>
 * <li>FOP. Performs the layout.</li>
 * <li>Renderer. Interprets the layout as PEF.</li>
 * <li>Braille finalizer. Post-rendering character-by-character braille injection (replacing e.g. spaces and hyphens)</li>
 * <li>Volume splitter</li>
 * <li>Validation</li>
 * <li>System clean up</li>
 * </ol>
 * <p>
 * This transformer handles steps 2-7.
 * </p>
 * 
 * @author Joel Håkansson
 * @version 2008-05-16
 * @since 2008-01-21
 * 
 */
//TODO: Externalize FOP, or include in path 
//TODO: Externalize regex-file (/text/regex.def)
public class DTBook2PEF extends Transformer {
	private HashMap<String, String> map;
	private final static HashMap<String, ArrayList<InternalTask>> system;
	
	/*
	 *  The conversion system is defined in the static code block below. It can contain 
	 *  any number of conversion setups.
	 *
	 *  Each setup consists of a series of tasks that, put together, performs conversion from 
	 *  DTBook to PEF. A setup is labeled by an identifier when inserted into the system.
	 *  The recommended practice is to use a language region (or sub region) as identifier.
	 *
	 *  New setups can be added to the conversion system by following the example below. 
	 *  Depending on setup needs, additional code may be required.
	 *
	 */
	static {
		system = new HashMap<String, ArrayList<InternalTask>>();
		// Setup for Swedish
			system.put("sv-SE", compileSetup_sv_SE());
		// Add setup here
			// ...
			// For example:
			// ArrayList<InternalTask> tmp = new ArrayList<InternalTask>();
			// tmp.add(new InternalTask());
			// system.put("xx-YY", tmp);
	}
	
	// Setup for Swedish
	private static ArrayList<InternalTask> compileSetup_sv_SE() {
		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		// Check input conformance 
		//tmp.add(new ConformaceTask());

		// Add braille markers based on text contents
		ArrayList<StringFilter> filters = new ArrayList<StringFilter>();
		// One or more digit followed by zero or more digits, commas or periods
		filters.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
		// Add upper case marker to the beginning of any upper case sequence
		filters.add(new RegexFilter("(\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
		// Add another upper case marker if the upper case sequence contains more than one character
		filters.add(new RegexFilter("(\\u2820\\p{Lu}\\u00ad*\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
		// Add to setup
		setup.add(new TextNodeTask("Braille markers injector", filters));
		
		filters = new ArrayList<StringFilter>();
		// Change case to lower case
		filters.add(new CaseFilter(CaseFilter.Mode.LOWER_CASE));
		// Transcode characters
		filters.add(new CharFilter("./sv_SE/text/default-table.xml"));
		// Add to setup
		setup.add(new TextNodeTask("Character replacer (Swedish)", filters));
		
		// Redefines dtbook as XSL-FO input
		setup.add(new XsltTask("DTBook to XSL-FO converter", "./sv_SE/definers/dtbook2xslfo.xsl", null));

		// Perform layout
		setup.add(new XslFoTask());
		
		// Transform into PEF precursor
		setup.add(new XsltTask("Area tree to PEF converter", "./common/renderers/areatree2pef.xsl", null));
		
		// Finalizes character data on rows
		setup.add(new XsltTask("Braille finalizer", "./common/renderers/braille-finalizer.xsl", null));
		
		// Split result into volumes 
		setup.add(new XsltTask("Volume splitter", "./common/splitters/simple-splitter.xsl", null));

		return setup;
	}

	/**
	 * Default constructor
	 * @param inListener
	 * @param isInteractive
	 */
	public DTBook2PEF(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {
		progress(0);
		// get parameters
		String input = parameters.remove("input");
		File outdir = new File(parameters.remove("output"));
		String filename = parameters.remove("filename");
		String setup = parameters.remove("setup");
		String dateFormat = parameters.remove("dateFormat");
		boolean keepTempFiles = "true".equals(parameters.remove("keepTempFiles"));

		final File output = new File(outdir, filename);

		map = new HashMap<String, String>();
		map.putAll(parameters);
		
		try {
			//TODO: move to task
			if (!map.containsKey("date")) {
			    Calendar c = Calendar.getInstance();
			    c.setTime(new Date());
			    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				map.put("date", sdf.format(c.getTime()));
			}
			if (!map.containsKey("language")) {				
				map.put("language", Locale.getDefault().getLanguage());
			}
			if (!map.containsKey("identifier")) {
				String id = Double.toHexString(Math.random());
				id = id.substring(id.indexOf('.')+1);
				id = id.substring(0, id.indexOf('p'));
				map.put("identifier", "dummy-id-"+ id);
			}
			
			// Copy resource
			//FileUtils.writeInputStreamToFile(this.getTransformerDirectoryResource("./lib/pef2xhtml.xsl").openStream(), new File(outdir, "pef2xhtml.xsl"));
			
			// Run tasks
			FileJuggler fj = new FileJuggler(new File(input), output);
			ArrayList<InternalTask> tasks = system.get(setup);
			if (tasks==null) {
				throw new TransformerRunException("Unable to load setup " + setup);
			}
			sendMessage("Setup \"" + setup + "\" loaded");
			for (InternalTask task : tasks) {
				sendMessage("Running " + task.getName());
				task.execute(fj.getInput(), fj.getOutput(), map);
				fj.swap();
			}
			fj.close();
			
		} catch (IOException e) {
			throw new TransformerRunException("IO error: ", e.getCause());
		} finally {
			if (!keepTempFiles) {

			}
		}
		progress(1);
		return true;
	}
}
