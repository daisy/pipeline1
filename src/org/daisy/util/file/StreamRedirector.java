/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.daisy.util.text.LineFilter;

/**
 * Redirects an input stream to a output stream in a separate thread.
 * @author Linus Ericson
 */
public class StreamRedirector extends Thread {
    
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected LineFilter filter;
    protected boolean flush;
    
    /**
     * Constructor specifying a <code>LineFilter</code> and the ability to select whether the
     * stream should be flushed after each write or not.
     * @param inStream the input stream to redirect
     * @param outSteam the output stream to redirect the input to
     * @param lineFilter the line filter to filter the contents through before it is written to the output stream
     * @param flushOutstream <code>true</code> if the output stream should be
     *  flushed after each write, <code>false</code> otherwise
     */
    public StreamRedirector(InputStream inStream, OutputStream outSteam, LineFilter lineFilter, boolean flushOutstream) {
        inputStream = inStream;
        outputStream = outSteam;
        filter = lineFilter;
        flush = flushOutstream;
    }
    
    /**
     * Creates a new <code>StreamRedirector</code>.
     * @param inStream the input stream to redirect
     * @param outSteam the output stream to redirect the input to.
     */
    public StreamRedirector(InputStream inStream, OutputStream outSteam) {
    	this(inStream, outSteam, false);
    }

    public StreamRedirector(InputStream inStream, OutputStream outSteam, boolean flushOutstream) {
    	this(inStream, outSteam, null, flushOutstream);
    }
    
    public void run() {
        try {
            PrintWriter writer = null;
            if (outputStream != null) {
                writer = new PrintWriter(outputStream);
            }
                
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                if (writer != null) {
                	if (filter != null) {
                		line = filter.filterLine(line);
                	}
                	if (line != null) {
                		writer.println(line);
                		if (flush) {
                			writer.flush();
                		}
                	}
                }  
            }
            if (writer != null) {
                writer.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }    
    }
}
