package org.daisy.pipeline.lite;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.exception.DMFCConfigurationException;

public class PipelineLiteCLI {

	private Options options;
	private String cmdName = "pipeline";
	private PipelineCore pipeline;
	private boolean showErrorDialog;
	private PipelineLiteGUI gui;

	public PipelineLiteCLI() {
		gui = new PipelineLiteGUI();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Fix for MacOSX 10.5: no context class loader was set
		// which results in factory loading problems in JAXP
		if (Thread.currentThread().getContextClassLoader() == null) {
			Thread.currentThread().setContextClassLoader(
					ClassLoader.getSystemClassLoader());
		}

		PipelineLiteCLI app = new PipelineLiteCLI();
		CommandLine line = app.parseArgs(args);
		if (line.getArgList().size() > 0) {
			app.printError("illegal argument -- " + args[0]);
			System.exit(1);
		}
		if (line.hasOption('h')) {
			app.printHelp();
			System.exit(0);
		}
		PipelineLiteGUI gui = app.getGUI();
		app.setShowErrorDialog(!line.hasOption('i'));
		PipelineCore pipeline;
		File scriptFile;
		if (line.hasOption('s')) {
			scriptFile = new File(line.getOptionValue('s'));
			if (!scriptFile.exists()) {
				app.printError("script file '" + scriptFile
						+ "' does not exist");
				System.exit(1);
			}
			// Load pipeline core finally
			pipeline = app.loadPipeline();
		} else {
			// Load pipeline core first
			pipeline = app.loadPipeline();
			scriptFile = gui.openScriptSelectionDialog(new File(pipeline
					.getHomeDirectory(), "scripts"));
			if (scriptFile == null) {
				System.exit(0);
			}
		}
		// Create script
		Script script = app.createScript(scriptFile);

		// If '-i': print script info and exit
		if (line.hasOption('i')) {
			script.printInfo(new PrintWriter(System.out));
			System.exit(0);
		}

		// Parse parameters and create job
		Map<String, String> parameters = new HashMap<String, String>();
		if (line.hasOption('p')) {
			for (String pdecl : line.getOptionValues('p')) {
				Pattern paramPattern = Pattern.compile("(\\w+)=(.+)");
				Matcher matcher = paramPattern.matcher(pdecl);
				if (matcher.matches()) {
					parameters.put(matcher.group(1), matcher.group(2));
				}
			}
		}
		Job job = app.createJob(script, parameters);

		// If not '-x': show configure dialog
		if (!line.hasOption('x') || !job.allRequiredParametersSet()) {
			if (gui.openJobConfigDialog(job) != PipelineLiteGUI.OK) {
				System.exit(0);
			}
		}

		// Execute job in progress dialog with a message manager
		boolean monitorSubtasks = !line.hasOption("no-subtask");
		MessageEvent.Type severity = null;
		if (line.hasOption("verbosity")) {
			try {
				severity = MessageEvent.Type.valueOf(line
						.getOptionValue("verbosity"));
			} catch (IllegalArgumentException iae) {
				// do nothing
			}
		}
		if (severity == null) {
			severity = MessageEvent.Type.INFO;
		}
		MessageManager messMan = new MessageManager();
		if (gui.openProgressDialogAndExecute(job, pipeline, messMan,
				monitorSubtasks, severity) != PipelineLiteGUI.OK) {
			System.exit(0);
		}

		// If not '-q': show result dialog
		if (!line.hasOption('q')) {
			gui.openResultDialog(messMan, severity);
		}
	}

	private void setShowErrorDialog(boolean b) {
		showErrorDialog = b;
	}

	private PipelineLiteGUI getGUI() {
		return gui;
	}

	private PipelineCore loadPipeline() {
		pipeline = null;
		MessageManager.getDefault();
		try {
			pipeline = new PipelineCore();
		} catch (DMFCConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return pipeline;
	}

	public Script createScript(File scriptFile) {
		if (scriptFile == null) {
			throw new IllegalArgumentException("Script file must not be null");
		}
		Script script = null;
		try {
			script = pipeline.newScript(scriptFile.toURI().toURL());
		} catch (ScriptValidationException e) {
			if (showErrorDialog) {
				gui.openErrorDialog(e);
			}
			e.printStackTrace();
		} catch (MalformedURLException e) {
			if (showErrorDialog) {
				gui.openErrorDialog(e);
			}
			e.printStackTrace();
		}
		return script;
	}

	public Job createJob(Script script, Map<String, String> parameters) {
		if (script == null) {
			throw new IllegalArgumentException("Script must not be null");
		}
		if (parameters == null) {
			throw new IllegalArgumentException("Parameters must not be null");
		}
		Job job = new Job(script);
		for (String name : parameters.keySet()) {
			String value = parameters.get(name);
			ScriptParameter param = job.getScriptParameter(name);
			if (param == null) {
				System.out.println("Ignoring unknown parameter '" + name + "'");
			}
			try {
				job.setParameterValue(name, value);
			} catch (DatatypeException e) {

				if (showErrorDialog) {
					gui.openErrorDialog(e);
				}
				e.printStackTrace();
			}
		}
		return job;
	}

	private CommandLine parseArgs(String[] args) {
		try {
			options = new Options();
			options.addOption("h", "help", false, "print this help message");
			options.addOption("i", "info", false,
					"display information on the script specified with '-s'");
			options
					.addOption("x", "execute", false,
							"execute the script directly if all required parameters are set");
			options.addOption("q", "quit", false,
					"quit after a successful execution (no result dialog)");

			OptionBuilder.withLongOpt("no-subtask");
			OptionBuilder
					.withDescription("don't display the subtask label in the progress dialog");
			options.addOption(OptionBuilder.create());

			OptionBuilder.withLongOpt("verbosity");
			OptionBuilder
					.withDescription("the severity above which messages are logged. Possible values are: DEBUG, INFO_FINER (default), INFO, WARNING, ERROR");
			OptionBuilder.hasArg();
			OptionBuilder.withArgName("threshold");
			options.addOption(OptionBuilder.create('v'));

			OptionBuilder.withLongOpt("script");
			OptionBuilder.withDescription("the script file to execute");
			OptionBuilder.hasArg();
			OptionBuilder.withArgName("file");
			options.addOption(OptionBuilder.create('s'));

			OptionBuilder.hasArgs();
			OptionBuilder.withValueSeparator(',');
			OptionBuilder.withArgName("param=value,...");
			OptionBuilder.withDescription("set script parameters");
			OptionBuilder.withLongOpt("params");
			options.addOption(OptionBuilder.create("p"));

			CommandLineParser parser = new PosixParser();
			return parser.parse(options, args, false);
		} catch (ParseException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			return null;
		}
	}

	private void printError(String message) {
		String str = cmdName + ": " + message + "\nTry `" + cmdName
				+ " --help` for more information";
		System.out.println(str);

	}

	private void printHelp() {
		PrintWriter out = new PrintWriter(System.out);
		String usage = cmdName
				+ " [options] [-s <file>] [-p <param=value,...>]";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(usage, "\nOptions:", options, null, false);
		out.println();
		out.println("Examples:");
		out.print(" 1. ");
		out.println("pipeline -i -s path/to/DTBAudioEncoder.taskScript");
		out.print(" 2. ");
		out
				.println("pipeline -x -q -s path/to/DTBAudioEncoder.taskScript -p input=path/to/manifest.opf output=path/to/dir bitrate=32");
		out.flush();
	}
}
