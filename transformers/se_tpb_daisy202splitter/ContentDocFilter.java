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

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import org.daisy.util.fileset.D202TextualContentFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * StaxFilter for updating the SMIL file links in the textual content
 * document(s). Links to SMIL files belonging to other volumes than the
 * current one are changed to point to the "change disc" smils.
 * @author Linus Ericson
 *
 */
public class ContentDocFilter extends StaxFilter {
    
    private PromptSet promptSet;
    private Map<FilesetFile,Integer> smilVolumeNumberMap;
    private D202TextualContentFile contentDoc;
    private int currentVolumeNumber;

    /**
     * Constructor.
     * @param xer an XMLEvent reader
     * @param xof an XMLOutputFactory
     * @param outStream the output stream to write the result to
     * @param promptSet the set of prompt files to use
     * @param smilVolumeNumberMap a mapping between a smil file and its volume number
     * @param contentDoc the content document
     * @param currentVolumeNumber the current volume number
     * @throws XMLStreamException
     */
    public ContentDocFilter(XMLEventReader xer, XMLOutputFactory xof, OutputStream outStream, 
            PromptSet promptSet, Map<FilesetFile,Integer> smilVolumeNumberMap,
            D202TextualContentFile contentDoc, int currentVolumeNumber) throws XMLStreamException {
        super(xer, xof, outStream);
        this.promptSet = promptSet;
        this.smilVolumeNumberMap = smilVolumeNumberMap;
        this.contentDoc = contentDoc;
        this.currentVolumeNumber = currentVolumeNumber;
    }

    /*
     * (non-Javadoc)
     * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
     */
    @Override
    protected StartElement startElement(StartElement event) {
        String localName = event.getName().getLocalPart();
        if ("a".equals(localName)) {
            Attribute hrefAttr = AttributeByName.get(new QName("href"), event);
            // Find "a" elemnts with a href attribute
            if (hrefAttr != null) {
            	// Strip away the fragment identifier
            	String hrefString = hrefAttr.getValue();
            	if (hrefString.indexOf("#") != -1) {
            		hrefString = hrefString.substring(0, hrefString.indexOf("#"));
            	}
            	// Resolve to a file in the input fileset
                URI hrefURI = contentDoc.getFile().toURI().resolve(hrefString);
                FilesetFile fsf = contentDoc.getReferencedLocalMember(hrefURI);
                if (fsf != null) {
                    if (smilVolumeNumberMap.containsKey(fsf)) {
                    	// Check which volume the smil belongs to
                        int volume = smilVolumeNumberMap.get(fsf);
                        if (volume != currentVolumeNumber) {
                        	// The smil belongs to another volume than the current one. The link must be updated
                            // FIXME assuming the content doc is located in the same dir as the ncc
                            String newHref = promptSet.getPromptVolume(volume).smilFile.getName() + "#cd" + volume;
                            List<Attribute> attrs = new ArrayList<Attribute>();
                            for (Iterator<Attribute> iter = event.getAttributes(); iter.hasNext(); ) {
                                Attribute attr = iter.next();
                                if ("href".equals(attr.getName().getLocalPart())) {
                                    attr = this.getEventFactory().createAttribute("href", newHref);
                                }
                                attrs.add(attr);
                            }
                            event = this.getEventFactory().createStartElement(event.getName(), attrs.iterator(), event.getNamespaces());
                        }
                    }
                }
            }
        }
        return event;
    }    
    
}
