/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_xmldetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
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
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * @author Linus Ericson
 */
public abstract class XMLBreakDetector {
    
    protected final static Pattern dtdPattern = Pattern.compile("<!DOCTYPE\\s+\\w+(\\s+((SYSTEM\\s+(\"[^\"]*\"|'[^']*')|PUBLIC\\s+(\"[^\"]*\"|'[^']*')\\s+(\"[^\"]*\"|'[^']*'))))?\\s*(\\[.*\\]\\s*)?>");
    
    protected File outputFile = null;
    
    protected XMLEventWriter writer = null;
    protected XMLInputFactory inputFactory = null;
    protected XMLEventFactory eventFactory = null;
    protected XMLOutputFactory outputFactory = null;
    
    protected BreakSettings breakSettings = null;
    protected BreakFinder breakFinder = null;
    protected ContextStack writeStack = new ContextStack();
    
    protected Set allowedPaths = null;
    
    private boolean rootElementSeen = false;
    private boolean alreadyCalled = false;

    public XMLBreakDetector (File outFile) throws CatalogExceptionNotRecoverable, FileNotFoundException, XMLStreamException {
        inputFactory = XMLInputFactory.newInstance();
        eventFactory = XMLEventFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);        
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
            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        }
        
        outputFile = outFile;
    }
    
    public void detect(Set paths) throws UnsupportedDocumentTypeException, FileNotFoundException, XMLStreamException {
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
    }
        
    protected abstract void detect() throws XMLStreamException, UnsupportedDocumentTypeException, FileNotFoundException;
    
    
    protected void parseDoctype(String doctype) throws UnsupportedDocumentTypeException {
        Matcher matcher = dtdPattern.matcher(doctype);
        if (matcher.matches()) {
            if (matcher.group(3).startsWith("PUBLIC")) {
                String pub = matcher.group(5);
                String sys = matcher.group(6);
                pub = pub.substring(1, pub.length() - 1);
                sys = sys.substring(1, sys.length() - 1);
                breakSettings.setup(pub, sys);
            } else {
                String sys = matcher.group(4);                        
                sys = sys.substring(1, sys.length() - 1);
                breakSettings.setup(null, sys);
            }
        } else {
            throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration");
        }
    }
    
    protected void writeEvent(XMLEvent event) throws XMLStreamException {        
        if (event.isStartElement()) {
            if (!rootElementSeen) {
                /* 
                 * Make sure a namespace declaration for the break element exists 
                 */
                QName breakElement = getBreakElement();
                StartElement se = event.asStartElement();
                Vector namespaces = new Vector();
                boolean alreadyExists = false;
                for (Iterator it = se.getNamespaces(); it.hasNext(); ) {
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
                event = eventFactory.createStartElement(se.getName(), se.getAttributes(), namespaces.iterator());                
                rootElementSeen = true;
            }
        }
        writeStack.addEvent(event);
        writer.add(event);
    }
    
    protected void writeString(String text) throws XMLStreamException {
        writeEvent(eventFactory.createCharacters(text));        
    }
    
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
    
    protected Iterator getBreakAttributes() {
        Iterator result = null;
        Map attributeMap = breakSettings.getBreakAttributes(); 
        if (attributeMap != null) {
            Vector v = new Vector();
            for (Iterator it = attributeMap.keySet().iterator(); it.hasNext(); ) {
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
