package org_pef_pefFileMerger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

import org.daisy.braille.pef.PEFFileMerger.SortType;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

public class PEFFileMerger extends Transformer {

	public PEFFileMerger(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		progress(0);
		File input = new File(parameters.get("input"));
		File output = new File(parameters.get("output"));
		String identifier = parameters.get("identifier");
		SortType sort = SortType.valueOf(parameters.get("sortType").toUpperCase());
		
		org.daisy.braille.pef.PEFFileMerger fm = new org.daisy.braille.pef.PEFFileMerger();
		boolean ret;
		try {
			ret = fm.merge(input, new FileOutputStream(output), identifier, sort);
		    progress(1);
		    return ret;
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("Error creating output stream", e);
		}
	}

}
