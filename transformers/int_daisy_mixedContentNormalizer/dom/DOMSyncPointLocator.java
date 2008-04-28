/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package int_daisy_mixedContentNormalizer.dom;

import int_daisy_mixedContentNormalizer.AbstractSyncPointLocator;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 * A sync point locator using DOM. This locator assumes that the Source has passed through DOMNormalizer.
 * @author Markus Gylling
 */
public class DOMSyncPointLocator extends AbstractSyncPointLocator  {
	private DOMConfig mConfig = null;
	private int mInputDocElementCount = 0;
	private Map<String, Attr> mSyncPointAttrCache = null;
	private QName mScopeQName = null;
	Element mScopeElement = null;
	private int mCurrentElementCount = 0;
	
	public DOMSyncPointLocator(TransformerDelegateListener transformer, DOMConfig config){
		super(transformer);
		mConfig = config;
		mSyncPointAttrCache = new HashMap<String, Attr>();
	}
	
	public Result locate(Source input) throws TransformerRunException {
		mSyncPointCount = 0;
		
		try{
			
			DOMSource ds = (DOMSource) input;
			Document doc = (Document)ds.getNode();
					    
		    mScopeQName = mConfig.getSyncPointScope(doc.getDocumentElement().getNamespaceURI());

			/*
			 * Set up a TreeWalker and iterate over the DOM instance.
			 */
			DocumentTraversal dt = (DocumentTraversal)doc;
            TreeWalker walker = dt.createTreeWalker(doc.getDocumentElement(),
            		NodeFilter.SHOW_ELEMENT, new NodeFilterImpl(), true); 
            
            Element e = null;            
		    while ((e=(Element)walker.nextNode())!=null) {					    			    	
		    	if(mTransformer.delegateCheckAbort()) throw new TransformerRunException ("user abort");		    	
		    	if(e.getFirstChild()!=null){ //never sync on empty elements

		    		if(mConfig.isSyncForce(e)) {
		    			addSyncPoint(e);
		    		}
		    		else if(hasOnlyIgnorableChildren(e) && !hasSyncedAncestor(e)) {
			    		addSyncPoint(e);
			    	}
			    	else if(e.getUserData("isWrapper")!=null  && !hasSyncedAncestor(e)){ 
			    		//TODO && !hasSyncedAncestor(e) only for nested wrapper bug
			    		addSyncPoint(e);
			    	}
			    	//else if (!hasSyncedAncestor(e) && DOMUtil.hasTextChild(e)) {
			    	//alt::
			    	else if (!hasSyncedAncestor(e) && mConfig.hasTextChild(e)) {
			    	//end alt::
			    		addSyncPoint(e);
			    	}
		    	}
		    }
		    
		    /*
		     * Done.
		     */		    
		    return new DOMResult(doc);
		    					
		}catch (Exception e) {			
			throw new TransformerRunException(e.getMessage(),e);
		}	
	}

	
	
	private void addSyncPoint(Element e) {
		String currentNS = e.getNamespaceURI();
		Attribute src = mConfig.getSyncPointAttribute(currentNS);
		
		if(src==null) {			
			XMLEventFactory xef = StAXEventFactoryPool.getInstance().acquire();
			src = xef.createAttribute("smil", "http://www.w3.org/2001/SMIL20/", "sync", "true");
			StAXEventFactoryPool.getInstance().release(xef);
		}
		
		Attr syncAttr = mSyncPointAttrCache.get(currentNS);
		if(syncAttr==null) {				
				String prefix = src.getName().getPrefix();
				String name = src.getName().getLocalPart();
				String nsuri = src.getName().getNamespaceURI();
				if(prefix!=null){								
					syncAttr = e.getOwnerDocument().createAttribute(prefix+":"+name);
					e.getOwnerDocument().getDocumentElement().setAttribute("xmlns:" + prefix, nsuri);
				}else{
					syncAttr = e.getOwnerDocument().createAttribute(name);
					e.getOwnerDocument().getDocumentElement().setAttribute("xmlns", nsuri);
				}								
				syncAttr.setNodeValue(src.getValue());				
				mSyncPointAttrCache.put(currentNS, syncAttr);
		}		
		e.setAttributeNode((Attr)syncAttr.cloneNode(true));
		e.setUserData("isSynced", "true", null);
		++mSyncPointCount;
	}
		
	private boolean hasOnlyIgnorableChildren(Element e) {				
		return mConfig.isIgnorableElementsAndWhitespaceOnly(e.getChildNodes());		
	}


	private boolean hasSyncedAncestor(Node e) {		
		Node parent = e.getParentNode();		
		if(parent==null || parent.getNodeType()!= Node.ELEMENT_NODE) return false;
		if(((Element)parent).getUserData("isSynced")!=null) return true;
		return hasSyncedAncestor(parent);
	}

	
	class NodeFilterImpl implements NodeFilter{
		public short acceptNode(Node n) {	
			mCurrentElementCount++;
			mTransformer.delegateProgress(this, ((double)mCurrentElementCount/mInputDocElementCount));
			if(mScopeElement==null) {
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					Element t = (Element) n;
					if(t.getLocalName().equals(mScopeQName.getLocalPart())) {
						if(t.getNamespaceURI().equals(mScopeQName.getNamespaceURI())) {
							mScopeElement = t;
						}	
					}
				}
			}
			if(mScopeElement==null) return NodeFilter.FILTER_SKIP;
			
			if(!DOMUtil.isDescendant(n, mScopeElement)) {
				return NodeFilter.FILTER_SKIP;
			}
			
			return NodeFilter.FILTER_ACCEPT;
		}		
	}
		
	public void setInputDocElementCount(int count) {
		mInputDocElementCount = count;		
	}		
}
