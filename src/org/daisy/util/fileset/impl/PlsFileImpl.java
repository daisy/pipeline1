package org.daisy.util.fileset.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.util.fileset.interfaces.text.PlsFile;
import org.daisy.util.mime.MIMETypeException;
/**
 * @author Markus Gylling
 */
public class PlsFileImpl extends PlayListImpl implements PlsFile {

	private String statedVersion = null;
	private String statedNumberOfEntries = null;
	private Map fileTitlePairs = new HashMap(); //<FileLine,TitleLine>
	
	PlsFileImpl(URI uri) throws IOException, MIMETypeException {
        super(uri,PlsFile.mimeStringConstant);          
    }
    	
	public void parse() throws IOException {	
		//[playlist]
    	//File=bleh.mp3
    	//Title=title
		//Length=3948
		//NumberOfEntries=1
		//Version=2

		for(String line; (line = reader.readLine()) != null;) {
			String currentFileLine = null;
			line=line.trim();
        	if(line.startsWith("File=")) {
        		line=line.replace("File=","");
        		putUriValue(line);
        		currentFileLine = line;
        	}else if (line.startsWith("Title=")) {
        		line=line.replace("Title=","");
        		if(currentFileLine!=null) {
        			fileTitlePairs.put(currentFileLine,line);
        		}
        	}else if (line.startsWith("Version=")) {
        		line=line.replace("Version=","");
        		statedVersion=line;
        	}else if (line.startsWith("NumberOfEntries=")) {
        		line=line.replace("NumberOfEntries=","");
        		statedNumberOfEntries=line;
        	}        	
        }		
	}

	public int getStatedNumberOfEntries() throws NumberFormatException  {
		return Integer.parseInt(statedNumberOfEntries);
	}

	public int getStatedVersion() throws NumberFormatException {
		return Integer.parseInt(statedVersion);
	}

	public String getHeadingForFile(File mp3File) {
		Iterator i = fileTitlePairs.keySet().iterator();
		while(i.hasNext()) {
			String fileURL = (String)i.next();
			if(fileURL.indexOf(mp3File.getName())>=0) {
				return (String)fileTitlePairs.get(fileURL);
			}
		}
		return null;
	}

	private static final long serialVersionUID = 380698236131126742L;
	
}
