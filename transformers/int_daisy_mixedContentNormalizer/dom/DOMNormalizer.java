package int_daisy_mixedContentNormalizer.dom;

import int_daisy_mixedContentNormalizer.AbstractNormalizer;
import int_daisy_mixedContentNormalizer.SiblingState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.CharUtils;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * A mixed content normalizer using DOM.
 * @author Markus Gylling
 */
public class DOMNormalizer extends AbstractNormalizer {
	private DOMConfig mConfig = null;		
	private int mInputDocElementCount = 0;
	private int mCurrentElementCount = 0;
	private Map<String, Element> mWrapperElementCache = null;
	
	
	public DOMNormalizer(TransformerDelegateListener transformer, DOMConfig dnc) throws TransformerRunException{
		super(transformer);
		mConfig = dnc;
		mWrapperElementCache = new HashMap<String, Element>(); 
	}
		
	public Result normalize(Source input) throws TransformerRunException {
		mModCount = 0;		
		try{
			
			DOMSource ds = (DOMSource) input;
			Document doc = (Document)ds.getNode();
						
			/*
			 * Set up a TreeWalker and iterate over the DOM instance.
			 */
			DocumentTraversal dt = (DocumentTraversal)doc;
            TreeWalker walker = dt.createTreeWalker(doc.getDocumentElement(),
            		NodeFilter.SHOW_ELEMENT, new NodeFilterImpl(), true); 
            
            Element e = null;
                        
		    while ((e=(Element)walker.nextNode())!=null) {					    			    	
		    	mTransformer.delegateProgress(this, ((double)mCurrentElementCount/(mInputDocElementCount+mModCount)));
		    	if(mUserAbort) throw new TransformerRunException ("user abort");			    	
		    	NodeList children = e.getChildNodes();		
		    	SiblingState state = getSiblingState(children);
		    	if(state==SiblingState.UNBALANCED) normalizeChildren(e);
		    	mCurrentElementCount++;
		    }
		    
//		    //TEST*************************************************************
//            TreeWalker cleaner = dt.createTreeWalker(doc.getDocumentElement(),
//            		NodeFilter.SHOW_ELEMENT, null, true);
//		    
//		    e = null;
//            cur = 0;            
//		    while ((e=(Element)cleaner.nextNode())!=null) {					    			    					
//		    	if(e.getUserData("isWrapper")!=null){
//					Element unwantedWrapper = hasWrapperAncestor(e);
//					if(unwantedWrapper!=null) {
//						Element unwantedWrappersParent = (Element)unwantedWrapper.getParentNode(); 
//						NodeList unwantedWrappersChildren = unwantedWrapper.getChildNodes();
//						for (int i = 0; i < unwantedWrappersChildren.getLength(); i++) {
//							Node unwantedWrappersChild = unwantedWrapper.removeChild(unwantedWrappersChildren.item(i));
//							unwantedWrappersParent.insertBefore(unwantedWrappersChild, unwantedWrapper);
//						}
//						unwantedWrappersParent.removeChild(unwantedWrapper);
//						mModCount--;
//						cleaner.setCurrentNode(cleaner.previousNode());
//					}
//		    	}
//		    }
//		  //END TEST*************************************************************
		    
		    
		    /*
		     * Done.
		     */
		    return new DOMResult(doc);
		    					
		}catch (Exception e) {			
			throw new TransformerRunException(e.getMessage(),e);
		}	
	}
	
	public void setInputDocElementCount(int count) {
		mInputDocElementCount = count;
	}
	
	private SiblingState getSiblingState(NodeList siblings) {
		boolean hasNonWhiteSpaceText = false;
		boolean hasIgnorableElems = false;
		boolean hasNonIgnorableElems = false;
		
		for (int i = 0; i < siblings.getLength(); i++) {
			Node child = siblings.item(i);
			if(child.getNodeType() == Node.TEXT_NODE) {
				if(!CharUtils.isXMLWhiteSpace(child.getNodeValue())) {
					hasNonWhiteSpaceText = true;		
				}				
			}
			else
			if(child.getNodeType() == Node.ELEMENT_NODE) {				
				if (!mConfig.isIgnorable((Element)child)) {
					hasNonIgnorableElems = true;	
				}else{
					hasIgnorableElems = true;
				}
			}	
			else {
				System.err.println("unexpected nodetype in getSiblingDescription");
			}
		}	
		
		if(hasNonWhiteSpaceText && !hasNonIgnorableElems) return SiblingState.BALANCED;
		if(hasIgnorableElems && !hasNonIgnorableElems) return SiblingState.BALANCED;					
		if(hasNonIgnorableElems && !hasIgnorableElems && !hasNonWhiteSpaceText) return SiblingState.BALANCED;
		return SiblingState.UNBALANCED;		
	}
	
	class NodeFilterImpl implements NodeFilter{
		public short acceptNode(Node n) {
			Element e = (Element)n;			
			if(e.getUserData("isWrapper")!=null){
				//System.err.println("walker skipping: wrapper encountered " + e.getNodeName());
				return NodeFilter.FILTER_SKIP;
			}

			if(e.getFirstChild()==null){
				//System.err.println("walker skipping: empty element " + e.getNodeName());
				return NodeFilter.FILTER_SKIP;
			}
		    				    
			return NodeFilter.FILTER_ACCEPT;
		}		
	}
	
