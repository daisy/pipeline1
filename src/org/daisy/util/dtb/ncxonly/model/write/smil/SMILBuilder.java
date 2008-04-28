/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
 */
package org.daisy.util.dtb.ncxonly.model.write.smil;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.util.dtb.ncxonly.model.Item;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.file.Directory;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.SmilClock;

/**
 * Build a representation of all to-be SMIL files.
 * @author Markus Gylling
 */
public class SMILBuilder {	
	private List<SMILFile> mSpine = null;
	Model mModel = null;
		
	public SMILBuilder(Model model) {
		mSpine = new LinkedList<SMILFile>();
		mModel = model;
		IDGenerator idGenerator = new IDGenerator("smil");
		
		LinkedList<Item> subList = new LinkedList<Item>();
		subList.add(model.getFirst());
		int count = 0;
		for (int i = 1; i < model.size(); i++) {
			Item item = model.get(i);
			if(Model.isHeading(item)) {
				mSpine.add(new SMILFile(getFileName(++count),subList,idGenerator));
				subList.clear();
			}
			subList.add(item);			
		}
		mSpine.add(new SMILFile(getFileName(++count),subList,idGenerator));
	}
	
	private String getFileName(int i) {
		return "smil_" + i +".smil";
	}
			
	/**
	 * Get the SMIL spine
	 */
	public List<SMILFile> getSpine() {
		return mSpine;
	}
	
	public double getDurationSeconds() {
		double s = 0;
		for (SMILFile sf : mSpine) {
			s+= sf.getDurationSeconds();
		}
		return s;
	}
	
	public SmilClock getDurationClock() {
		SmilClock totalElapsedClock = new SmilClock(0);		
		for(SMILFile smil : mSpine) {			
			totalElapsedClock = new SmilClock(totalElapsedClock.millisecondsValue() + (long)(smil.getDurationSeconds()*1000));
		}	
		return totalElapsedClock;
	}
	
	/**
	 * Get a URI, excluding path but including filename and fragment, that
	 * will represent the incoming item once the SMIL is rendered
	 * to disk.
	 */	
	public String getURI(Item item) {
		StringBuilder sb = new StringBuilder();
		for(SMILFile sf: mSpine) {
			if(sf.containsItem(item)) {
				sb.append(sf.getFileName());
				sb.append('#');
				sb.append(sf.getID(item));
				return sb.toString();
			}
		}
		throw new IllegalArgumentException();		
	}
	
	/**
	 * Merge AudioClips whose .shouldMergeWithPredecessor is true.
	 * Note: this method assumes that all AudioClips within each SMILFile
	 * are clips backed by one and the same file (ie run WAVBuilder first), 
	 * else an IllegalStateException is thrown.
	 */
	public void mergeClips() {
		for(SMILFile sf : mSpine) {
			sf.mergeClips();
		}
	}
	
	/**
	 * Render all SMIL files to disk.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void render(Directory destination, XMLEventFactory xef, XMLOutputFactory xof) throws FileNotFoundException, XMLStreamException {
		SmilClock totalElapsedClock = new SmilClock(0);		
		for(SMILFile smil : mSpine) {			
			File dest = new File(destination,smil.getFileName());			
			smil.render(dest,totalElapsedClock,mModel.getMetadata(),xof,xef);			
			totalElapsedClock = new SmilClock(totalElapsedClock.millisecondsValue() + (long)(smil.getDurationSeconds()*1000));
		}
	}

	public boolean hasSkippableItems() {
		for(SMILFile smil : mSpine) {
			if(!smil.getSkippableTypes().isEmpty())
			return true;
		}
		return false;
	}
	
}
