package org.daisy.util.dtb.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

import org.daisy.util.dtb.meta.MetadataItem;
import org.daisy.util.dtb.meta.MetadataList;
import org.daisy.util.xml.Namespaces;
import org.daisy.util.xml.pool.StAXEventFactoryPool;
import org.daisy.util.xml.pool.StAXOutputFactoryPool;


/**
 * Creates a valid Z39.86-2005 NCX from given data
 * @author jpritchett@rfbd.org
 * This class uses the NavigationItem hierarchy to store the data for the various structures within the file.
 * The caller is obligated to populate lists of NavigationItems (one each for points and pages)
 * and MetadataItems; this class just outputs a conforming Z39.86-2005 NCX file from those inputs.

 */
public class NcxBuilder {
	private MetadataList metadata;
	private Collection<NavigationItemPoint> map;
	private Collection<NavigationItemPage> pages;
	private NavigationLabel title;
	private Collection<NavigationLabel> authors;
	private HashSet<String> customTests;
	private XMLEventFactory xef = null;

	// This is a lookup table for the @bookStruct values for different customTest names
	private static HashMap<String,String> customTestBookStructValues = new HashMap<String,String>();
	
	// PROPERTIES
	
	/**
	 * Get the list of metadata items for this NCX
	 */
	public MetadataList getMetadata() {
		return metadata;
	}
	/**
	 * Set the list of metadata items for this NCX
	 * @param metadata
	 */
	public void setMetadata(MetadataList metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Get the list of points for the navMap
	 */
	public Collection<NavigationItemPoint> getMap() {
		return map;
	}
	/**
	 * Set the list of points for the navMap
	 * @param map
	 */
	public void setMap(Collection<NavigationItemPoint> map) {
		this.map = map;
	}
	
	/**
	 * Get the list of pages for this NCX
	 */
	public Collection<NavigationItemPage> getPages() {
		return pages;
	}
	/**
	 * Set the list of pages for this NCX
	 * @param pages
	 */
	public void setPages(Collection<NavigationItemPage> pages) {
		this.pages = pages;
	}
	
	/**
	 * Get the label representing the title of the book
	 */
	public NavigationLabel getTitle() {
		return title;
	}
	/**
	 * Set the label representing the title of the book
	 * @param title
	 */
	public void setTitle(NavigationLabel title) {
		this.title = title;
	}
	
	/**
	 * Get the label(s) representing the author(s) of the book
	 */
	public Collection<NavigationLabel> getAuthors() {
		return authors;
	}
	/**
	 * Set the label(s) representing the author(s) of the book
	 * @param authors
	 */
	public void setAuthors(Collection<NavigationLabel> authors) {
		this.authors = authors;
	}
	
	/**
	 * Set the list of custom test names referenced by SMIL files in this book
	 * @param customTests Set of custom test names (strings)
	 */
	public void setCustomTests(HashSet<String> customTests) {
		this.customTests = customTests;
	}

	public NcxBuilder() {
		// Some conveniences and helpers that are used in various places
		xef = StAXEventFactoryPool.getInstance().acquire();
		
		// If necessary, fill the bookStruct table
		if (customTestBookStructValues.isEmpty()) {
			customTestBookStructValues.put("pagenum", "PAGE_NUMBER");
			customTestBookStructValues.put("note", "NOTE");
			customTestBookStructValues.put("noteref", "NOTE_REFERENCE");
			customTestBookStructValues.put("annotation", "ANNOTATION");
			customTestBookStructValues.put("linenum", "LINE_NUMBER");
			customTestBookStructValues.put("prodnote", "OPTIONAL_PRODUCER_NOTE");
			customTestBookStructValues.put("sidebar", "OPTIONAL_SIDEBAR");
		}
	}
	
	/**
	 * @param metadata List of the metadata items to include in this NCX
	 * @param map List of points for the navMap
	 * @param pages List of pages for the pageList
	 * @param title Label representing the title of the book
	 * @param authors Label(s) representing the author(s) of the book
	 * @param customTests List of customTests referenced in this book
	 */
	public NcxBuilder(MetadataList metadata, Collection<NavigationItemPoint>map, Collection<NavigationItemPage>pages, NavigationLabel title, Collection<NavigationLabel>authors, HashSet<String>customTests) {
		this();
		this.metadata = metadata;
		this.map = map;
		this.pages = pages;
		this.title = title;
		this.authors = authors;
		this.customTests = customTests;
	}
	
	/**
	 * Render this as a conforming Z39.86-2005 NCX file
	 * @param destination The URL of the output file to create
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void render(URL destination) throws IOException, XMLStreamException {
		// Set up output writer
		Map<String,Object> properties = StAXOutputFactoryPool.getInstance().getDefaultPropertyMap();
		File outputNCX = new File(destination.getPath());
		XMLEventWriter writer = StAXOutputFactoryPool.getInstance().acquire(properties).createXMLEventWriter(new FileOutputStream(outputNCX));
		PrettyEventWriter pew = new PrettyEventWriter(writer,"  ");
		
	// Render the prolog
		pew.writeEvent(xef.createStartDocument("utf-8", "1.0"));
		pew.writeEvent(xef.createDTD("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\" >"));
		
	// Root element
		Namespace ncxNamespace = xef.createNamespace(Namespaces.Z2005_NCX_NS_URI);
		Collection<Namespace> namespaces = new ArrayList<Namespace>();
		namespaces.add(ncxNamespace);
		pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"ncx", null, namespaces.iterator()));
		//pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"ncx"));
		pew.writeEvent(xef.createAttribute("version", "2005-1"));

	// Head element with metadata and customTests
		pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"head"));
		
		// TODO:  Validate metadata against Z39.86-2005 requirements
		for (MetadataItem mi : metadata) {
			pew.writeEvents(mi.asXMLEvents().iterator());
		}
		
		if (customTests != null && customTests.size() > 0) {
			for (String customTestName : customTests ) {
				ArrayList<Attribute> atts = new ArrayList<Attribute>();
				atts.add(xef.createAttribute("defaultState", "false"));
				atts.add(xef.createAttribute("override", "visible"));
				atts.add(xef.createAttribute("id", customTestName));
				if (customTestBookStructValues.containsKey(customTestName)) {
					atts.add(xef.createAttribute("bookStruct", customTestBookStructValues.get(customTestName)));
				}

				pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI, "smilCustomTest", atts.iterator(), null));
				pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI, "smilCustomTest"));
				
			}
		}
		pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"head"));
		
	// docTitle and any docAuthors we might have
		pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"docTitle"));
		pew.writeEvents(title.asEvents().iterator());
		pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"docTitle"));

		if (authors != null && authors.size()>0) {
			for (NavigationLabel authorLabel : authors) {
				pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"docAuthor"));
				pew.writeEvents(authorLabel.asEvents().iterator());
				pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"docAuthor"));
			}
		}

	// Now put in the navMap by rendering all navigation points
		// TODO:  Empty map is an error?
		if (map != null && map.size()>0) {
			pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"navMap"));
			for (NavigationItemPoint mapPoint : map) {
				pew.writeEvents(mapPoint.asEvents().iterator());
			}
			pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"navMap"));
		}

	// Now add the list of pages
		if (pages != null && pages.size()>0) {
			pew.writeEvent(xef.createStartElement("", Namespaces.Z2005_NCX_NS_URI ,"pageList"));
			for (NavigationItemPage pageTarget  : pages) {
				pew.writeEvents(pageTarget.asEvents().iterator());
			}
			pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"pageList"));
		}

	// And close it all up
		pew.writeEvent(xef.createEndElement("", Namespaces.Z2005_NCX_NS_URI ,"ncx"));
		pew.writeEvent(xef.createEndDocument());
		writer.close();
	}
}
