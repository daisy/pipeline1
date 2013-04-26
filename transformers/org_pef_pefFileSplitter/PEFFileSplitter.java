package org_pef_pefFileSplitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

public class PEFFileSplitter extends Transformer {

	public PEFFileSplitter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		progress(0);
		File input = new File(parameters.get("input"));
		File directory = new File(parameters.get("output"));
		String prefix = parameters.get("prefix");
		String postfix = parameters.get("postfix");
		String inputName = input.getName();
		String inputExt = ".pef";
		
		int index = inputName.lastIndexOf('.');
		if (index >= 0) {
			if (index < inputName.length()) {
				inputExt = inputName.substring(index);
			}
			inputName = inputName.substring(0, index);
			
		}
		if (prefix==null || "".equals(prefix)) {
			prefix = inputName + "-";
		}
		if (postfix==null || "".equals(postfix)) {
			postfix = inputExt;
		}
		
		org.daisy.braille.pef.PEFFileSplitter ps = new org.daisy.braille.pef.PEFFileSplitter();
		try {
			ps.split(new FileInputStream(input), directory, prefix, postfix);
	        progress(1);
	        return true;
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("Failed to create input stream.", e);
		}

	}
}
