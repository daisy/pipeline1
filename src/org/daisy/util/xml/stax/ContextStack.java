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
package org.daisy.util.xml.stax;

import java.util.Iterator;
import java.util.Locale;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.i18n.LocaleUtils;

/**
 * A class that keeps track of a context path in an XML document.
 * <p>For every event that you read from your javax.xml.stream.XMLEventReader, add the
 * event to an instance of this class. The function
 * <code>getContextXPath()</code> will return an XPath statement 
 * corresponding to the EventReaders location in the XML document, e.g. 
 * <code>//dtbook/book/bodymatter/level1</code>.</p>
 * <p>XPath statement syntax can be controlled by using the extended getContextXPath(params) method.</p>
 * <p>This class also keeps track of the current language, as specified by
 * the <code>xml:lang</code> attributes. Just ask for 
 * <code>getCurrentLocale()</code>.</p>
 * @author Linus Ericson
 * @author Markus Gylling
 */
public class ContextStack {
    		
	private boolean attributeStackingMode = false;
    protected Stack<ContextInfo> context = new Stack<ContextInfo>();
    
    /**
     * Sets the mode for XPath rendering to only include elements in the location path.
     * <code>//dtbook/book/level1/p/sent</code>
     */
    public static final int XPATH_SELECT_ELEMENTS_ONLY = 8;

    /**
     * Sets the mode for XPath rendering to include attributes at the end of the location path.
     * <p>Note that will only yield a difference from XPATH_SELECT_ELEMENTS if the topmost
     * element in the stack carries attributes, <strong>and</strong> if the ContextStack has been instantiated
     * to include attributes in the stack.</p>
     * <code>//dtbook/book/level1/p/sent/@id</code>
     * @see #ContextStack(boolean)
     */
    public static final int XPATH_SELECT_ELEMENTS_AND_TRAILING_ATTRIBUTES = 16;
    
    /**
     * Sets the mode for XPath rendering to omit predicates.
     * <code>//dtbook/book/level1/p/sent</code>
     */
    public static final int XPATH_PREDICATES_NONE = 32;

    /**
     * Sets the mode for XPath rendering to include attribute predicates on the last element selector in the location path.
     * <code>//dtbook/book/level1/p/sent[@id='foo' and @class='bar']</code>
     */
    public static final int XPATH_PREDICATES_ALL_ATTRIBUTES_OF_LAST_ELEMENT = 64;
        	
    /**
     * Sets the mode for XPath rendering to include an id attribute predicate on the last element selector in the location path,
     * if that last element carries an id attribute.
     * <code>//dtbook/book/level1/p/sent[@id='foo']</code>
     */
    public static final int XPATH_PREDICATES_ID_ATTRIBUTE_OF_LAST_ELEMENT = 128;

    
	/**
	 * Default constructor. 
	 * <p>When using this constructor, Attribute events will not be included in the stack.
	 * (assuming they are fed as Attribute events to the #addEvent method). </p>
	 * @see #ContextStack(boolean)
	 */
	public ContextStack() {
		
	}

	/**
	 * Extended constructor.
	 * @param includeAttributes whether Attribute events should be included in the stack 
	 * (assuming they are fed as Attribute events to the #addEvent method). 
	 */
	public ContextStack(boolean includeAttributes) {
		this.attributeStackingMode = includeAttributes;
	}
    

	/**
     * Add an XMLEvent to the stack.
     * <p>Note that if the includeAttributes switch is true, Attribute events still must be fed as inparams
     * to this method; StartElement.getAttributes() is for obvious reasons not used in this implementation.</p>
     * <p>Note - for correct behavior, remember that <strong>every</strong> event (regardless of eventtype) 
     *  that you read from your EventReader must be added to an instance of this class.</p>
     */
	//that every event statement isnt really true; whats actually needed is StartElement, EndElement, 
	//and when Attribute stacking is active, Attribute and Characters.
    public boolean addEvent(XMLEvent event) {
    	context=popAttributes();    	
    	if (event.isStartElement()||(attributeStackingMode && event.isAttribute())) {
    		context.push(new ContextInfo(event));    		
    	}else if (event.isEndElement()) {        	
        	context.pop();        	
        }    	
    	return true;    	
    }
            
