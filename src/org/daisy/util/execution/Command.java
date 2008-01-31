/*
 * org.daisy.util - The DAISY java utility library
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
package org.daisy.util.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.daisy.util.file.NullOutputStream;
import org.daisy.util.file.StreamRedirector;
import org.daisy.util.text.LineFilter;

/**
 * A utility class for executing external commands.
 * @author Linus Ericson
 */
public class Command {
    
    /**
     * Execute an external program.
     * @param command the program to execute
     * @param workDir the directory to use as working directory during execution.
     * @param stdout the <code>File</code> to send stdout messages to (specifying 
     * <code>null</code> will send them to the standard output of the caller). 
     * @param stderr the <code>File</code> to send stderr messages to (specifying 
     * <code>null</code> will send them to the standard error of the caller).
     * @param timeout the timeout in milliseconds before the program will be aborted.
     * @param pollInterval the interval between checks to see if the program has finished running. 
     * @return the exit value of the program that was run.
     * @throws ExecutionException
     */
	@Deprecated
    public static int execute(String command, File workDir, OutputStream stdout, OutputStream stderr, int timeout, int pollInterval) throws ExecutionException {        
        // Check working directory
        if (workDir != null && !workDir.isDirectory()) {
            throw new ExecutionException("'" + workDir + "' is not a directory");
        }
        
        // Check poll interval
        if (pollInterval < 10) {
            throw new ExecutionException("Poll interval must be at least 10 ms");
        }
        
        // Execute command  
        boolean finished = false;
        int exitVal = 1;
        try {
            Runtime runtime = Runtime.getRuntime();
            
            // Calculate timeout
            Date startDate = new Date();
            Date timeoutDate = null;
            if (timeout > 0) {
                timeoutDate = new Date(startDate.getTime() + timeout);
            }
            
            // Start the program            
            Process proc = runtime.exec(command, null, workDir);
            
            // Setup and start stream redirectors
            OutputStream outStream = System.out;
            OutputStream errStream = System.err;
            if (stdout != null) {
                outStream = stdout;
            }            
            if (stderr != null) {
                errStream = stderr;
            }            
            StreamRedirector out = new StreamRedirector(proc.getInputStream(), outStream);
            StreamRedirector err = new StreamRedirector(proc.getErrorStream(), errStream);            
            out.start();
            err.start();
            
            // Wait (by polling) for exit value            
            while (!finished) {
                try {
                    exitVal = proc.exitValue();
                    finished = true;                    
                } catch (IllegalThreadStateException e) {
                    // Not finished yet
                }
                if (!finished) {
                    if (timeoutDate != null && timeoutDate.before(new Date())) {
                        proc.destroy();
                        throw new ExecutionException("Timeout. Aborting program execution.");   
                    }
                    Thread.sleep(pollInterval);
                }
            }
        } catch (FileNotFoundException e) {
            throw new ExecutionException("Cannot write to file", e);
        } catch (IOException e) {
            throw new ExecutionException("Cannot run command", e);        
        } catch (InterruptedException e) {
            throw new ExecutionException("Interrupted in sleep", e);
        }
        
        // If the program was aborted, it is a failure
        if (!finished) {
            throw new ExecutionException("Timeout. Aborting program execution.");            
        }
        
        return exitVal;
    }
    
