package se_tpb_aligner.subtree;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 * 
 * @author Markus Gylling
 */
public class DefaultWellFormerImpl implements WellFormer {

	public List<XMLEvent> makeWellFormed(List<XMLEvent> input) {
		List<XMLEvent> result = new LinkedList<XMLEvent>();
		Stack<StartElement> openElements = new Stack<StartElement>();
		Stack<EndElement> endElements = new Stack<EndElement>();
		
		XMLEventFactory xef = null;
		try{
			
			xef=StAXEventFactoryPool.getInstance().acquire();
					
			// If the first event is not a StartElement, add one		
			XMLEvent firstEvent = input.get(0);
			if(firstEvent.getEventType()!= XMLEvent.START_ELEMENT){
				result.add(SubTreeHandler.ignoreStartElement);
				openElements.add(SubTreeHandler.ignoreStartElement);
			}
								
			//forward loop, close all open elements
			for(XMLEvent e : input) {
				if(e.isStartElement()) openElements.push(e.asStartElement());
				if(e.isEndElement() && !openElements.empty()) openElements.pop();
				result.add(e);
			}
		
			while(!openElements.empty()) {
				StartElement se = openElements.pop();
				//add an empty element marker
				result.add(SubTreeHandler.ignoreStartElement);
				result.add(SubTreeHandler.ignoreEndElement);
				//add the ghost element
				result.add(xef.createEndElement(se.getName().getPrefix(), 
						se.getName().getNamespaceURI(), se.getName().getLocalPart()));
			}
						
			//second, loop backwards over result list and balance any close elements without opens			
			for (int i = result.size()-1 ; i > -1 ; i--) {
				XMLEvent e = result.get(i);
				if(e.isEndElement()) endElements.push(e.asEndElement());
				if(e.isStartElement() && !endElements.empty()) endElements.pop();
			}
									
			while(!endElements.empty()) {			
				EndElement se = endElements.pop();
				//add the ghost element
				result.add(0,xef.createStartElement(se.getName().getPrefix(), se.getName().getNamespaceURI(), se.getName().getLocalPart()));
				result.add(1,SubTreeHandler.ignoreAttribute);
			}
			
			//finally, a root 
			result.add(0,SubTreeHandler.ignoreStartElement);
			result.add(SubTreeHandler.ignoreEndElement);
			
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
		}
		return result;
	}

}
