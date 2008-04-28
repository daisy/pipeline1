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
package int_daisy_validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

import org.daisy.util.fileset.validation.Validator;
import org.daisy.util.fileset.validation.message.ValidatorErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorMessage;
import org.daisy.util.fileset.validation.message.ValidatorSevereErrorMessage;
import org.daisy.util.fileset.validation.message.ValidatorWarningMessage;

/**
 * A class producing a validation report using XML. This class is 
 * used from int_daisy_validator.ValidatorDriver only. 
 * 
 * @author Martin Blomberg
 *
 */
public class XMLReporter {

	private long mExecutionTime;						// time used for validation
	private XMLEventWriter mEventWriter;				// xml output writer
	private XMLEventFactory mEventFactory;				// xml factory, creates all report nodes
	private Characters mNewLine;						// a new line character event, improves the output readability.
	
	private String mXmlStylesheet;						// the xml-stylesheet value url
	private Stack<XMLEvent> mPendingEvents = new Stack<XMLEvent>();			// stack containing pending elements: write all of these before finishing.
	public final Set<Namespace> NAMESPACES = new HashSet<Namespace>();		// container of (the single) namespace

	public static final String NAMESPACE_PREFIX = "";												// validator abbr.
	public static final String NAMESPACE_URI = "http://www.daisy.org/ns/pipeline/validator/";			// unique string
	
	//public static final QName MESSAGE_QNAME = new QName(NAMESPACE_URI, "message", NAMESPACE_PREFIX);	// qname for frequent element
	public static final QName MESSAGE_QNAME = new QName("message");										// qname for frequent element
	//public static final QName EXCEPTION_QNAME = new QName(NAMESPACE_URI, "exception", NAMESPACE_PREFIX);// qname for frequent element
	public static final QName EXCEPTION_QNAME = new QName("exception");// qname for frequent element
			
	/**
	 * Constructs an XMLReporter to report errors to the file <code>outputFile</code>.
	 * 
	 * @param outputFile the file to which the xml report will be stored.
	 * @param xmlStylesheet the xml-stylesheet to be used.
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public XMLReporter(File outputFile, String xmlStylesheet) throws FileNotFoundException, XMLStreamException {
		// check to see if outputFile is ok
		if (null == outputFile) {
			String msg = "The validation xml report output file may not be null!";
			throw new IllegalArgumentException(msg);
		}
		try {
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "Unable to use file " + outputFile + 
			" as destination for xml report: " + e.getMessage();
			throw new IllegalArgumentException(msg, e);
		}
		
		if (outputFile.isDirectory()) {
			String msg = "The validation xml report output " +
					"file may not be a directory: " + outputFile.getAbsolutePath();
			throw new IllegalArgumentException(msg);
		}
		
		if (!outputFile.canWrite()) {
			String msg = "Unable to write to file: " + outputFile.getAbsolutePath();
			throw new IllegalArgumentException(msg);
		}
		
		mXmlStylesheet = xmlStylesheet;
		
		initEventFactory();
		initEventWriter(outputFile);
		mNewLine = mEventFactory.createCharacters(System.getProperty("line.separator"));
		
		NAMESPACES.add(mEventFactory.createNamespace(NAMESPACE_PREFIX, NAMESPACE_URI));
		
		beginReport();
		
		mExecutionTime = System.currentTimeMillis();
	}
	
	/**
	 * Constructs an XMLReporter to report errors to the file <code>outputFile</code>
	 * using the string <code>inputFilename</code> as name of the validated file.
	 * 
	 * @param outputFile the file to which the xml report will be stored.
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public XMLReporter(File outputFile) throws FileNotFoundException, XMLStreamException {
		this(outputFile, null);
	}
	
	/**
	 * Initializes the xml event writer.
	 * 
	 * @param outputFile the file to write to.
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private void initEventWriter(File outputFile) throws FileNotFoundException, XMLStreamException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		mEventWriter = outputFactory.createXMLEventWriter(
				new FileOutputStream(outputFile), 
				"UTF-8");	
	}
	
	/**
	 * Initializes the xml event factory.
	 */
	private void initEventFactory() {
		mEventFactory = XMLEventFactory.newInstance();
	}
	
	
	/**
	 * Writes the event <code>event</code> to the output stream.
	 * @param event the event to write.
	 * @throws XMLStreamException
	 */
	private void writeEvent(XMLEvent event) throws XMLStreamException {
		mEventWriter.add(event);
		if (event.isEndElement()) {
			mEventWriter.add(mNewLine);
		}
	}
	
	
	/**
	 * Finishes the report, prints the results to file.
	 * @throws XMLStreamException
	 */
	public void finishReport() throws XMLStreamException {
		while (mPendingEvents.size() > 2) {
			writeEvent(mPendingEvents.pop());
		}
		
		printFootSection();
		
		while (!mPendingEvents.isEmpty()) {
			writeEvent(mPendingEvents.pop());
		}
		
		mEventWriter.flush();
		mEventWriter.close();
	}
	
