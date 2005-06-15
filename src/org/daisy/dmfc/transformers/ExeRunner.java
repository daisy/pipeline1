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
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

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
     * Constructs a new ExeRunner transformer.
     * @param inputListener an input listener
     * @param eventListeners a set of event listeners
     * @param interactive specified whether the Transformer should be run in interactive mode
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
        
        // Compile successRegex pattern
        Pattern success = null;
        if (successRegex != null) {
            try {
                success = Pattern.compile(successRegex);
            } catch (PatternSyntaxException e) {
                throw new TransformerRunException(i18n("INVALID_SUCCESS_REGEX"), e);
            }
        }
        
        // Expand command pattern
        String command = expandCommandPattern(commandPattern, parameters);        
        
        // Setup workdir
        File dir = null;
        if (workDir != null) {
            dir = new File(workDir);
        }
        
        // Redirect stdout
        File stdoutFile = null;
        if (stdout != null) {
            stdoutFile = new File(stdout);  
        }
        
        // Redirect stderr
        File stderrFile = null;
        if (stderr != null) {
            stderrFile = new File(stderr);
        }
        
        int maxRunningTime = Integer.parseInt(timeout);
        
        // Read poll interval
        int pollInterval = Integer.parseInt(System.getProperty("dmfc.pollExeInterval", "500"));
        if (pollInterval < 10) {
            sendMessage(Level.WARNING, i18n("POLL_INTERVAL_TO_SMALL"));
            pollInterval = 500;
        }
        
        int exitVal;
        try {
            sendMessage(Level.FINE, i18n("RUNNING_COMMAND", command));
            Date startDate = new Date();
            exitVal = Command.execute(command, dir, stdoutFile, stderrFile, maxRunningTime, pollInterval);
            Date doneDate = new Date();
            sendMessage(Level.FINE, i18n("PROGRAM_RAN_FOR", command, new Long((doneDate.getTime() - startDate.getTime())/1000)));
            
        } catch (ExecutionException e) {
            sendMessage(Level.WARNING, e.getMessage());
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
                throw new TransformerRunException(i18n("UNRECOGIZED_COMMAD_PATTERN_VARIABLE", variable));
            }
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
