/*
 * Daisy Pipeline
 * Copyright (C) 2008  Daisy Consortium
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
package se_tpb_skippabilityTweaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.file.Directory;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Gives the final touch to SMIL files.
 * Updates the ncc:timeInThisSmil, ncc:totalElapsedTime and the dur attribute
 * of the main seq.
 * 
 * Should this class be moved to the util library?
 * 
 * @author Linus Ericson
 */
class SmilFileClockFixer {

    private XMLInputFactory factory = null;
    private StaxFilter filter = null;
    
    public SmilFileClockFixer() throws CatalogExceptionNotRecoverable {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);        
    }
        
    public long fix(Iterator<D202SmilFile> smilFileIterator, Directory outputFolder, long totalElapsedTime) throws XMLStreamException, IOException {
    	while (smilFileIterator.hasNext()) {
    		D202SmilFile smilFile = smilFileIterator.next();
    		File outFile = new File(outputFolder, smilFile.getName());
    		totalElapsedTime = this.fix(smilFile.getFile(), outFile, totalElapsedTime);
    	}
    	return totalElapsedTime;
    }
    
    public long fix(File inFile, File outFile, long totalElapsedTime) throws XMLStreamException, IOException {
        long timeInThisSmil = 0;        
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(inFile));
        
        // Calculate timeInThisSmil by checking all audio clips of the SMIL
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                
                if ("audio".equals(se.getName().getLocalPart())) {
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
        filter = new MySmilClock(reader, os, timeInThisSmil, totalElapsedTime);
        filter.filter();
        os.close();
        reader.close();
        return timeInThisSmil;
    }
    
    private class MySmilClock extends StaxFilter {
        long timeInThisSmil = 0;
        long totalElapsedTime = 0;
        boolean firstSeq = true;        
        public MySmilClock(XMLEventReader xer, OutputStream os, long smilTime, long totalTime) throws XMLStreamException {
            super(xer, os);
            timeInThisSmil = smilTime;
            totalElapsedTime = totalTime;            
        }
        protected StartElement startElement(StartElement se) {
            if ("meta".equals(se.getName().getLocalPart())) {
                Attribute name = se.getAttributeByName(new QName("name"));
                if (name!=null && "ncc:timeInThisSmil".equals(name.getValue())) {
                	// Update ncc:timeInThisSmil
                    long diff = timeInThisSmil % 1000;
                    long timeInSmil = 0;
                    if (diff >= 500) {
                        timeInSmil = timeInThisSmil + (1000-diff);
                    } else {
                        timeInSmil = timeInThisSmil - diff;
                    }
                    SmilClock sc = new SmilClock(timeInSmil);                    
                    Attribute content = this.getEventFactory().createAttribute("content", sc.toString());
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(name);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                } else if (name!=null && "ncc:totalElapsedTime".equals(name.getValue())) {
                	// Update ncc:totalElapsedTime
                    long diff = totalElapsedTime % 1000;
                    long totalTime = 0;
                    if (diff >= 500) {
                        totalTime = totalElapsedTime + (1000-diff);
                    } else {
                        totalTime = totalElapsedTime - diff;
                    }
                    SmilClock sc = new SmilClock(totalTime);
                    Attribute content = this.getEventFactory().createAttribute("content", sc.toString());
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(name);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                }
            } else if ("seq".equals(se.getName().getLocalPart())) {
                if (firstSeq) {
                	// Add the dur attribute to the first seq
                    firstSeq = false;
                    SmilClock sc = new SmilClock(timeInThisSmil);
                    Attribute dur = this.getEventFactory().createAttribute("dur", sc.toString(SmilClock.TIMECOUNT_SEC));
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(dur);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                }
                
            } else if ("audio".equals(se.getName().getLocalPart())) {
                Attribute clipBegin = se.getAttributeByName(new QName("clip-begin"));
                Attribute clipEnd = se.getAttributeByName(new QName("clip-end"));
                String begin = "npt=" + new SmilClock(clipBegin.getValue()).toString(SmilClock.TIMECOUNT_SEC);
                String end = "npt=" + new SmilClock(clipEnd.getValue()).toString(SmilClock.TIMECOUNT_SEC);
                Collection<Attribute> coll = new ArrayList<Attribute>();         
                // Make sure the attributes are in the order src, clip-begin, clip-end, id to
                // make all players happy
                coll.add(se.getAttributeByName(new QName("src")));                
                coll.add(this.getEventFactory().createAttribute("clip-begin", begin));
                coll.add(this.getEventFactory().createAttribute("clip-end", end));
                coll.add(se.getAttributeByName(new QName("id")));
                StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                return result;
            }
            return se;
        }
        /*
		protected Characters characters(Characters event) {
			// Convert to DOS line endings to make all players happy
			return this.getEventFactory().createCharacters(event.getData().replace("\r\n", "\n").replace("\n", "\r\n"));
		} 
		*/       
    }
    
}
