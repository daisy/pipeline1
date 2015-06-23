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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.daisy.util.file.StreamRedirector;

import se_tpb_speechgen2.tts.TTSConstants;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.util.TTSUtils;

/**
 * Uses stdin-stdout piping to communicate with an 
 * external Filibuster TTS process.
 * 
 * This process writes text on stdout and reads the number 
 * of bytes, new line + audio data on stdin. Typically
 * used when distributing the tts system.
 * 
 * @author Martin Blomberg
 *
 */
public class FilibusterTTS extends AbstractTTSAdapter {

	private Map<String, String> mParams;	// configuration parameters defined by user
	
	private String mLastText;				// the text most recently being processed
	private String mCommand;				// the command to use to start external tts process
	private Process mProcess;				// the abstraction of the external tts process
	
	private OutputStreamWriter mWriter;		// writes data to the external tts process
	private InputStream mInput;				// reads data from the external tts process
	private long mTimeout = 30000;			// default timeout in milliseconds
	private StreamRedirector mStreamRedirector;	// a stream redirector, piping the external stderr to this stderr.
	
	private long mMaxIOWait = 0;			// records the longest wait for audio
	private String threadName;				// thread name printed to logs
	private long mTotalAudioSize = 0;		// records the total audio size received
	
	private static int slaveNr = 0;			// static counter to assign all threads a unique name 
	public static boolean DEBUG = true;		// whether or not to print debug messages
	
