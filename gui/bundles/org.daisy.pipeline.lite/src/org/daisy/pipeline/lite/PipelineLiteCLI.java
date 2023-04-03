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
	private String cmdName = "pipeline"; //$NON-NLS-1$
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
			Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
		}

		PipelineLiteCLI app = new PipelineLiteCLI();
		CommandLine line = app.parseArgs(args);
		if (line.getArgList().size() > 0) {
			app.printError(Messages.getString("cli.error.illegalArgument") + args[0]); //$NON-NLS-1$
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
				app.printError(Messages.getString("cli.error.scriptNotFound", scriptFile)); //$NON-NLS-1$
				System.exit(1);
			}
			// Load pipeline core finally
			pipeline = app.loadPipeline();
		} else {
			// Load pipeline core first
			pipeline = app.loadPipeline();
			scriptFile = gui.openScriptSelectionDialog(new File(pipeline.getHomeDirectory(), "scripts")); //$NON-NLS-1$
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
				Pattern paramPattern = Pattern.compile("(\\w+)=(.+)"); //$NON-NLS-1$
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
		boolean monitorSubtasks = !line.hasOption("no-subtask"); //$NON-NLS-1$
		MessageEvent.Type severity = null;
		if (line.hasOption("verbosity")) { //$NON-NLS-1$
			try {
				severity = MessageEvent.Type.valueOf(line.getOptionValue("verbosity")); //$NON-NLS-1$
			} catch (IllegalArgumentException iae) {
				// do nothing
			}
		}
		if (severity == null) {
			severity = MessageEvent.Type.INFO;
		}
		MessageManager messMan = new MessageManager();
		if (gui.openProgressDialogAndExecute(job, pipeline, messMan, monitorSubtasks, severity) != PipelineLiteGUI.OK) {
			System.exit(2);
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
			throw new IllegalArgumentException("Script file must not be null"); //$NON-NLS-1$
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
			throw new IllegalArgumentException("Script must not be null"); //$NON-NLS-1$
		}
		if (parameters == null) {
			throw new IllegalArgumentException("Parameters must not be null"); //$NON-NLS-1$
		}
		Job job = new Job(script);
		for (String name : parameters.keySet()) {
			String value = parameters.get(name);
			ScriptParameter param = job.getScriptParameter(name);
			if (param == null) {
				System.out.println(Messages.getString("cli.info.ignoringParam", name)); //$NON-NLS-1$
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
			options.addOption("h", "help", false, Messages.getString("cli.info.help")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			options.addOption("i", "info", false, //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("cli.info.info")); //$NON-NLS-1$
			options.addOption("x", "execute", false, //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("cli.info.execute")); //$NON-NLS-1$
			options.addOption("q", "quit", false, //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("cli.info.quit")); //$NON-NLS-1$

			OptionBuilder.withLongOpt("no-subtask"); //$NON-NLS-1$
			OptionBuilder.withDescription(Messages.getString("cli.info.nosubtask")); //$NON-NLS-1$
			options.addOption(OptionBuilder.create());

			OptionBuilder.withLongOpt("verbosity"); //$NON-NLS-1$
			OptionBuilder.withDescription(Messages.getString("cli.info.verbosity")); //$NON-NLS-1$
			OptionBuilder.hasArg();
			OptionBuilder.withArgName("threshold"); //$NON-NLS-1$
			options.addOption(OptionBuilder.create('v'));

			OptionBuilder.withLongOpt("script"); //$NON-NLS-1$
			OptionBuilder.withDescription(Messages.getString("cli.info.script")); //$NON-NLS-1$
			OptionBuilder.hasArg();
			OptionBuilder.withArgName("file"); //$NON-NLS-1$
			options.addOption(OptionBuilder.create('s'));

			OptionBuilder.hasArgs();
			OptionBuilder.withValueSeparator(',');
			OptionBuilder.withArgName(Messages.getString("cli.argname.params")); //$NON-NLS-1$
			OptionBuilder.withDescription(Messages.getString("cli.info.params")); //$NON-NLS-1$
			OptionBuilder.withLongOpt("params"); //$NON-NLS-1$
			options.addOption(OptionBuilder.create("p")); //$NON-NLS-1$

			CommandLineParser parser = new PosixParser();
			return parser.parse(options, args, false);
		} catch (ParseException e) {
			System.err.println(Messages.getString("cli.error.parsingFailed") + e.getMessage()); //$NON-NLS-1$
			return null;
		}
	}

	private void printError(String message) {
		String str = cmdName + ": " + message + "\n" + Messages.getString("cli.error.tryhelp", cmdName);//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		System.out.println(str);

	}

	private void printHelp() {
		PrintWriter out = new PrintWriter(System.out);
		String usage = cmdName + Messages.getString("cli.info.usage"); //$NON-NLS-1$
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(usage, "\n" //$NON-NLS-1$
				+ Messages.getString("cli.info.options"), options, null, false); //$NON-NLS-1$
		out.println();
		out.println("\n" + Messages.getString("cli.info.examples")); //$NON-NLS-1$ //$NON-NLS-2$
		out.print(" 1. "); //$NON-NLS-1$
		out.println(Messages.getString("cli.info.example1")); //$NON-NLS-1$
		out.print(" 2. "); //$NON-NLS-1$
		out.println(Messages.getString("cli.info.example2")); //$NON-NLS-1$
		out.flush();
	}
}
