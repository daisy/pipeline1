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
     * @param a_inputListener
     * @param a_eventListeners
     * @param a_interactive
     */
    public ExeRunner(InputListener a_inputListener, Set a_eventListeners, Boolean a_interactive) {
        super(a_inputListener, a_eventListeners, a_interactive);
    }
    
    public boolean execute(Map a_parameters) throws TransformerRunException {
        // Read parameters
        String _commandPattern = (String)a_parameters.remove("exe_command");
        String _workDir = (String)a_parameters.remove("exe_workdir");
        String _stdout = (String)a_parameters.remove("exe_stdout");
        String _stderr = (String)a_parameters.remove("exe_stderr");
        String _timeout = (String)a_parameters.remove("exe_timeout");
        String _successRegex = (String)a_parameters.remove("exe_success_regex");
        
        // Compile regex pattern
        Pattern _success = null;
        if (_successRegex != null) {
            try {
                _success = Pattern.compile(_successRegex);
            } catch (PatternSyntaxException e) {
                throw new TransformerRunException("Invalid regex pattern for parameter exe_success_regex", e);
            }
        }
                
        // Expand command pattern
        String _command = expandCommandPattern(_commandPattern, a_parameters);
        
        // Execute command  
        boolean _finished = false;
        int _exitVal = 1;
        try {
            Runtime _runtime = Runtime.getRuntime();
            
            // Set working directory
            File _wd = null;
            if (_workDir != null) {
                _wd = new File(_workDir);
            }
            
            // Calculate timeout
            Date _startDate = new Date();
            Date _timeoutDate = null;
            if (_timeout != null) {
                _timeoutDate = new Date(_startDate.getTime() + Integer.parseInt(_timeout));
            }
            
            // Start the program
            sendMessage(Level.FINE, "Running '" + _command + "'");
            Process _proc = _runtime.exec(_command, null, _wd);
            
            // Setup and start stream redirectors
            OutputStream _outStream = System.out;
            OutputStream _errStream = System.err;
            if (_stdout != null) {
                _outStream = new FileOutputStream(_stdout);
            }            
            if (_stderr != null) {
                _errStream = new FileOutputStream(_stderr);
            }            
            StreamRedirector _out = new StreamRedirector(_proc.getInputStream(), _outStream);
            StreamRedirector _err = new StreamRedirector(_proc.getErrorStream(), _errStream);            
            _out.start();
            _err.start();
            
            // Wait (by polling) for exit value            
            while (!_finished) {
                try {
                    _exitVal = _proc.exitValue();
                    _finished = true;
                    Date _doneDate = new Date();
                    sendMessage(Level.FINE, i18n("PROGRAM_RAN_FOR", _command, new Long((_doneDate.getTime() - _startDate.getTime())/1000)));
                } catch (IllegalThreadStateException e) {
                    // Nothing
                }
                if (!_finished && _timeoutDate != null) {
                    if (_timeoutDate.before(new Date())) {
                        sendMessage(Level.SEVERE, "Terminating ExeRunner due to timeout");
                        _proc.destroy();
                        _exitVal = 1;
                        break;                        
                    }
                }
                // FIXME magic number
                if (!_finished) {
                    Thread.sleep(500);
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
        if (!_finished) {
            return false;
        }
        
        // If no exe_success_regex was specified, a exit value of 0 is counted
        // as a success. All other exit values are considered a failure.
        if (_success == null) {
            return _exitVal == 0;
        }
        
        // Match the exit value against the specified regex
        return _success.matcher(String.valueOf(_exitVal)).matches();
    }

    private String expandCommandPattern(String a_commandPattern, Map a_parameters) throws TransformerRunException {
        if (a_commandPattern == null) {
            return "";
        }
        Matcher _matcher = variablePattern.matcher(a_commandPattern);
        StringBuffer _sb = new StringBuffer();
        while (_matcher.find()) {
            String _variable = _matcher.group(1);
            String _value = (String)a_parameters.get(_variable);
            if (_value == null) {
                throw new TransformerRunException("Unrecognized variable in command pattern string: " + _variable);
            }
            _matcher.appendReplacement(_sb, _value);
        }
        _matcher.appendTail(_sb);
        return _sb.toString();
    }
}
