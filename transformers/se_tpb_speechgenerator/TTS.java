package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.namespace.QName;

import org.daisy.dmfc.exception.TransformerRunException;
import org.w3c.dom.Document;

/* Interface for a narrator tts-implementation. */
/**
 * @author Martin Blomberg
 *
 */
public interface TTS {
	
	/**
	 * Makes an announcement, introduction, for each start element in the list
	 * to the file <code>outputFile</code>. 
	 * @param startElements List of <code>StartElement</code>s to announce prior to
	 * the content of the same elements.
	 * @param attributeQName the QName for the attribute containing the introduction text.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milliseconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	long introduceStruct(List startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * Makes an announcement, termination, for each start element in the list
	 * to the file <code>outputFile</code>. 
	 * @param startElements List of <code>StartElement</code>s to announce after 
	 * the content of the same elements.
	 * @param attributeQName the QName for the attribute containing the termination text.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milliseconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	long terminateStruct(List startElements, QName attributeQName, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * A DOM containing text to read.
	 * @param synchPoint A part of the manuscript identified as a point of synchronization.
	 * @param outputFile the audio file to generate.
	 * @return the time of the speach generated in milliseconds.
	 * @throws UnsupportedAudioFileException 
	 * @throws TransformerRunException 
	 */
	long say(Document synchPoint, File outputFile) throws IOException, UnsupportedAudioFileException, TransformerRunException;
	
	/**
	 * Closes the TTS.
	 * @throws IOException 
	 */
	 void close() throws IOException;
	
	/**
	 * Sets the absolute path to the binary a TTS might need.
	 * @throws IOException 
	 */
	//void setBinaryPath(File pathToBinary) throws IOException;
	
	/**
	 * Gives the TTS the Map <code>params</code> with parameters.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	void setParamMap(Map params) throws MalformedURLException, IOException;
}
