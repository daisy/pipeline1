package org_pef_dtbook2pef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 * <p>
 * The transformer runs Dotify with the supplied parameters.
 * </p>
 * 
 * @author Joel Håkansson
 * 
 */
public class DTBook2PEF extends Transformer {
	
	/**
	 * Default constructor.
	 * @param inListener
	 * @param isInteractive
	 */
	public DTBook2PEF(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters) throws TransformerRunException {
		progress(0);
		// get parameters
		File input = new File(parameters.remove("input"));
		File output = new File(parameters.remove("output"));
		String setup = parameters.remove("setup");
		String locale = parameters.remove("locale");
		String dotifyCliPath = parameters.remove("dotifyCliPath");

		if (dotifyCliPath == null || "".equals(dotifyCliPath)) {
			dotifyCliPath = System.getProperty("pipeline.dotify.cli.path");
		}

		File cliPath = new File(dotifyCliPath);
		if (dotifyCliPath == null || !cliPath.exists()) {
			throw new TransformerRunException("Cannot locate Dotify executable at '" + dotifyCliPath + "'. Check your configuration. For more information, see the transformer documentation.");
		}

		ArrayList<String> command = new ArrayList<String>();
		if (dotifyCliPath.endsWith(".jar")) {
			String separator = System.getProperty("file.separator");
			String path = System.getProperty("java.home") + separator + "bin" + separator + "java";

			command.add(path);
			command.add("-jar");
		}
		command.add(dotifyCliPath);

		List<String> version = getDotifyVersion(new File(cliPath.getParentFile(), "version"));

		if ("2".equals(version.get(0))) {
			command.add(input.getAbsolutePath());
			command.add(output.getAbsolutePath());
			command.add(setup);
			command.add(locale.toString());
			for (String arg : parameters.keySet()) {
				if (parameters.get(arg) != null) {
					command.add("-" + arg + "=" + parameters.get(arg));
				}
			}
		} else {
			throw new TransformerRunException("Unsupported major version: " + version.get(0));
		}

		sendMessage("About to run: " + command.toString());

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		try {
			Process process = processBuilder.start();
			// hook up child process output to parent
			InputStream lsOut = process.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(lsOut));
			// read the child process' output
			String line;
			try {
				while ((line = in.readLine()) != null) {
					sendMessage(line);
				}
			} catch (IOException e) {
				sendMessage("Failed to read from console." + e.getMessage(), MessageEvent.Type.ERROR);
			}
			try {
				in.close();
			} catch (IOException e) {
			}
			process.waitFor();
			if (process.exitValue()!=0) {
				throw new TransformerRunException("Dotify returned a non-zero exit value: " + process.exitValue());
			}
		} catch (IOException e) {
			throw new TransformerRunException("Failed to start process.", e);
		} catch (InterruptedException e) {
			throw new TransformerRunException("Could not wait for process.", e);
		}

		progress(1);
		return true;
	}

	public static boolean isSupported() {
		double v = getJavaVersion();
		// System.out.println("Version: " + v);
		return v >= 1.6;
	}

	static double getJavaVersion() {
		String[] version = System.getProperty("java.version").split("\\.");
		return Double.parseDouble(version[0] + (version.length >= 2 ? "." + version[1] : ""));
	}

	public static void main(String[] args) {
		isSupported();
	}

	static List<String> getDotifyVersion(File v) {
		List<String> segments = new ArrayList<String>();
		if (v.exists()) {
			FileInputStream is = null;
			try {
				is = new FileInputStream(v);
				int i;
				StringBuilder sb = new StringBuilder();
				while ((i = is.read()) > -1) {
					if (i >= (int) '0' && i <= (int) '9') {
						sb.append((char) i);
					} else {
						if (sb.length() > 0) {
							segments.add(sb.toString());
						}
						sb = new StringBuilder();
						if (i != (int) '.') {
							break;
						}
					}
				}
				if (sb.length() > 0) {
					segments.add(sb.toString());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		// add defaults
		switch (segments.size()) {
			case 0:
				segments.add("2");
				//$FALL-THROUGH$
			case 1:
				segments.add("0");
				//$FALL-THROUGH$
			case 2:
				segments.add("0");
		}
		return segments;
	}

}