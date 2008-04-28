/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package se_tpb_speechgen2.tts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.w3c.dom.Document;

/**
 * This is what SpeechGen2 knows about a TTS. 
 * 
 * The TTS has two different states, between which only one transition 
 * exists. The states are "loading" and "fetching", with respect to 
 * sync points/audio.
 * 
 * The methods addSyncPoint(args) and addAnnouncements(args) can be called 
 * several times until the method getNext() is called for the first time.
 * getNext() initializes the actual speech generation and after that no more
 * jobs are allowed to be added the the TTS.
 * 
 * (This is done to give the (possibly several) underlying worker threads
 * the chance to know when they are done)
 * 
 * TODO:
 * Instead, this could be done using some kind of poison:
 * 	When the client (the transformer class) is ready to fetch the audio it
 * 	could add a "poison synchpoint" to the queue. That poison is duplicated
 * 	so that all tts-threads gets one, and then they die.
 * 
 * @author Martin Blomberg
 *
 */
public interface TTS {

	/**
	 * Adds a synch point to the queue of synch points to generate.
	 * @param scope the xml scope.
	 * @param outputFile the destination of the generated audio.
	 */
	public void addSyncPoint(Document scope, File outputFile);
	
	/**
	 * Adds a list of introductions to the queue of synch points to generate.
	 * @param introductions a list containing start element with announcement attribute.
	 * @param attrName the qname indicating which attribute to use for announcements.
	 * @param outputFile the destination of the generated audio.
	 */
	public void addAnnouncements(List<StartElement> introductions, QName attrName, File outputFile);
	
	/**
	 * Initialized and starts all tts slaves.
	 */
	public void start();
	
	/**
	 * If the tts is not initialized and started, calling getNext() will do that.
	 * Returns the next TTSOutput. Output is delivered using the same order
	 * as input was added.
	 * @return the next TTSOutput.
	 */
	public TTSOutput getNext();
	
	/**
	 * Returns <code>true</code> if there are more output to be delivered, 
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if there are more output to be delivered, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasNext();
	
	/**
	 * Closes the TTS.
	 * @throws IOException
	 * @throws TTSException
	 */
	public void close() throws IOException, TTSException;
	
}
