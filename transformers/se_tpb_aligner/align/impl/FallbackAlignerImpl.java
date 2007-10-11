package se_tpb_aligner.align.impl;

import java.io.IOException;

import org.daisy.util.file.FileUtils;

import se_tpb_aligner.align.Aligner;
import se_tpb_aligner.align.AlignerException;
import se_tpb_aligner.util.AudioSource;
import se_tpb_aligner.util.XMLResult;
import se_tpb_aligner.util.XMLSource;

/**
 * A fallback aligner that only knows how to sync an entire fragment with an entire audiofile.
 * @author Markus Gylling
 */
public class FallbackAlignerImpl extends Aligner {

	@Override
	public XMLResult process(XMLSource inputXML, AudioSource inputAudioFile, String inputLanguage, XMLResult result) throws AlignerException {
		
		//TODO get the audiofile length
		//TODO add smil data to non-ignore elems
		try {
			FileUtils.copyFile(inputXML, result);
		} catch (IOException e) {
			throw new AlignerException(e.getMessage(),e);
		}
		return result;		
				
	}

	
	@Override
	public boolean supportsLanguage(String language) {			
		return false;		
	}

}
