package se_tpb_aligner.textpre;

import se_tpb_aligner.util.Result;
import se_tpb_aligner.util.Source;


/**
 * Apply textual preprocessing on an XML document. 
 * @author Markus Gylling
 */
public abstract class PreProcessor {

	/**
	 * Apply text preprocessing.
	 * @param source The input XML document.
	 * @param language Language of the input XML document.
	 * @param result Where to store the result
	 * @return a processed XML document.
	 */
	public abstract Result process(Source source, String language, Result result) throws PreProcessorException;
	
	public abstract boolean supportsLanguage(String language);
	
}
