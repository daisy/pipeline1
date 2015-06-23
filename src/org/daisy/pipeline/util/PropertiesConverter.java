/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
 */package org.daisy.pipeline.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.daisy.util.file.EFile;
import org.daisy.util.file.Directory;

/**
 * Convert transformer messages.properties to xml
 * @author Markus Gylling
 */
public class PropertiesConverter {

	public PropertiesConverter(String path) throws IOException {
		Directory baseDir = new Directory(path);
		assert(baseDir.exists());
		
		Collection<File> coll = baseDir.getFiles(true, "messages(.+)?\\.properties");
		
		for (File file : coll) {			
			EFile efile = new EFile(file);
			if(efile.getAbsolutePath().contains("INSERTSELECTORHERE")) {				
				Directory parent = efile.getParentFolder();
				System.err.println("Converting " + efile.getAbsolutePath());
				Properties props = new Properties();
				props.load(efile.asInputStream());			
				String newName = "converted.messages";
				File output = new File(parent, newName);
				props.storeToXML(new FileOutputStream(output), "local messages for " +  parent.getName());			
				//efile.delete();
			}
		}
	}

	/**
	 * @param args first argument contains pathspec of dir to recursively search
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {		
		new PropertiesConverter(args[0]);
	}

}