	/**
	 * Iterate over a list of siblings and normalize.
	 * <p>assumes coalescing</p>
	 * <p>assumes entity resolution done</p>
	 */
	private void normalizeChildren(Element parent) {
		Node currentNode = null;		
		List<Node> ignorables = new LinkedList<Node>();
		NodeList children = null;
		
		//find as many consecutive ignorables (text or config ignores) as possible; 0-n per try
		int loops = 0;
		do {
			loops++;
			children = parent.getChildNodes();
						
			for (int i = 0; i < children.getLength(); i++) {			
				currentNode = children.item(i);				
				if (currentNode.getNodeType() == Node.TEXT_NODE 
						|| (currentNode.getNodeType() == Node.ELEMENT_NODE 
								&& mConfig.isIgnorable((Element)currentNode))) {
					//ignorable element, whitespace node or text node
					ignorables.add(currentNode);				
				}else{
					//non-ignorable										
					ignorables = evaluate(ignorables);
				}
			}	
			ignorables = evaluate(ignorables);
			
		} while (currentNode != parent.getLastChild());		
		//if(loops>1) System.err.println("did the do loop " + loops +" times");		
	}

	private List<Node> evaluate(List<Node> ignorables) {
		//remove leading and trailing whitespace					
		ignorables = trim(ignorables);		
		//if we after trim have exactly one non-ws textnode 
		//or if length (regardless of type) > 1, then wrap		
		if(ignorables.size()>1 || 
				(ignorables.size()==1 
						&& ignorables.get(0).getNodeType() == Node.TEXT_NODE )) { 						
			Element wrapper = wrap(ignorables);		
		}	
		ignorables.clear();
		return ignorables;
	}
	
	/**
	 * Return a wrapper parent of inparam wrapper, or null if one does not exist
	 */
	private Element hasWrapperAncestor(Element wrapper) {		
		Node parent = wrapper.getParentNode();		
		if(parent==null || parent.getNodeType()!= Node.ELEMENT_NODE) return null;
		if(((Element)parent).getUserData("isWrapper")!=null) return (Element)parent;
		return hasWrapperAncestor((Element)parent);
	}

	/**
	 * Remove leading and trailing whitespace nodes. Remove leading and trailing empty elements.
	 */
	private List<Node> trim(List<Node> nodes) {						
		while(!nodes.isEmpty()) {
			Node n = nodes.get(0);
			if(n.getNodeType()==Node.TEXT_NODE){
				if(CharUtils.isXMLWhiteSpace(n.getNodeValue())){
					nodes.remove(0);	
				}else{
					break;
				}
			} else if(n.getNodeType()==Node.ELEMENT_NODE){
				Element e = (Element) n;
				if(e.getFirstChild()==null) {
					nodes.remove(0);
				}else{
					break;
				}
			}else{
				break;
			}
		}
		
		while(!nodes.isEmpty()) {
			Node n = nodes.get(nodes.size()-1);
			if(n.getNodeType()==Node.TEXT_NODE){
				if(CharUtils.isXMLWhiteSpace(n.getNodeValue())){
					nodes.remove(nodes.size()-1);	
				}else{
					break;
				}
			} else if(n.getNodeType()==Node.ELEMENT_NODE){
				Element e = (Element) n;
				if(e.getFirstChild()==null) {
					nodes.remove(nodes.size()-1);
				}else{
					break;
				}	
			}else{
				break;
			}
		}
		return nodes;
	}

	
	/**
	 * Move inparam nodes into a wrapper element. 
	 * The resulting wrapper will be placed at the position of the first node in the inparam list.
	 */
	private Element wrap (List<Node> nodelist) {	
		//System.out.println("wrapping " + nodelist.size() + " nodes");
		Element parent = (Element)nodelist.get(0).getParentNode();
		Element newElem = createWrapper(parent);
		newElem.setUserData("isWrapper","true",null);
		parent.insertBefore(newElem, nodelist.get(0));
		for(Node n : nodelist) {
			Node move = n.getParentNode().removeChild(n);
			newElem.appendChild(move);
			move.setUserData("isWrapped", "true", null);
		}		
		mModCount++;
		return newElem;				
	}
	
	
	private Element createWrapper(Element source) {
		String currentNS = source.getNamespaceURI();
		Element newElem = mWrapperElementCache.get(currentNS);
		if(newElem == null) {				
			StartElement wrapperSource = mConfig.getWrapperElement(currentNS);		
			if(wrapperSource==null){
				XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();				
				wrapperSource = xef.createStartElement(source.getPrefix(), source.getNamespaceURI(), source.getLocalName());
				StAXEventFactoryPool.getInstance().release(xef);
			}			
			newElem = source.getOwnerDocument().createElementNS(
				wrapperSource.getName().getNamespaceURI(), 
				wrapperSource.getName().getLocalPart());			
			for (Iterator iter = wrapperSource.getAttributes(); iter.hasNext();) {
				Attribute a = (Attribute) iter.next();
				newElem.setAttribute(a.getName().getLocalPart(), a.getValue());
			}
			mWrapperElementCache.put(currentNS, newElem);
		}
		return (Element)newElem.cloneNode(true);
	}

	/**
	 * Retrieve the number of elements in input document after normalization has been completed.
	 * <p>If this method is called prior to calling {@link #normalize(Source)}, it will return
	 * the initial number of elements in input document.
	 */
	public int getFinalElementCount() {
		return mInputDocElementCount + mModCount;
	}

}
