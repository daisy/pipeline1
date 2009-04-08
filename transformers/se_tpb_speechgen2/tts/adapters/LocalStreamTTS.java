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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.daisy.util.file.StreamRedirector;

import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * Uses simple stdin-stdout piping to communicate with an external (sapi, e.g)
 * process.
 * 
 * @author Martin Blomberg
 * 
 */
/**
 * @author martin
 * 
 */
public class LocalStreamTTS extends AbstractTTSAdapter {

	private String mLastText; // the text most recently being processed
	private String mCommand; // the command to use to start external tts
	// process
	private Process mProcess; // the abstraction of the external tts process

	private OutputStreamWriter mWriter; // writes data to the external tts
	// process
	private BufferedReader mReader; // reads data from the external tts process
	private boolean DEBUG = false;

	public LocalStreamTTS(TTSUtils tu, Map<String, String> params) throws IOException {
		super(tu, params);
		mCommand = mParams.get(TTSConstants.COMMAND);
		Runtime rt = Runtime.getRuntime();
		mProcess = rt.exec(mCommand);

		mReader = new BufferedReader(new InputStreamReader(mProcess
				.getInputStream()));
		mWriter = new OutputStreamWriter(new BufferedOutputStream(mProcess
				.getOutputStream()), "utf-8");

		new StreamRedirector(mProcess.getErrorStream(), System.err, true)
				.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se_tpb_speechgen2.tts.concurrent.TTSAdapter#close()
	 */
	public void close() throws IOException {
		mWriter.write("\n");
		mWriter.flush();
		mWriter.close();
		mReader.close();

		mProcess.destroy();

		/*
		 * mWriter.write(System.getProperty("line.separator")); mWriter.flush();
		 * mWriter.close(); mReader.close();
		 * 
		 * int exitVal = -1; long pollInterval = 1000; long startTime =
		 * System.currentTimeMillis();
		 * 
		 * do { try { exitVal = mProcess.exitValue(); } catch
		 * (IllegalThreadStateException ignored) {
		 * 
		 * try { Thread.sleep(pollInterval); } catch (InterruptedException
		 * ignoredAgain) { / * nothing here, we are just trying to shutdown. * / } } }
		 * while (exitVal < 0 && (mTimeout > (System.currentTimeMillis() -
		 * startTime)));
		 * 
		 * if (exitVal < 0) { mProcess.destroy(); }
		 * 
		 * if (exitVal != 0) { String msg = "Filibuster error occurred: At least " +
		 * exitVal + " skipped phrase" + (exitVal > 1? "s" : "") + ", please
		 * refer to the filibuster error logs for more information."; throw new
		 * TTSException(msg); } else if (exitVal < 0) { String msg = "Filibuster
		 * returned error: " + exitVal + ", " + "please refer to the filibuster
		 * error logs for more information."; throw new TTSException(msg); }
		 * 
		 */
	}

	/**
	 * Sends a job to the tts.
	 * 
	 * @param line
	 *            the text to synthesize.
	 * @param destination
	 *            the file in which to store the produced audio.
	 * @throws IOException
	 */
	private void send(String line, File destination) throws IOException {
		String sapiVoiceSelection = mParams.get("sapiVoiceSelection");
		if (sapiVoiceSelection != null) {
			line = "<voice optional=\"" + sapiVoiceSelection + "\">" + line
					+ "</voice>";
		}
		mLastText = line;
		mWriter.write(destination.getAbsolutePath());
		mWriter.write("\n");
		mWriter.write(line);
		mWriter.write("\n");
		mWriter.flush();
	}

	/**
	 * Waits for the external tts process to write the audio to file and send an
	 * "OK" back. This is just to get the two processes synchronized.
	 * 
	 * @throws IOException
	 * @throws TTSException
	 */
	private void receive() throws IOException, TTSException {
		String line = mReader.readLine();
		if (line == null || !line.equals("OK")) {
			String msg = "Connection to the TTS was lost unexpectedly, "
					+ "read " + line + " instead of OK.\n"
					+ "Waited for speech >>" + mLastText + "<<";
			throw new TTSException(msg);
		}
	}

	@SuppressWarnings("unused")
	private void DEBUG(String msg) {
		if (DEBUG) {
			String base = "DEBUG (" + getClass().getName() + "): ";
			System.err.println(base + msg);
		}
	}

	@Override
	public void read(String line, File destination) throws IOException,
			TTSException {
		send(line, destination);
		receive();

	}

}
