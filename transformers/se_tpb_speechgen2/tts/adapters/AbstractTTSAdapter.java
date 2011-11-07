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
package se_tpb_speechgen2.tts.adapters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.w3c.dom.Document;

import se_tpb_speechgen2.audio.AudioFiles;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.concurrent.TTSAdapter;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * An abstract TTS adapter using {@link TTSUtils} to convert the input lines to
 * speakable strings.
 * <p>
 * This adapter is intended to be subclassed to create ne TTS adapters
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public abstract class AbstractTTSAdapter implements TTSAdapter {
	/** The reference to the TTSUtils passed to the constructor */
	protected TTSUtils mUtils;
	/** The reference to the parameters passed to the constructor */
	protected Map<String, String> mParams;
	protected TransformerDelegateListener mTransformer;

	/**
	 * Creates the adapter and stores the given utils and parameters.
	 * 
	 * @param ttsUtils
	 *            TTS utils
	 * @param params
	 *            the parameters for this TTS adapater
	 * @throws IllegalArgumentException
	 *             if one of the argument is <code>null</code>.
	 */
	public AbstractTTSAdapter(TTSUtils ttsUtils, Map<String, String> params) {
		if (ttsUtils == null) {
			throw new IllegalArgumentException(
					"No TTSUtils supplied! Unable to continue");
		}
		if (params == null) {
			throw new IllegalArgumentException(
					"No parameters supplied! Unable to continue");
		}
		this.mUtils = ttsUtils;
		this.mParams = params;
	}

	/**
	 * Closes the TTSInstance, flushes and closes streams, releases resources
	 * and so on. This default implementation does nothing.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException, TTSException {
		// default implementation does nothing
	}

	/**
	 * Reads announcements. This implementation converts the announcements to a
	 * speakable string using the {@link TTSUtils} methods then delegates to
	 * {@link #read(String, File)}.
	 * 
	 * @param announcements
	 *            a list containing start elements with announcements.
	 * @param attrName
	 *            attribute which holds the announcement.
	 * @param destination
	 *            destination/file for produced audio.
	 * @return the duration of the generated audio.
	 * @throws IOException
	 * @throws TTSException
	 */
	public long read(List<StartElement> announcements, QName attrName,
			File destination) throws IOException, TTSException {
		try {
			String line = TTSUtils.concatAttributes(announcements, attrName);
			line = mUtils.str2input(line);
			if (canSpeak(line)) {
				read(line, destination);
				return AudioFiles.getAudioFileDuration(destination);
			}
			return 0;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new TTSException(e.getMessage());
		}
	}

	/**
	 * Reads a syncpoint. This implementation converts the syncpoint to a
	 * speakable string using the {@link TTSUtils} methods then delegates to
	 * {@link #read(String, File)}.
	 * 
	 * @param doc
	 *            the document/xml data to read.
	 * @param destination
	 *            the local file in which to store the generated audio.
	 * @return The duration of the generated audio.
	 * @throws IOException
	 * @throws TTSException
	 */
	public long read(Document doc, File destination) throws IOException,
			TTSException {
		try {
			String line = mUtils.dom2input(doc);
			if (canSpeak(line)) {
				read(line, destination);
				return AudioFiles.getAudioFileDuration(destination);
			}
			return 0;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new TTSException(e.getMessage(),e);
		}
	}

	/**
	 * Read the given speakable string.
	 * 
	 * @param line
	 *            the line to read.
	 * @param destination
	 *            the local file in which to store the generated audio.
	 * @throws TTSException, IOException
	 */
	public abstract void read(String line, File destination)
			throws IOException, TTSException;

	/**
	 * Whether the given line is speakable.
	 * 
	 * @param line
	 *            a line to be read.
	 * @return <code>true</code> iff <code>line</code> is speakable by the
	 *         TTS adapter.
	 */
	protected boolean canSpeak(String line) {
		if (line == null) {
			return false;
		}
		boolean canSpeak = false;
		int i = 0;
		while (!canSpeak && i < line.length()) {
			int cp = Character.codePointAt(line, i++);
			canSpeak = !(Character.isSpaceChar(cp) || Character
					.isWhitespace(cp));
		}
		return canSpeak;
	}

	public void setTransformerDelegateListener(TransformerDelegateListener tdl) {
		this.mTransformer = tdl;
	}

	/**
	 * This default implementation does nothing
	 */
	public void init() {}

}
