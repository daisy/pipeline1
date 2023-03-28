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
package org.daisy.pipeline.core.transformer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.exception.NotSupposedToHappenException;
import org.daisy.pipeline.exception.TdfParseException;
import org.daisy.util.i18n.I18n;
import org.daisy.util.mime.MIMEException;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.text.URIUtils;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Romain Deltour
 * 
 */
public class TdfParser {
    private Map<String, Object> xifProperties;
    private I18n i18n = new I18n();
    private String nicename;
    private boolean isPlatformSupported = true;
    private String classname;
    private Collection<String> jars = new HashSet<String>();
    private List<Parameter> parameters = new ArrayList<Parameter>();
    private File transformersDir;
    private URI documentationUri;
    private String description;

    public TdfParser() {

	xifProperties = new HashMap<String, Object>();
	xifProperties.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
	xifProperties.put(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
	xifProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);

    }

    public void parseTdf(URL url, File transformersDir)
	    throws TdfParseException {
	if (url == null) {
	    throw new NullPointerException("TDF URL is null");
	}
	this.transformersDir = transformersDir;
	try {
	    validate(url);
	    parse(url);
	} catch (ValidationException e) {
	    throw new TdfParseException(i18n.format("TDF_IO_EXCEPTION"), e);
	} catch (SAXException e) {
	    throw new TdfParseException(e.getMessage(), e);
	} catch (TransformerException e) {
	    throw new TdfParseException(i18n.format("TDF_IO_EXCEPTION"), e);
	} catch (XMLStreamException e) {
	    throw new TdfParseException(i18n.format("PROBLEMS_PARSING_TDF"), e);
	} catch (IOException e) {
	    throw new TdfParseException(i18n.format("TDF_IO_EXCEPTION"), e);
	} catch (MIMEException e) {
	    throw new TdfParseException("MIME exception", e);
	}
    }

    /**
     * @param url
     * @throws IOException
     * @throws XMLStreamException
     * @throws MIMEException
     * @throws MIMETypeRegistryException
     */
    private void parse(URL url) throws XMLStreamException, IOException,
	    MIMETypeRegistryException, MIMEException {

	// Read properties using StAX
	XMLInputFactory factory = StAXInputFactoryPool.getInstance().acquire(
		xifProperties);
	try {
	    XMLEventReader er = factory.createXMLEventReader(url.openStream());

	    String current = null;
	    while (er.hasNext()) {
		XMLEvent event = er.nextEvent();
		switch (event.getEventType()) {
		case XMLStreamConstants.START_ELEMENT:
		    StartElement se = event.asStartElement();
		    String seName = se.getName().getLocalPart();
		    if (seName.equals("transformer")) {
			current = "transformer";
			// Attribute att = se.getAttributeByName(new
			// QName("version"));
			// mVersion = att.getValue();
		    } else if (seName.equals("name")) {
			current = "name";
		    } else if (seName.equals("description")) {
			current = "description";
		    } else if (seName.equals("classname")) {
			current = "classname";
		    } else if (seName.equals("documentation")) {
			Attribute att = se.getAttributeByName(new QName("uri"));
			try {
				documentationUri = URIUtils.resolve(url.toURI(), att.getValue());
			} catch (Exception e) {
			    EventBus.getInstance().publish(
				    new CoreMessageEvent(this, i18n.format(
					    "DOCUMENTATION_URI_FAILURE",
					    nicename, att.getValue()),
					    MessageEvent.Type.ERROR,
					    MessageEvent.Cause.SYSTEM));
			}
		    } else if (seName.equals("jar")) {
			current = "jar";
		    } else if (seName.equals("parameters")) {
			parseParameters(er);
		    } else if (seName.equals("platforms")) {
			parsePlatform(er);
		    }
		    break;
		case XMLStreamConstants.CHARACTERS:
		    String data = event.asCharacters().getData();
		    if (current == null) {
			break;
		    }
		    if (current.equals("name")) {
			nicename = data;
		    } else if (current.equals("description")) {
			description = data;
		    } else if (current.equals("classname")) {
			classname = data;
		    } else if (current.equals("jar")) {
			jars.add(data);
		    }
		    break;
		case XMLStreamConstants.END_ELEMENT:
		    current = null;
		    break;
		}
	    }
	} finally {
	    StAXInputFactoryPool.getInstance().release(factory, xifProperties);
	}

    }

