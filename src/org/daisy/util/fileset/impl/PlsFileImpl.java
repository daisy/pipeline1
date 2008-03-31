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

package org.daisy.util.fileset.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.util.fileset.PlsFile;
/**
 * @author Markus Gylling
 */
final class PlsFileImpl extends PlayListImpl implements PlsFile {

	private String statedVersion = null;
	private String statedNumberOfEntries = null;
	private Map fileTitlePairs = new HashMap(); //<FileLine,TitleLine>
	
	PlsFileImpl(URI uri) throws IOException {
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
