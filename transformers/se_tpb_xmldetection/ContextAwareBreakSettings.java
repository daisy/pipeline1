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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.settings.SettingsResolver;
import org.daisy.util.xml.settings.SettingsResolverException;
import org.daisy.util.xml.stax.ContextStack;

/**
 * @author Linus Ericson
 */
/*package*/ class ContextAwareBreakSettings extends ContextStack implements BreakSettings {

    protected SettingsResolver resolver = null;
    protected boolean sentenceInstance = false;
    protected QName sentenceElement = null;
    protected QName wordElement = null;
    protected QName abbrElement = null;
    protected QName acronymElement = null;
    protected QName initialismElement = null;
    protected QName fixElement = null;
    
    protected Map sentenceAttributes = new HashMap();
    protected Map wordAttributes = new HashMap();
    protected Map abbrAttributes = new HashMap();
    protected Map acronymAttributes = new HashMap();
    protected Map initialismAttributes = new HashMap();
    protected Map fixAttributes = new HashMap();
    
    protected String initialismExpand = null;
    protected String acronymExpand = null;
    protected String abbrExpand = null;
    protected String fixExpand = null;
    
    protected Set nonSentenceBreaking = new HashSet();
    protected Set sentenceSkip = new HashSet();
    protected Set wordSkip = new HashSet();
    protected Set mayContainText = new HashSet();
    
    protected Set defaultPaths = new HashSet();
    
    public ContextAwareBreakSettings(boolean sentence) throws CatalogExceptionNotRecoverable {
        try {
            //resolver = BreakSettingsResolver.getInstance();
            resolver = SettingsResolver.getInstance("type.xml", this.getClass());
        } catch (SettingsResolverException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sentenceInstance = sentence;
    }
       
    public boolean isSentenceBreaking(QName elementName) {
        String local = elementName.getLocalPart();
        if (nonSentenceBreaking.contains(local)) {
            return false;
        }
        return true;
    }

    public boolean skipContent(QName elementName) {
        Set skipSet = wordSkip;
        if (sentenceInstance) {
            skipSet = sentenceSkip;
        }
        String local = elementName.getLocalPart();
        if (skipSet.contains(local)) {
            return true;
        }
        return false;
    }
        
    public boolean mayContainText(QName elementName) {
        String local = elementName.getLocalPart();
        if (mayContainText.contains(local)) {
            return true;
        }
        return false;
    }

    public boolean setup(String publicId, String systemId) throws UnsupportedDocumentTypeException {
        try {
            URL url = resolver.resolve(publicId, systemId);            
            if (url == null) {
                throw new UnsupportedDocumentTypeException("Unsupported document type.", publicId, systemId);
            }
            parseXml(url);
        } catch (IOException e) {
            // FIXME do something uselful here
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // FIXME do something uselful here
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean setup(String namespaceURI) {        
        try {
            URL url = resolver.resolve(namespaceURI);
            parseXml(url);
            return true;
        } catch (XMLStreamException e) {
            // FIXME do something uselful here
            e.printStackTrace();
        } catch (IOException e) {
            // FIXME do something uselful here
            e.printStackTrace();
        }
        return false;
    }

    protected void parseXml(URL url) throws XMLStreamException, IOException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        
        XMLEventReader er = factory.createXMLEventReader(url.openStream());
        
        Set currentSet = null;
        Map sentOrWordAttributes = null;
        
        while (er.hasNext()) {
            XMLEvent event = er.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if (se.getName().getLocalPart().equals("element")) {
                    Attribute name = se.getAttributeByName(new QName("name"));
                    if (name == null) {
                        throw new RuntimeException("A name of the element must be declared");
                    }
                    currentSet.add(name.getValue());
                } else if (se.getName().getLocalPart().equals("nonSentenceBreaking")) {
                    currentSet = nonSentenceBreaking;
                } else if (se.getName().getLocalPart().equals("sentenceSkip")) {
                    currentSet = sentenceSkip;
                } else if (se.getName().getLocalPart().equals("wordSkip")) {
                    currentSet = wordSkip;
                } else if (se.getName().getLocalPart().equals("mayContainText")) {
                    currentSet = mayContainText;
                } else if (se.getName().getLocalPart().equals("sentenceElement")) {
                    sentenceElement = getQName(se);
                    sentOrWordAttributes = sentenceAttributes;
                } else if (se.getName().getLocalPart().equals("wordElement")) {
                    wordElement = getQName(se);
                    sentOrWordAttributes = wordAttributes;
                } else if (se.getName().getLocalPart().equals("abbrElement")) {
                    abbrExpand = getExpandAttribute(se);
                    abbrElement = getQName(se);
                    sentOrWordAttributes = abbrAttributes;
                } else if (se.getName().getLocalPart().equals("acronymElement")) {
                    acronymExpand = getExpandAttribute(se);
                    acronymElement = getQName(se);
                    sentOrWordAttributes = acronymAttributes;
                } else if (se.getName().getLocalPart().equals("initialismElement")) {
                    initialismExpand = getExpandAttribute(se);
                    initialismElement = getQName(se);
                    sentOrWordAttributes = initialismAttributes;
                } else if (se.getName().getLocalPart().equals("fixElement")) {
                    fixExpand = getExpandAttribute(se);
                    fixElement = getQName(se);
                    sentOrWordAttributes = fixAttributes;
                } else if (se.getName().getLocalPart().equals("attribute")) {
                    Attribute name = se.getAttributeByName(new QName("name"));
                    Attribute value = se.getAttributeByName(new QName("value"));
                    if (name == null) {
                        throw new RuntimeException("A name of the attribute must be declared");
                    }
                    if (value == null) {
                        throw new RuntimeException("A value of the attribute must be declared");
                    }
                    sentOrWordAttributes.put(name.getValue(), value.getValue());                    
                } else if (se.getName().getLocalPart().equals("path")) {
                    Attribute name = se.getAttributeByName(new QName("name"));
                    if (name == null) {
                        throw new RuntimeException("A name of the path must be declared");
                    }
                    defaultPaths.add(name.getValue());
                }
            } else if (event.isEndElement()) {
                EndElement ee = event.asEndElement();
                if (ee.getName().getLocalPart().equals("nonSentenceBreaking")) {
                    currentSet = null;
                } else if (ee.getName().getLocalPart().equals("sentenceSkip")) {
                    currentSet = null;
                } else if (ee.getName().getLocalPart().equals("wordSkip")) {
                    currentSet = null;
                } else if (ee.getName().getLocalPart().equals("mayContainText")) {
                    currentSet = null;
                } 
            } /*else if (event.isCharacters() && currentSet!=null) {
                Characters ch = event.asCharacters();
                String text = ch.getData();
                currentSet.add(text);
            }   */         
        }
                
        if (wordElement == null) {
            throw new RuntimeException("No word element was declared");
        } else if (sentenceElement == null) {
            throw new RuntimeException("No sentence element was declared");
        }
        
        er.close();
    }
    
    private String getExpandAttribute(StartElement se) {
        Attribute expand = se.getAttributeByName(new QName("expandAttribute"));
        if (expand != null) {
            return expand.getValue();
        } 
        return null;        
    }
    
    private QName getQName(StartElement se) {
        Attribute namespace = se.getAttributeByName(new QName("namespace"));
        Attribute prefix = se.getAttributeByName(new QName("prefix"));
        Attribute name = se.getAttributeByName(new QName("name"));
        if (prefix == null) {
            throw new RuntimeException("A prefix for '" + se.getName().getLocalPart() + "' must be declared (may be empty string)");
        }
        if (name == null) {
            throw new RuntimeException("A name for '" + se.getName().getLocalPart() + "' must be declared");
        }
        return new QName(namespace==null?null:namespace.getValue(), name.getValue(), prefix.getValue());
    }
    
    public QName getBreakElement() {
        if (sentenceInstance) {
            return sentenceElement;  
        }
        return wordElement;           
    }
    
    public Map getBreakAttributes() {
        if (sentenceInstance) {
            return sentenceAttributes;  
        }
        return wordAttributes;           
    }


    public Set getDefaultPaths() {
        return defaultPaths;
    }

    public QName getAbbrElement() {
        return abbrElement;
    }
    
    public QName getAcronymElement() {
        return acronymElement;
    }
    
    public QName getInitialismElement() {
        return initialismElement;
    }
    
    public QName getFixElement() {
        return fixElement;
    }
    
    public Map getAbbrAttributes() {
        return abbrAttributes;
    }
    
    public Map getAcronymAttributes() {
        return acronymAttributes;
    }
    
    public Map getInitialismAttributes() {
        return initialismAttributes;
    }
    
    public Map getFixAttributes() {
        return fixAttributes;
    }
    
    public String getInitialismExpandAttribute() {
        return initialismExpand;
    }
    
    public String getAcronymExpandAttribute() {
        return acronymExpand;
    }
    
    public String getAbbrExpandAttribute() {
        return abbrExpand;
    }
    
    public String getFixExpandAttribute() {
        return fixExpand;
    }
    
}
