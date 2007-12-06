package org.daisy.util.dtb.ncxonly.model.write.smil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.Version;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.dtb.ncxonly.model.AudioClip;
import org.daisy.util.dtb.ncxonly.model.Item;
import org.daisy.util.dtb.ncxonly.model.Semantic;
import org.daisy.util.dtb.ncxonly.model.AudioClip.Nature;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SmilClock;

/**
 * An object representation of what is about to rendered as a SMIL file to disk.
 * @author Markus Gylling
 */
public class SMILFile {
	private String mFileName;
	private Map<String, Item> mContents; //ID, Item
	//private Set<Semantic> mSkippables;
	
	private final String SMIL2_DOCTYPE = "<!DOCTYPE smil PUBLIC \"-//NISO//DTD dtbsmil 2005-1//EN\" \"http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd\">"; 
	private final String LINEBREAK = "\n";
	private final String TAB = "\t";
	private final QName qRoot = new QName(Namespaces.SMIL_20_NS_URI,"smil");
	private final QName qHead = new QName(Namespaces.SMIL_20_NS_URI,"head");
	private final QName qMeta = new QName(Namespaces.SMIL_20_NS_URI,"meta");
	private final QName qCustomAttributes = new QName(Namespaces.SMIL_20_NS_URI,"customAttributes");
	private final QName qCustomTest = new QName(Namespaces.SMIL_20_NS_URI,"customTest");
	private final QName qBody = new QName(Namespaces.SMIL_20_NS_URI,"body");
	private final QName qSeq = new QName(Namespaces.SMIL_20_NS_URI,"seq");
	private final QName qPar = new QName(Namespaces.SMIL_20_NS_URI,"par");
	private final QName qAudio = new QName(Namespaces.SMIL_20_NS_URI,"audio");

	SMILFile(String fileName,
			List<Item> contents,
			IDGenerator idGenerator) {
		mFileName = fileName;
		mContents = new LinkedHashMap<String, Item>();
		//mSkippables = new HashSet<Semantic>(); 
		
		//contents = mergeClips(contents);
		
		for (Item item : contents) {
			mContents.put(idGenerator.generateId(), item);
		}
	}

	/**
	 * Merge AudioClips whose .shouldMergeWithPredecessor is true.
	 * Note: this method assumes that all AudioClips within this SMILFile
	 * are clips backed by one and the same file.
	 */
	public void mergeClips() {
		for(Item item : mContents.values()) {
			AudioClip prevClip = null;
			for(AudioClip clip : item.getAudioClips()) {			
				if(clip.getNature() == Nature.TRANSIENT) {
					if(prevClip != null) {
						if(prevClip.getFile() != clip.getFile()) {
							throw new IllegalStateException("cannot merge clips that are not from the same file");
						}						
						//change prev clip, and then remove current
						prevClip.setEndSeconds(clip.getEndSeconds());						
						item.getAudioClips().remove(clip);	
						//System.err.println("SMILFile#mergeClips: what happens to the loop here? concurrent mod"); 
					}										
				}
				prevClip = clip;
			}						
		}						
	}

	/**
	 * Get a sequentially ordered list of the audio clips
	 * within this to-be SMIL file.
	 */
	List<AudioClip> getAudioClips() {
		List<AudioClip> clips = new LinkedList<AudioClip>();
		for (Item item : mContents.values()) {
			clips.addAll(item.getAudioClips());
		}
		return clips;
	}

	/**
	 * Get a sequentially ordered list of the audio files
	 * referenced within this to-be SMIL file.
	 */
	public List<File> getAudioFiles() {
		List<File> files = new LinkedList<File>();
		for (Item item : mContents.values()) {
			List<AudioClip> clips = item.getAudioClips();
			for (AudioClip clip : clips) {
				if (!files.contains(clip.getFile()))
					files.add(clip.getFile());
			}
		}
		return files;
	}

	public Iterator<Item> iterator() {
		return mContents.values().iterator();
	}

	boolean containsItem(Item item) {
		for (Item value : mContents.values()) {
			if (value == item)
				return true;
		}
		return false;
	}

	boolean containsID(String ID) {
		for (String id : mContents.keySet()) {
			if (id == ID)
				return true;
		}
		return false;
	}

	public String getFileName() {
		return mFileName;
	}

	String getID(Item item) {
		for (String id : mContents.keySet()) {
			Item value = mContents.get(id);
			if (value == item)
				return id;
		}
		return null;
	}

	double getDurationSeconds() {
		double s = 0;
		for (Item item : mContents.values()) {
			s += item.getDurationSeconds();
		}
		return s;
	}

