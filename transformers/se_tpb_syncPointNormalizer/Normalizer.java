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
package se_tpb_syncPointNormalizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.settings.SettingsResolver;
import org.daisy.util.xml.settings.SettingsResolverException;
import org.daisy.util.xml.settings.UnsupportedDocumentTypeException;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Performs mixed content normalization. In document tyoe specific
 * configuration files, the element that wraps the text nodes (e.g.
 * &lt;span/&gt;) is configured. The configuration files also specify
 * a list of elements that are excluded from normalization.
 * @author Linus Ericson
 */
public class Normalizer extends Transformer {

    private XMLInputFactory xif = null;
    private XMLOutputFactory xof = null;
    private XMLEventFactory xef = null;
    
    private AttributedContext context = null;
    
    private QName tagName = null;
    
    private Set mustSynchronize = new HashSet();
    private Set synchronize = new HashSet();
    
    /**
     * @param inListener
     * @param eventListeners
     * @param isInteractive
     */
    public Normalizer(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    /**
     * Note: Due to a limitation of woodstox, entity references are always resolved.
     */
    protected boolean execute(Map parameters) throws TransformerRunException {
        // Read paramters
        String input = (String)parameters.remove("input");
        String output = (String)parameters.remove("output");
        
        // Create factories
        xif = XMLInputFactory.newInstance();
        xof = XMLOutputFactory.newInstance();
        xef = XMLEventFactory.newInstance();        
        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        //xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        
        try {
            // Use the catalog entity resolver for entity resolution
            xif.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
            
            // First pass: record elements content info
            XMLEventReader xer = xif.createXMLEventReader(new FileInputStream(input));
            Map elementInfo = this.getElementInfo(xer);            
            this.sendMessage(i18n("ELEMENTS_FOUND",elementInfo.size()), MessageEvent.Type.DEBUG);
            xer.close();
            
            // Second pass: normalize elements with mixed content
            xer = xif.createXMLEventReader(new FileInputStream(input));
            this.performNormalization(xer, output, elementInfo);
            xer.close();
            
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (ConfigurationException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (SettingsResolverException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        return true;
    }
    
    private void performNormalization(XMLEventReader xer, String output, Map elementInfo) throws FileNotFoundException, XMLStreamException {
        context = new AttributedContext();
        ContextInfo lastInfo = null;
        int elementNumber = 0;
        EventWriterCache cache = null;
        XMLEventWriter xew = null;
        while (xer.hasNext()) {
            XMLEvent event = xer.nextEvent();
            context.register(event);
            
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();                
                ++elementNumber;
                context.peek().number = elementNumber;
                String name = se.getName().getLocalPart();
                
                ContextInfo contextInfo = (ContextInfo)elementInfo.get(new Integer(elementNumber));
                //System.err.println(contextInfo.toString());
                
                if (contextInfo.childOfWantElement && !contextInfo.hasMustElement && !mustSynchronize.contains(name)) {
                    ContextInfo parentInfo = (ContextInfo)elementInfo.get(new Integer(context.getParent().number));
                    if (!context.getParent().spanOpen && parentInfo.hasMustElement) {
	                    cache.writeEvent(this.getStartElement(), true);
	                    context.getParent().spanOpen = true;
                    }
                } else if ((mustSynchronize.contains(name) || contextInfo.hasMustElement) && context.hasParent() && context.getParent().spanOpen) {
                    cache.writeEvent(this.getEndElement(), true);
                    context.getParent().spanOpen = false;
                } 
                cache.writeEvent(se, false);
                
            } else if (event.isEndElement()) {
                EndElement ee = event.asEndElement();
                
                if (lastInfo.spanOpen) {
                    cache.writeEvent(this.getEndElement(), true);                  
                }
                
                cache.writeEvent(ee, false);
            } else if (event.isCharacters()) {
                Characters ch = event.asCharacters();
                if(!context.isEmpty()){ //mg20071011
	                int currentNumber = context.peek().number; 
	                if (currentNumber > 0) {
		                ContextInfo contextInfo = (ContextInfo)elementInfo.get(new Integer(currentNumber));
		                String name = contextInfo.name.getLocalPart();
		                
		                if ((synchronize.contains(name) || contextInfo.childOfWantElement)  && contextInfo.hasMustElement) {
		                    if (!context.peek().spanOpen && !ch.getData().matches("\\s*")) {
			                    cache.writeEvent(this.getStartElement(), true);
			                    context.peek().spanOpen = true;
		                    }
		                }
	                }
                }
                
                cache.writeEvent(ch, false);
            } else if (event.isStartDocument()) {
                // Check encoding and create writer
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    xew = xof.createXMLEventWriter(new FileOutputStream(output), sd.getCharacterEncodingScheme());
                    cache = EventWriterCache.newInstance(xew, EventWriterCache.NONE);
                    cache.writeEvent(event, false);
                } else {
                    xew = xof.createXMLEventWriter(new FileOutputStream(output), "utf-8");
                    cache = EventWriterCache.newInstance(xew, EventWriterCache.NONE);
                    cache.writeEvent(xef.createStartDocument("utf-8", "1.0"), false);
                }                
            } else {
                cache.writeEvent(event, false);
            }
            
            if (!context.isEmpty()) {
                lastInfo = context.peek();
            }
        }
        
        // Flush the write cache to the output stream
        cache.flush();
        xew.close();
    }
    
    private Map getElementInfo(XMLEventReader xer) throws XMLStreamException, IOException, ConfigurationException, SettingsResolverException {
        Map result = new HashMap();
        context = new AttributedContext();
        int elementNumber = 0;
        boolean rootElementSeen = false;
        SettingsResolver resolver = SettingsResolver.getInstance("type.xml", this.getClass());
        URL configUrl = null;
        while (xer.hasNext()) {
            XMLEvent ev = xer.nextEvent();
            context.register(ev);
            
            if (ev.isStartElement()) {
                StartElement se = ev.asStartElement();
                // If the is the root element, make sure we have a configuration URL and then load the configuration
                if (!rootElementSeen) {    
                    // Do we have a config file URL (via doctype)?
                    if (configUrl == null) {
                        // No doctype has been found. Use root element namespace URI instead 
                        configUrl = resolver.resolve(se.getName().getNamespaceURI());
                        if (configUrl == null) {
                            // This is bad. No configuration file found.
                            throw new ConfigurationException("No configuration file for mixed content normalization found");
                        }
                    }
                    rootElementSeen = true;
                    
                    // Load configuration file for this document type
                    loadConfiguration(configUrl, se);
                }
                
                // Set the number of the current start element
                elementNumber++;                
                context.peek().number = elementNumber;
                result.put(new Integer(elementNumber), context.peek());
                
                // If this element is in the <mustSynchronize> category, register it so
                if (mustSynchronize.contains(se.getName().getLocalPart())) {
                    context.setHasMustElement();
                }
                // If this element is in the <synchronize> category, register it so
                if (synchronize.contains(se.getName().getLocalPart())) {
                    context.setHasWantElement();
                }                
                // If there is a parent element, it has an element child (the current element)
                if (context.hasParent()) {
                    ContextInfo parent = context.getParent();
                    parent.hasElements = true;
                    if (parent.childOfWantElement || synchronize.contains(parent.name.getLocalPart())) {
                        context.peek().childOfWantElement = true;
                    }
                }
            } else if (ev.isCharacters()) {
                /* If this text node is inside an element (i.e. it is not before the root element),
                 * that element has a text child (unless the text is whitespace only)
                 */
                if (!context.isEmpty() && !ev.asCharacters().getData().matches("\\s*")) {
                    context.peek().hasText = true;
                }
            } else if (ev.getEventType() == XMLStreamConstants.DTD) {
                try {
                    configUrl = resolver.parseDoctype(((DTD)ev).getDocumentTypeDeclaration());
                } catch (UnsupportedDocumentTypeException e) {
                    // Ignore this. We might find a namespace URI on the root element we recognize
                } 
            }
        }
        return result;
    }
    
    

    private void loadConfiguration(URL url, StartElement rootElement) throws XMLStreamException, IOException, ConfigurationException {
        XMLEventReader reader = xif.createXMLEventReader(url.openStream());
        boolean mustSynchronizeSection = false;
        boolean synchronizeSection = false;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("element".equals(se.getName().getLocalPart())) {
                    Attribute att = se.getAttributeByName(new QName("name"));
                    if (att != null) {  
                        if (mustSynchronizeSection && !synchronizeSection) {
                            mustSynchronize.add(att.getValue());
                        } else if (synchronizeSection && !mustSynchronizeSection) {
                            synchronize.add(att.getValue());
                        } else {
                            System.err.println("Configuration file error!");
                        }
                    }
                } else if ("mustSynchronize".equals(se.getName().getLocalPart())) {
                    mustSynchronizeSection = true;
                } else if ("synchronize".equals(se.getName().getLocalPart())) {
                    synchronizeSection = true;
                } else if ("spanElement".equals(se.getName().getLocalPart())) {
                    String qnamePref = rootElement.getName().getPrefix();
                    String qnameNS = rootElement.getName().getNamespaceURI();
                    String qnameName = rootElement.getName().getLocalPart();
                    Attribute prefix = se.getAttributeByName(new QName("prefix"));
                    Attribute namespace = se.getAttributeByName(new QName("namespace"));
                    Attribute name = se.getAttributeByName(new QName("name"));
                    qnameName = name.getValue();
                    if (prefix != null && namespace != null) {
                        qnamePref = prefix.getValue();
                        qnameNS = namespace.getValue();
                        if (rootElement.getNamespaceContext().getNamespaceURI(qnamePref) != null && !qnameNS.equals(rootElement.getNamespaceContext().getNamespaceURI(qnamePref))) {                            
                            throw new ConfigurationException("prefix clash: '" + qnamePref + "," + qnameNS + "' and '" + rootElement.getName().getPrefix() + "," + rootElement.getName().getNamespaceURI() + "'");
                        }
                    } else if (prefix != null && namespace == null) {
                        qnamePref = prefix.getValue();
                        qnameNS = rootElement.getNamespaceContext().getNamespaceURI(qnamePref);
                        if (qnameNS != null && !qnameNS.equals(rootElement.getName().getNamespaceURI())) {
                            throw new ConfigurationException("Prefix '" + qnamePref + "' already bound to other namespace: '" + qnameNS + "'");
                        }
                        qnameNS = rootElement.getName().getNamespaceURI();
                    } else if (prefix == null && namespace != null) {
                        qnameNS = namespace.getValue();
                        qnamePref = rootElement.getNamespaceContext().getPrefix(qnameNS);
                        if (qnamePref == null) {
                            throw new ConfigurationException("Prefix for namespace '" + qnameNS + "' must be defined");
                        }
                    } else /*prefix == null && namepace == null*/ {
                        qnamePref = rootElement.getName().getPrefix();
                        qnameNS = rootElement.getName().getNamespaceURI();
                    }
                    /*
                    System.err.println("rootElem:  " + rootElement.getName().getLocalPart());
                    System.err.println("rootPref:  " + rootElement.getName().getPrefix());
                    System.err.println("rootNS:    " + rootElement.getName().getNamespaceURI());
                    System.err.println();
                    System.err.println("element:   " + qnameName);
                    System.err.println("prefix:    " + qnamePref);
                    System.err.println("namespace: " + qnameNS);
                    */
                    tagName = new QName(qnameNS, qnameName, qnamePref);
                }
            } else if (event.isEndElement()) {
                EndElement ee = event.asEndElement();
                if ("mustSynchronize".equals(ee.getName().getLocalPart())) {
                    mustSynchronizeSection = false;
                } else if ("synchronize".equals(ee.getName().getLocalPart())) {
                    synchronizeSection = false;
                }
            }
        }
        reader.close();
    }
    
    private StartElement getStartElement() {
        return xef.createStartElement(tagName, null, null);
    }

    private EndElement getEndElement() {
        return xef.createEndElement(tagName, null);
    }
    
    
}
