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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.daisy.util.file.TempFile;
import org.daisy.util.fileset.D202SmilFile;
import org.daisy.util.fileset.FilesetFile;
import org.daisy.util.xml.SmilClock;
import org.daisy.util.xml.stax.AttributeByName;
import org.daisy.util.xml.stax.StaxFilter;

/**
 * Class responsible for building the title smil. The title smil is build by
 * stripping away all content except the first par from the first SMIL file of the book.
 * @author Linus Ericson
 */
public class TitleSmilBuilder {
    
    private XMLInputFactory xif;
    private XMLOutputFactory xof;
    
    /**
     * Constructor.
     * @param xif an XMLInputFactory
     * @param xof an XMLOutputFactory
     */
	public TitleSmilBuilder(XMLInputFactory xif, XMLOutputFactory xof) {
	    this.xif = xif;
	    this.xof = xof;
	}

	/**
	 * Create the title SMIL.
	 * @param firstSmil the first SMIL file of the book
	 * @param outputFile the output file
	 * @return the set of fileset files referenced from the title SMIL
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public Set<FilesetFile> createTitleSmil(D202SmilFile firstSmil, File outputFile) throws XMLStreamException, IOException {
		
		TempFile tempFile = new TempFile();
		
		// Step 1: remove all pars except the first one
		XMLEventReader reader = xif.createXMLEventReader(new FileInputStream(firstSmil.getFile()));
		OutputStream outputStream = new FileOutputStream(tempFile.getFile());
		ParStripperFilter parStripperFilter = new ParStripperFilter(reader, xof, outputStream);
		parStripperFilter.filter();
		parStripperFilter.close();
		reader.close();
		
		// The length of the audio in the title smil
		SmilClock timeInSmil = parStripperFilter.getAudioLength();
	
		// Step 2: remove ncc:totalElapsedTime, update ncc:timeInThisSmil and seq@dur
		// FIXME change value of text@id attribute as well!
		reader = xif.createXMLEventReader(new FileInputStream(tempFile.getFile()));
		outputStream = new FileOutputStream(outputFile);
		StaxFilter filter = new MetadataFixFilter(reader, xof, outputStream, timeInSmil);
		filter.filter();
		filter.close();
		reader.close();
		
		// Delete temp file
		tempFile.delete();
		
		// Get the media files referenced by the SMIL (i.e. audio and ncc/contentdoc)
		Set<FilesetFile> mediaFiles = new HashSet<FilesetFile>();
		for (String uriString : parStripperFilter.getMediaURIs()) {
		    URI uri = firstSmil.getFile().toURI().resolve(uriString);
		    FilesetFile fsf = firstSmil.getReferencedLocalMember(uri);
		    mediaFiles.add(fsf);
		}
		
		return mediaFiles;			
		
	}
	
	/**
	 * StaxFilter for stripping away all pars except the first
	 */
	private class ParStripperFilter extends StaxFilter {
		
		private boolean firstParPassed = false;
		private SmilClock audioLength = new SmilClock();
		private Set<String> mediaURIs = new HashSet<String>();

		/**
		 * Constructor
		 * @param xer an XMLEventReader
		 * @param xof an XMLOutputFactory
		 * @param outStream the output stream to write the output to
		 * @throws XMLStreamException
		 */
		public ParStripperFilter(XMLEventReader xer, XMLOutputFactory xof, OutputStream outStream) throws XMLStreamException {
			super(xer, xof, outStream);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
		 */
		@Override
		protected StartElement startElement(StartElement event) {
			if (firstParPassed) {
				// Strip everything once we have passed the first par
				return null;
			}
			String localName = event.getName().getLocalPart();
			if ("audio".equals(localName)) {
				Attribute clipBegin = AttributeByName.get(new QName("clip-begin"), event);
                Attribute clipEnd = AttributeByName.get(new QName("clip-end"), event);                    
                Attribute src = AttributeByName.get(new QName("src"), event);
                audioLength = audioLength.addTime(new SmilClock(clipEnd.getValue()));
                audioLength = audioLength.subtractTime(new SmilClock(clipBegin.getValue()));
                mediaURIs.add(src.getValue());
			}
			if ("text".equals(localName)) {
                Attribute src = AttributeByName.get(new QName("src"), event);
                mediaURIs.add(this.getDocUri(src.getValue()));
            }
			return event;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#endElement(javax.xml.stream.events.EndElement)
		 */
		@Override
		protected EndElement endElement(EndElement event) {
			String localName = event.getName().getLocalPart();
			if ("par".equals(localName)) {
				// We have now seen the end of the first par
				firstParPassed = true;
			}
			return event;
		}
		
		/**
		 * Gets the length of all audio referenced from the title SMIL
		 * @return the audio length
		 */
		public SmilClock getAudioLength() {
			return audioLength;
		}
		
		/**
		 * Strip away the fragment identifier from the specified uri
		 * @param uri
		 * @return
		 */
		private String getDocUri(String uri) {
	        int hashIndex = uri.indexOf("#");
	        if (hashIndex != -1) {
	            uri = uri.substring(0, hashIndex);
	        }	        
	        return uri;
	    }
		
		/**
		 * Gets the set of media uri strings
		 * @return a set of media uri strings
		 */
		public Set<String> getMediaURIs() {
		    return mediaURIs;
		}
		
	}
	
	/**
	 * StaxFilter for fixing the metadata of the title SMIL
	 */
	private class MetadataFixFilter extends StaxFilter {
		
		private SmilClock timeInThisSmil;
		private boolean firstSeq = true;

		public MetadataFixFilter(XMLEventReader xer, XMLOutputFactory xof, OutputStream outStream, SmilClock timeInSmil) throws XMLStreamException {
			super(xer, xof, outStream);
			this.timeInThisSmil = timeInSmil;
		}

		/* (non-Javadoc)
		 * @see org.daisy.util.xml.stax.StaxFilter#startElement(javax.xml.stream.events.StartElement)
		 */
		@Override
		protected StartElement startElement(StartElement event) {
			String localName = event.getName().getLocalPart();
			if ("meta".equals(localName)) {
				Attribute nameAttr = AttributeByName.get(new QName("name"), event);
				if (nameAttr != null && "ncc:totalElapsedTime".equals(nameAttr.getValue())) {
					// Remove ncc:totalElapsedTime meta element
					return null;
				}
				if (nameAttr != null && "ncc:timeInThisSmil".equals(nameAttr.getValue())) {
                	// Update ncc:timeInThisSmil
                    SmilClock roundedTimeInThisSmil = new SmilClock(timeInThisSmil.secondsValueRoundedDouble());                    
                    Attribute content = this.getEventFactory().createAttribute("content", roundedTimeInThisSmil.toString());
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(nameAttr);
                    coll.add(content);
                    StartElement result = this.getEventFactory().createStartElement(event.getName(), coll.iterator(), event.getNamespaces());
                    return result;
                }
			} else if ("seq".equals(localName)) {
				if (firstSeq) {
                	// Add the dur attribute to the first seq
                    firstSeq = false;
                    Attribute dur = this.getEventFactory().createAttribute("dur", timeInThisSmil.toString(SmilClock.TIMECOUNT_SEC));
                    Collection<Attribute> coll = new ArrayList<Attribute>();
                    coll.add(dur);
                    StartElement result = this.getEventFactory().createStartElement(event.getName(), coll.iterator(), event.getNamespaces());
                    return result;
                }
			}
			return event;
		}
		
	}
}
