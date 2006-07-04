package se_tpb_speechgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.StreamRedirector;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Martin Blomberg
 *
 */
public final class SAPIImpl extends ExternalTTS {

	private PrintWriter pw;
	private BufferedReader reader;
	private String absoluteNativeLocation;
	private Process nativeSapi;
	private boolean DEBUG = false;
	private String sapiVoiceSelection = null;
	
	public SAPIImpl(Map params) throws IOException {
		super(params);
		String bin = (String) params.get(TTSBuilder.BINARY);
		if (null == bin) {
			String message = "Missing property " + TTSBuilder.BINARY +
			" for tts " + getClass().getName();
			throw new IllegalArgumentException(message);
		} else {
			setBinaryPath(new File(bin));
		}
		initialize();
	}	
	
	private void initialize() throws IOException {
		Runtime rt = Runtime.getRuntime();
		nativeSapi = rt.exec(absoluteNativeLocation);
		StreamRedirector sr = new StreamRedirector(nativeSapi.getErrorStream(), System.err);
		sr.start();
		
		if (reader != null && reader.ready()) {
			reader.close();
		}
		
		if (pw != null) {
			pw.close();
		}
		
		if (null != parameters.get("sapiVoiceSelection")) {
			sapiVoiceSelection = (String) parameters.get("sapiVoiceSelection");
		}
		
		pw = new PrintWriter(nativeSapi.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(nativeSapi.getInputStream()));
	}
	
	
	protected long sayImpl(Document doc, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException {		
		String content = "";
		try {
			content = xsltFilter(doc);
		} catch (CatalogExceptionNotRecoverable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XSLTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (content.length() == 0) {
			return 0;
		}
		
		return sayImpl(content, file);
	}

	
	protected long sayImpl(String line, File file) throws IOException, UnsupportedAudioFileException, TransformerRunException {
		if (line.length() == 0) {
			return 0;
		}
		
		line = regexFilter(line);
		line = yearFilter(line);
		line = replaceUChars(line);
		line = normalizeWhitespace(line);
		String noSelection = null;
		
		if (sapiVoiceSelection != null) {
			noSelection = line;
			line = "<voice optional=\"" + sapiVoiceSelection + "\">" + line + "</voice>";
		}
		
		long timeVal = 0;
		send(file.getAbsolutePath());
		send(line);
		timeVal = getAudioLength(file); 
		
		if (timeVal == 0 && noSelection != null) {
			// an error occured, 
			// try to speak without the sapi voice selection.
			initialize();
			send(file.getAbsolutePath());
			send(noSelection);
			timeVal = getAudioLength(file);
			
			// we were better off without the voice selection, 
			// don't use it next time.
			if (timeVal > 0) {
				System.err.println("Error using SAPI's voice selection, continuing with SAPI's default voice.");
				sapiVoiceSelection = null;
				parameters.remove("sapiVoiceSelection");
			}
		}
		
		if (timeVal == 0) {
			DEBUG("\"" + line + "\"");
			String message = "error speaking sentence: " + line + ",\n" +
				"error writing file " + file.getAbsolutePath();
			throw new TransformerRunException("An error occured using SAPI:\n" + message);
		}
		return timeVal;
	}
	
	
	/* reads one line of data from the SAPI-program just
	 * to get the two processes synchronized. That line
	 * must be the text string "OK", everything else is considered
	 * an error.
	 * Calculates and returns the duration of the generated
	 * speech (millis).
	 */
	private long getAudioLength(File file) throws IOException, UnsupportedAudioFileException {
		// just to get the two processes synchronized:
		String line = reader.readLine();
		if (line == null || !line.equals("OK")) {
			return 0;
		}
		
		AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = aff.getFormat();
		return (long)(1000.0 * aff.getFrameLength() / format.getFrameRate());
	}
	
	
	private void send(String str) throws IOException {
		if (null == absoluteNativeLocation) {
			throw new IllegalArgumentException("Path to TTS binary must be specified explicitly!");
		}
		
		pw.println(str);
	}


	public void close() throws IOException {
		if (null != pw) {
			pw.println("");
			pw.flush();
			pw.close();
			pw = null;
			reader.close();
			reader = null;
		}
		
		long pollInterval = 100;
		try {
			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException ignored) {
			}
			nativeSapi.exitValue();                
		} catch (IllegalThreadStateException e) {
			nativeSapi.destroy();
		}		
	}


	public void setBinaryPath(File pathToBinary) throws IOException {
		this.absoluteNativeLocation = pathToBinary.getAbsolutePath();
	}
	
	protected void DEBUG(String msg) {
		if (DEBUG) {
			System.err.println("SAPIImpl: " + msg);
		}
	}
}
