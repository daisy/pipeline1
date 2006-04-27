package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import org.daisy.util.fileset.interfaces.text.M3UFile;
import org.daisy.util.mime.MIMETypeException;

public class M3UFileImpl extends PlayListImpl implements M3UFile {

	M3UFileImpl(URI uri) throws IOException, MIMETypeException {
        super(uri,M3UFile.mimeStringConstant);          
    }
    	
	public void parse() throws IOException {		
        for(String line; (line = reader.readLine()) != null;) {
        	line=line.trim();
    		if (regex.matches(regex.FILE_MP3,line)) {
    			putUriValue(line);
    		}		
        }		
	}

	private static final long serialVersionUID = -4034017854976245341L;
}
