package se_tpb_aligner.align;

import se_tpb_aligner.util.AudioSource;
import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * Align textnodes of an XML document with time pointers in an audio file. 
 * Produce an XML document which is a copy of the input document, with SMIL data added.
 * @author Markus Gylling
 */

public abstract class Aligner {
	
	/**
	 * Constructor.
	 */
	public Aligner() {
		
	}
			
	/**
	 * Apply alignment.
	 * @param inputXML The input XML document.
	 * @param inputAudioFile The input audio file.
	 * @param inputLanguage language of the input data
	 * @param result Where to store the result.
	 * @return the XML document with SMIL attributes added on 1-n nodes.
	 */
	public abstract XMLResult process(XMLSource inputXML, AudioSource inputAudioFile, String inputLanguage, XMLResult result) throws AlignerException;
	
	/**
	 * Test whether this instance of Aligner supports a given language.
	 */
	public abstract boolean supportsLanguage(String language);
	
	
}