    /**
     * @return the full context stack [Stack&lt;ContextInfo&gt];
     */
	public Stack<ContextInfo> getContext() {
        Stack<ContextInfo> result = new Stack<ContextInfo>();
        for (Iterator<ContextInfo> it = context.iterator(); it.hasNext(); ) {
            ContextInfo ci = it.next();
            result.push(ci);
        }
        return result;
    }
    
    /**
     * @return the parent context stack [Stack&lt;ContextInfo&gt];
     */
    public Stack<ContextInfo> getParentContext() {
        Stack<ContextInfo> result = this.getContext();
        result.pop();
        return result;
    }
        
    public Locale getCurrentLocale() {
        for (int i = context.size() - 1; i >= 0; --i) {
            ContextInfo info = context.elementAt(i);
            Locale loc = info.getLocale();
            if(loc!=null) return loc;
        }
        return null;
    }
    
    /**
     * Convenience method.
     * @return the topmost event of the stack, equals the last added XMLEvent.
     */
    public ContextInfo getLastEvent(){
    	return context.lastElement();
    }
    
    /**
     * Retrieve an XPath statement describing the current cursor position of the reader feeding the context stack.
     */
    public String getContextXPath() {
        return buildXPath(context, ContextStack.XPATH_SELECT_ELEMENTS_AND_TRAILING_ATTRIBUTES, ContextStack.XPATH_PREDICATES_NONE);
    }

    /**
     * Retrieve an XPath statement describing the current cursor position of the reader feeding the context stack.
     * @param xPathSelectMode constant available in the ContextStack class. 
     * @param xPathPredicateMode constant available in the ContextStack class.
     */
    public String getContextXPath(int xPathSelectMode, int xPathPredicateMode) {    	
        return buildXPath(context, xPathSelectMode, xPathPredicateMode);
    }
    
    /**
     * Retrieve an XPath statement describing the current cursor position of the reader feeding the context stack.
     * @param list the stack.  
     * @param xPathSelectMode constant available in the ContextStack class. 
     * @param xPathPredicateMode constant available in the ContextStack class.
     */
    public String getContextXPath(Stack<ContextInfo> list, int xPathSelectMode, int xPathPredicateMode) {    	
        return buildXPath(list, xPathSelectMode, xPathPredicateMode);
    }
    
	private String buildXPath(Stack<ContextInfo> list, int xpathSelectMode, int xpathPredicateMode) {		
		//this is gonna need heavy rewrite if we want to extend modes
		//TODO support prefixed names 
        StringBuilder xpathBuilder = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder();
        boolean isAttribute;
        boolean attributeAddedToAxis = false; //currently we support only one attribute per axis; at the end
        boolean predicatePopulated = false;
        
        //determine appropriate leading axis specificer        
        xpathBuilder.append("/"); //TODO not always from doc root
        
        //build the select axis
        for (Iterator<ContextInfo> it = list.iterator(); it.hasNext(); ) {
            ContextInfo cur = it.next(); 
            isAttribute = cur.getXMLEventType() == XMLEvent.ATTRIBUTE;      
            nameBuilder.delete(0,nameBuilder.length());
            if(isAttribute) nameBuilder.append('@');
            nameBuilder.append(cur.getName().getLocalPart());
        	        	        	
        	if(!isAttribute||xpathSelectMode==ContextStack.XPATH_SELECT_ELEMENTS_AND_TRAILING_ATTRIBUTES) {
        		xpathBuilder.append('/').append(nameBuilder);
        		if(isAttribute) attributeAddedToAxis = true;
        	}	
        }

        //build the last axis node predicates
        if(xpathPredicateMode != ContextStack.XPATH_PREDICATES_NONE) {
        	//get the topmost element in inputlist
        	StartElement se = getTopMostStartElementEvent(list);
        	if(se!=null){
            	//build the predicate
            	StringBuilder predicateBuilder = new StringBuilder();
            	Iterator<?> i = se.getAttributes();
            	predicateBuilder.append('[');
            	while(i.hasNext()){
            		Attribute a = (Attribute)i.next();
            		if((xpathPredicateMode == ContextStack.XPATH_PREDICATES_ALL_ATTRIBUTES_OF_LAST_ELEMENT)
            				||(xpathPredicateMode == ContextStack.XPATH_PREDICATES_ID_ATTRIBUTE_OF_LAST_ELEMENT && (a.getName().getLocalPart()=="id"||a.getDTDType()=="ID"))) {
            			predicateBuilder.append('@').append(a.getName().getLocalPart());            			
            			predicateBuilder.append('=').append("'").append(a.getValue()).append("' and ");
    	        		predicatePopulated = true;            			
            		} //if((xpathPredicateMode
            	} //while(i.hasNext())
        		
	        	if(predicatePopulated) {
	        		predicateBuilder.delete(predicateBuilder.length()-5, predicateBuilder.length()).append(']');	   
	        		//determine where to insert it
	            	if(attributeAddedToAxis){
	            		xpathBuilder.insert(xpathBuilder.lastIndexOf("/"),predicateBuilder.toString());
	            	}else{
	            		xpathBuilder.append(predicateBuilder);
	            	}
	        	}
        	}//if(se!=null)        	
        } //if(xpathPredicateMode != ContextStack.XPATH_PREDICATES_NONE)
                
        return xpathBuilder.toString();
	}

