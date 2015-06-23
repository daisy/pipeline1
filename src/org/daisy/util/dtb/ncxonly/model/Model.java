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
package org.daisy.util.dtb.ncxonly.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;


/**
 * Simple list-based representation of a to-be NCX-only DTB.
 * <p>Use the <code>write</code> subpackage to render a Model instance to a physical DTB</p>
 * @author Markus Gylling
 */
public class Model extends LinkedList<Item> {

	MetadataList metadata = null;
		
	public Model() {
		metadata = new MetadataList();
	}
	
	public MetadataList getMetadata() {
		return metadata;		
	}
		
	public double getDurationSeconds() {
		double s = 0;
		for(Item item:this) {
			s+=getDurationSeconds(item);			
		}
		return s;
	}
	
	public double getDurationSeconds(Item item) {
		double s = 0;		
		for(AudioClip clip:item.getAudioClips()) {
			s+= clip.getDurationSeconds();
		}		
		return s;
	}
		
	/**
	 * Return true if this items semantic can be
	 * categorized as a heading
	 */
	public static boolean isHeading(Item item) {
		return item.getSemantic().name().startsWith("HEADING");		
	}

	/**
	 * Get the NCX playOrder of given item,
	 * or -1 if it could not be computed.
	 * Note - we currently assume that NCX contains only headings and pages.
	 */
	public int getPlayOrder(Item item) {
		int playOrder = 1;
		for(Item cur : this) {
			if(cur == item) return playOrder;
			if(isHeading(cur)||isPage(cur)) playOrder++;
		}				
		return -1;
	}
	
	/**
	 * Return true if this items semantic can be
	 * categorized as a page
	 */
	public static boolean isPage(Item item) {
		return item.getSemantic().name().startsWith("PAGE");		
	}
	
	/**
	 * Return true if model contains semantics that can be
	 * categorized as pages
	 */
	public static boolean hasPages(Model model) {
		for(Item item : model) {
			if (Model.isPage(item)) return true;
		}
		return false;
	}
	
	public static int getPageCount(Model model) {
		int count = 0;
		for(Item item : model) {
			if (Model.isPage(item)) count++;
		}		
		return count;
	}
	
	/**
	 * Return the depth of inparam heading
	 */
	public static int getHeadingDepth(Item item) {
		if(!isHeading(item)) 
			throw new IllegalArgumentException(item.toString());
		String last = item.getSemantic().name().substring(7);
		return Integer.parseInt(last);
	}
	
	/**
	 * Return the heading depth of inparam model 
	 */
	public static int getHeadingDepth(Model model) {
		int highest = 1;
		for(Item item : model) {
			if(Model.isHeading(item)) {
				int cur = Model.getHeadingDepth(item);
				if(highest<cur) highest = cur;
			}	
		}
		return highest;
	}
	
	/**
	 * Depending on inparams, appliy different kinds 
	 * of revaluing of each Item Value in this Model instance .
	 */
	public void revalue(Semantic semantic, Object mode) {
		if(semantic == Semantic.PAGE_NORMAL && mode instanceof Integer) {
			renumberNormalPages((Integer)mode);
			return;
		}
		throw new IllegalArgumentException();
	}

	private void renumberNormalPages(Integer start) {
		int number = start.intValue();		
		for(Item item : this) {		
			if(item.getSemantic()==Semantic.PAGE_NORMAL) {
				item.setValue(new Value(Integer.toString(number)));
				number++;
			}
		}		
	}
	
	/**
	 * Render the model as plain XML.
	 * @throws XMLStreamException 
	 */
	public void toXML(OutputStream destination) throws IOException, XMLStreamException {
		Map<String,Object> properties = null;
		XMLOutputFactory xof = null;
		XMLEventFactory xef = null;
		try{
			QName QNAME_ROOT = new QName("model");
			QName QNAME_ITEM = new QName("item");
			QName QNAME_SEMANTIC = new QName("semantic");
			QName QNAME_VALUE = new QName("value");
			QName QNAME_AUDIOCLIPS = new QName("audioclips");
			QName QNAME_CLIP = new QName("clip");
			String newline = "\n";
			String tab = "\t";
			
			properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
			xof = StAXOutputFactoryPool.getInstance().acquire(properties);
			xef = StAXEventFactoryPool.getInstance().acquire();
			
			XMLEventWriter writer = xof.createXMLEventWriter(destination);
			writer.add(xef.createStartDocument());
			writer.add(xef.createCharacters(newline));
			writer.add(xef.createStartElement(QNAME_ROOT,null,null));
			writer.add(xef.createAttribute("duration", new SmilClock(getDurationSeconds()).toString()));
			writer.add(xef.createAttribute("itemCount", Integer.toString(this.size())));			
			writer.add(xef.createCharacters(newline));
			
			SmilClock duration = new SmilClock(0);
			for(Item item : this) {
				writer.add(xef.createStartElement(QNAME_ITEM,null,null));
				writer.add(xef.createAttribute("onset", duration.toString()));	
				SmilClock itemDur = new SmilClock(getDurationSeconds(item));
				writer.add(xef.createAttribute("dur", itemDur.toString()));
				duration = new SmilClock(itemDur.millisecondsValue()+duration.millisecondsValue());
				writer.add(xef.createCharacters(newline));
				
				writer.add(xef.createCharacters(tab));
				writer.add(xef.createStartElement(QNAME_SEMANTIC,null,null));				
				writer.add(xef.createCharacters(item.getSemantic().toString()));
				writer.add(xef.createEndElement(QNAME_SEMANTIC,null));
				writer.add(xef.createCharacters(newline));
				
				writer.add(xef.createCharacters(tab));
				writer.add(xef.createStartElement(QNAME_VALUE,null,null));				
				writer.add(xef.createCharacters(item.getValue().toString()));
				writer.add(xef.createEndElement(QNAME_VALUE,null));
				writer.add(xef.createCharacters(newline));
				
				writer.add(xef.createCharacters(tab));
				writer.add(xef.createStartElement(QNAME_AUDIOCLIPS,null,null));				
				writer.add(xef.createAttribute("count", Integer.toString(item.getAudioClips().size())));
				
				writer.add(xef.createCharacters(newline));
				
				for(AudioClip clip : item.getAudioClips()) {
					writer.add(xef.createCharacters(tab));
					writer.add(xef.createCharacters(tab));
					writer.add(xef.createStartElement(QNAME_CLIP,null,null));
					writer.add(xef.createAttribute("file", clip.getFile().getName()));
					writer.add(xef.createAttribute("start", Double.toString(new SmilClock(clip.getStartSeconds()).secondsValue())));
					writer.add(xef.createAttribute("end", Double.toString(new SmilClock(clip.getEndSeconds()).secondsValue())));
					writer.add(xef.createAttribute("format", clip.getAudioFormat().toString()));
					writer.add(xef.createEndElement(QNAME_CLIP,null));
					writer.add(xef.createCharacters(newline));					
				}
				
				writer.add(xef.createCharacters(tab));
				writer.add(xef.createEndElement(QNAME_AUDIOCLIPS,null));
				writer.add(xef.createCharacters(newline));
				writer.add(xef.createEndElement(QNAME_ITEM,null));
				writer.add(xef.createCharacters(newline));
			}
			
			writer.add(xef.createEndElement(QNAME_ROOT,null));
			
			writer.flush();
			writer.close();
		}finally{
			destination.close();
			StAXOutputFactoryPool.getInstance().release(xof, properties);
			StAXEventFactoryPool.getInstance().release(xef);
		}
	}

	private static final long serialVersionUID = 6646758594847852257L;


}
