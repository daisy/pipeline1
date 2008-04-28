package se_tpb_aligner.textpre.impl;

import java.io.IOException;

import org.daisy.util.file.FileUtils;

import se_tpb_aligner.textpre.PreProcessor;
import se_tpb_aligner.textpre.PreProcessorException;
import se_tpb_aligner.util.Result;
import se_tpb_aligner.util.Source;

/**
 * A fallback preprocessor that does not do much good.
 * @author Markus Gylling
 */
public class FallbackPreProcessorImpl extends PreProcessor {
		
	public FallbackPreProcessorImpl() {
		super();
	}
	
	@Override
	public Result process(Source source, @SuppressWarnings("unused")String language, Result result) throws PreProcessorException {		
		try {
			FileUtils.copyFile(source, result);
		} catch (IOException e) {
			throw new PreProcessorException(e.getMessage(),e);
		}
		return result;		
	}

	@Override
	public boolean supportsLanguage(@SuppressWarnings("unused")String language) {		
		return false;
	}

}
