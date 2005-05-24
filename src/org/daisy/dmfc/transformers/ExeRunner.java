/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package org.daisy.dmfc.transformers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.StreamRedirector;

/**
 * Run arbitraty applications.
 * ExeRunner reads a set of parameters.
 * <ul>
 * <li><b>exe_command</b> the name pattern of the program to run.
 * <li><b>exe_workdir</b> (optional) sets the working directory of the program
 * to run.
 * <li><b>exe_stdout</b> optional parameter specifying a filename where the
 * stdout stream is sent. If the parameter is not specified, stdout is sent
 * to the stdout of DMFC.
 * <li><b>exe_stderr</b> optional parameter specifying a filename where the
 * stderr stream is sent.  If the parameter is not specified, stderr is sent
 * to the stdout of DMFC.
 * <li><b>exe_timeout</b> the number of milliseconds before the program
 * should be interrupted by DMFC. Not setting this parameter disables this
 * feature.
 * <li><b>exe_success_regex</b> a regular expression describing the successful
 * exit codes of the program. All other exit codes will be considered as
 * failures. If this parameter is not specified, the ExeRunner plugin will
 * only treat the exit code 0 as successful. 
 * </ul>
 * 
 * 
 * @author Linus Ericson
 */
public class ExeRunner extends Transformer {

    private static Pattern variablePattern = Pattern.compile("\\$\\{(\\w+)\\}");
    
    /**
     * @param inListener
     * @param eventListeners
     * @param interactive
     */    
    public ExeRunner(InputListener inListener, Set eventListeners, Boolean interactive) {
        super(inListener, eventListeners, interactive);
    }
    
    protected boolean execute(Map parameters) throws TransformerRunException {
        // Read parameters
        String commandPattern = (String)parameters.remove("exe_command");
        String workDir = (String)parameters.remove("exe_workdir");
        String stdout = (String)parameters.remove("exe_stdout");
        String stderr = (String)parameters.remove("exe_stderr");
        String timeout = (String)parameters.remove("exe_timeout");
        String successRegex = (String)parameters.remove("exe_success_regex");
        
        // Compile regex pattern
        Pattern success = null;
        if (successRegex != null) {
            try {
                success = Pattern.compile(successRegex);
            } catch (PatternSyntaxException e) {
                throw new TransformerRunException("Invalid regex pattern for parameter exe_success_regex", e);
            }
        }
                
        // Expand command pattern
        String command = expandCommandPattern(commandPattern, parameters);
        
        // Execute command  
        boolean finished = false;
        int exitVal = 1;
        try {
            Runtime runtime = Runtime.getRuntime();
            
            // Set working directory
            File wd = null;
            if (workDir != null) {
                wd = new File(workDir);
            }
            
            // Calculate timeout
            Date startDate = new Date();
            Date timeoutDate = null;
            if (timeout != null) {
                timeoutDate = new Date(startDate.getTime() + Integer.parseInt(timeout));
            }
            
            // Start the program
            sendMessage(Level.FINE, "Running '" + command + "'");
            Process proc = runtime.exec(command, null, wd);
            
            // Setup and start stream redirectors
            OutputStream outStream = System.out;
            OutputStream errStream = System.err;
            if (stdout != null) {
                outStream = new FileOutputStream(stdout);
            }            
            if (stderr != null) {
                errStream = new FileOutputStream(stderr);
            }            
            StreamRedirector out = new StreamRedirector(proc.getInputStream(), outStream);
            StreamRedirector err = new StreamRedirector(proc.getErrorStream(), errStream);            
            out.start();
            err.start();
            
            int pollInterval;
            try {
                pollInterval = Integer.parseInt(System.getProperty("dmfc.pollExeInterval", "500"));
                if (pollInterval < 10) {
                    throw new NumberFormatException("Must be at least 10");
                }
            } catch (NumberFormatException e) {
                sendMessage(Level.WARNING, System.getProperty("dmfc.pollExeInterval") + " is not a valid poll interval (must be at least 10ms)");
                pollInterval = 500;
            }
            
            // Wait (by polling) for exit value            
            while (!finished) {
                try {
                    exitVal = proc.exitValue();
                    finished = true;
                    Date doneDate = new Date();
                    sendMessage(Level.FINE, i18n("PROGRAM_RAN_FOR", command, new Long((doneDate.getTime() - startDate.getTime())/1000)));
                } catch (IllegalThreadStateException e) {
                    // Nothing
                }
                if (!finished && timeoutDate != null) {
                    if (timeoutDate.before(new Date())) {
                        sendMessage(Level.SEVERE, "Terminating ExeRunner due to timeout");
                        proc.destroy();
                        exitVal = 1;
                        break;                        
                    }
                }
                if (!finished) {
                    Thread.sleep(pollInterval);
                }
            }
        } catch (FileNotFoundException e) {
            throw new TransformerRunException("Cannot write to file", e);
        } catch (IOException e) {
            throw new TransformerRunException("Cannot run command", e);        
        } catch (InterruptedException e) {
            throw new TransformerRunException("Interrupted in sleep", e);
        }
        
        // If the program was aborted, it is a failure
        if (!finished) {
            return false;
        }
        
        // If no exe_success_regex was specified, a exit value of 0 is counted
        // as a success. All other exit values are considered a failure.
        if (success == null) {
            return exitVal == 0;
        }
        
        // Match the exit value against the specified regex
        return success.matcher(String.valueOf(exitVal)).matches();
    }

    private String expandCommandPattern(String commandPattern, Map parameters) throws TransformerRunException {
        if (commandPattern == null) {
            return "";
        }
        Matcher matcher = variablePattern.matcher(commandPattern);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String value = (String)parameters.get(variable);
            if (value == null) {
                throw new TransformerRunException("Unrecognized variable in command pattern string: " + variable);
            }
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
