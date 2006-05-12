package org.daisy.dmfc.gui.scripttree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ScriptTreeLabelProvider implements ILabelProvider{
	
	private List listeners;
	
	/**
	 * There are no images, but we would add them here.
	 *
	 */
	public ScriptTreeLabelProvider(){
		listeners = new ArrayList();
	}
	
	/**
	 * if there are images, access them here
	 * for example, an image for file, and image for directory
	 */
	public Image getImage(Object obj){
		return null;
	}
	
	
	/**
	 * most important, how this is viewed
	 */
	public String getText(Object arg){
		String text = ((File) arg).getName();
		//if name is blank, get the path
		
		if (text.length()==0){
			text=((File)arg).getPath();
		}
		return text;
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

