/*
 * Daisy Pipeline (C) 2005-2009 Daisy Consortium
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
package se_tpb_daisy202splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.LinkedHashSet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.stax.AttributeByName;

/**
 * Class responsible for building a prompt set
 * @author Linus Ericson
 */
public class PromptSetBuilder {

    private XMLInputFactory xif;
    
    /**
     * Constructor.
     * @param xif an XMLinputFactory
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public PromptSetBuilder(XMLInputFactory xif) throws FileNotFoundException, XMLStreamException {
        this.xif = xif;
    }

    /**
     * Gets the prompt set for the specified language.
     * <p>If a prompt set for the specified language cannot be found, the first prompt set
     * found in the config file is used.
     * </p>
     * @param manifestFile the prompt file manifest
     * @param language the requested language of the prompts
     * @return a prompt set
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public PromptSet getPromptSet(File manifestFile, String language) throws FileNotFoundException, XMLStreamException {
    	// The config file is parsed twice. In the first parse, the set of available languages
    	// is collected. In the second parse, the prompt set for the requested language is built.
    	
        LinkedHashSet<String> languages = new LinkedHashSet<String>();
        XMLEventReader reader = null;
        PromptSet promptSet = new PromptSet();
        // Collect available languages
        reader = xif.createXMLEventReader(new FileInputStream(manifestFile));
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("set".equals(se.getName().getLocalPart())) {
                    Attribute langAttr = AttributeByName.get(new QName("lang"), se);
                    if (langAttr != null) {
                    	// A new language was found
                        languages.add(langAttr.getValue());
                    }
                }
            }
        }
        reader.close();
        
        // If the requested language doesn't exist, use the first language found in the config file
        if (!languages.contains(language)) {        	
            language = languages.iterator().next();
        }
        
        // Build prompt set
        reader = xif.createXMLEventReader(new FileInputStream(manifestFile));
        // Forward to correct lang
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if ("set".equals(se.getName().getLocalPart())) {
                    Attribute langAttr = AttributeByName.get(new QName("lang"), se);
                    if (langAttr != null && langAttr.getValue().equals(language)) {
                        // Correct language found
                        break;
                    }
                }
            }
        }
        String nr = null;
        File smilFile = null;
        File audioFile = null;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                String localName = se.getName().getLocalPart();
                if ("volume".equals(localName)) {
                    Attribute nrAttr = AttributeByName.get(new QName("nr"), se);
                    if (nrAttr != null) {
                        nr = nrAttr.getValue();
                    }
                } else if ("smilFile".equals(localName)) {
                    Attribute srcAttr = AttributeByName.get(new QName("src"), se);
                    if (srcAttr != null) {
                        URI smilURI = manifestFile.toURI().resolve(srcAttr.getValue());
                        smilFile = new File(smilURI);
                    }
                } else if ("audioFile".equals(localName)) {
                    Attribute srcAttr = AttributeByName.get(new QName("src"), se);
                    if (srcAttr != null) {
                        URI audioURI = manifestFile.toURI().resolve(srcAttr.getValue());
                        audioFile = new File(audioURI);
                    }
                }
            } else if (event.isEndElement()) {
                EndElement ee = event.asEndElement();
                if ("volume".equals(ee.getName().getLocalPart())) {
                    PromptSet.PromptVolume promptVolume = new PromptSet.PromptVolume();
                    promptVolume.smilFile = smilFile;
                    promptVolume.audioFile = audioFile;
                    promptSet.setPromptVolume(Integer.parseInt(nr), promptVolume);
                } else if ("set".equals(ee.getName().getLocalPart())) {
                	// End of the volume set found. Stop parsing.
                	break;
                }
            }
        }
        reader.close();
        
        return promptSet;
        
    }
}