	/**
	 * Add a ValidatorMessage to the report.
	 *
	 * @param validator the validator that reported the error
	 * @param validatorMessage the validator message
	 * @throws XMLStreamException 
	 */
	public void report(Validator validator, ValidatorMessage validatorMessage) throws XMLStreamException {
		String severity = null;
		if (validatorMessage instanceof ValidatorErrorMessage) {
			severity = "Error";
		} else if (validatorMessage instanceof ValidatorSevereErrorMessage) {
			severity = "Severe error";
		} else if (validatorMessage instanceof ValidatorWarningMessage) {
			severity = "Warning";
		}
		
		String message = validatorMessage.getMessage();		
		Set<Attribute> attributes = new HashSet<Attribute>();
		
		String file = "";
		URI uri = validatorMessage.getFile();
		if(uri!=null) file=uri.toString();
						
		attributes.add(mEventFactory.createAttribute("level", severity));
		attributes.add(mEventFactory.createAttribute("file", file));
		attributes.add(mEventFactory.createAttribute("line", String.valueOf(validatorMessage.getLine())));
		attributes.add(mEventFactory.createAttribute("col", String.valueOf(validatorMessage.getColumn())));
		attributes.add(mEventFactory.createAttribute("msg", message));
			
		writeEvent(mEventFactory.createStartElement(MESSAGE_QNAME, attributes.iterator(), NAMESPACES.iterator()));
		writeEvent(mEventFactory.createEndElement(MESSAGE_QNAME, NAMESPACES.iterator()));
		
		
	}
	
	
	/**
	 * Reports an exception thrown during validation.
	 * 
	 * @param e the <code>Exception</code>.
	 * @throws XMLStreamException
	 */
	public void report(Exception e) throws XMLStreamException {
		String message = e.getMessage();
		
		// construct the stacktrace
		StringBuilder stackTrace = new StringBuilder(2048);
		StringWriter sw = new StringWriter(2048);
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		stackTrace.append(sw.toString());
		
	
		Set<Attribute> attributes = new HashSet<Attribute>();
		
		// level - Exceptions are always SEVERE 
		attributes.add(mEventFactory.createAttribute("level", "Severe error"));		
		// message
		attributes.add(mEventFactory.createAttribute("msg", String.valueOf(message)));		
		// stacktrace
		attributes.add(mEventFactory.createAttribute("str", String.valueOf(stackTrace + System.getProperty("line.separator") + stackTrace)));

		writeEvent(mEventFactory.createStartElement(EXCEPTION_QNAME, attributes.iterator(), NAMESPACES.iterator()));
		writeEvent(mEventFactory.createEndElement(EXCEPTION_QNAME, NAMESPACES.iterator()));
		
	}
	
	
	/**
	 * Constructs the beginning of the xml report.
	 * @throws XMLStreamException 
	 */
	private void beginReport() throws XMLStreamException {
		QName documentQName = new QName(NAMESPACE_URI, "validator", NAMESPACE_PREFIX);
		QName bodyQName = new QName(NAMESPACE_URI, "body", NAMESPACE_PREFIX);
		
		XMLEvent event = null;
		// start document
		event = mEventFactory.createStartDocument("UTF-8", "1.0");
		writeEvent(event);
		mEventWriter.add(mNewLine);
		mPendingEvents.push(mEventFactory.createEndDocument());
		
		// xml-stylesheet
		if (mXmlStylesheet != null) {
			String value = "type=\"text/xsl\" href=\""+ mXmlStylesheet + "\"";
			event = mEventFactory.createProcessingInstruction("xml-stylesheet", value);
			writeEvent(event);
			
		}
		
		// docelem
		event = mEventFactory.createStartElement(documentQName, null, NAMESPACES.iterator());
		writeEvent(event);
		mEventWriter.add(mNewLine);
		mPendingEvents.push(mEventFactory.createEndElement(documentQName, NAMESPACES.iterator()));
				
		printHeadSection();
		
		// body
		event = mEventFactory.createStartElement(bodyQName, null, NAMESPACES.iterator());
		writeEvent(event);
		mEventWriter.add(mNewLine);
		mPendingEvents.push(mEventFactory.createEndElement(bodyQName, NAMESPACES.iterator()));
		
	}
	

