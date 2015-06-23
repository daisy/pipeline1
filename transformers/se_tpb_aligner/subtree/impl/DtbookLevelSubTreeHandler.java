package se_tpb_aligner.subtree.impl;

import java.util.LinkedList;
import java.util.List;

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
 * A subtreehandler that knows how to split a DTBook document in subtrees based on dtbook:level elements.
 * <p>All events occuring before the first level element open are assembled in one subtree.</p>
 * <p>All following subtrees will begin with a level element open, and end with whatever event comes before the next level element open.</p>
 * <p>The final subtree will begin at the last level element open, and end at the last character of the document.</p>

 * @author Markus Gylling
 */
public class DtbookLevelSubTreeHandler extends SubTreeHandler{
		
	public DtbookLevelSubTreeHandler() {
		super();		
	}
	
	public DtbookLevelSubTreeHandler(XMLSource doc, DivisionStrategy divider) {
		super(doc,divider);		
	}

	@Override
	public void read(XMLEventReader reader)throws XMLStreamException {
				
		List<XMLEvent> currentList = new LinkedList<XMLEvent>();
		while(reader.hasNext()) {
			currentList.add(reader.nextEvent());			
			XMLEvent peek = reader.peek();
			if(peek!=null && peek.isStartElement() && 
					peek.asStartElement().getName().getLocalPart()
					.matches("^level$|^level1$|^level2$|^level3$|^level4$|^level5$|^level6$")) {				
				this.add(new SubTree(currentList,new DefaultWellFormerImpl()));
				currentList = new LinkedList<XMLEvent>();
			}
		}
		//all collected after last level start til doc end
		if(!currentList.isEmpty()) {
			//remove the end document event
			currentList.remove(currentList.size()-1);
			this.add(new SubTree(currentList,new DefaultWellFormerImpl()));
		}
		
	}
	
	@Override
	public boolean supportsDivisionStrategy(DivisionStrategy divider) {
		if(divider == DivisionStrategy.LEVELS) return true;
		return false;
	}

	@Override
	public boolean supportsDocumentType(PeekResult peek) {
		if(peek.getRootElementLocalName().equals("dtbook")) return true;
		return false;
	}
	
	private static final long serialVersionUID = 3081327486423871315L;

}
