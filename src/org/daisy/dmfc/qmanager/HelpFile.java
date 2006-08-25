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

package org.daisy.dmfc.qmanager;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.daisy.dmfc.core.transformer.TransformerHandler;

/**
 * For the help files
 * doc.html is created for each transformer and lives
 * in the directory for that transformer
 * 
 * @author Laurie Sherve
 *
 */

public class HelpFile {

	private Collection helpFileCollection;
	private TransformerHandler th;
	
	public HelpFile(TransformerHandler th){
		this.th=th;
		this.helpFileCollection=th.getDocumentation();
		
	}
	
	/**
	 * get method - returns a collection of doc.html files for all transformer.
	 * @return Collection
	 */
	public Collection getHelpFileCollection(){
		return this.helpFileCollection;
	}
	
	/**
	 * Returns a file
	 * Warning, file may be null if help file does not exist for Transformer
	 * Check if calling this method.
	 * @param parentMatch (the transformer name)
	 * @return File
	 */
	public File getHelpFileForTransformer(String parentMatch){
		Iterator it = this.helpFileCollection.iterator();
		String parent="";
		File helpFile=null;
		while (it.hasNext()){
			File file = (File)it.next();
			if(parentMatch.equalsIgnoreCase(file.getParent()));
				parent = parentMatch;
				helpFile = new File (th.getTransformerDir() + File.pathSeparator + parent + File.pathSeparator);
		}
		return helpFile;
	}
	
}