    private void validate(URL url) throws ValidationException, SAXException,
	    TransformerException, TdfParseException {
	ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();
	SimpleValidator validator = new SimpleValidator(getClass().getResource(
		"transformer-1.1.rng"), errorHandler);

	// Validate the transformer description file
	if (!validator.validate(url) || errorHandler.receivedError()) {
	    throw new TdfParseException(i18n.format("TDF_NOT_VALID"));
	}

    }

    /**
     * Loop over all &lt;parameter&gt; elements. This method assumes the
     * &lt;parameters&gt; start tag has just been read from the XML event
     * reader.
     * 
     * @param er
     *            the XML event reader
     * @throws MIMEException
     * @throws XMLStreamException
     * @throws MIMETypeRegistryException
     */
    private void parseParameters(XMLEventReader er) throws MIMEException,
	    XMLStreamException, MIMETypeRegistryException {
	while (er.hasNext()) {
	    XMLEvent event = er.nextEvent();
	    switch (event.getEventType()) {
	    case XMLStreamConstants.START_ELEMENT:
		StartElement se = event.asStartElement();
		if (se.getName().getLocalPart().equals("parameter")) {
		    Parameter param = new Parameter(se, er, transformersDir);
		    parameters.add(param);
		}
		break;
	    case XMLStreamConstants.END_ELEMENT:
		EndElement ee = event.asEndElement();
		if (ee.getName().getLocalPart().equals("parameters")) {
		    // Stop when the </parameters> end tag is found.
		    return;
		}
		break;
	    }
	}
	throw new NotSupposedToHappenException(
		"Did not find </parameters> end tag in TDF");
    }

    /**
     * Checks if the current platform is supported by this Transformer. This
     * method assumes the &lt;platforms&gt; start tag has just been read from
     * the XMLEventReader.
     * 
     * @param er
     *            the XML event reader to read the platform specification from
     * @return <code>true</code> if the platform is supported,
     *         <code>false</code> otherwise
     * @throws XMLStreamException
     */
    private boolean parsePlatform(XMLEventReader er) throws XMLStreamException {
	/*
	 * Only one of the <platform> sub elements needs to evaluate to true for
	 * the current platform to be considered as supported. Therefore, the
	 * result is initialized to false and set to true once a <platform>
	 * element that evaluates to true if found.
	 */
	isPlatformSupported = false;
	while (er.hasNext()) {
	    XMLEvent event = er.nextEvent();
	    switch (event.getEventType()) {
	    case XMLStreamConstants.START_ELEMENT:
		StartElement se = event.asStartElement();
		String seName = se.getName().getLocalPart();
		if (seName.equals("platform")) {
		    isPlatformSupported = isPlatformSupported
			    || checkSinglePlatformElement(er);
		    /*
		     * Continue reading an processing events until the
		     * <platforms> end tag is found. That way we leave the
		     * XMLEventReader in a nice state and we are able to
		     * validate the rest of the platform checks.
		     */
		}
		break;
	    }
	}
	throw new NotSupposedToHappenException(
		"Did not find </platforms> end tag in TDF");
    }

