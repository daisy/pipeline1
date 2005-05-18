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
package org.daisy.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Redirects an input stream to a output stream in a separate thread.
 * @author Linus Ericson
 */
public class StreamRedirector extends Thread {
    
    protected InputStream inputStream;
    protected OutputStream outputStream;
    
    /**
     * Creates a new <code>StreamRedirector</code>.
     * @param inStream the input stream to redirect
     * @param outSteam the output stream to redirect the input to.
     */
    public StreamRedirector(InputStream inStream, OutputStream outSteam) {
        inputStream = inStream;
        outputStream = outSteam;
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
                    writer.println(line);
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