	public class ContextInfo {		                
        private XMLEvent event;
        
        private ContextInfo(XMLEvent xe) {
        	this.event = xe;        	        	
        }
        		
        public QName getName() {     
        	if(this.event.isStartElement()) {
        		return event.asStartElement().getName();        		
        	}else if(this.event.isAttribute()) {
        		Attribute a = (Attribute)event;
        		return a.getName();
        	}
        	return null;        	        	
        }
        
        public int getXMLEventType() {
        	return this.event.getEventType();
        }

        public XMLEvent getXMLEvent() {
        	return this.event;
        }
        
        public Locale getLocale() {
        	if(this.event.isStartElement()) {
        		StartElement se = event.asStartElement();
        		for (Iterator<?> it = se.getAttributes(); it.hasNext(); ) {
	    	        Attribute att = (Attribute)it.next();
	    	        if ("lang".equals(att.getName().getLocalPart()) && "xml".equals(att.getName().getPrefix())) {
	    	          return LocaleUtils.string2locale(att.getValue());
	    	        }
        		}              		
        	}else if(this.event.isAttribute()) {
    	        Attribute att = (Attribute)this.event;
    	        if ("lang".equals(att.getName().getLocalPart()) && "xml".equals(att.getName().getPrefix())) {
    	          return LocaleUtils.string2locale(att.getValue());
    	        }
        	}
        	
        	return null;
        }	
        
    }

	/**
	 * @return the topmost StartElement event in the ContextInfo stack.
	 */
    private StartElement getTopMostStartElementEvent(Stack<ContextInfo> list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            ContextInfo info = context.elementAt(i);
            if(info.getXMLEventType() == XMLEvent.START_ELEMENT) {
            	return (StartElement)info.getXMLEvent();
            }	
        }
        return null;
	}

    /**
     * @deprecated
     */
    public String getContextPath(Stack<ContextInfo> list) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<ContextInfo> it = list.iterator(); it.hasNext(); ) {
            ContextStack.ContextInfo tmp = it.next();
            
        	QName name = tmp.getName();
            buffer.append("/").append(name.getLocalPart());
        }
        if (list.isEmpty()) {
            buffer.append("/");
        }
        return buffer.toString();
    }

    /**
     * @deprecated
     */

    public String getContextPath() {
        return getContextPath(getContext());
    }
    
    /**
     * Pops the stack until the topmost Event is not an Attribute.
     */
    private Stack<ContextInfo> popAttributes() {        	
    	do {
    		if(!context.isEmpty()){
	    		ContextInfo ci = context.lastElement();
	    		if (ci.getXMLEventType()==XMLEvent.ATTRIBUTE) {
	    			context.pop();
	    		}else{
	    			break;
	    		}
    		}else{
    			break;
    		}	
    	} while (true);  
    	
    	return context;    		
	}
    
}