    /**
     * Evaluates a single &lt;platform&gt; element. This method assumes a
     * &lt;platform&gt; start tag has just been read from the XMLEventReader.
     * 
     * @param er
     *            the XML event reader to read the platform information from.
     * @return <code>true</code> if the platform is supported,
     *         <code>false</code> otherwise
     * @throws XMLStreamException
     */
    private boolean checkSinglePlatformElement(XMLEventReader er)
	    throws XMLStreamException {
	/*
	 * All <property> elements within a <platform> must evaluate to true for
	 * the platform element to evaluate to true. Therefore the result is
	 * initailized to true and set to false once a property that evaluates
	 * to false is found.
	 */
	boolean result = true;
	String propertyName = null;
	String propertyValue = null;
	String current = null;
	while (er.hasNext()) {
	    XMLEvent event = er.nextEvent();
	    switch (event.getEventType()) {
	    case XMLStreamConstants.START_ELEMENT:
		StartElement se = event.asStartElement();
		String seName = se.getName().getLocalPart();
		if (seName.equals("property")) {
		    propertyName = null;
		    propertyValue = null;
		} else if (seName.equals("name")) {
		    current = "name";
		} else if (seName.equals("value")) {
		    current = "value";
		}
		break;
	    case XMLStreamConstants.CHARACTERS:
		String data = event.asCharacters().getData();
		if (current == null) {
		    break;
		}
		if (current.equals("name")) {
		    propertyName = data;
		} else if (current.equals("value")) {
		    propertyValue = data;
		}
		break;
	    case XMLStreamConstants.END_ELEMENT:
		EndElement ee = event.asEndElement();
		String eeName = ee.getName().getLocalPart();
		if (eeName.equals("property")) {
		    /*
		     * Once a </property> end tag is found, a property can be
		     * evaluated. Continue processing events until the
		     * </platform> end tag is found even if the result is set to
		     * false so the XMLEventReader is left in a nice state and
		     * all properties can be validated.
		     */
		    String realValue = System.getProperty(propertyName);
		    try {
			if (realValue == null) {
			    EventBus.getInstance().publish(
				    new CoreMessageEvent(this, i18n.format(
					    "UNKNOWN_PROPERTY", propertyName),
					    MessageEvent.Type.WARNING));
			    result = false;
			} else if (!realValue.matches(propertyValue)) {
			    result = false;
			}
		    } catch (PatternSyntaxException e) {
			EventBus.getInstance().publish(
				new CoreMessageEvent(this, i18n.format(
					"INCORRECT_PROPERTY_VALUE",
					propertyName),
					MessageEvent.Type.WARNING));
			result = false;
		    }
		} else if (eeName.equals("platform")) {
		    return result;
		}
		current = null;
		break;
	    }
	}
	throw new NotSupposedToHappenException(
		"Did not find </platform> end tag in TDF");
    }

    /**
     * @return the nicename
     */
    public String getNicename() {
	return nicename;
    }

    /**
     * @return the isPlatformSupported
     */
    public boolean isPlatformSupported() {
	return isPlatformSupported;
    }

    /**
     * @return the classname
     */
    public String getClassname() {
	return classname;
    }

    /**
     * @return the jars
     */
    public Collection<String> getJars() {
	return jars;
    }

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
	return parameters;
    }

    /**
     * @return the documentationUri
     */
    public URI getDocumentationUri() {
	return documentationUri;
    }

    /**
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    private class ValidatorErrorHandler implements ErrorHandler {
	private boolean receivedError;

	public boolean receivedError() {
	    return receivedError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException exception) throws SAXException {
	    saxWarn(exception);
	    receivedError = true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
	    saxWarn(exception);
	    receivedError = true;

	}

	public void warning(SAXParseException e) throws SAXException {
	    if (!e.getMessage().contains("XSLT 1.0")) {
		// temp hack to avoid saxon 8 version warning messages
		saxWarn(e);
	    }
	}

	private void saxWarn(SAXParseException e) {
	    EventBus.getInstance()
		    .publish(
			    new CoreMessageEvent(this, i18n.format(
				    "SAX_VALIDATION_EXCEPTION",
				    e.getSystemId(), e.getLineNumber(), e
					    .getMessage()),
				    MessageEvent.Type.WARNING,
				    MessageEvent.Cause.INPUT));
	}
    }

}
