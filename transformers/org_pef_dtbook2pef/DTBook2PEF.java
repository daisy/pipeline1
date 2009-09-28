package org_pef_dtbook2pef;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileJuggler;
import org_pef_dtbook2pef.setups.sv_SE.SwedishBrailleSystem;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;

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
		systemSetups.put("sv-SE", new SwedishBrailleSystem(this)); 
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
		File outdir = new File(parameters.get("output"));
		String filename = parameters.get("filename");
		String setup = parameters.get("setup");
		String dateFormat = parameters.get("dateFormat");
		boolean keepTempFiles = "true".equals(parameters.get("keepTempFiles"));

		final File output = new File(outdir, filename);

		map = new HashMap<String, String>();
		map.putAll(parameters);

		try {
			//TODO: move to task?
			if (!map.containsKey("date")) {
			    Calendar c = Calendar.getInstance();
			    c.setTime(new Date());
			    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				map.put("date", sdf.format(c.getTime()));
			}
			if (!map.containsKey("identifier")) {
				String id = Double.toHexString(Math.random());
				id = id.substring(id.indexOf('.')+1);
				id = id.substring(0, id.indexOf('p'));
				map.put("identifier", "dummy-id-"+ id);
			}
			map.put("input-uri", new File(input).toURI().toString());

			HashMap<String, TaskSystem> systemSetups = compileSystemSetups(map);
			
			// Copy resource
			//FileUtils.writeInputStreamToFile(this.getTransformerDirectoryResource("./lib/pef2xhtml.xsl").openStream(), new File(outdir, "pef2xhtml.xsl"));
			
			// Run tasks
			FileJuggler fj = new FileJuggler(new File(input), output);
			ArrayList<InternalTask> tasks = systemSetups.get(setup).compile(map);
			if (tasks==null) {
				throw new TransformerRunException("Unable to load setup " + setup);
			}
			sendMessage("Setup \"" + setup + "\" loaded");
			double i = 0;
			for (InternalTask task : tasks) {
				sendMessage("Running " + task.getName());
				task.execute(fj.getInput(), fj.getOutput(), map);
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
