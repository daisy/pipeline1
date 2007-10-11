package se_tpb_aligner.util;


/**
 * Hold an association between two sources and a result.
 * @author Markus Gylling
 */

public class Triple {
	private XMLSource mXMLSource = null;
	private AudioSource mAudioSource = null;
	private XMLResult mXMLResult = null;
	
	
	public Triple(XMLSource xml, AudioSource audio, XMLResult result) {
		mXMLSource = xml;
		mXMLResult = result;
		mAudioSource = audio;
	}
	
	public XMLSource getXMLSource() {
		return mXMLSource;
	}
	
	public XMLResult getXMLResult() {
		return mXMLResult;
	}
	
	public AudioSource getAudioSource() {
		return mAudioSource;
	}
}
