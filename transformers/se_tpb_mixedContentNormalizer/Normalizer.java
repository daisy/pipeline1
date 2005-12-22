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
package se_tpb_mixedContentNormalizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
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
    private Set skipList = new HashSet();
    
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
            
            // First pass: record elements with mixed content
            XMLEventReader xer = xif.createXMLEventReader(new FileInputStream(input));
            Set mixedContentElements = this.getMixedContentList(xer);
            sendMessage(Level.INFO, mixedContentElements.size() + " mixed content elements found.");
            xer.close();
            
            // Second pass: normalize elements with mixed content
            xer = xif.createXMLEventReader(new FileInputStream(input));
            this.performNormalization(xer, output, mixedContentElements);
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
    
    /**
     * Performs the mixed content nomalization.
     * @param xer
     * @param output
     * @param mixedContentElements
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private void performNormalization(XMLEventReader xer, String output, Set mixedContentElements) throws FileNotFoundException, XMLStreamException {
        context = new AttributedContext();
        AttributedContext.ContextInfo lastInfo = null;
        EventWriterCache cache = null;
        int elementNumber = 0;
        XMLEventWriter xew = null;
        boolean rootElementSeen = false;
        
        while (xer.hasNext()) {
            XMLEvent ev = xer.nextEvent();
            context.register(ev);
            if (ev.isStartElement()) {                
                elementNumber++;
                // Set the number of the current start element
                context.peek().number = elementNumber;
                
                // Was there an open span to close? Don't do it if element is in skip list
                if (context.hasParent() && context.getParent().spanOpen && !skippable(context)) {
                    cache.writeEvent(this.getEndElement(), true);
                    context.getParent().spanOpen = false;
                }
                
                if (!rootElementSeen) {
                    rootElementSeen = true;
                    StartElement se = ev.asStartElement();
                    Collection coll = new ArrayList();                    
                    for (Iterator it = se.getNamespaces(); it.hasNext(); ) {
                        coll.add(it.next());
                    }
                    // Add namespace declaration if needed
                    if (se.getNamespaceContext().getNamespaceURI(tagName.getPrefix()) == null || se.getNamespaceContext().getPrefix(tagName.getNamespaceURI()) == null) {
		                xew.setPrefix(tagName.getPrefix(), tagName.getNamespaceURI());
		                coll.add(xef.createNamespace(tagName.getPrefix(), tagName.getNamespaceURI()));
                    }
                    cache.writeEvent(xef.createStartElement(se.getName(), se.getAttributes(), coll.iterator()), false);                    
                } else {                
                    // Write event
                    cache.writeEvent(ev, false);
                }
                
                // Open span if this is a mixed content element
                if (mixedContentElements.contains(Integer.valueOf(elementNumber))) {
                    cache.writeEvent(this.getStartElement(), true);
                    context.peek().spanOpen = true; 
                }
                
            } else if (ev.isCharacters()) {
                int currentNumber = context.peek().number;
                
                // If the text is in a mixed content element, and span is not open, open it
                if (mixedContentElements.contains(Integer.valueOf(currentNumber)) && !context.peek().spanOpen) {
                    cache.writeEvent(this.getStartElement(), true);
                    context.peek().spanOpen = true;                        
                }
                
                // Write text
                cache.writeEvent(ev, false);
                
            } else if (ev.isEndElement()) {
                // Was there an open span to close?
                if (lastInfo.spanOpen) {
                    cache.writeEvent(this.getEndElement(), true);                  
                }
                
                // Write end element
                cache.writeEvent(ev, false);
                
            } else if (ev.isStartDocument()) {
                // Check encoding and create writer
                StartDocument sd = (StartDocument)ev;
                if (sd.encodingSet()) {
                    xew = xof.createXMLEventWriter(new FileOutputStream(output), sd.getCharacterEncodingScheme());
                    cache = EventWriterCache.newInstance(xew, EventWriterCache.RELAXED);
                    cache.writeEvent(ev, false);
                } else {
                    xew = xof.createXMLEventWriter(new FileOutputStream(output), "utf-8");
                    cache = EventWriterCache.newInstance(xew, EventWriterCache.RELAXED);
                    cache.writeEvent(xef.createStartDocument("utf-8", "1.0"), false);
                }                
            } else {
                // Just copy all other events
                cache.writeEvent(ev, false);
            }
            
            /* If the next event we bump into is an end element, it
               removes an item from the context stack. Before reading
               the next event, save the last context so we can query
               it later. */ 
            if (!context.isEmpty()) {
                lastInfo = context.peek();
            }
        }
        
        // Flush the write cache to the output stream
        cache.flush();        
        
        xew.close();
    }
    
    /**
     * Find all elements containing mixed content. Each element gets its own
     * number, determined by the position in the stream of the start element.
     * If both start element nodes and text nodes is found in a specific element,
     * its number is added to the set of mixed content node numbers.
     * @throws XMLStreamException
     * @throws IOException
     * @throws ConfigurationException
     * @throws SettingsResolverException
     */    
    private Set getMixedContentList(XMLEventReader xer) throws XMLStreamException, IOException, ConfigurationException, SettingsResolverException {
        Set result = new HashSet();
        context = new AttributedContext();
        int elementNumber = 0;
        boolean rootElementSeen = false;
        SettingsResolver resolver = SettingsResolver.getInstance("type.xml", this.getClass());
        URL configUrl = null;
        while (xer.hasNext()) {
            XMLEvent ev = xer.nextEvent();
            context.register(ev);
            
            if (ev.isStartElement()) {
                // If the is the root element, make sure we have a configuration URL and then load the configuration
                if (!rootElementSeen) {    
                    // Do we have a config file URL (via doctype)?
                    if (configUrl == null) {
                        // No doctype has been found. Use root element namespace URI instead 
                        configUrl = resolver.resolve(ev.asStartElement().getName().getNamespaceURI());
                        if (configUrl == null) {
                            // This is bad. No configuration file found.
                            throw new ConfigurationException("No configuration file for mixed content normalization found");
                        }
                    }
                    rootElementSeen = true;
                    
                    // Load configuration file for this document type
                    loadConfiguration(configUrl, ev.asStartElement());
                }
                
                // Set the number of the current start element
                elementNumber++;                
                context.peek().number = elementNumber;
                
                // If there is a parent element, it has an element child (the current element)
                if (context.hasParent() && !skippable(context)) {
                    context.getParent().hasElements = true;
                    maybeAddToList(result, context.getParent());
                }
            } else if (ev.isCharacters()) {
                /* If this text node is inside an element (i.e. it is not before the root element),
                 * that element has a text child (unless the text is whitespace only)
                 */
                if (!context.isEmpty() && !ev.asCharacters().getData().matches("\\s*")) {
                    context.peek().hasText = true;
                    maybeAddToList(result, context.peek());
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
    
    /**
     * If both element nodes and text nodes has been seen inside the specified
     * ContextInfo, add its number to the list of mixed content node numbers.
     * @param result
     * @param info
     */
    private void maybeAddToList(Set result, AttributedContext.ContextInfo info) {
        // Check if element has both text and element children
        if (info.hasElements && info.hasText) {
            if (result.add(Integer.valueOf(info.number))) {
                //System.err.println("Element " + info.name.getLocalPart() + " (number " + info.number + ") added");
            }
        }
    }

    private void loadConfiguration(URL url, StartElement rootElement) throws XMLStreamException, IOException, ConfigurationException {
        XMLEventReader reader = xif.createXMLEventReader(url.openStream());
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("element".equals(se.getName().getLocalPart())) {
                    Attribute att = se.getAttributeByName(new QName("name"));
                    if (att != null) {                        
                        skipList.add(att.getValue());
                    }
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
    
    private boolean skippable(AttributedContext attributedContext) {
        return skipList.contains(attributedContext.peek().name.getLocalPart());
    }
    
}