	/**
	 * Produces the head section of the xml report, such as dmfcVersion, javaVersion and so on.
	 * @throws XMLStreamException
	 */
	private void printHeadSection() throws XMLStreamException {
		QName headQName = new QName("head");
		QName tmpQName = null;
				
		XMLEvent event = mEventFactory.createStartElement(headQName, null, NAMESPACES.iterator());
		writeEvent(event);
		mEventWriter.add(mNewLine);
		
		tmpQName = new QName("pipelineVersion");
		event = mEventFactory.createStartElement(tmpQName, null, NAMESPACES.iterator());
		writeEvent(event);
		event = mEventFactory.createCharacters(org.daisy.pipeline.Version.getVersion());
		writeEvent(event);
		event = mEventFactory.createEndElement(tmpQName, NAMESPACES.iterator());
		writeEvent(event);
				
		tmpQName = new QName("javaVersion");
		event = mEventFactory.createStartElement(tmpQName, null, NAMESPACES.iterator());
		writeEvent(event);
		event = mEventFactory.createCharacters(System.getProperty("java.version"));
		writeEvent(event);
		event = mEventFactory.createEndElement(tmpQName, NAMESPACES.iterator());
		writeEvent(event);
				
		event = mEventFactory.createEndElement(headQName, NAMESPACES.iterator());
		writeEvent(event);
		
	}
	
	
	/**
	 * Procduces the foot section of the xml report, such as execution time.
	 * @throws XMLStreamException
	 */
	private void printFootSection() throws XMLStreamException {
		QName footQName = new QName("foot");
		QName tmpQName = null;
		
		XMLEvent event = mEventFactory.createStartElement(footQName, null, NAMESPACES.iterator());
		writeEvent(event);
		mEventWriter.add(mNewLine);
		
		
		// execution time
		tmpQName = new QName("executionTime");
		event = mEventFactory.createStartElement(tmpQName, null, NAMESPACES.iterator());
		writeEvent(event);
				
		mExecutionTime = System.currentTimeMillis() - mExecutionTime;
		long ms = mExecutionTime % 1000;
		mExecutionTime /= 1000;
		long s = mExecutionTime % 60;
		mExecutionTime /= 60;
		long m = mExecutionTime % 60;
		mExecutionTime /= 60;
		long h = mExecutionTime % 60;
		String time = 
			h + ":" + 
			(m > 9 ? "" : "0") + m + ":" + 
			(s > 9 ? "" : "0") + s + "." + ms ;
		
		event = mEventFactory.createCharacters(time);
		
		writeEvent(event);
		event = mEventFactory.createEndElement(tmpQName, NAMESPACES.iterator());
		writeEvent(event);
		

		event = mEventFactory.createEndElement(footQName, NAMESPACES.iterator());
		writeEvent(event);
		
	}
}
