package org.daisy.util.dtb.ncxonly.model.write.ncx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.Version;
import org.daisy.util.dtb.ncxonly.model.AudioClip;
import org.daisy.util.dtb.ncxonly.model.Item;
import org.daisy.util.dtb.ncxonly.model.Model;
import org.daisy.util.dtb.ncxonly.model.Semantic;
import org.daisy.util.dtb.ncxonly.model.AudioClip.Nature;
import org.daisy.util.dtb.ncxonly.model.write.smil.SMILBuilder;
import org.daisy.util.file.EFolder;
import org.daisy.util.xml.IDGenerator;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.SmilClock;

/**
 *
 * @author Markus Gylling
 */
public class NCXBuilder {
	private final String NCX_DOCTYPE = "<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">"; 
	private final String LINEBREAK = "\n";
	private final String TAB = "\t";
	private XMLEvent nl = null;
	private XMLEvent tab = null;
	private XMLEvent tab2 = null;
	private XMLEvent tab3 = null;
	
	private final QName qRoot = new QName(Namespaces.Z2005_NCX_NS_URI,"ncx");
	private final QName qHead = new QName(Namespaces.Z2005_NCX_NS_URI,"head");	
	private final QName qMeta = new QName(Namespaces.Z2005_NCX_NS_URI,"meta");
	private final QName qSmilCustomTest = new QName(Namespaces.Z2005_NCX_NS_URI,"smilCustomTest");		
	private final QName qDocTitle = new QName(Namespaces.Z2005_NCX_NS_URI,"docTitle");
	private final QName qDocAuthor = new QName(Namespaces.Z2005_NCX_NS_URI,"docAuthor");	
	private final QName qText = new QName(Namespaces.Z2005_NCX_NS_URI,"text");
	private final QName qAudio = new QName(Namespaces.Z2005_NCX_NS_URI,"audio");	
	private final QName qNavMap = new QName(Namespaces.Z2005_NCX_NS_URI,"navMap");
	private final QName qNavPoint = new QName(Namespaces.Z2005_NCX_NS_URI,"navPoint");
	private final QName qNavLabel = new QName(Namespaces.Z2005_NCX_NS_URI,"navLabel");
	private final QName qNavList = new QName(Namespaces.Z2005_NCX_NS_URI,"navList");
	private final QName qNavTarget = new QName(Namespaces.Z2005_NCX_NS_URI,"navTarget");
	private final QName qPageList = new QName(Namespaces.Z2005_NCX_NS_URI,"pageList");
	private final QName qPageTarget = new QName(Namespaces.Z2005_NCX_NS_URI,"pageTarget");
	private final QName qContent = new QName(Namespaces.Z2005_NCX_NS_URI,"content");
		
	private SMILBuilder mSmilBuilder = null;
	private Model mModel = null;
	
	/**
	 * Constructor.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public NCXBuilder(Model model, SMILBuilder smilBuilder, EFolder dest, XMLOutputFactory xof, XMLEventFactory xef) throws FileNotFoundException, XMLStreamException {
		mSmilBuilder = smilBuilder;
		mModel = model;
		
		File outputNCX = new File(dest,getName());
		nl = xef.createCharacters(LINEBREAK);
		tab = xef.createCharacters(TAB);
		tab2 = xef.createCharacters(TAB+TAB);
		tab3 = xef.createCharacters(TAB+TAB+TAB);	
		IDGenerator idg = new IDGenerator("ncx_");
		
		XMLEventWriter writer = xof.createXMLEventWriter(new FileOutputStream(outputNCX));		
		writer.add(xef.createStartDocument()); writer.add(nl);			
		writer.add(xef.createDTD(NCX_DOCTYPE)); writer.add(nl);						
		writer.add(xef.createStartElement(qRoot, null, null)); 		
		writer.add(xef.createAttribute("version","2005-1"));
		writer.add(xef.createAttribute("xml:lang",mModel.getMetadata().get(new QName(Namespaces.DUBLIN_CORE_NS_URI,"Language","dc")).getValue()));
		writer.add(nl);
		writer.add(xef.createStartElement(qHead, null, null)); 				
		createMetaElements(writer,model,xef);						
		writer.add(nl);
		writer.add(xef.createEndElement(qHead, null)); 
		writer.add(nl);

		/*
		 * Create docAuthor and docTitle.
		 * We assume phrase 1 is title, and phrase 2 is author (alt: dont add audio element)
		 */		
		writer.add(xef.createStartElement(qDocTitle, null,null)); 
		writer.add(nl);writer.add(tab);
		createTextElement(writer,xef, model.getMetadata().get(new QName(Namespaces.DUBLIN_CORE_NS_URI,"Title","dc")).getValue());
		writer.add(nl);
		AudioClip first = model.getFirst().getAudioClips().get(0);
		writer.add(tab);
		createAudioElement(xef, writer,first);
		writer.add(nl);
		writer.add(xef.createEndElement(qDocTitle, null)); writer.add(nl);
		
