package org_pef_text.pef2text;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provides an interface for writing a volume of braille to a file.
 * @author Joel Håkansson, TPB
 */
public interface VolumeWriter extends EmbosserProperties {

	/**
	 * Writes the pages in this volume to a file
	 * @param pages the pages to write
	 * @param f the file to write to
	 * @return returns true if writing was successful, false otherwise
	 * @throws IOException
	 */
	public boolean write(List<? extends List<Byte>> pages, File f) throws IOException;

}
