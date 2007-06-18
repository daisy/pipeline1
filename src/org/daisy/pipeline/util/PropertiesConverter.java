/*
 * Created on 2007 apr 8
 */
package org.daisy.pipeline.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.daisy.util.file.EFile;
import org.daisy.util.file.EFolder;

/**
 * Convert transformer messages.properties to xml
 * @author Markus Gylling
 */
public class PropertiesConverter {

	public PropertiesConverter(String path) throws IOException {
		EFolder baseDir = new EFolder(path);
		assert(baseDir.exists());
		
		Collection<File> coll = baseDir.getFiles(true, "messages(.+)?\\.properties");
		
		for (File file : coll) {			
			EFile efile = new EFile(file);
			if(efile.getAbsolutePath().contains("INSERTSELECTORHERE")) {				
				EFolder parent = efile.getParentFolder();
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
	 * @param args[0] pathspec of dir to recursively search
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {		
		PropertiesConverter pc = new PropertiesConverter(args[0]);
	}

}
