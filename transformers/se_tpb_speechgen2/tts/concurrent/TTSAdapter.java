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
package se_tpb_speechgen2.tts.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.w3c.dom.Document;

import se_tpb_speechgen2.tts.TTSException;

/**
 * The TTSRunner's view of the tts communication.
 * @author Martin Blomberg
 *
 */
public interface TTSAdapter {
	
	/**
	 * Reads announcements.
	 * 
	 * @param announcements a list containing start elements with announcements.
	 * @param attrName attribute which holds the announcement.
	 * @param destination destination/file for produced audio.
	 * @return the duration of the generated audio.
	 * @throws IOException
	 * @throws TTSException
	 */
	long read(List<StartElement> announcements, QName attrName, File destination) throws IOException, TTSException;
	
	
	/**
	 * Reads a syncpoint.
	 * @param doc the document/xml data to read.
	 * @param destination the local file in which to store the generated audio.
	 * @return The duration of the generated audio.
	 * @throws IOException
	 * @throws TTSException
	 */
	long read(Document doc, File destination) throws IOException, TTSException;
	
	
	/**
	 * Closes the TTSInstance, flushes and closes streams, releases resources and so on.
	 * @throws IOException
	 */
	void close() throws IOException, TTSException;
	
	/**
	 * Registers a transformer delegate listener to publish messages.
	 * @param tdl the transformer delegate listener
	 */
	void setTransformerDelegateListener(TransformerDelegateListener tdl);
	
	/**
	 * Intializes this TTS adapter 
	 */
	void init();
}
