/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005-2007  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.dmfc.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.dmfc.core.listener.MessageListener;
import org.daisy.dmfc.core.listener.ScriptProgressListener;
import org.daisy.dmfc.core.listener.TransformerProgressListener;
import org.daisy.dmfc.core.message.Message;
import org.daisy.dmfc.core.message.TransformerMessage;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.ScriptRunner;
import org.daisy.dmfc.core.script.ScriptValidationException;
import org.daisy.dmfc.core.script.datatype.BooleanDatatype;
import org.daisy.dmfc.core.script.datatype.Datatype;
import org.daisy.dmfc.core.script.datatype.DatatypeException;
import org.daisy.dmfc.core.script.datatype.EnumDatatype;
import org.daisy.dmfc.core.script.datatype.EnumItem;
import org.daisy.dmfc.core.script.datatype.FileDatatype;
import org.daisy.dmfc.core.script.datatype.IntegerDatatype;
import org.daisy.dmfc.core.script.datatype.StringDatatype;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.ScriptException;

/**
 * A simple command line UI for running DMFC.
 * @author Linus Ericson
 * @author Markus Gylling
 */
//public class CommandLineUI implements InputListener, EventListener {
public class CommandLineUI implements InputListener, MessageListener, TransformerProgressListener, ScriptProgressListener {
	
