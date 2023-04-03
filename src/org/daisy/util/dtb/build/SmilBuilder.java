package org.daisy.util.dtb.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.xml.Namespaces;


/**
 * Creates a SMIL file for a Z39.86-2005 book (currently only for text-only books)
 * @author jpritchett@rfbd.org
 *
 * This class uses the SmilStructure hierarchy to store the data for the various structures within the file.
 * The caller is obligated to populate lists of SmilStructures and MetadataItems; this class just outputs
 * a conforming Z39.86-2005 SMIL file from those inputs.
 */
public class SmilBuilder {
	private MetadataList metadata;
	private ArrayList<SmilStructure> structures;
	private XMLEventFactory xef;

	// PROPERTIES
	
	/**
	 * Get the list of metadata items for this SMIL file
	 */
	public MetadataList getMetadata() {
		return metadata;
	}
	
	/**
	 * Set the list of metadata items for this SMIL file
	 * @param metadata
	 */
	public void setMetadata(MetadataList metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Get the list of SMIL structures contained in this file
	 */
	public ArrayList<SmilStructure> getSmilStructures() {
		return structures;
	}
	
	/**
	 * Set the list of SMIL structures to be contained in this file
	 * @param smilStructures
	 */
	public void setSmilStructures(ArrayList<SmilStructure> smilStructures) {
		this.structures = smilStructures;
	}

	/**
	 * Get the list of customTests referenced in this SMIL file
	 * @return Set of customTest names (strings)
	 */
	public HashSet<String> getCustomTests() {
		// Build the set based on our list of structures
		return buildCustomTestsSet(structures);
	}
	
	// Recursive function that actually builds the set
	// This function is smart enough to go into containers
	private HashSet<String> buildCustomTestsSet(ArrayList<SmilStructure> smilList) {
		HashSet<String> customTests = new HashSet<String>();
		
		// Iterate over all structures and add any new customTest names you find
		for (SmilStructure ss : smilList) {
			if (ss.getCustomTestName() != null && !customTests.contains(ss.getCustomTestName())) {
				customTests.add(ss.getCustomTestName());
			}
			// If this is a container, recurse
			if (ss instanceof SmilStructureContainer) {
				customTests.addAll(buildCustomTestsSet(((SmilStructureContainer)ss).getChildren()));
			}
		}
		return customTests;
	}
	
	public SmilBuilder() {
		xef = XMLEventFactory.newInstance();
	}
	
	/**
	 * Render this as a conforming Z39.86-2005 SMIL file
	 * @param destination URL of the output file
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void render(URL destination) throws IOException, XMLStreamException {
		
		// Create the output writer
		File outputFile = new File(destination.getPath());
		XMLEventWriter writer;

		writer = XMLOutputFactory.newInstance().createXMLEventWriter(new FileOutputStream(outputFile), "utf-8");
		PrettyEventWriter pew = new PrettyEventWriter(writer, "  ");

		// Render the prolog		
		pew.writeEvent(xef.createStartDocument("utf-8", "1.0"));
		pew.writeEvent(xef.createDTD("<!DOCTYPE smil PUBLIC '-//NISO//DTD dtbsmil 2005-1//EN' 'http://www.daisy.org/z3986/2005/dtbsmil-2005-1.dtd'>"));
		
		
		// Root element
		Namespace smilNamespace = xef.createNamespace(Namespaces.SMIL_20_NS_URI);
		Collection<Namespace> namespaces = new ArrayList<Namespace>();
		namespaces.add(smilNamespace);
		pew.writeEvent(xef.createStartElement("", Namespaces.SMIL_20_NS_URI ,"smil", null, namespaces.iterator()));
		
		// Head element with metadata and customTests
		pew.writeEvent(xef.createStartElement("", Namespaces.SMIL_20_NS_URI,"head"));
		// TODO:  Validate metadata against spec requirements
		for (MetadataItem mi : metadata) {
			pew.writeEvents(mi.asXMLEvents().iterator());
		}
		
		// See if we have any customTests and add them if necessary
		HashSet<String> customTestNames = getCustomTests();
		if (customTestNames.size() > 0) {
			pew.writeEvent(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "customAttributes"));
			for (String customTestName : customTestNames) {
				ArrayList<Attribute> atts = new ArrayList<Attribute>();
				atts.add(xef.createAttribute("defaultState", "false"));
				atts.add(xef.createAttribute("override", "visible"));
				atts.add(xef.createAttribute("id", customTestName));
				pew.writeEvent(xef.createStartElement("", Namespaces.SMIL_20_NS_URI, "customTest", atts.iterator(), null));
				pew.writeEvent(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "customTest"));
			}
			pew.writeEvent(xef.createEndElement("", Namespaces.SMIL_20_NS_URI, "customAttributes"));
		}
		
		pew.writeEvent(xef.createEndElement("", Namespaces.SMIL_20_NS_URI ,"head"));
		
		
		// Body and all SMIL structures
		// Write the body and master seq
		// TODO: Make this general purpose (currently hard-wired for text-only
		// books)
		pew.writeEvent(xef.createStartElement("", "", "body"));
		pew.writeEvent(xef.createStartElement("", "", "seq"));
		pew.writeEvent(xef.createAttribute("dur", "0:00:00.000"));
		pew.writeEvent(xef.createAttribute("fill", "remove"));
		pew.writeEvent(xef.createAttribute("id", "mseq"));

		// Write out all the SMIL structures
		for (SmilStructure ss : structures) {
			pew.writeEvents(ss.asEventIterator());
		}

		// Close out the seq and body
		pew.writeEvent(xef.createEndElement("", "", "seq"));
		pew.writeEvent(xef.createEndElement("", "", "body"));
			
		
		// And close it all up
		pew.writeEvent(xef.createEndElement("", Namespaces.SMIL_20_NS_URI ,"smil"));
		pew.writeEvent(xef.createEndDocument());
		writer.close();
	}
}
