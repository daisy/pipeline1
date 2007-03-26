package int_daisy_filesetAudioTagger.playlist;

import java.io.File;
import java.io.IOException;

import org.daisy.util.fileset.exception.FilesetFatalException;

/**
 * 
 * @author Markus Gylling
 */
public interface PlaylistWriter {

	/**
	 * Build the output as an internal string.
	 */
	public void initialize() throws  FilesetFatalException;
	
	/**
	 * Render the string representation to a file.
	 */
	public void render(File destination) throws IOException;
	

		
}
