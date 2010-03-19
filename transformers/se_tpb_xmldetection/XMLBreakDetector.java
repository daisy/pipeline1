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
package se_tpb_xmldetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.ContextStack;
import org.daisy.util.xml.stax.DoctypeParser;
import org.daisy.util.xml.stax.EmptyElementFilter;
import org.daisy.util.xml.stax.EventWriterCache;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Base class for break detectors.
 * 
 * @author Linus Ericson
 */
@SuppressWarnings("unchecked")
public abstract class XMLBreakDetector {
    
    protected final static Pattern dtdPattern = Pattern.compile("<!DOCTYPE\\s+\\w+(\\s+((SYSTEM\\s+(\"[^\"]*\"|'[^']*')|PUBLIC\\s+(\"[^\"]*\"|'[^']*')\\s+(\"[^\"]*\"|'[^']*'))))?\\s*(\\[.*\\]\\s*)?>");
    
    protected File outputFile = null;
    
    private XMLEventWriter writer = null;
    private EventWriterCache writerCache = null;
    protected XMLInputFactory inputFactory = null;
    protected XMLEventFactory eventFactory = null;
    protected XMLOutputFactory outputFactory = null;
    
    protected BreakSettings breakSettings = null;
    protected BreakFinder breakFinder = null;
    protected ContextStack writeStack = new ContextStack();
    
    protected Set<String> allowedPaths = null;
    
    private boolean rootElementSeen = false;
    private boolean alreadyCalled = false;

    /**
     * Constructor. This constructor instantiates the stax readers and writers to be used
     * when processing the file. 
     * @param outFile the output file
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     */
    public XMLBreakDetector (File outFile) throws CatalogExceptionNotRecoverable, XMLStreamException {
        inputFactory = XMLInputFactory.newInstance();
        eventFactory = XMLEventFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        
        //mg20080613 With IS_REPAIRING_NAMESPACES set to FALSE and using the mathml extension,
        //the output is really messay re ns declarations, but that needs to be cleaned up later:
        //if set to TRUE, ns is clean but will break against the DTD later on when the DTD is reinserted. 
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
        
        //System.err.println(inputFactory.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES));
        //System.err.println(inputFactory.isPropertySupported(XMLInputFactory.SUPPORT_DTD));
        //System.err.println(inputFactory.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));

        String factoryClass = inputFactory.getClass().getName();
        if (factoryClass.equals("com.bea.xml.stream.MXParserFactory")) {
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        } else {
            if (!factoryClass.equals("com.ctc.wstx.stax.WstxInputFactory")) {
                System.err.println("Warning: unknown StAX implementation: " + factoryClass);
            }
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
            inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
            inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
            //mg20080423 set to true
            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        }
        
        outputFile = outFile;
    }
    
    /**
     * Template method for performing the detection.
     * @param paths the set of allowed paths. 
     * @throws UnsupportedDocumentTypeException
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public void detect(Set<String> paths) throws UnsupportedDocumentTypeException, FileNotFoundException, XMLStreamException {
        if (alreadyCalled) {
            throw new IllegalStateException("This method may only be called once");
        }
        alreadyCalled = true;
        
        if (paths == null) {
            allowedPaths = breakSettings.getDefaultPaths();
        } else {
            allowedPaths = paths;
        }
        detect();
        writerCache.flush();
        writer.close();
    }
        
    protected abstract void detect() throws XMLStreamException, UnsupportedDocumentTypeException, FileNotFoundException;
    
    /**
     * Parses the doctype declaration and loads the break settings file matching the public or system ID.
     * @param doctype the doctype declaration
     * @throws UnsupportedDocumentTypeException
     */
    protected void parseDoctype(String doctype) throws UnsupportedDocumentTypeException {
    	//mg20080613: previous routine does grok internal subsets  
    	try{
    		DoctypeParser dp = new DoctypeParser(doctype);
    		if(dp.getSystemId()==null) throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration");
    		if(dp.getPublicId()==null) breakSettings.setup(null, dp.getSystemId());
    		breakSettings.setup(dp.getPublicId(), dp.getSystemId());
    	}catch (Exception e) {
    		throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration");
		}
    	
//        Matcher matcher = dtdPattern.matcher(doctype);        
//        if (matcher.matches()) {
//            if (matcher.group(3).startsWith("PUBLIC")) {
//                String pub = matcher.group(5);
//                String sys = matcher.group(6);
//                pub = pub.substring(1, pub.length() - 1);
//                sys = sys.substring(1, sys.length() - 1);
//                breakSettings.setup(pub, sys);
//            } else {
//                String sys = matcher.group(4);                        
//                sys = sys.substring(1, sys.length() - 1);
//                breakSettings.setup(null, sys);
//            }
//        } else {
//            throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration");
//        }
    }
    
    /**
     * Load the matching break settings file using a namespace declaration
     * @param namespaceURI the namespace URI
     * @return true if the load was successful, false otherwise
     */
    protected boolean parseNamespace(String namespaceURI) {
        return breakSettings.setup(namespaceURI);
    }
    
    /**
     * Sets the xml event writer and sets up an event writer cache
     * @param xew the xml event weriter to set
     */
    protected void setXMLEventWriter(XMLEventWriter xew) {
    	writer = xew;
    	writerCache = new EmptyElementFilter(writer);
    }
    
