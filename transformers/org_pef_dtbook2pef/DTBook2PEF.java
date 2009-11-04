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
import org.daisy.util.file.FileUtils;

import org_pef_dtbook2pef.setups.sv_SE.SwedishBrailleSystem;
import org_pef_dtbook2pef.setups.sv_SE.SwedishTextSystem;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;

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
 * @author Joel Håkansson, TPB
 * @version 2008-05-16
 * @since 2008-01-21
 * 
 */

//TODO: Format detector (dtbook2pef -> xml2text)
public class DTBook2PEF extends Transformer {
	private HashMap<String, String> map;
	

	/*private HashMap<String, TaskSystem> compileSystemSetups(Format format, Map<String, String> parameters) {
		HashMap<String, TaskSystem> systemSetups = new HashMap<String, TaskSystem>();

		// Setup for Swedish //
		switch (format) {
			case PEF:
				systemSetups.put("sv-SE", new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_A4.xml"));
				systemSetups.put("sv-SE-FA44", new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_FA44.xml"));
				break;
			case TEXT:
				systemSetups.put("sv-SE", new SwedishTextSystem(getResource("setups/"), "sv_SE/config/text_A4.xml"));
				break;
		}
		// Add more Braille systems here //
		return systemSetups;
	}*/

	public enum OutputFormat {PEF, TEXT};
	public enum Setup {sv_SE, sv_SE_FA44};
	/**
	 *  System setups are defined here. Modification to other parts of this file should
	 *  not be needed.
	 *  
	 *  Each system setup consists of a series of tasks that, put together, performs conversion from 
	 *  XML to a {@link PagedMediaWriter}. A system is labeled by an identifier when inserted into the HashMap.
	 *  The recommended practice is to use a language region (or sub region) as identifier.
	 *
	 *  New system setups can be added to the conversion system by following the example below.
	 */
	private TaskSystem getSystemSetup(OutputFormat outputFormat, Setup setup, Map<String, String> parameter) {
		switch (outputFormat) {
			case PEF:
				switch (setup) {
					// Braille setups for Swedish //
					case sv_SE: 
						return new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_A4.xml");
					case sv_SE_FA44:
						return new SwedishBrailleSystem(getResource("setups/"), "sv_SE/config/default_FA44.xml");
					// Add more Braille systems here //
				}
				break;
			case TEXT:
				switch (setup) {
					// Text setup for Swedish //
					case sv_SE: 
						return new SwedishTextSystem(getResource("setups/"), "sv_SE/config/text_A4.xml");
					// Add more text systems here //
				}
				break;
		}
		return null;
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
		File debug = new File(parameters.get("tempFilesDirectory"));
		Setup setup = Setup.valueOf(parameters.get("setup").replace('-', '_'));
		OutputFormat outputformat = OutputFormat.valueOf(parameters.get("outputFormat").toUpperCase());
		String dateFormat = parameters.get("dateFormat");
		boolean writeTempFiles = "true".equals(parameters.get("writeTempFiles"));

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

			//HashMap<String, TaskSystem> systemSetups = compileSystemSetups(format, map);

			// Run tasks
			FileJuggler fj = new FileJuggler(new File(input), output);
			ArrayList<InternalTask> tasks = null;
			try {
				tasks = getSystemSetup(outputformat, setup, map).compile(map);
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
				if (writeTempFiles) {
					String it = ""+((int)i+1);
					while (it.length()<3) {
						it = "0" + it; 
					}
					FileUtils.copy(fj.getOutput(), new File(debug, "debug_dtbook2pef_" + it + "_" + task.getName().replaceAll("\\s+", "_")));
				}
				fj.swap();
				i++;
				progress(i/tasks.size());
			}
			fj.close();
			
		} catch (IOException e) {
			throw new TransformerRunException("IO error: ", e.getCause());
		}
		progress(1);
		return true;
	}
	
	public URL getResource(String subPath) throws IllegalArgumentException {
		return getTransformerDirectoryResource(subPath);
	}

}
