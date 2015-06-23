package se_tpb_aligner.subtree.impl;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.peek.PeekResult;

import se_tpb_aligner.subtree.DefaultWellFormerImpl;
import se_tpb_aligner.subtree.DivisionStrategy;
import se_tpb_aligner.subtree.SubTree;
import se_tpb_aligner.subtree.SubTreeHandler;
import se_tpb_aligner.util.XMLSource;

/**
 * A subtreehandler that knows how to split a DTBook document in subtrees based on pagenum elements.
 * <p>All events occuring before the first pagenum element are assembled in one subtree.</p>
 * <p>All following subtrees will begin with a pagenum element, and end with whatever event comes before the next pagenum element.</p>
 * <p>The final subtree will begin at the last pagenum element, and end at the last character of the document.</p>
 * 
 * @author Markus Gylling
 */
public class DtbookPageSubTreeHandler extends SubTreeHandler{
	
	private QName dtbookPagenum = null;
	
	public DtbookPageSubTreeHandler(XMLSource doc, DivisionStrategy divider) {
		super(doc,divider);			
	}

	public DtbookPageSubTreeHandler() {
		super();		
	}
	
	@Override	
	public void read(XMLEventReader reader) throws XMLStreamException {
		dtbookPagenum = new QName(DTBOOK_NSURI, "pagenum");
				
		List<XMLEvent> currentList = new LinkedList<XMLEvent>();
		while(reader.hasNext()) {
			currentList.add(reader.nextEvent());			
			XMLEvent peek = reader.peek();
			if(peek!=null && peek.isStartElement() && peek.asStartElement().getName().equals(dtbookPagenum)) {				
				this.add(new SubTree(currentList,new DefaultWellFormerImpl()));
				currentList = new LinkedList<XMLEvent>();
			}
		}
		//all collected after last pagenum til doc end
		if(!currentList.isEmpty()) {
			//remove the end document event
			currentList.remove(currentList.size()-1);
			this.add(new SubTree(currentList,new DefaultWellFormerImpl()));
		}	
	}
	
	@Override
	public boolean supportsDivisionStrategy(DivisionStrategy divider) {
		if(divider == DivisionStrategy.PAGES) return true;
		return false;
	}

	@Override
	public boolean supportsDocumentType(PeekResult peek) {
		if(peek.getRootElementLocalName().equals("dtbook")) return true;
		return false;
	}
	
	private static final long serialVersionUID = -4332521895575129024L;



}
