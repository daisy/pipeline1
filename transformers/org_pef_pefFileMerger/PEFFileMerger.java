package org_pef_pefFileMerger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PEFFileMerger extends Transformer implements ErrorHandler {
	enum SortType {NUMERAL_GROUPING, STANDARD}

	public PEFFileMerger(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		progress(0);
		File input = new File(parameters.get("input"));
		File output = new File(parameters.get("output"));
		String identifier = parameters.get("identifier");
		SortType sort = SortType.valueOf(parameters.get("sortType").toUpperCase());
		
		File[] files = input.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				return pathname.isFile();
			}});
        switch (sort) {
	    	case NUMERAL_GROUPING:
		        Arrays.sort(files, new Comparator<File>() {
					public int compare(File o1, File o2) {
						NumeralSortString s1 = new NumeralSortString(o1.getName().toLowerCase());
						NumeralSortString s2 = new NumeralSortString(o2.getName().toLowerCase());
						return s1.compareTo(s2);
					}});
	    		break;
	    	case STANDARD:
	    		Arrays.sort(files);
	    		break;
        }
		sendMessage("Checking input files");
        SimpleValidator sv;
		try {
			sv = new SimpleValidator("pef-2008-1.rng", this);
	        for (File f : files) {
	        	sendMessage("Examining " + f.getName(), MessageEvent.Type.INFO_FINER);
	        	try {
		        	if (!sv.validate(f.toURI().toURL())) {
		        		sendMessage("Validation of input file \"" + f.getName() + "\" failed.", MessageEvent.Type.ERROR);
		        		return false;
		        	}
	        	} catch (ValidationException e) {
	        		sendMessage("Validation of input file \"" + f.getName() + "\" failed.", MessageEvent.Type.ERROR);
	        		return false;
	        	}
	        	sendMessage(f.getName() + " ok!", MessageEvent.Type.INFO_FINER);
	        }
	        sendMessage("Input files ok");
	       sendMessage("Assembling files");
	       boolean ret = writeFile(files, output, identifier);
	       progress(1);
	       return ret;
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException", e);
		} catch (TransformerException e) {
			throw new TransformerRunException("TransformerException", e);
		} catch (MalformedURLException e) {
			throw new TransformerRunException("MalformedURLException", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XMLStreamException", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException", e);
		}
	}
	
	private boolean writeFile(File[] volumes, File output, String identifier) throws XMLStreamException, IOException {
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);

		FileInputStream is = new FileInputStream(volumes[0]);
		FileOutputStream os = new FileOutputStream(output);
		XMLEventReader reader = inFactory.createXMLEventReader(is);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter writer = outputFactory.createXMLEventWriter(os);
		QName volume = new QName("http://www.daisy.org/ns/2008/pef", "volume");
		QName body = new QName("http://www.daisy.org/ns/2008/pef", "body");
		QName dcIdentifier = new QName("http://purl.org/dc/elements/1.1/", "identifier");
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.getEventType()==XMLStreamConstants.END_ELEMENT && body.equals(event.asEndElement().getName())) {
				// start copying
				boolean first = true;
				for (File f : volumes) {
					if (first) {
						// skip first volume, it has already been processed
						first = false;
					} else {
						FileInputStream i2 = new FileInputStream(f);
						XMLEventReader r2 = inFactory.createXMLEventReader(i2);
						boolean copy = false;
						while (r2.hasNext()) {
							XMLEvent e2 = r2.nextEvent();
							if (e2.getEventType()==XMLStreamConstants.START_ELEMENT && volume.equals(e2.asStartElement().getName())) {
								copy = true;
							}
							if (copy) {
								writer.add(e2);
							}
							if (e2.getEventType()==XMLStreamConstants.END_ELEMENT && volume.equals(e2.asEndElement().getName())) {
								copy = false;
							}
						}
						r2.close();
						i2.close();
					}
				}
			}
			if (event.getEventType()==XMLStreamConstants.START_ELEMENT && dcIdentifier.equals(event.asStartElement().getName())) {
				while (!
						(event.getEventType()==XMLStreamConstants.END_ELEMENT && dcIdentifier.equals(event.asEndElement().getName()))) {
					if (event.getEventType()==XMLStreamConstants.CHARACTERS) {
						writer.add(eventFactory.createCharacters(identifier));
					} else {
						writer.add(event);
					}
					event = reader.nextEvent();
				}
				writer.add(event);
			} else {
				writer.add(event);
			}
		}
		writer.close();
		reader.close();
		os.close();
		is.close();

		return true;
	}

	public void error(SAXParseException exception) throws SAXException {
		throw new SAXException(exception);
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw new SAXException(exception);
	}

	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
	}
}
