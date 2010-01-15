package org_pef_pefFileSplitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

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
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.stax.StaxEntityResolver;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PEFFileSplitter extends Transformer  implements ErrorHandler  {
	enum State {HEADER, BODY, FOOTER};

	public PEFFileSplitter(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		progress(0);
		File input = new File(parameters.get("input"));
		File directory = new File(parameters.get("output"));
		String prefix = parameters.get("prefix");
		String postfix = parameters.get("postfix");
		String inputName = input.getName();
		String inputExt = ".pef";
		int index = inputName.lastIndexOf('.');
		if (index >= 0) {
			if (index < inputName.length()) {
				inputExt = inputName.substring(index);
			}
			inputName = inputName.substring(0, index);
			
		}
		if (prefix==null || "".equals(prefix)) {
			prefix = inputName;
		}
		if (postfix==null || "".equals(postfix)) {
			postfix = inputExt;
		}
		directory.mkdirs();
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        
    	try {
			inFactory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
		} catch (CatalogExceptionNotRecoverable e1) {
			e1.printStackTrace();
		}
		sendMessage("Splitting");
		try {
		    FileInputStream is = new FileInputStream(input);
		    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLEventReader reader = inFactory.createXMLEventReader(is);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			ArrayList<XMLEvent> header = new ArrayList<XMLEvent>();
			Stack<File> files = new Stack<File>();
			Stack<XMLEventWriter> writers = new Stack<XMLEventWriter>();
			Stack<FileOutputStream> os = new Stack<FileOutputStream>();
			QName volume = new QName("http://www.daisy.org/ns/2008/pef", "volume");
			QName body = new QName("http://www.daisy.org/ns/2008/pef", "body");
			int i = 0;
			State state = State.HEADER;
	        while (reader.hasNext()) {
	            XMLEvent event = reader.nextEvent();
	            if (event.getEventType()==XMLStreamConstants.START_ELEMENT
	            		&& volume.equals(event.asStartElement().getName())) {
	            	state = State.BODY;
	        		i++;
	        		files.push(new File(directory, prefix + "-" + i + postfix));
	    			os.push(new FileOutputStream(files.peek()));
	    			writers.push(outputFactory.createXMLEventWriter(os.peek()));
	    			// output header information
	    			boolean ident = false;
	    			QName dcIdentifier = new QName("http://purl.org/dc/elements/1.1/", "identifier");
	    			for (XMLEvent e : header) {
	    				if (e.getEventType()==XMLStreamConstants.START_ELEMENT &&
	    						dcIdentifier.equals(e.asStartElement().getName())) {
	    					ident = true;
	    					writers.peek().add(e);
	    				} else if (ident==true && e.getEventType()==XMLStreamConstants.CHARACTERS) {
	    					ident = false;
	    					XMLEvent e2 = eventFactory.createCharacters(e.asCharacters().getData()+"-"+i);
	    					writers.peek().add(e2);
	    				} else {
	    					writers.peek().add(e);
	    				}
	    			}
		        } else if (event.getEventType()==XMLStreamConstants.END_ELEMENT &&
		            	body.equals(event.asEndElement().getName())) {
            		state = State.FOOTER;
            	}
	            switch (state) {
	            	case HEADER:
	            		//push header event
	            		header.add(event);
	            		break;
	            	case BODY:
	            		writers.peek().add(event);
	            		break;
	            	case FOOTER:
	            		// write footer to all files
	              		for (XMLEventWriter w : writers) {
	            			w.add(event);
	            		}
	            		break;
	            }
	        }
	        for (FileOutputStream s : os) {
	        	s.close();
	        }
	        for (XMLEventWriter w : writers) {
	        	w.close();
	        }
	        is.close();
	        sendMessage("Checking result for errors");
	        progress(0.5);
	        SimpleValidator sv = new SimpleValidator("pef-2008-1.rng", this);
	        for (File f : files) {
	        	sendMessage("Examining " + f.getName(), MessageEvent.Type.INFO_FINER);
	        	if (!sv.validate(f.toURI().toURL())) {
	        		sendMessage("Validation of result file failed: " + f.getName(), MessageEvent.Type.ERROR);
	        		return false;
	        	}
	        	sendMessage(f.getName() + " ok!", MessageEvent.Type.INFO_FINER);
	        }
	        sendMessage("All ok!");
	        progress(1);
	        return true;
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException: ", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException: ", e);
		} catch (XMLStreamException e) {
			throw new TransformerRunException("XMLStreamException: ", e);
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException: ", e);
		} catch (TransformerException e) {
			throw new TransformerRunException("TransformerException: ", e);
		} catch (ValidationException e) {
			throw new TransformerRunException("ValidationException: ", e);
		}
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
