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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Gives the final touch to master.smil by updating the ncc:timeInThisSmil
 *
 * Should this class be moved to the util library?
 * 
 * @author Linus Ericson
 */
class MasterSmilFileClockFixer {

    private XMLInputFactory factory = null;
    private StaxFilter filter = null;
    
    public MasterSmilFileClockFixer() throws CatalogExceptionNotRecoverable {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);        
    }
    
    public SmilClock fix(File inFile, File outFile, SmilClock totalElapsedTime) throws XMLStreamException, IOException {       
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(inFile));        
        
        // Update ncc:timeInThisSmil
        reader = factory.createXMLEventReader(new FileInputStream(inFile));
        OutputStream os = new FileOutputStream(outFile);
        filter = new MyMasterSmilClock(reader, os, totalElapsedTime);
        filter.filter();
        os.close();
        reader.close();
        return totalElapsedTime;
    }
    
    private class MyMasterSmilClock extends StaxFilter {
        SmilClock totalElapsedTime = new SmilClock(0);
        boolean firstSeq = true;        
        public MyMasterSmilClock(XMLEventReader xer, OutputStream os, SmilClock totalElapsedTime2) throws XMLStreamException {
            super(xer, os);
            totalElapsedTime = totalElapsedTime2;            
        }
        protected StartElement startElement(StartElement se) {
            if ("meta".equals(se.getName().getLocalPart())) {
                Attribute name = se.getAttributeByName(new QName("name"));
                if (name!=null && "ncc:timeInThisSmil".equals(name.getValue())) {
                	// Update ncc:timeInThisSmil
                    SmilClock roundedTotalElapsedTime = new SmilClock(totalElapsedTime.secondsValueRoundedDouble());
                    Attribute content = this.getEventFactory().createAttribute("content", roundedTotalElapsedTime.toString());
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(name);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(se.getName(), coll.iterator(), se.getNamespaces());
                    return result;
                }
            }
            return se;
        }
    }
    
}
