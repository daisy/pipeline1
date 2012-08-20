package org_pef_dtbook2pef;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.system.InternalTaskException;
import org.daisy.dotify.system.TaskSystemFactory;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

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
		File input = new File(parameters.get("input"));
		File output = new File(parameters.get("output"));
		String setup = parameters.get("setup");
		FilterLocale locale = FilterLocale.parse(parameters.get("locale"));

		map = new HashMap<String, String>();
		map.putAll(parameters);
		org.daisy.dotify.Main dotify = new org.daisy.dotify.Main();
		try {
			dotify.run(input, output, setup, locale, map);
		} catch (InternalTaskException e) {
			throw new TransformerRunException("InternalTaskException", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException", e);
		}

		progress(1);
		return true;
	}

}