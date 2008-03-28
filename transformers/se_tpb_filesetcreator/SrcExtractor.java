package se_tpb_filesetcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;

/**
 * Utility for extracting all values of src attributes from an xml-file.
 * @author Martin Blomberg
 *
 */
public class SrcExtractor {

	private QName attribName = new QName("src");
	private File xmlFile;
	private Set<String> srcValues = new HashSet<String>();
	
	/**
	 * @param xmlFile the file in which to look for references.
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws CatalogExceptionNotRecoverable
	 */
	public SrcExtractor(File xmlFile) throws XMLStreamException, IOException, CatalogExceptionNotRecoverable {
		this.xmlFile = xmlFile;
		
		// open a stream to xmlFile
		// check if the are any src attributes
		// if so, put them in some kind of set
		FileInputStream fileInputStream = new FileInputStream(xmlFile);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
		inputFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		XMLEventReader reader = inputFactory.createXMLEventReader(fileInputStream);
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				extractSrc(event.asStartElement());
			}
		}
		
		reader.close();
		fileInputStream.close();
	}
	
	/**
	 * Extracts the value of the attrubute with <code>attribName</code>
	 * if present. 
	 * @param se the start element to search for <code>attribName</code>.
	 */
	private void extractSrc(StartElement se) {
		Attribute attrib;
		if ((attrib = se.getAttributeByName(attribName)) != null) {
			String val = attrib.getValue();
			if (val != null && val.trim().length() > 0) {
				srcValues.add(val);
			}
		}
	}
	
	/**
	 * Returns a set containing strings. The strings are 
	 * the values of the src attrubutes.
	 * @return a set containing strings. The strings are 
	 * the values of the src attrubutes.
	 */
	public Set<String> getSrcValues() {
		return srcValues;
	}
	
	/**
	 * Returns the base directory to which the relative paths are
	 * relative.
	 * @return the base directory to which the relative paths are
	 * relative.
	 */
	public File getBaseDir() {
		return xmlFile.getParentFile();
	}
}
