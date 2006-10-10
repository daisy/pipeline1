/**
 *DAISY Multi-Format Converter, or DAISY Pipeline Graphical User Interface.
Copyright (C) 2006 DAISY Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.daisy.dmfc.gui.scripttree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * Labels for each item in the tree. jFace
 * @author Laurie
 *
 */

public class ScriptTreeLabelProvider implements ILabelProvider{
	
	private List listeners;
	private HashMap hmScriptHandlers;
	
	/**
	 * There are no images, but we would add them here.
	 *
	 */
	public ScriptTreeLabelProvider(HashMap hm){
		listeners = new ArrayList();
		this.hmScriptHandlers=hm;
	}
	
	/**
	 * if there are images, access them here
	 * for example, an image for file, and image for directory
	 */
	public Image getImage(Object obj){
		return null;
	}
	
	
	/**
	 * There is a hierarchy in the file structure that organises the
	 * transformers by type.  None of these "directory names" have associated
	 * script handler objects and must be viewed as a directory name.
	 * 
	 * However, each script file does have an associated script handler and
	 * a name.  This is shown in the tree view for each script handler.
	 * 
	 * The most important method in this class, how each item in the tree is viewed.
	 */
	public String getText(Object arg){
		String text = ((File) arg).getPath();
		String scriptName="";
		//if name is blank, get the path
		
		if (text.length()==0){
			text=((File)arg).getPath();
		}
		
		ScriptHandler sh=(ScriptHandler)hmScriptHandlers.get(text);
		if (sh!=null){
			scriptName=sh.getName();
			return scriptName;
		}
		
		else{
			return ((File) arg).getName();
		}
	}
	
	/**
	 * if there are images, dispose of them here.
	 */
	public void dispose(){
		//nothing to dispose
	}
	
	public void addListener(ILabelProviderListener ilpl){
		listeners.add(ilpl);
	}
	
	public void removeListener(ILabelProviderListener ilpl){
		listeners.remove(ilpl);
	}
	
	public boolean isLabelProperty(Object obj, String str){
		return false;
	}
	
	
}