	private static Pattern optionPattern = Pattern.compile("-(.*)");
	private static Pattern paramPattern = Pattern.compile("--(\\w+)=(.+)");

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.InputListener#isAborted()
	 */
    public boolean isAborted() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.dmfc.core.InputListener#getInputAsString(org.daisy.dmfc.core.message.TransformerMessage)
     */
	public String getInputAsString(TransformerMessage message) {
		System.err.println("[" + message.getSource().getClass().getSimpleName() + "] Prompt: " + message.getText()); //TODO Transformer.getName()
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		    line = br.readLine();
        } catch (IOException e) {
        }
		return line;
	}

    
    /**
     * @deprecated
     */
	public String getInputAsString(Prompt prompt) {
		System.err.println("[" + prompt.getMessageOriginator() + "] Prompt: " + prompt.getMessage());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		try {
		    line = br.readLine();
        } catch (IOException e) {
        }
		return line;
	}

	/**
	 * Main method to start DMFC from the command line.
	 * <pre>
	 * org.daisy.dmfc.ui.CommandLineUI [-h|-f|-i] scriptfile --name=value...
	 * 
	 * -h
	 *   Print this help text
	 *   
	 * -i
	 *   Print help and parameter information about the specified script.
	 *   
	 * -f
	 *   Force run. Run the script even though all required parameters are
	 *   not specified.
	 *   
	 * scriptfile
	 *   The scriptfile to run.
	 *   
	 * --name=value
	 *   All parameters required (and possibly any optional ones) parameters
	 * </pre>
	 * @param args
	 */
	public static void main(String[] args) {
		File scriptFile = null;
		boolean information = false;
		boolean force = false;
		int index = 0;		
		Map<String,String> parameters = new HashMap<String,String>();
		
		// We need at least one parameter
		if (args.length <= 0) {
			usage();
			System.exit(1);
		}
		
		// Check command line options
		Matcher matcher = optionPattern.matcher(args[0]);		
		if (matcher.matches()) {
			if ("i".equals(matcher.group(1))) {
				information = true;
			} else if ("f".equals(matcher.group(1))) {
				force = true;
			} else {
				// Help (-h) or unknown parameter
				usage();
				System.exit(1);
			}
			if (args.length <= 1) {
				// No filename
				usage();
				System.exit(1);
			} else {
				// Filename must be at args index 1
				index = 1;			
			}
		} 
		
		// Check filename
		scriptFile = new File(args[index]);
		if (!scriptFile.exists()) {
			System.out.println("Error: script file '" + args[index] + "' does not exist\n");
			System.exit(1);
		}
		
		// Loop through script parameters
		for (int i = index+1; i < args.length; i++) {
			matcher = paramPattern.matcher(args[i]); 
			if (matcher.matches()) {
				String key = matcher.group(1);
				String value = matcher.group(2);
				parameters.put(key, value);
			} else {
				System.out.println("Error: invalid parameter '" + args[i] + "'\n");
				usage();
				System.exit(1);
			}
		}		
		try {
			CommandLineUI ui = new CommandLineUI();
        	//DMFCCore dmfc = new DMFCCore(ui, ui, new Locale("en"));
			//mg: after listener refactoring:
			DMFCCore dmfc = new DMFCCore(ui,ui,ui,ui, new Locale("en"));
        	Script script = dmfc.newScript(scriptFile.toURL());
        	ScriptRunner runner = new ScriptRunner(script);
        	
        	// Only print information?
        	if (information) {
        		System.out.println(getScriptInfo(script));
        		System.exit(0);
        	}
        	
        	// Force?
        	if (force) {
        		// Loop through required parameters, set default value
        		setDefaultValues(script, runner);
        	}
        	
        	// Set parameters
        	for (String name : parameters.keySet()) {
        		String value = parameters.get(name);
        		ScriptParameter param = runner.getScriptParameter(name);
        		if (param == null) {
        			System.out.println("Error: Unknown parameter '" + name + "'\n");
        			System.out.println(getScriptInfo(script));
        			System.exit(1);
        		}
        		runner.setParameterValue(name, value);
        	}
			
        	// Execute script
        	dmfc.execute(runner);
        	
        } catch (DMFCConfigurationException e) {            
            e.printStackTrace();
        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ScriptValidationException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (DatatypeException e) {
			e.printStackTrace();
		} 
	}
	
	private static void setDefaultValues(Script script, ScriptRunner runner) throws DatatypeException {
		for (ScriptParameter param : script.getParameters().values()) {
			if (param.isRequired()) {
				runner.setParameterValue(param.getName(), param.getValue());
			}
		}
	}
	
	/**
	 * Create some script information.
	 * @param script
	 * @return
	 */
	private static String getScriptInfo(Script script) {
		StringBuilder builder = new StringBuilder();
		builder.append("\nScript information\n");
		builder.append("------------------\n");
		builder.append("Name:        ").append(script.getNicename()).append("\n");
		builder.append("Description: ").append(script.getDescription()).append("\n\n");
		
		builder.append("Parameters:\n");
		for (ScriptParameter param : script.getParameters().values()) {
			builder.append("\n  ").append(param.getName()).append("\n");
			builder.append("    Name:        ").append(param.getNicename()).append("\n");
			builder.append("    Description: ").append(param.getDescription()).append("\n");
			builder.append("    Datatype:    ");
			Datatype datatype = param.getDatatype();
			switch (datatype.getType()) {
			case BOOLEAN:
				builder.append("boolean (").append(((BooleanDatatype)datatype).getTrueValue());
				builder.append(", ").append(((BooleanDatatype)datatype).getFalseValue()).append(")\n");
				break;
			case ENUM:
				builder.append("enum ( ");
				for (EnumItem item : ((EnumDatatype)datatype).getItems()) {
					builder.append("'").append(item.getValue()).append("' ");
				}
				builder.append(")\n");
				break;
			case FILE:
				if (((FileDatatype)datatype).isInput()) {
					builder.append("input ");
				} else {
					builder.append("output ");
				}
				if ("application/x-filesystemDirectory".equals(((FileDatatype)datatype).getMime())) {
					builder.append("directory\n");
				} else {
					builder.append("file (").append(((FileDatatype)datatype).getMime()).append(")\n");
				}
				break;
			case INTEGER:
				builder.append("integer [").append(((IntegerDatatype)datatype).getMin());
				builder.append(", ").append(((IntegerDatatype)datatype).getMax()).append("]\n");
				break;
			case STRING:
				builder.append("string (matching ").append(((StringDatatype)datatype).getRegex());
				builder.append(")\n");
				break;
			}
			builder.append("    Occurrence:  ");
			if (param.isRequired()) {
				builder.append("required\n");
			} else {
				builder.append("optional (default '").append(param.getValue()).append("')\n");
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Print usage information.
	 */
	private static void usage() {
		System.out.println("Usage:\n");
		System.out.println("org.daisy.dmfc.ui.CommandLineUI [-h|-f|-i] scriptfile --name=value...\n");
		System.out.println("  -h\n\tPrint this help text.");		
		System.out.println("  -i\n\tPrint help and parameter information about the specified script.");
		System.out.println("  -f\n\tForce run. Run the script even though all required parameters are\n\tnot specified.");
		System.out.println("  scriptfile\n\tThe scriptfile to run.");
		System.out.println("  --name=value\n\tAll parameters required (and possibly any optional ones) parameters");
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.MessageListener#message(org.daisy.dmfc.core.message.Message)
	 */
	public void message(Message message) {
		// TODO Auto-generated method stub
		System.out.println(message.getText());
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.TransformerProgressListener#transformerEnd(org.daisy.dmfc.core.transformer.Transformer)
	 */
	public void transformerEnd(Transformer transformer) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.TransformerProgressListener#transformerProgress(double, org.daisy.dmfc.core.transformer.Transformer)
	 */
	public void transformerProgress(double progress, Transformer transformer) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.TransformerProgressListener#transformerStart(org.daisy.dmfc.core.transformer.Transformer)
	 */
	public void transformerStart(Transformer transformer) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.ScriptProgressListener#scriptEnd(org.daisy.dmfc.core.script.Script)
	 */
	public void scriptEnd(Script script) {
		// TODO Auto-generated method stub
		System.out.println("Script " + script.getNicename() + " just finished runnning.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.listener.ScriptProgressListener#scriptStart(org.daisy.dmfc.core.script.Script)
	 */
	public void scriptStart(Script script) {
		System.out.println("Script " + script.getNicename() + " just started runnning.");		
	}
}