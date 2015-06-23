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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.daisy.util.file.StreamRedirector;

import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * Uses stdin-stdout piping to communicate with an external tts process.
 * 
 * This process writes text on stdout and reads the number of bytes, new line +
 * audio data on stdin. Typically used when distributing the tts system.
 * 
 * @author Martin Blomberg
 * 
 */
public class RemoteStreamTTS extends AbstractTTSAdapter {

	private String mLastText; // the text most recently being processed
	private String mCommand; // the command to use to start external tts
	// process
	private Process mProcess; // the abstraction of the external tts process

	private OutputStreamWriter mWriter; // writes data to the external tts
	// process
	private InputStream mInput; // reads data from the external tts process
	private long mTimeout = 30000; // default timeout in milliseconds
	private StreamRedirector mStreamRedirector; // a stream redirector, piping

	// the external stderr to this
	// stderr.

	/**
	 * Constructs a new instance given the parameters in <code>params</code>
	 * and the util functions <code>tu</code>. None of those may be
	 * <code>null</code>.
	 * 
	 * @param tu
	 *            the utility functions.
	 * @param params
	 *            the parameters provided by configuration.
	 * @throws IOException
	 */
	public RemoteStreamTTS(TTSUtils tu, Map<String, String> params)
			throws IOException {
		super(tu, params);

		if (null != mParams.get(TTSConstants.TIMEOUT)) {
			String s = mParams.get(TTSConstants.TIMEOUT);
			mTimeout = Long.parseLong(s);
		}

		mCommand = mParams.get(TTSConstants.COMMAND);
		Runtime rt = Runtime.getRuntime();
		mProcess = rt.exec(mCommand);

		mInput = mProcess.getInputStream();
		mWriter = new OutputStreamWriter(new BufferedOutputStream(mProcess
				.getOutputStream()), "utf-8");

		mStreamRedirector = new StreamRedirector(mProcess.getErrorStream(),
				System.err, true);
		mStreamRedirector.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se_tpb_speechgen2.tts.concurrent.TTSInstance#close()
	 */
	public void close() throws IOException, TTSException {

		if (null != mWriter) {
			mWriter.write(System.getProperty("line.separator"));
			mWriter.flush();

			mWriter.close();
			mWriter = null;
			mInput.close();
			mInput = null;
		}

		int exitVal = -1;
		long pollInterval = 1000;
		long startTime = System.currentTimeMillis();

		do {
			try {
				exitVal = mProcess.exitValue();
			} catch (IllegalThreadStateException ignored) {

				try {
					Thread.sleep(pollInterval);
				} catch (InterruptedException ignoredAgain) {
					/* nothing here, we are just trying to shutdown. */
				}
			}
		} while (exitVal < 0
				&& (mTimeout > (System.currentTimeMillis() - startTime)));

		if (exitVal < 0) {
			mProcess.destroy();
		}

		if (exitVal > 0) {
			String msg = "Filibuster error occurred: At least "
					+ exitVal
					+ " skipped phrase"
					+ (exitVal > 1 ? "s" : "")
					+ ", please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		} else if (exitVal < 0) {
			String msg = "Filibuster returned error: "
					+ exitVal
					+ ", "
					+ "please refer to the filibuster error logs for more information.";
			throw new TTSException(msg);
		}
	}

	/**
	 * Sends one line of text to the (remote) TTS using the piped reader/input
	 * stream.
	 * 
	 * @param line
	 *            the text to be sent to the TTS.
	 * @throws IOException
	 */
	private void send(String line) throws IOException {
		mLastText = line;
		mWriter.write(line);
		mWriter.write("\n");
		mWriter.flush();
	}

	/**
	 * Reads the audio data on the following form:
	 * 
	 * First, there is ascii characters telling how many audio bytes will be
	 * delivered. That "size string" is ended with a new line character.
	 * 
	 * Next, there comes the audio data, no new lines or anything strange. The
	 * audio data must include a RIFF header (ie. not raw PCM).
	 * 
	 * @return a byte[] containing the contents of the file.
	 * @throws IOException
	 * @throws IOException
	 */
	private byte[] receive() throws TTSException, IOException {
		// read the first line, it contains the number of audio bytes
		String size = "";
		int c;

		String timeOutMessage = "Wait for the TTS data timed out, was wating for "
				+ "audio data for >>"
				+ mLastText
				+ "<<, timeout was "
				+ mTimeout + " ms";

		try {
			TTSUtils.awaitIO(mInput, mTimeout);
		} catch (IOException e) {
			throw new TTSException(timeOutMessage, e);
		}

		while ((c = mInput.read()) != '\n') {
			if (-1 == c) {
				String msg = "Connection to the TTS was lost unexpectedly, "
						+ "read EOF (InputStream.read() = -1) instead of ascii "
						+ "telling the number of audio bytes produced.\n"
						+ "Waited for speech >>" + mLastText + "<<";
				throw new TTSException(msg);
			}
			size += (char) c;
			try {
				TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
		}

		// read the audio
		byte[] audio = new byte[Integer.parseInt(size)];
		int len = audio.length;
		do {
			try {
				TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
			len -= mInput.read(audio, audio.length - len, len);
		} while (len > 0);

		return audio;
	}

	@Override
	protected boolean canSpeak(String line) {
		/*
		 * During a meeting 2007-02-20 Kåre and Christina claimed that
		 * Filibuster will survive any input character without producing errors,
		 * short silence will be returned for unspeakable characters.
		 * 
		 * That means that no specific input check needs to be done here, just
		 * make sure the line isn't null or empty.
		 * 
		 * That amounts to:
		 */
		return super.canSpeak(line);
	}

//	private static String formatTime(long time) {
//		StringBuffer sb = new StringBuffer();
//
//		long ms = time % 1000;
//		time /= 1000;
//		long s = time % 60;
//		time /= 60;
//		long m = time % 60;
//		time /= 60;
//		long h = time;
//
//		// hours
//		sb.append(h);
//		sb.append('h');
//
//		// minutes
//		if (m < 10) {
//			sb.append('0');
//		}
//		sb.append(m);
//		sb.append('m');
//
//		// seconds
//		if (s < 10) {
//			sb.append('0');
//		}
//		sb.append(s);
//		sb.append('.');
//
//		// and the ms (as part of the sec part)..
//		if (ms < 10) {
//			sb.append('0');
//			sb.append('0');
//		} else if (ms < 100) {
//			sb.append('0');
//		}
//		sb.append(ms);
//		sb.append('s');
//
//		return sb.toString();
//	}

//	private static String formatByteSize(long bytes) {
//		StringBuffer sb = new StringBuffer();
//
//		long mbytes = bytes / (1024 * 1024);
//
//		if (mbytes < 1) {
//			sb.append(bytes);
//			sb.append(" b");
//		} else {
//			sb.append('~');
//			sb.append(mbytes);
//			sb.append(" m");
//		}
//
//		return sb.toString();
//	}

	@Override
	public void read(String line, File destination) throws IOException,
			TTSException {
		send(line);
		byte[] audio = receive();
		TTSUtils.writeAudio(audio, destination);
	}

}
