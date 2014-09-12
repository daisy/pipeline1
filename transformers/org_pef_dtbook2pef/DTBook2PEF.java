package org_pef_dtbook2pef;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
			System.out.println("HERE");
			dotifyCliPath = System.getProperty("pipeline.dotify.cli.path");
		}

		if (dotifyCliPath == null || !new File(dotifyCliPath).exists()) {
			throw new TransformerRunException("Cannot locate Dotify executable at '" + dotifyCliPath + "'. Check your configuration. For more information, see the transformer documentation.");
		}

		String separator = System.getProperty("file.separator");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		ArrayList<String> command = new ArrayList<String>();
		command.add(path);
		command.add("-jar");

		try {
			command.add(new File(getClass().getResource("dotify-dist/dotify-cli.jar").toURI()).getAbsolutePath());
		} catch (URISyntaxException e1) {
			throw new TransformerRunException("", e1);
		}

		command.add(input.getAbsolutePath());
		command.add(output.getAbsolutePath());
		command.add(setup);
		command.add(locale.toString());
		for (String arg : parameters.keySet()) {
			if (parameters.get(arg) != null) {
				command.add("-" + arg + "=" + parameters.get(arg));
			}
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
		} catch (IOException e) {
			throw new TransformerRunException("Failed to start process.", e);
		} catch (InterruptedException e) {
			throw new TransformerRunException("Could not wait for process.", e);
		}

		progress(1);
		return true;
	}

	public static boolean isSupported() {
		double v = getVersion();
		// System.out.println("Version: " + v);
		return v >= 1.6;
	}

	static double getVersion() {
		String[] version = System.getProperty("java.version").split("\\.");
		return Double.parseDouble(version[0] + (version.length >= 2 ? "." + version[1] : ""));
	}

	public static void main(String[] args) {
		isSupported();
	}

}