	void render(File dest,
			SmilClock totalElapsedTime,
			MetadataList metadata,
			XMLOutputFactory xof,
			XMLEventFactory xef)
			throws FileNotFoundException,
			XMLStreamException {

		XMLEvent nl = xef.createCharacters(LINEBREAK);
		XMLEvent tab = xef.createCharacters(TAB);
		XMLEvent tab2 = xef.createCharacters(TAB + TAB);
		XMLEvent tab3 = xef.createCharacters(TAB + TAB + TAB);

		XMLEventWriter writer = xof.createXMLEventWriter(new FileOutputStream(dest));

		writer.add(xef.createStartDocument());
		writer.add(nl);
		writer.add(xef.createDTD(SMIL2_DOCTYPE));
		writer.add(nl);
		writer.add(xef.createStartElement(qRoot, null, null));
		writer.add(nl);
		writer.add(xef.createStartElement(qHead, null, null));
		writer.add(nl);
		//metadata
		Map<String, String> values = new HashMap<String, String>();
		values.put("dtb:uid", metadata.get(new QName(Namespaces.DUBLIN_CORE_NS_URI,"Identifier","dc")).getValue());
		values.put("dtb:generator", "Pipeline " + Version.getVersion());
		values.put("dtb:totalElapsedTime", totalElapsedTime.toString());
		for (String key : values.keySet()) {
			writer.add(tab);
			writer.add(xef.createStartElement(qMeta, null, null));
			writer.add(xef.createAttribute("name", key));
			writer.add(xef.createAttribute("content", values.get(key)));
			writer.add(xef.createEndElement(qMeta, null));
			writer.add(nl);
		}		
		//end metadata
		
		//customAttributes
		//we look for pagenum items whose first phrase is shouldPersist
		Set<Semantic> skippables = getSkippableTypes(mContents.values()); 
		if(!skippables.isEmpty()) {
			writer.add(tab);
			writer.add(xef.createStartElement(qCustomAttributes, null, null));
			writer.add(nl);
			for(Semantic sem : skippables) {
				writer.add(tab2);
				writer.add(xef.createStartElement(qCustomTest, null, null));
				writer.add(xef.createAttribute("defaultState", "false"));
				writer.add(xef.createAttribute("override", "visible"));
				writer.add(xef.createAttribute("id", sem.toString()));				
				writer.add(xef.createEndElement(qCustomTest, null));
				writer.add(nl);
			}		
			writer.add(tab);
			writer.add(xef.createEndElement(qCustomAttributes, null));
		}
		//end customAttributes
		
		writer.add(nl);
		writer.add(xef.createEndElement(qHead, null));
		writer.add(nl);
		writer.add(xef.createStartElement(qBody, null, null));
		writer.add(nl);
		writer.add(tab);
		writer.add(xef.createStartElement(qSeq, null, null));
		writer.add(xef.createAttribute("id", "mseq"));
		writer.add(xef.createAttribute("dur", new SmilClock(this.getDurationSeconds()).toString()));
		writer.add(nl);

		int pagenumCount = 1;
		for (Iterator<String> it = this.mContents.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Item item = mContents.get(key);
			int audioElemCount = 1;
						
			for (AudioClip clip : item.getAudioClips()) {
				
				boolean shouldWrap = false;				
				if(clip.getNature() == Nature.NONTRANSIENT && item.getSemantic().name().startsWith("PAGE")) {
					shouldWrap = true;
					writer.add(tab2);
					writer.add(xef.createStartElement(qSeq, null, null));
					writer.add(xef.createAttribute("customTest", "pagenum"));
					writer.add(xef.createAttribute("id", "pagenum_" + pagenumCount));
					writer.add(nl);
					pagenumCount++;
				}
				
				if(shouldWrap) {
					writer.add(tab3);
				}else{
					writer.add(tab2);
				}
				
				writer.add(xef.createStartElement(qAudio, null, null));
				String id;
				if (audioElemCount > 1) {
					id = key + "_ph" + audioElemCount; //first phrase is the items key, rest is phrases
				} else {
					id = key;					
				}
				writer.add(xef.createAttribute("id", id));
				writer.add(xef.createAttribute("clipBegin", new SmilClock(clip.getStartSeconds()).toString(SmilClock.TIMECOUNT_SEC)));
				writer.add(xef.createAttribute("clipEnd", new SmilClock(clip.getEndSeconds()).toString(SmilClock.TIMECOUNT_SEC)));
				writer.add(xef.createAttribute("src", clip.getFile().getName()));					
				writer.add(xef.createEndElement(qAudio, null));
				writer.add(nl);
				
				
				
				if(shouldWrap) {
					writer.add(tab2);
					writer.add(xef.createEndElement(qSeq, null));
					writer.add(nl);
				}
												
				audioElemCount++;
			}
		}

		writer.add(tab);
		writer.add(xef.createEndElement(qSeq, null));
		writer.add(nl);
		writer.add(xef.createEndElement(qBody, null));
		writer.add(nl);
		writer.add(xef.createEndElement(qRoot, null));
		writer.add(xef.createEndDocument());
		writer.flush();
		writer.close();
	}

	
	
	private Set<Semantic> getSkippableTypes(Collection<Item> values) {
		Set<Semantic> ret = new HashSet<Semantic>();
		for(Item item : values) {
			for(AudioClip clip : item.getAudioClips()) {				
				if(clip.getNature() == Nature.NONTRANSIENT 
						&& item.getSemantic().name().startsWith("PAGE")) {
					//we dont differ between page types in skippability
					//so just set one of the PAGE_ types.
					ret.add(Semantic.PAGE_NORMAL);										
				}				
			}
		}		
		return ret;		
	}

	/**
	 * Get a list of occurrences of skippable semantic types in this SMILFile,
	 * or an empty Set if none occurred.
	 */
	public Set<Semantic> getSkippableTypes() {
		return getSkippableTypes(mContents.values());
	}

}