    /**
     * Execute an external program.
     * @param cmdarray the program to execute
     * @param workDir the directory to use as working directory during execution.
     * @param stdout the <code>File</code> to send stdout messages to (specifying 
     * <code>null</code> will send them to the standard output of the caller). 
     * @param stderr the <code>File</code> to send stderr messages to (specifying 
     * <code>null</code> will send them to the standard error of the caller).
     * @param timeout the timeout in milliseconds before the program will be aborted.
     * @param pollInterval the interval between checks to see if the program has finished running. 
     * @param lineFilter the <code>LineFilter</code> to filter the <code>stdout</code> and <code>stderr</code> streams through
     * @return the exit value of the program that was run.
     * @throws ExecutionException
     */
    public static int execute(String[] cmdarray, File workDir, OutputStream stdout, OutputStream stderr, int timeout, int pollInterval, LineFilter lineFilter) throws ExecutionException {        
        // Check working directory
        if (workDir != null && !workDir.isDirectory()) {
            throw new ExecutionException("'" + workDir + "' is not a directory");
        }
        
        // Check poll interval
        if (pollInterval < 10) {
            throw new ExecutionException("Poll interval must be at least 10 ms");
        }
        
        // Execute command  
        boolean finished = false;
        int exitVal = 1;
        try {
            Runtime runtime = Runtime.getRuntime();
            
            // Calculate timeout
            Date startDate = new Date();
            Date timeoutDate = null;
            if (timeout > 0) {
                timeoutDate = new Date(startDate.getTime() + timeout);
            }
            
            // Start the program            
            Process proc = runtime.exec(cmdarray, null, workDir);
            
            // Setup and start stream redirectors
            OutputStream outStream = System.out;
            OutputStream errStream = System.err;
            if (stdout != null) {
                outStream = stdout;
            }            
            if (stderr != null) {
                errStream = stderr;
            }            
            StreamRedirector out = new StreamRedirector(proc.getInputStream(), outStream, lineFilter, false);
            StreamRedirector err = new StreamRedirector(proc.getErrorStream(), errStream, lineFilter, false);            
            out.start();
            err.start();
            
            // Wait (by polling) for exit value            
            while (!finished) {
                try {
                    exitVal = proc.exitValue();
                    finished = true;                    
                } catch (IllegalThreadStateException e) {
                    // Not finished yet
                }
                if (!finished) {
                    if (timeoutDate != null && timeoutDate.before(new Date())) {
                        proc.destroy();
                        throw new ExecutionException("Timeout. Aborting program execution.");   
                    }
                    Thread.sleep(pollInterval);
                }
            }
        } catch (FileNotFoundException e) {
            throw new ExecutionException("Cannot write to file", e);
        } catch (IOException e) {
            throw new ExecutionException("Cannot run command", e);        
        } catch (InterruptedException e) {
            throw new ExecutionException("Interrupted in sleep", e);
        }
        
        // If the program was aborted, it is a failure
        if (!finished) {
            throw new ExecutionException("Timeout. Aborting program execution.");            
        }
        
        return exitVal;
    }
    
    public static int execute(String[] cmdarray, File workDir, OutputStream stdout, OutputStream stderr, int timeout, int pollInterval) throws ExecutionException {
    	return execute(cmdarray, workDir, stdout, stderr, timeout, pollInterval);
    }
    
    /**
     * Execute an external program.
     * <p>
     * Overloaded method. This is the same as calling
     * </p>
     * <pre>
     * execute(command, null, null, null, -1, 500);
     * </pre>
     * @param command the command to execute
     * @return the exit value of the program.
     * @throws ExecutionException
     * @see #execute(String, File, File, File, int, int)
     */
    @Deprecated
    public static int execute(String command) throws ExecutionException {
        return execute(command, null, (OutputStream)null, (OutputStream)null, -1, 500);
    }
    
    public static int execute(String[] cmdarray) throws ExecutionException {
        return execute(cmdarray, null, (OutputStream)null, (OutputStream)null, -1, 500);
    }
    
    @Deprecated
    public static int execute(String command, boolean ignoreOutput) throws ExecutionException {
        OutputStream out = null;
        OutputStream err = null;
        if (ignoreOutput) {
            out = new NullOutputStream();
            err = new NullOutputStream();
        }
        return execute(command, null, out, err, -1, 500);
    }
    
    public static int execute(String[] cmdarray, boolean ignoreOutput) throws ExecutionException {
        OutputStream out = null;
        OutputStream err = null;
        if (ignoreOutput) {
            out = new NullOutputStream();
            err = new NullOutputStream();
        }
        return execute(cmdarray, null, out, err, -1, 500);
    }
    
    @Deprecated
    public static int execute(String command, File workDir, File stdout, File stderr, int timeout, int pollInterval) throws ExecutionException {
        try {
            return execute(command, workDir, new FileOutputStream(stdout), new FileOutputStream(stderr), timeout, pollInterval);
        } catch (ExecutionException e) {
            throw new ExecutionException(e.getMessage(), e);            
        } catch (FileNotFoundException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }
    
    public static int execute(String[] cmdarray, File workDir, File stdout, File stderr, int timeout, int pollInterval) throws ExecutionException {
        try {
            return execute(cmdarray, workDir, new FileOutputStream(stdout), new FileOutputStream(stderr), timeout, pollInterval);
        } catch (ExecutionException e) {
            throw new ExecutionException(e.getMessage(), e);            
        } catch (FileNotFoundException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }
}