		writer.add(xef.createStartElement(qDocAuthor, null,null)); 
		writer.add(nl);writer.add(tab);
		createTextElement(writer,xef, model.getMetadata().get(new QName(Namespaces.DUBLIN_CORE_NS_URI,"Creator","dc")).getValue());
		writer.add(nl);
		AudioClip second;
		try{
			second = model.getFirst().getAudioClips().get(1);
		}catch (IndexOutOfBoundsException e) {
			//TODO warn, or skip audio element
			second = model.getFirst().getAudioClips().get(0);
		}	
		writer.add(tab);
		createAudioElement(xef, writer,second);
		writer.add(nl);
		writer.add(xef.createEndElement(qDocAuthor, null)); writer.add(nl);
		
		createNavMap(xef,idg,writer);
				
		createPageList(writer,xef,idg);
			
		writer.add(nl);
		writer.add(xef.createEndElement(qRoot, null));			
		writer.add(xef.createEndDocument());
		writer.flush();
		writer.close();
	}

	private void createPageList(XMLEventWriter writer, XMLEventFactory xef, IDGenerator idg) throws XMLStreamException {		
		/*
		 * Make sure there are pages
		 */		
		if(!Model.hasPages(mModel)) return;
				
		writer.add(xef.createStartElement(qPageList, null, null));
		
		for(Item item : mModel){
			if(Model.isPage(item)) {
				writer.add(nl);
				writer.add(tab);
				writer.add(xef.createStartElement(qPageTarget, null, null));	
					writer.add(xef.createAttribute("id",idg.generateId()));
					writer.add(xef.createAttribute("class",item.getSemantic().toString()));
					writer.add(xef.createAttribute("playOrder",Integer.toString(mModel.indexOf(item)+1)));
					String type = getPageType(item);
					writer.add(xef.createAttribute("type",type));
					if(type.equals("normal")) {
						writer.add(xef.createAttribute("value",item.getValue().toString()));
					}				
					writer.add(nl);
					writer.add(tab2);
					createNavLabelElement(item, xef, writer, 2);
					writer.add(nl);
					writer.add(tab2);
					createContentElement(item, xef, writer);				
				writer.add(tab);	
				writer.add(xef.createEndElement(qPageTarget, null));								
			}
		}		
		writer.add(nl);
		writer.add(xef.createEndElement(qPageList, null));			
	}
	
	private String getPageType(Item item) {
		Semantic sem = item.getSemantic();
		if(sem==Semantic.PAGE_FRONT) {
			return "front";
		}else if(sem==Semantic.PAGE_NORMAL) {
			return "normal";
		}else if(sem==Semantic.PAGE_SPECIAL) {
			return "special";
		}
		throw new IllegalArgumentException();
	}

	private void createNavMap(XMLEventFactory xef,IDGenerator idg,XMLEventWriter writer) throws XMLStreamException {
		Stack<Item> openNavPoints = new Stack<Item>();				
		List<Item> headingItems = new LinkedList<Item>();
		
		/*
		 * Filter out items we will not include include in map.
		 * We do this for straightforward lookahead.
		 */		
		for(Item item : mModel){
			if(Model.isHeading(item)) {
				headingItems.add(item);
			}
		}
		
		/*
		 * And build the List.
		 */
		writer.add(xef.createStartElement(qNavMap, null, null));
		writer.add(nl);
		for (int i = 0; i < headingItems.size(); i++) {
			Item item = headingItems.get(i);										
			createNavPoint(item,xef,idg,writer);
			if(i<headingItems.size()-1) { 
				Item nextItem = headingItems.get(i+1);			
				int curHeadingDepth = Model.getHeadingDepth(item);	
				int nextHeadingDepth = Model.getHeadingDepth(nextItem);
				if(nextHeadingDepth>curHeadingDepth) {
					//dont close current
					openNavPoints.push(item);
				}else if(nextHeadingDepth==curHeadingDepth) {
					openNavPoints.push(item);
					closeNavPoint(writer,openNavPoints,xef);	
					writer.add(nl);	
				}else{
					//nextHeadingDepth<curHeadingDepth
					openNavPoints.push(item);
					while(!openNavPoints.empty()) {
						Item closed = closeNavPoint(writer,openNavPoints,xef);
						writer.add(nl);	
						int closedDepth = Model.getHeadingDepth(closed);
						if(closedDepth<=nextHeadingDepth) break;
					}					
				}
			}else{
				//last navpoint				
				openNavPoints.push(item);
			}						
		}		
		
		//close any still open navpoints
		while(!openNavPoints.empty()) {
			closeNavPoint(writer,openNavPoints,xef);
			writer.add(nl);
		}
		
		writer.add(nl);
		writer.add(xef.createEndElement(qNavMap, null));		
		writer.add(nl);
		
	}
	
	/**
	 * Close a navPoint, return item popped if the operation is performed, null if stack is empty.
	 * @throws XMLStreamException 
	 */
	private Item closeNavPoint(XMLEventWriter writer, Stack<Item> stack, XMLEventFactory xef) throws XMLStreamException {
		if(!stack.empty()) {
			Item item = stack.pop();							
			int depth = Model.getHeadingDepth(item);			
			for (int i = 0; i < depth; i++) {
				writer.add(tab);	
			}		
			//System.err.println("NAVCLOSE " + item.getSemantic().toString());
			writer.add(xef.createEndElement(qNavPoint, null));			
			return item;
		}
		return null;					
	}
	
	/**
	 * Create a navPoint, excluding the navPoint close tag.
	 * @throws XMLStreamException 
	 */
	private void createNavPoint(Item item,XMLEventFactory xef,IDGenerator idg,XMLEventWriter writer) throws XMLStreamException {		
		int depth = Model.getHeadingDepth(item);			
		for (int i = 0; i < depth; i++) {
			writer.add(tab);	
		}						
		//System.err.println("NAVOPEN " + item.getSemantic().toString());
		writer.add(xef.createStartElement(qNavPoint, null, null));
			writer.add(xef.createAttribute("id",idg.generateId()));
			writer.add(xef.createAttribute("class",item.getSemantic().toString()));
			writer.add(xef.createAttribute("playOrder",Integer.toString(mModel.indexOf(item)+1)));		
		writer.add(nl);	
		depth++;
		for (int i = 0; i < depth; i++) {
			writer.add(tab);	
		}	
		
		depth = createNavLabelElement(item, xef, writer, depth);
		
		writer.add(nl);
		
		for (int i = 0; i < depth; i++) {
			writer.add(tab);	
		}		
		createContentElement(item, xef, writer);
	}

	private int createNavLabelElement(Item item, XMLEventFactory xef, XMLEventWriter writer, int indent) throws XMLStreamException {
		writer.add(xef.createStartElement(qNavLabel, null, null));
			writer.add(nl);
			indent++;
			for (int i = 0; i < indent; i++) {
				writer.add(tab);	
			}
			createTextElement(writer,xef,item.getValue().toString());
						
			writer.add(nl);
			for (int i = 0; i < indent; i++) {
				writer.add(tab);	
			}
			//we assume the first phrase of the item is the announcement
			AudioClip clip = item.getAudioClips().get(0);
			createAudioElement(xef, writer, clip);
			
			writer.add(nl);
			indent--;
			for (int i = 0; i < indent; i++) {
				writer.add(tab);	
			}	
		writer.add(xef.createEndElement(qNavLabel, null));
		return indent;
	}

	private void createContentElement(Item item, XMLEventFactory xef, XMLEventWriter writer) throws XMLStreamException {
		writer.add(xef.createStartElement(qContent, null, null));
			writer.add(xef.createAttribute("src",mSmilBuilder.getURI(item)));
		writer.add(xef.createEndElement(qContent, null));
		writer.add(nl);
	}
	
	private void createMetaElements(XMLEventWriter writer, Model model, XMLEventFactory xef) throws XMLStreamException {
		QName dcId = new QName(Namespaces.DUBLIN_CORE_NS_URI,"Identifier","dc");
		writeMetaElement(xef,writer,"dtb:uid",model.getMetadata().get(dcId).getValue());		
		writeMetaElement(xef,writer,"dtb:depth",Integer.toString(Model.getHeadingDepth(model)));
		writeMetaElement(xef,writer,"dtb:generator","Pipeline " + Version.getVersion());
		writeMetaElement(xef,writer,"dtb:totalPageCount",Integer.toString(Model.getPageCount(model)));
		Item item = getLastPage(model);
		String val = "0";
		if(item!=null) {
			val=item.getValue().toString();
		}	
		writeMetaElement(xef,writer,"dtb:maxPageNumber",val);		
		//add customTests
		Set<Semantic> added = new HashSet<Semantic>();
		for(Item itm : model) {
			if(itm.getAudioClips().get(0).getNature() == Nature.NONTRANSIENT) {
				if(! added.contains(item.getSemantic())) {
					writer.add(nl);	
					writer.add(tab);
					writer.add(xef.createStartElement(qSmilCustomTest, null, null));
					writer.add(xef.createAttribute("bookStruct", getBookStruct(item.getSemantic())));
					writer.add(xef.createAttribute("defaultState", "false"));
					writer.add(xef.createAttribute("id", item.getSemantic().toString()));
					writer.add(xef.createAttribute("override", "visible"));				
					writer.add(xef.createEndElement(qSmilCustomTest, null));
					added.add(item.getSemantic());
				}
			}
		}
	}

	private Item getLastPage(Model model) {
		Item last = null;
		for(Item item : model) {
			if(Model.isPage(item)) {
				last = item;
			}
		}
		return last;
	}

	private void writeMetaElement(XMLEventFactory xef, XMLEventWriter writer, String name, String content) throws XMLStreamException {
		writer.add(nl);
		writer.add(tab);
		writer.add(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "meta"));	
		writer.add(xef.createAttribute("name", name));
		writer.add(xef.createAttribute("content", content));
		writer.add(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "meta"));
	}
	
	private void createTextElement(XMLEventWriter writer,XMLEventFactory xef, String string) throws XMLStreamException {
		writer.add(xef.createStartElement(qText, null, null));
		writer.add(xef.createCharacters(string));
		writer.add(xef.createEndElement(qText, null));		
	}
	
	private void createAudioElement(XMLEventFactory xef,XMLEventWriter writer,AudioClip clip) throws XMLStreamException {
		writer.add(xef.createStartElement(qAudio, null,null)); 
		writer.add(xef.createAttribute("clipBegin",new SmilClock(clip.getStartSeconds()).toString()));
		writer.add(xef.createAttribute("clipEnd",new SmilClock(clip.getEndSeconds()).toString()));
		writer.add(xef.createAttribute("src",clip.getFile().getName()));
		writer.add(xef.createEndElement(qAudio, null));
	}
	
	public String getName() {
		return "navigation.ncx";
	}
	
	private String getBookStruct(Semantic sem) {
		if(sem.name().startsWith("PAGE")) {
			return "PAGE_NUMBER";
		}
		throw new IllegalArgumentException(sem.toString());
	}
	
}
