package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.BitstreamException;

/**
 * @author Markus Gylling
 */
public interface AudioFile extends FilesetFile {
	
	public void parse() throws FileNotFoundException, IOException, BitstreamException;
	
	public int getSampleFrequency();
	
	public boolean isMono();

}
