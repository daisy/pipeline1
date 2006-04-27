package org.daisy.util.fileset.interfaces.audio;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.daisy.util.fileset.interfaces.FilesetFile;



import javazoom.jl.decoder.BitstreamException;

/**
 * @author Markus Gylling
 */
public interface AudioFile extends FilesetFile {
	
	public void parse() throws FileNotFoundException, IOException, BitstreamException;
	
	public int getSampleFrequency();
	
	public boolean isMono();

}