    /**
     * Writes an xml event
     * @param event the event to write
     * @throws XMLStreamException
     */
    protected void writeEvent(XMLEvent event) throws XMLStreamException {
    	this.writeEvent(event, false);
    }
    
    /**
     * Writes an xml event. If the event is the root element, add the
     * namespace declaration for the break element (if needed) as well.
     * @param event the event to write
     * @param filter
     * @throws XMLStreamException
     */
    protected void writeEvent(XMLEvent event, boolean filter) throws XMLStreamException {        
        if (event.isStartElement()) {
            if (!rootElementSeen) {
                /* 
                 * Make sure a namespace declaration for the break element exists 
                 */
                QName breakElement = getBreakElement();
                QName expAttrName = breakSettings.getExpAttr();
                StartElement se = event.asStartElement();
                Vector<Namespace> namespaces = new Vector<Namespace>();
                boolean alreadyExists = false;
                for (Iterator<?> it = se.getNamespaces(); it.hasNext(); ) {
                    Namespace ns = (Namespace)it.next();
                    if (ns.getPrefix().equals(breakElement.getPrefix())) {
                        if (ns.getNamespaceURI() == null) {
                            if (breakElement.getNamespaceURI() == null) {
                                alreadyExists = true;
                            } else {
                                throw new XMLStreamException("Break element has conflicting prefix/namespace");
                            }
                        } else {
                            if (ns.getNamespaceURI().equals(breakElement.getNamespaceURI())) {
                                alreadyExists = true;
                            } else {
                                throw new XMLStreamException("Break element has conflicting prefix/namespace");
                            }
                        }
                    }
                    namespaces.add(ns);
                }
                if (!alreadyExists) {
                    namespaces.add(eventFactory.createNamespace(breakElement.getPrefix(), breakElement.getNamespaceURI()));
                }
                maybeAddExpAttrNS(namespaces, expAttrName);
                event = eventFactory.createStartElement(se.getName(), se.getAttributes(), namespaces.iterator());                
                rootElementSeen = true;
            }
        }
        writeStack.addEvent(event);
        //writer.add(event);
        writerCache.writeEvent(event, filter);
    }
    
    /**
     * Adds the namespace of <code>expAttrName</code> to the <code>namespaces</code> vector
     * @param namespaces the namespaces vector
     * @param expAttrName the QName
     * @throws XMLStreamException
     */
    private void maybeAddExpAttrNS(Vector<Namespace> namespaces, QName expAttrName) throws XMLStreamException {
        if (expAttrName != null) {
            boolean alreadyExists = false;
	        for (Iterator<Namespace> it = namespaces.iterator(); it.hasNext(); ) {
	            Namespace ns = it.next();
	            if (ns.getPrefix().equals(expAttrName.getPrefix())) {
	                if (ns.getNamespaceURI() == null) {
	                    if (expAttrName.getNamespaceURI() == null) {
	                        alreadyExists = true;
	                    } else {
	                        throw new XMLStreamException("Break element has conflicting prefix/namespace");
	                    }
	                } else {
	                    if (ns.getNamespaceURI().equals(expAttrName.getNamespaceURI())) {
	                        alreadyExists = true;
	                    } else {
	                        throw new XMLStreamException("Break element has conflicting prefix/namespace");
	                    }
	                }
	            }
	        }
	        if (!alreadyExists) {
	            namespaces.add(eventFactory.createNamespace(expAttrName.getPrefix(), expAttrName.getNamespaceURI()));
	        }
        }
    }
    
    /**
     * Writes a characters event
     * @param text the text to write
     * @throws XMLStreamException
     */
    protected void writeString(String text) throws XMLStreamException {
        writeEvent(eventFactory.createCharacters(text));        
    }
    
    /**
     * Used for debug only. Print an event to STDERR.
     * @param event the event to print
     */
    protected void printEvent(XMLEvent event) {
        if (event.isStartElement()) {
            StartElement se = event.asStartElement();
            System.err.println("Event: <" + se.getName().getLocalPart() + ">");
        } else if (event.isEndElement()) {
            EndElement ee = event.asEndElement();
            System.err.println("Event: </" + ee.getName().getLocalPart() + ">");
        } else if (event.isCharacters()) {
            System.err.println("Event: char(" + event.asCharacters().getData() + ")");
        } else {
            System.err.println("Event: " + event);
        }
    }
    
    public void setBreakSettings(BreakSettings ebi) {
        breakSettings = ebi;
    }
    

    public void setBreakFinder(BreakFinder bf) {
        breakFinder = bf;
    }
    
    protected QName getBreakElement() {
        return breakSettings.getBreakElement();
    }
    
    protected Iterator<Attribute> getBreakAttributes() {
        Iterator<Attribute> result = null;
        Map attributeMap = breakSettings.getBreakAttributes(); 
        if (attributeMap != null) {
            Vector<Attribute> v = new Vector<Attribute>();
            for (Iterator<?> it = attributeMap.keySet().iterator(); it.hasNext(); ) {
                String name = (String)it.next();
                String value = (String)attributeMap.get(name);
                Attribute attr = eventFactory.createAttribute(name, value);
                v.add(attr);
            }
            result = v.iterator();
        }
        return result;
    }
    
}
