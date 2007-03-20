/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.fileset.interfaces.audio;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.xml.SmilClock;



import javazoom.jl.decoder.BitstreamException;

/**
 * @author Markus Gylling
 */
public interface AudioFile extends FilesetFile {
	
	public void parse() throws FileNotFoundException, IOException, BitstreamException;
	
	public int getSampleFrequency();
	
	public boolean isMono();
	
	/**
	 * @return the length (temporal duration) of this AudioFile encapsulated in a SmilClock object for convenience
	 */
	public SmilClock getLength();

}
