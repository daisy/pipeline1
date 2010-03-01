package org_pef_dtbook2pef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org_pef_dtbook2pef.setups.TaskSystemFactory;
import org_pef_dtbook2pef.setups.TaskSystemFactoryException;
import org_pef_dtbook2pef.setups.TaskSystemFactory.OutputFormat;
import org_pef_dtbook2pef.setups.TaskSystemFactory.Setup;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.InternalTaskException;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;

/**
 * <p>The transformer loads a TaskSystem implementation and runs the steps
 * in it. The original intent was to convert DTBook into braille (PEF),
 * but other uses are possible.</p>
 * 
 * <p>Before running the setup, a few globally useful parameters,
 * such as todays date, are added.</p>
 * 
 * <p>The result from each step can be kept using the writeTempFiles
 * parameter in the tdf.</p>
 * 
 * <p>Additional conversions can be implemented by modifying the {@link TaskSystemFactory}</p>
 *   
 * @author Joel Håkansson, TPB
 * @version 2010-02-08
 * @since 2008-01-21
 * 
 */
public class DTBook2PEF extends Transformer {
	private HashMap<String, String> map;
	
	/**
	 * Default constructor.
	 * @param inListener
	 * @param isInteractive
	 */
	public DTBook2PEF(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	/**
	 * <p>This is the transformer entry point that 
	 * loads and runs the specified setup.</p>
	 * 
	 * <p>Before running the setup, a few globally useful parameters,
	 * such as todays date, are added.</p>
	 * 
	 * <p>The result from each step can be kept using the writeTempFiles
	 * parameter in the tdf.</p>
	 */
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
		String cols = parameters.get("cols");
		if (cols==null || "".equals(cols)) {
			parameters.remove("cols");
		}

		map = new HashMap<String, String>();
		map.putAll(parameters);
		map.put("systemRelease", "20100125");
		map.put("conversionDate", new Date().toString());
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

			// Load setup
			ArrayList<InternalTask> tasks = null;
			TaskSystem ts = null;
			try {
				ts = new TaskSystemFactory().newTaskSystem(outputformat, setup, map);
				tasks = ts.compile(map);
			} catch (TaskSystemException e) {
				throw new TransformerRunException("Unable to load '" + (ts!=null?ts.getName():"") + "' with parameters " + map.toString(), e);
			} catch (TaskSystemFactoryException e) {
				throw new TransformerRunException("Unable to retrieve a TaskSystem", e);
			}
			if (tasks==null) {
				// Should never happen
				throw new TransformerRunException("Unable to load \"" + (ts!=null?ts.getName():"") + "\"");
			}
			sendMessage("About to run TaskSystem \"" + (ts!=null?ts.getName():"") + "\"");

			// Run tasks
			double i = 0;
			FileJuggler fj = new FileJuggler(new File(input), output);
			for (InternalTask task : tasks) {
				sendMessage("Running " + task.getName());
				task.execute(fj.getInput(), fj.getOutput());
				if (writeTempFiles) {
					String it = ""+((int)i+1);
					while (it.length()<3) {
						it = "0" + it; 
					}
					FileUtils.copy(fj.getOutput(), new File(debug, "debug_dtbook2pef_" + it + "_" + task.getName().replaceAll("[\\s:]+", "_")));
				}
				fj.swap();
				i++;
				progress(i/tasks.size());
			}
			fj.close();
			
		} catch (IOException e) {
			throw new TransformerRunException("IO error: ", e.getCause());
		} catch (InternalTaskException e) {
			throw new TransformerRunException("Error in internal task", e);
		}
		progress(1);
		return true;
	}

}