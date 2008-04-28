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
package org.daisy.pipeline.ui;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.PipelineCore;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.BusListener;
import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.JobStateChangeEvent;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.RequestEvent;
import org.daisy.pipeline.core.event.StateChangeEvent;
import org.daisy.pipeline.core.event.TaskMessageEvent;
import org.daisy.pipeline.core.event.TaskStateChangeEvent;
import org.daisy.pipeline.core.event.UserReplyEvent;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.core.script.datatype.BooleanDatatype;
import org.daisy.pipeline.core.script.datatype.Datatype;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.core.script.datatype.DirectoryDatatype;
import org.daisy.pipeline.core.script.datatype.EnumDatatype;
import org.daisy.pipeline.core.script.datatype.EnumItem;
import org.daisy.pipeline.core.script.datatype.FileDatatype;
import org.daisy.pipeline.core.script.datatype.IntegerDatatype;
import org.daisy.pipeline.core.script.datatype.StringDatatype;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.xml.stax.ExtendedLocationImpl;

/**
 * A simple command line UI for running DMFC.
 * 
 * @author Linus Ericson
 * @author Markus Gylling
 */

public class CommandLineUI implements InputListener, BusListener {

    private static Pattern optionPattern = Pattern.compile("-(.*)");
    private static Pattern paramPattern = Pattern.compile("--(\\w+)=(.+)");

    /**
     * Main method to start DMFC from the command line.
     * 
     * <pre>
     * org.daisy.pipeline.ui.CommandLineUI [-h|-f|-i] scriptfile --name=value...
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
     * 
     * @param args
     */
    public static void main(String[] args) {

        File scriptFile = null;
        boolean information = false;
        boolean force = false;
        int index = 0;
        Map<String, String> parameters = new HashMap<String, String>();

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
            System.out.println("Error: script file '" + args[index]
                    + "' does not exist\n");
            System.exit(1);
        }

        // Loop through script parameters
        for (int i = index + 1; i < args.length; i++) {
            matcher = paramPattern.matcher(args[i]);
            if (matcher.matches()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                parameters.put(key, value);
            } else {
                System.out.println("Error: invalid parameter '" + args[i]
                        + "'\n");
                usage();
                System.exit(1);
            }
        }

        CommandLineUI ui = null;
        try {
            ui = new CommandLineUI();

            // subscribe to all message and state change events.
            EventBus.getInstance().subscribe(ui, MessageEvent.class);
            EventBus.getInstance().subscribe(ui, StateChangeEvent.class);

            // Load user properties
            URL propsURL = CommandLineUI.class.getClassLoader().getResource(
                    "pipeline.user.properties");
            XMLProperties properties = new XMLProperties();
            try {
                properties.loadFromXML(propsURL.openStream());
            } catch (IOException e) {
                throw new DMFCConfigurationException(
                        "Can't read pipeline.user.properties", e);
            }

            PipelineCore dmfc = new PipelineCore(ui, findHomeDirectory(),properties);
            Script script = dmfc.newScript(scriptFile.toURI().toURL());
            Job job = new Job(script);

            // Only print information?
            if (information) {
                System.out.println(getScriptInfo(script));
                System.exit(0);
            }

            // Force?
            if (force) {
                // Loop through required parameters, set default value
                setDefaultValues(script, job);
            }

            // Set parameters
            for (String name : parameters.keySet()) {
                String value = parameters.get(name);
                ScriptParameter param = job.getScriptParameter(name);
                if (param == null) {
                    System.out.println("Error: Unknown parameter '" + name
                            + "'\n");
                    System.out.println(getScriptInfo(script));
                    System.exit(1);
                }
                job.setParameterValue(name, value);
            }

            // Execute script
            dmfc.execute(job);

            // Exit successfully
            System.exit(0);
            
        } catch (DMFCConfigurationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ScriptValidationException e) {
            e.printStackTrace();
        } catch (JobFailedException e) {
            e.printStackTrace();
        } catch (DatatypeException e) {
            e.printStackTrace();
        } finally {
            EventBus.getInstance().unsubscribe(ui, MessageEvent.class);
            EventBus.getInstance().unsubscribe(ui, StateChangeEvent.class);
        }
        
