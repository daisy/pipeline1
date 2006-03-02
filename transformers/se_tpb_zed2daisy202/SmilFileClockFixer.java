/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
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
package se_tpb_zed2daisy202;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Gives the final touch to SMIL files.
 * Updates the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute
 * of the main seq.
 * @author Linus Ericson
 */
class SmilFileClockFixer {

    private XMLInputFactory factory = null;
    private StaxFilter filter = null;
    
    public SmilFileClockFixer() throws CatalogExceptionNotRecoverable {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        //factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);        
    }
    
    /**
     * Fixes a SMIL file.
     * @param inFile
     * @param outFile
     * @param totalElapsedTime
     * @param fragment
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    public long fix(File inFile, File outFile, long totalElapsedTime, String fragment) throws XMLStreamException, IOException {
        long timeInThisSmil = 0;
        boolean idForFragmentSeen = false;
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(inFile));
        
        // Calculate timeInThisSmil by checking all audio clips of the SMIL
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                Attribute id = se.getAttributeByName(new QName("id"));
                if (fragment==null || (id!=null && id.getValue().equals(fragment)) ) {
                    idForFragmentSeen = true;
                }
                if (idForFragmentSeen && "audio".equals(se.getName().getLocalPart())) {
                    Attribute clipBegin = se.getAttributeByName(new QName("clip-begin"));
                    Attribute clipEnd = se.getAttributeByName(new QName("clip-end"));                    
                    timeInThisSmil += new SmilClock(clipEnd.getValue()).millisecondsValue();
                    timeInThisSmil -= new SmilClock(clipBegin.getValue()).millisecondsValue();
                }                
            }
        }
        reader.close();
        
        //System.err.println("Time in this smil: " + timeInThisSmil);

        // Update ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute of the main seq.
        reader = factory.createXMLEventReader(new FileInputStream(inFile));
        OutputStream os = new FileOutputStream(outFile);
        filter = new MySmilClock(reader, os, timeInThisSmil, totalElapsedTime, fragment);
        filter.filter();
        os.close();
        reader.close();
        return timeInThisSmil;
    }
    
    private class MySmilClock extends StaxFilter {
        long timeInThisSmil = 0;
        long totalElapsedTime = 0;
        boolean firstSeq = true;
        boolean idForFragmentSeen = false;
        String fragment = null;
        public MySmilClock(XMLEventReader xer, OutputStream os, long smilTime, long totalTime, String idFragment) throws XMLStreamException {
            super(xer, os);
            timeInThisSmil = smilTime;
            totalElapsedTime = totalTime;
            fragment = idFragment;
        }
        protected StartElement startElement(StartElement se) {
            Attribute id = se.getAttributeByName(new QName("id"));
            if (fragment==null || (id!=null && id.getValue().equals(fragment)) ) {
                idForFragmentSeen = true;
            }
            if ("meta".equals(se.getName().getLocalPart())) {
                Attribute name = se.getAttributeByName(new QName("name"));
                if (name!=null && "ncc:timeInThisSmil".equals(name.getValue())) {
                    long diff = timeInThisSmil % 1000;
                    if (diff >= 500) {
                        timeInThisSmil += (1000-diff);
                    } else {
                        timeInThisSmil -= diff;
                    }
                    SmilClock sc = new SmilClock(timeInThisSmil);                    
                    Attribute content = this.getEventFactory().createAttribute("content", sc.toString());
                    Collection coll = new ArrayList();
                    coll.add(name);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                } else if (name!=null && "ncc:totalElapsedTime".equals(name.getValue())) {
                    long diff = totalElapsedTime % 1000;
                    if (diff >= 500) {
                        totalElapsedTime += (1000-diff);
                    } else {
                        totalElapsedTime -= diff;
                    }
                    SmilClock sc = new SmilClock(totalElapsedTime);
                    Attribute content = this.getEventFactory().createAttribute("content", sc.toString());
                    Collection coll = new ArrayList();
                    coll.add(name);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                }
            } else if ("seq".equals(se.getName().getLocalPart())) {
                if (firstSeq) {
                    firstSeq = false;
                    SmilClock sc = new SmilClock(timeInThisSmil);
                    Attribute dur = this.getEventFactory().createAttribute("dur", sc.toString(SmilClock.TIMECOUNT_SEC));
                    Collection coll = new ArrayList();
                    coll.add(dur);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                }
                if (!idForFragmentSeen) {
                    return null;
                }
            } else if ("par".equals(se.getName().getLocalPart())) {
                if (!idForFragmentSeen) {
                    return null;
                }
            } else if ("audio".equals(se.getName().getLocalPart())) {
                Attribute clipBegin = se.getAttributeByName(new QName("clip-begin"));
                Attribute clipEnd = se.getAttributeByName(new QName("clip-end"));
                String begin = "npt=" + new SmilClock(clipBegin.getValue()).toString(SmilClock.TIMECOUNT_SEC);
                String end = "npt=" + new SmilClock(clipEnd.getValue()).toString(SmilClock.TIMECOUNT_SEC);
                Collection coll = new ArrayList();                
                coll.add(se.getAttributeByName(new QName("src")));
                coll.add(se.getAttributeByName(new QName("id")));
                coll.add(this.getEventFactory().createAttribute("clip-begin", begin));
                coll.add(this.getEventFactory().createAttribute("clip-end", end));
                StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                return result;
            }
            return se;
        }
    }
    
}
