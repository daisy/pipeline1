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
 * @author Linus Ericson
 */
public class StreamRedirector extends Thread {
    
    protected InputStream inputStream;
    protected OutputStream outputStream;
    
    public StreamRedirector(InputStream a_inputStream, OutputStream a_outputSteam) {
        inputStream = a_inputStream;
        outputStream = a_outputSteam;
    }

    public void run() {
        try {
            PrintWriter _writer = null;
            if (outputStream != null) {
                _writer = new PrintWriter(outputStream);
            }
                
            InputStreamReader _isr = new InputStreamReader(inputStream);
            BufferedReader _br = new BufferedReader(_isr);
            String _line=null;
            while ( (_line = _br.readLine()) != null) {
                if (_writer != null) {
                    _writer.println(_line);
                }  
            }
            if (_writer != null) {
                _writer.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }    
    }
}
