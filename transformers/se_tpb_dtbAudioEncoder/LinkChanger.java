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
package se_tpb_dtbAudioEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.pool.StAXInputFactoryPool;

import com.ctc.wstx.api.WstxOutputProperties;

/**
 * @author Linus Ericson
 */
class LinkChanger {
    
    private XMLInputFactory inputFactory = null;
    private XMLEventFactory eventFactory = null;
    private XMLOutputFactory outputFactory = null;
    
    public LinkChanger() throws CatalogExceptionNotRecoverable {
    	StAXInputFactoryPool pool = StAXInputFactoryPool.getInstance();
        inputFactory = pool.acquire(pool.getDefaultPropertyMap(false));
        eventFactory = XMLEventFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
        if (outputFactory.isPropertySupported(WstxOutputProperties.P_COPY_DEFAULT_ATTRS )) {
        	outputFactory.setProperty(WstxOutputProperties.P_COPY_DEFAULT_ATTRS, Boolean.TRUE);
        }
    }
    
    public void changeLinksOpf(File inputFile, File outputFile, String regex, String replacement, String mediaType) throws FileNotFoundException, XMLStreamException {
        XMLEventReader reader = inputFactory.createXMLEventReader(new FileInputStream(inputFile));
        XMLEventWriter writer = null;
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("item".equals(se.getName().getLocalPart())) {
                    Attribute srcAtt = se.getAttributeByName(new QName("href"));
                    //System.err.println("att: " + srcAtt.getValue());
                    int index = srcAtt.getValue().lastIndexOf(".");
                    if (index != -1 && srcAtt.getValue().substring(index+1).matches(regex)) {
                        String newAtt = srcAtt.getValue().substring(0, index) + "." + replacement;
                        //System.err.println("newAtt: " + newAtt);
                        Collection<Attribute> coll = new ArrayList<Attribute>();
                        for (Iterator<?> it = se.getAttributes(); it.hasNext(); ) {
                            Attribute att = (Attribute)it.next();
                            if (att.getName().getLocalPart().equals(srcAtt.getName().getLocalPart())) {
                                coll.add(eventFactory.createAttribute("href", newAtt));
                            } else if (att.getName().getLocalPart().equals("media-type")) {
                                coll.add(eventFactory.createAttribute("media-type", mediaType));
                            } else {
                                coll.add(att);
                            }
                        }
                        writer.add(eventFactory.createStartElement(se.getName(), coll.iterator(), se.getNamespaces()));
                    } else {
                        writer.add(event);
                    }
                } else {
                    writer.add(event);
                }
            } else if (event.isStartDocument()) { 
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), sd.getCharacterEncodingScheme());
                    writer.add(event);
                } else {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
                    writer.add(eventFactory.createStartDocument("utf-8", "1.0"));                    
                }
            } else {
                writer.add(event);
            }
        }
        reader.close();
        writer.close();
    }
    
    public void changeLinks(File inputFile, File outputFile, String regex, String replacement) throws FileNotFoundException, XMLStreamException {
        XMLEventReader reader = inputFactory.createXMLEventReader(new FileInputStream(inputFile));
        XMLEventWriter writer = null;
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("audio".equals(se.getName().getLocalPart())) {
                    Attribute srcAtt = se.getAttributeByName(new QName("src"));
                    //System.err.println("att: " + srcAtt.getValue());
                    int index = srcAtt.getValue().lastIndexOf(".");
                    if (index != -1 && srcAtt.getValue().substring(index+1).matches(regex)) {
                        String newAtt = srcAtt.getValue().substring(0, index) + "." + replacement;
                        //System.err.println("newAtt: " + newAtt);
                        Collection<Attribute> coll = new ArrayList<Attribute>();
                        for (Iterator<?> it = se.getAttributes(); it.hasNext(); ) {
                            Attribute att = (Attribute)it.next();
                            if (att.getName().getLocalPart().equals("src")) {
                                coll.add(eventFactory.createAttribute("src", newAtt));
                            } else {
                                coll.add(att);
                            }
                        }
                        writer.add(eventFactory.createStartElement(se.getName(), coll.iterator(), se.getNamespaces()));
                    } else {
                        writer.add(event);
                    }
                } else {
                    writer.add(event);
                }
            } else if (event.isStartDocument()) { 
                StartDocument sd = (StartDocument)event;
                if (sd.encodingSet()) {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), sd.getCharacterEncodingScheme());
                    writer.add(event);
                } else {
                    writer = outputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
                    writer.add(eventFactory.createStartDocument("utf-8", "1.0"));                    
                }
            } else {
                writer.add(event);
            }
        }
        reader.close();
        writer.close();
    }
}
