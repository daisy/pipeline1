/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package se_tpb_xmlValidator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.Validator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A transformer for validating XML documents.
 * This class can validate an XML docuemnt against a RelaxNG schema with
 * optional schematron rules, and optionally against the DTD declared
 * in the XML file itself. Support for XML schema is planned.
 * @author Linus Ericson
 */
public class XmlValidator extends Transformer implements ErrorHandler {

    boolean abortOnWarning = true;
    boolean abortOnError = true;
    
    boolean abort = false;
    
    public XmlValidator(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String xml = (String)parameters.remove("xml");
        String relaxngSchema = (String)parameters.remove("relaxngSchema");
        String useSchematron = (String)parameters.remove("useSchematron");
        String useDTD = (String)parameters.remove("useDTD");
        String abortOn = (String)parameters.remove("abortOn");
        
        File xmlFile = FilenameOrFileURI.toFile(xml);
        File relaxFile = FilenameOrFileURI.toFile(relaxngSchema);
        boolean schematron = "true".equals(useSchematron);
        boolean dtd = "true".equals(useDTD);
        
        if ("ERROR".equals(abortOn)) {
            abortOnWarning = false;
        } else if ("FATAL".equals(abortOn)) {
            abortOnWarning = false;
            abortOnError = false;
        }
        
        try {
            // Validate against DTD
            if (dtd) {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            factory.setValidating(true);
	            SAXParser saxParser = factory.newSAXParser();
	            saxParser.parse(xmlFile, new MyDefaultHandler(this, CatalogEntityResolver.getInstance()));
            }            
            
            // Validate against using RelaxNG and schematron 
            Validator validator = new RelaxngSchematronValidator(relaxFile, this, true, schematron);
            validator.isValid(xmlFile);
            
            if (abort) {
                sendMessage(Level.SEVERE, i18n("ABORTING", xmlFile));
                return false;
            }
        } catch (ValidationException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        
        return true;
    }
    
    private Object[] getParams(SAXParseException e) {
        return new Object[]{e.getMessage(), Integer.valueOf(e.getLineNumber()), Integer.valueOf(e.getColumnNumber()), e.getSystemId()};
    }
    
    public void warning(SAXParseException e) throws SAXException {
        sendMessage(Level.WARNING, i18n("WARNING", this.getParams(e)));
        if (abortOnWarning) {
            abort = true;
        }
    }

    public void error(SAXParseException e) throws SAXException {
        sendMessage(Level.WARNING, i18n("ERROR", this.getParams(e)));
        if (abortOnError) {
            abort = true;
        }
    }

    public void fatalError(SAXParseException e) throws SAXException {
        sendMessage(Level.SEVERE, i18n("FATAL", this.getParams(e)));
        abort = true;
    }

}