	private static SimpleDateFormat formatter = 	// date format used when printing messages to log file
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	/**
	 * Constructs a new instance given the parameters in <code>params</code>
	 * and the util functions <code>tu</code>. 
	 * None of those may be <code>null</code>.
	 * @param tu the utility functions.
	 * @param params the parameters provided by configuration.
	 * @throws IOException
	 */
	public FilibusterTTS(TTSUtils tu, Map<String, String> params) throws IOException {
		super(tu, params);
		threadName = "FilibusterTTS-" + FilibusterTTS.getNextId();
		
		if (tu == null) {
			String msg = "No TTSUtils supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
		
		if (params != null) {
			mParams = params;
			
			if (null != mParams.get(TTSConstants.TIMEOUT)) {
				String s = mParams.get(TTSConstants.TIMEOUT);
				mTimeout = Long.parseLong(s);
				DEBUG("timeout = " + mTimeout + "ms");
			}
			
			mCommand = mParams.get(TTSConstants.COMMAND);
			DEBUG("command = " + mCommand);
			
			Runtime rt = Runtime.getRuntime();
			mProcess = rt.exec(mCommand);
			
			mInput = mProcess.getInputStream();
			mWriter = new OutputStreamWriter(
					new BufferedOutputStream(mProcess.getOutputStream()), "utf-8");
			
			mStreamRedirector = new StreamRedirector(mProcess.getErrorStream(), System.err, true);
			mStreamRedirector.start();
			
		} else {
			String msg = "No parameters supplied! Unable to continue";
			throw new IllegalArgumentException(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSInstance#close()
	 */
	public void close() throws IOException, TTSException {
		
		if (null != mWriter) {
			DEBUG("Writes Java property 'line.separator' to exit Filibuster...");
			mWriter.write(System.getProperty("line.separator"));
			mWriter.flush();
			DEBUG("Done!");
			
			mWriter.close();
			mWriter = null;
			mInput.close();
			mInput = null;
		}
		
		int exitVal = -1;
		long pollInterval = 1000;
		long startTime = System.currentTimeMillis();
		long closingTime = 0;
		
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
		} while (exitVal < 0 && (mTimeout > (System.currentTimeMillis() - startTime)));
		
		if (exitVal < 0) {
			mProcess.destroy();
		}
		closingTime = System.currentTimeMillis() - startTime;
		DEBUG("Terminates, exitVal: " + exitVal + ", closing time: " + closingTime + "ms");
		DEBUG("Max wait for IO: " + formatTime(mMaxIOWait));
		DEBUG("Total Audio Size: " + formatByteSize(mTotalAudioSize));
		DEBUG("Number of skipped phrases: " + exitVal);
		
		if (exitVal > 0) {
			String msg = "Filibuster error occurred: At least " + exitVal + " skipped phrase" + (exitVal > 1? "s" : "") 
			+ ", please refer to the filibuster error logs for more information. " +
					"Process started using command: " + mCommand;
			throw new TTSException(msg);
		} else if (exitVal < 0) {
			String msg = "Filibuster returned error: " + exitVal + ", " +
					"please refer to the filibuster error logs for more information. " +
					"Process started using command: " + mCommand;
			throw new TTSException(msg);
		}
	}

	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter#read(java.lang.String, java.io.File)
	 */
	@Override
	public void read(String line, File destination) throws TTSException {
		try {
			send(line);
			byte[] audio = receive();
			TTSUtils.writeAudio(audio, destination);
		} catch (IOException e) {
			throw new TTSException(e.getMessage(), e);
		}
	}
	
	/**
	 * Sends one line of text to the (remote) TTS using the piped reader/input stream.
	 * @param line the text to be sent to the TTS.
	 * @throws IOException
	 */
	private void send(String line) throws IOException {		
		mLastText = line;
		DEBUG("Writes the line >>" + mLastText + "<<");
		mWriter.write(line);
		mWriter.write("\n");
		mWriter.flush();
		DEBUG("Finished writing text.");
	}
	
	/**
	 * Reads the audio data on the following form:
	 * 
	 * First, there is ascii characters telling how many audio bytes 
	 * will be delivered. That "size string" is ended with a new line 
	 * character.
	 * 
	 * Next, there comes the audio data, no new lines or anything
	 * strange. The audio data must include a RIFF header (ie. not raw PCM).
	 * 
	 * @return a byte[] containing the contents of the file.
	 * @throws IOException 
	 * @throws IOException
	 */
	private byte[] receive() throws TTSException, IOException {	
		DEBUG("begin receive audio data");
		// read the first line, it contains the number of audio bytes
		String size = "";
		int c;
		
		long wait = 0;
		String timeOutMessage = "Wait for the TTS data timed out, was wating for " +
				"audio data for >>" + mLastText + "<<, timeout was " + mTimeout + " ms, " +
						"this process was started using command " + mCommand;
		
		try {
			wait = TTSUtils.awaitIO(mInput, mTimeout);		
		} catch (IOException e) {
			throw new TTSException(timeOutMessage, e);
		}
		mMaxIOWait = Math.max(mMaxIOWait, wait);
		
		while ((c = mInput.read()) != '\n') {
			if (-1 == c) {
				String msg = "Connection to the TTS was lost unexpectedly, " +
						"read EOF (InputStream.read() = -1) instead of ascii " +
						"telling the number of audio bytes produced.\n" +
						"Waited for speech >>" + mLastText + "<<, " +
								"this process was started using command " + mCommand;
				throw new TTSException(msg);
			}
			size += (char) c;
			try {
				wait = TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
			mMaxIOWait = Math.max(mMaxIOWait, wait);
		}
		
		// read the audio
		byte[] audio = new byte[Integer.parseInt(size)];
		int len = audio.length;
		mTotalAudioSize += audio.length;
		do {
			try {
				wait = TTSUtils.awaitIO(mInput, mTimeout);
			} catch (IOException e) {
				throw new TTSException(timeOutMessage, e);
			}
			mMaxIOWait = Math.max(mMaxIOWait, wait);
			len -= mInput.read(audio, audio.length - len, len);	
		} while (len > 0);
		
		DEBUG("finished receiving audio data");
		return audio;
	}
	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.adapters.AbstractTTSAdapter#canSpeak(java.lang.String)
	 */
	@Override
	protected boolean canSpeak(String line) {
		if (null == line) {
			return false;
		}
		
		if (line.trim().length() == 0) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns a human readable string representing the time portion
	 * {@code time}.
	 * @param time some amount of milliseconds
	 * @return a human readable string representing the {@code time}
	 */
	private static String formatTime(long time) {
		StringBuffer sb = new StringBuffer();
		
		long ms = time % 1000;
		time /= 1000;
		long s = time % 60;
		time /= 60;
		long m = time % 60;
		time /= 60;
		long h = time;
		
		// hours
		sb.append(h);
		sb.append('h');
		
		// minutes
		if (m < 10) {
			sb.append('0');
		}
		sb.append(m);
		sb.append('m');
		
		// seconds
		if (s < 10) {
			sb.append('0');
		}
		sb.append(s);
		sb.append('.');
		
		// and the ms (as part of the sec part).. 
		if (ms < 10) {
			sb.append('0');
			sb.append('0');
		} else if (ms < 100) {
			sb.append('0');
		}
		sb.append(ms);
		sb.append('s');
		
		return sb.toString();
	}
	
	/**
	 * Returns a human readable string representing a byte size.
	 * @param bytes the number of bytes.
	 * @return a human readable string representing a {@code bytes}
	 */
	private static String formatByteSize(long bytes) {
		StringBuffer sb = new StringBuffer();
		
		long mbytes = bytes / (1024 * 1024);
		
		if (mbytes < 1) {
			sb.append(bytes);
			sb.append(" b");
		} else {
			sb.append('~');
			sb.append(mbytes);
			sb.append(" m");
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the next number in sequence, used for thread numbering/naming.
	 * @return the next number in sequence
	 */
	private static synchronized int getNextId() {
		return slaveNr++;
	}
	
	/**
	 * Prints debug messages to the standard error stream if the boolean variable 
	 * {@code DEBUG} is {@code true}. The message is prefixed with current time 
	 * and thread id.
	 * @param msg the message
	 */
	private void DEBUG(String msg) {
		if (DEBUG) {
			String base = "DEBUG [" + formatter.format(new Date()) + ":" + threadName + "] " + getClass().getName() + ": ";
			System.err.println(base + msg);
		}
	}

}
