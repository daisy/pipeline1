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
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;

/**
 * @author Linus Ericson
 */
public class XMLAbbrDetector extends XMLWordDetector {

    private ContextAwareAbbrSettings caas = null;
    
    /**
     * @param inFile
     * @param outFile
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public XMLAbbrDetector(File inFile, File outFile) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        this(inFile, outFile, null, false);
    }
    
    /**
     * @param inFile
     * @param outFile
     * @throws CatalogExceptionNotRecoverable
     * @throws XMLStreamException
     * @throws IOException
     */
    public XMLAbbrDetector(File inFile, File outFile, URL customLang, boolean override) throws CatalogExceptionNotRecoverable, XMLStreamException, IOException {
        super(inFile, outFile);
        Set<String> xmllang = LangDetector.getXMLLangSet(inFile);
        caas = new ContextAwareAbbrSettings(); 
        setBreakSettings(caas);
        setContextStackFilter(caas);
        setBreakFinder(new DefaultAbbrBreakFinder(xmllang, customLang, override));        
    }

    protected void handleBreaks(String data) throws XMLStreamException {
        Vector<?> breaks = breakFinder.findBreaks(data, null);
        //System.err.println(breaks);  
        writeElements(data, breaks);
    }
    
    private void writeElements(String text, Vector<?> breaks) throws XMLStreamException {
        if (breaks.size() == 0) {
            writeString(text);
            return;
        }
        int oldEnd = 0;
        for (Iterator<?> it = breaks.iterator(); it.hasNext(); ) {
            Abbr abbr = (Abbr)it.next();
            int start = abbr.getStart();
            int end = abbr.getEnd();
            if (oldEnd < start) {
                writeString(text.substring(oldEnd, start));
            }
            String exp = abbr.getExpansion(); 
            QName expAttrQName = caas.getExpAttr();
            String expAtt = abbr.getExpAttr();
            switch (abbr.getType()) {
            case Abbr.INITIALISM:
                writeEvent(eventFactory.createStartElement(caas.getInitialismElement(), getBreakAttributes(caas.getInitialismAttributes(), caas.getInitialismExpandAttribute(), exp, expAttrQName, expAtt), null));
            	writeString(text.substring(start, end));
            	writeEvent(eventFactory.createEndElement(caas.getInitialismElement(), null));            	
                break;
            case Abbr.ACRONYM:
                writeEvent(eventFactory.createStartElement(caas.getAcronymElement(), getBreakAttributes(caas.getAcronymAttributes(), caas.getAcronymExpandAttribute(), exp, expAttrQName, expAtt), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getAcronymElement(), null));
                break;
            case Abbr.ABBREVIATION:
                writeEvent(eventFactory.createStartElement(caas.getAbbrElement(), getBreakAttributes(caas.getAbbrAttributes(), caas.getAbbrExpandAttribute(), exp, expAttrQName, expAtt), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getAbbrElement(), null));
                break;
            case Abbr.FIX:
                writeEvent(eventFactory.createStartElement(caas.getFixElement(), getBreakAttributes(caas.getFixAttributes(), caas.getFixExpandAttribute(), exp, expAttrQName, expAtt), null));
        		writeString(text.substring(start, end));
        		writeEvent(eventFactory.createEndElement(caas.getFixElement(), null));
                break;
            }
            oldEnd = end;
        }        
        writeString(text.substring(oldEnd));
    }
    
    private Iterator<Attribute> getBreakAttributes(Map<?,?> map, String att, String exp, QName expAttName, String expAttValue) {
        Iterator<Attribute> result = null;
        Vector<Attribute> v = new Vector<Attribute>();
        if (map != null) {
            for (Iterator<?> it = map.keySet().iterator(); it.hasNext(); ) {
                String name = (String)it.next();
                String value = (String)map.get(name);
                Attribute attr = eventFactory.createAttribute(name, value);
                v.add(attr);
            }
            result = v.iterator();
        }
        if (att != null && exp != null) {
            Attribute attr = eventFactory.createAttribute(att, exp);
            v.add(attr);
            result = v.iterator();
        }
        if (expAttName != null && expAttValue != null) {
            Attribute attr = eventFactory.createAttribute(expAttName, expAttValue);
            v.add(attr);
            result = v.iterator();
        }
        return result;
    }
    
    public static void main(String[] args) throws UnsupportedDocumentTypeException {
        XMLAbbrDetector detector;

        try {
            detector = new XMLAbbrDetector(new File(args[0]), new File(args[1]));
            detector.detect(null);
        } catch (CatalogExceptionNotRecoverable e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
}