        // We end up here if an exception occurred.  
        System.exit(1);
    }

    public void received(EventObject event) {
        // we are subscribing to MessageEvent and StateChangeEvent

        try {
            if (event instanceof MessageEvent) {
                MessageEvent sme = (MessageEvent) event;
                StringBuilder message = new StringBuilder();

                String type = null;
                switch (sme.getType()) {
                case INFO:
                    type = "INFO";
                    break;
                case INFO_FINER:
                    type = "INFO_FINER";
                    break;    
                case WARNING:
                    type = "WARNING";
                    break;
                case ERROR:
                    type = "ERROR";
                    break;
                case DEBUG:
                    type = "DEBUG";
                    break;
                }

                String who = null;
                if (sme instanceof CoreMessageEvent) {
                    who = "Pipeline Core";
                } else if (sme instanceof TaskMessageEvent) {
                    Task task = (Task) sme.getSource();
                    if (task.getTransformerInfo() != null) {
                        who = task.getTransformerInfo().getNiceName();
                    } else {
                        who = task.getName();
                    }
                } else {
                    who = "???";
                }

                StringBuilder location = new StringBuilder();
                if (sme.getLocation() != null) {
                    Location loc = sme.getLocation();
                    String sysId = loc.getSystemId();
                    if (sysId != null && sysId.length() > 0) {
                        File file = new File(sysId);
                        location.append(" Location: ");
                        location.append(file.getPath());
                        if (loc.getLineNumber() > -1) {
                            location.append(' ');
                            location.append(loc.getLineNumber());
                            if (loc.getColumnNumber() > -1) {
                                location.append(':');
                                location.append(loc.getColumnNumber());
                            }
                        }
                    }
                                   
                    //mg20070904: printing extended location info
                    if(loc instanceof ExtendedLocationImpl) {                    		                        
                    	ExtendedLocationImpl eLoc = (ExtendedLocationImpl)loc;                    	
                    	ExtendedLocationImpl.InformationType[] types = ExtendedLocationImpl.InformationType.values();                    	                    	
                    	for (int i = 0; i < types.length; i++) {                    		
                    		location.append("\n\t");
                    		location.append(types[i].toString()).append(':').append(' ');
                    		String value = eLoc.getExtendedLocationInfo(types[i]);
                    		location.append(value==null?"N/A":value);                            		
						}                    	
                    }
                    
                }//if (sme.getLocation() != null)

                message.append('[');
                message.append(type);
                message.append(',').append(' ');
                message.append(who);
                message.append(']').append(' ');
                message.append(sme.getMessage());
                message.append(location.toString());

                System.out.println(message.toString());

            } else if (event instanceof StateChangeEvent) {
                StateChangeEvent sce = (StateChangeEvent) event;

                String type = null;
                String name = null;
                String state = (sce.getState() == StateChangeEvent.Status.STARTED) ? "started"
                        : "stopped";

                if (event instanceof JobStateChangeEvent) {
                    type = "Task"; // we refer to scripts as "tasks" to users.
                    Job job = (Job) sce.getSource();
                    name = job.getScript().getNicename();
                } else if (event instanceof TaskStateChangeEvent) {
                    type = "Transformer";
                    Task task = (Task) sce.getSource();
                    name = task.getTransformerInfo().getNiceName();
                } else {
                    System.err.println(event.getClass().getSimpleName());
                }

                System.out.println("[STATE] " + type + " " + name + " just "
                        + state);

            } else {
                System.err.println(event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static void setDefaultValues(Script script, Job runner)
            throws DatatypeException {
        for (ScriptParameter param : script.getParameters().values()) {
            if (param.isRequired()) {
                runner.setParameterValue(param.getName(), param.getValue());
            }
        }
    }

    /**
     * Create some script information.
     * 
     * @param script
     * @return
     */
    private static String getScriptInfo(Script script) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nScript information\n");
        builder.append("------------------\n");
        builder.append("Name:        ").append(script.getNicename()).append(
                "\n");
        builder.append("Description: ").append(script.getDescription()).append(
                "\n\n");
        if (script.getDocumentation() != null) {
            builder.append("Documentation: ").append(
                    script.getDocumentation().toString()).append("\n\n");
        }

        builder.append("Parameters:\n");
        for (ScriptParameter param : script.getParameters().values()) {
            builder.append("\n  ").append(param.getName()).append("\n");
            builder.append("    Name:        ").append(param.getNicename())
                    .append("\n");
            builder.append("    Description: ").append(param.getDescription())
                    .append("\n");
            builder.append("    Datatype:    ");
            Datatype datatype = param.getDatatype();
            switch (datatype.getType()) {
            case BOOLEAN:
                builder.append("boolean (").append(
                        ((BooleanDatatype) datatype).getTrueValue());
                builder.append(", ").append(
                        ((BooleanDatatype) datatype).getFalseValue()).append(
                        ")\n");
                break;
            case ENUM:
                builder.append("enum ( ");
                for (EnumItem item : ((EnumDatatype) datatype).getItems()) {
                    builder.append("'").append(item.getValue()).append("' ");
                }
                builder.append(")\n");
                break;
            case FILE:
                if (((FileDatatype) datatype).isInput()) {
                    builder.append("input ");
                } else {
                    builder.append("output ");
                }
                if ("application/x-filesystemDirectory"
                        .equals(((FileDatatype) datatype).getMime())) {
                    builder.append("directory\n");
                } else {
                    builder.append("file (").append(
                            ((FileDatatype) datatype).getMime()).append(")\n");
                }
                break;
            case DIRECTORY:
                if (((DirectoryDatatype) datatype).isInput()) {
                    builder.append("input ");
                } else {
                    builder.append("output ");
                }
                builder.append("directory\n");
                break;
            case INTEGER:
                builder.append("integer [").append(
                        ((IntegerDatatype) datatype).getMin());
                builder.append(", ").append(
                        ((IntegerDatatype) datatype).getMax()).append("]\n");
                break;
            case STRING:
                builder.append("string (matching ").append(
                        ((StringDatatype) datatype).getRegex());
                builder.append(")\n");
                break;
            }
            builder.append("    Occurrence:  ");
            if (param.isRequired()) {
                builder.append("required\n");
            } else {
                builder.append("optional (default '").append(param.getValue())
                        .append("')\n");
            }
        }

        return builder.toString();
    }

    /**
     * Finds the pipeline home directory.
     * 
     * @param propertiesURL
     * @return
     * @throws DMFCConfigurationException
     */
    private static File findHomeDirectory() throws DMFCConfigurationException {
        URL propertiesURL = PipelineCore.class.getClassLoader().getResource(
                "pipeline.properties");
        File propertiesFile = null;
        try {
            propertiesFile = new File(propertiesURL.toURI());
        } catch (URISyntaxException e) {
            throw new DMFCConfigurationException(e.getMessage(), e);
        }
        // Is this the home dir?
        File folder = propertiesFile.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        // Test parent
        folder = folder.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        throw new DMFCConfigurationException(
                "Cannot locate the Daisy Pipeline home directory");
    }

    /**
     * Print usage information.
     */
    private static void usage() {
        System.out.println("Usage:\n");
        System.out
                .println("org.daisy.pipeline.ui.CommandLineUI [-h|-f|-i] scriptfile --name=value...\n");
        System.out.println("  -h\n\tPrint this help text.");
        System.out
                .println("  -i\n\tPrint help and parameter information about the specified script.");
        System.out
                .println("  -f\n\tForce run. Run the script even though all required parameters are\n\tnot specified.");
        System.out.println("  scriptfile\n\tThe scriptfile to run.");
        System.out
                .println("  --name=value\n\tAll parameters required (and possibly any optional ones) parameters");
    }

    public UserReplyEvent getUserReply(RequestEvent event) {
        String source;
        if (event.getSource() instanceof Transformer) {
            source = ((Transformer) event.getSource()).getTransformerInfo()
                    .getNiceName();
        } else {
            source = event.getSource().getClass().getSimpleName();
        }

        System.err.println("[" + source + "] Prompt: " + event.getRequest());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
        }
        return new UserReplyEvent(this, line);

    }

}