package org_pef_dtbook2pef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileJuggler;
import org_pef_dtbook2pef.setups.sv_SE.SwedishBrailleSystem;
import org_pef_dtbook2pef.setups.sv_SE.SwedishTextSystem;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;

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
//TODO: Externalize regex-file (/text/regex.def)
//TODO: Format detector (dtbook2pef -> xml2text)
public class DTBook2PEF extends Transformer {
	private HashMap<String, String> map;
	
	/**
	 *  System setups are defined here. Modification to other parts of this file should
	 *  not be needed.
	 *  
	 *  Each system setup consists of a series of tasks that, put together, performs conversion from 
	 *  DTBook to PEF. A system is labeled by an identifier when inserted into the HashMap.
	 *  The recommended practice is to use a language region (or sub region) as identifier.
	 *
	 *  New systems setups can be added to the conversion system by following the example below.
	 */
	private HashMap<String, TaskSystem> compileSystemSetups(Map<String, String> parameters) {
		HashMap<String, TaskSystem> systemSetups = new HashMap<String, TaskSystem>();

		// Setup for Swedish //
		systemSetups.put("sv-SE", new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_A4.xml"));
		systemSetups.put("sv-SE-FA44", new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_FA44.xml"));
		systemSetups.put("sv-SE-text", new SwedishTextSystem(getResource("setups/"), "sv_SE/config/text_A4.xml"));
		// Add more Braille systems here //
		
		return systemSetups;
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
		String input = parameters.get("input");
		File output = new File(parameters.get("output"));
		String setup = parameters.get("setup");
		String dateFormat = parameters.get("dateFormat");
		boolean keepTempFiles = "true".equals(parameters.get("keepTempFiles"));

		map = new HashMap<String, String>();
		map.putAll(parameters);

		// Add default values for optional parameters
		map.put("input-uri", new File(input).toURI().toString());
		if (map.get("date")==null || "".equals(map.get("date"))) {
		    Calendar c = Calendar.getInstance();
		    c.setTime(new Date());
		    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			map.put("date", sdf.format(c.getTime()));
		}
		if (map.get("identifier")==null || "".equals(map.get("identifier"))) {
			String id = Double.toHexString(Math.random());
			id = id.substring(id.indexOf('.')+1);
			id = id.substring(0, id.indexOf('p'));
			map.put("identifier", "dummy-id-"+ id);
		}
		try {
			// Load additional settings from file
			if (map.get("config")==null || "".equals(map.get("config"))) {
				map.remove("config");
			} else {
				File config = new File(parameters.get("config"));
				Properties p = new Properties();
				FileInputStream in = new FileInputStream(config);
				p.loadFromXML(in);
				for (Object key : p.keySet()) {
					map.put(key.toString(), p.get(key).toString());
				}
			}

			HashMap<String, TaskSystem> systemSetups = compileSystemSetups(map);

			// Copy resource
			//FileUtils.writeInputStreamToFile(this.getTransformerDirectoryResource("./lib/pef2xhtml.xsl").openStream(), new File(outdir, "pef2xhtml.xsl"));

			// Run tasks
			FileJuggler fj = new FileJuggler(new File(input), output);
			ArrayList<InternalTask> tasks = null;
			try {
				tasks = systemSetups.get(setup).compile(map);
			} catch (TaskSystemException e) {
				throw new TransformerRunException("Unable to load setup " + setup, e);
			}
			if (tasks==null) {
				throw new TransformerRunException("Unable to load setup " + setup);
			}
			sendMessage("Setup \"" + setup + "\" loaded");
			double i = 0;
			for (InternalTask task : tasks) {
				sendMessage("Running " + task.getName());
				task.execute(fj.getInput(), fj.getOutput());
				fj.swap();
				i++;
				progress(i/tasks.size());
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
	
	public URL getResource(String subPath) throws IllegalArgumentException {
		return getTransformerDirectoryResource(subPath);
	}

}
