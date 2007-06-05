/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package se_tpb_annonsator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.settings.SettingsResolver;
import org.daisy.util.xml.settings.SettingsResolverException;
import org.daisy.util.xml.settings.UnsupportedDocumentTypeException;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Linus Ericson 
 * @author Martin Blomberg
 */
public class Annonsator extends Transformer implements ErrorListener {

    private SettingsResolver resolver = null;
    
    /**
     * @param inListener
     * @param eventListeners
     * @param isInteractive
     */
    public Annonsator(InputListener inListener, Set eventListeners, Boolean isInteractive) {
        super(inListener, eventListeners, isInteractive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String input = (String)parameters.remove("input");
        String output = (String)parameters.remove("output");
        String xslOutput = (String) parameters.remove("xslOutput");
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            resolver = SettingsResolver.getInstance("type.xml", this.getClass()); 
            
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(input));
            URL configUrl = null;
            
            // Detect which config file to use
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.DTD) {
                    configUrl = resolver.parseDoctype(((DTD)event).getDocumentTypeDeclaration());
                    break;
                } else if (event.isStartElement()) {
                    StartElement se = event.asStartElement();
                    //System.err.println(se.getName().getNamespaceURI());
                    configUrl = resolver.resolve(se.getName().getNamespaceURI());
                    break;
                }
            }            
            reader.close();
            
            // Create stylesheet
            AnnonsatorXSLTBuilder xsltBuilder = new AnnonsatorXSLTBuilder(configUrl, getResource("attributes.template"), getResource("text.template"));
            xsltBuilder.setOutputFile(xslOutput);
            xsltBuilder.printToFile();
            Document xslt = xsltBuilder.getTemplate();
            sendMessage(Level.FINE, i18n("STYLESHEET_CREATED"));
            
            // Perform transformation
            DOMSource xsltSource = new DOMSource(xslt);            
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlreader = sp.getXMLReader();
            xmlreader.setEntityResolver(CatalogEntityResolver.getInstance());
            SAXSource xmlSource = new SAXSource(xmlreader, new InputSource(new FileInputStream(input)));
            Result result = new StreamResult(new FileOutputStream(output));
            // Attribute version of stylesheet uses XSLT 2.0 
            Stylesheet.apply(xmlSource, xsltSource, result, "net.sf.saxon.TransformerFactoryImpl", parameters, this);
            
        } catch (FileNotFoundException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (CatalogExceptionNotRecoverable e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (SettingsResolverException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (UnsupportedDocumentTypeException e) {
            throw new TransformerRunException(e.getMessage(), e);
        }
        
        return true;
    }
    
    public URL getResource(String resource) {
        URL url = null;
        try {
            url = new URL(getTransformerDirectory().toURI().toURL(), resource);
        } catch (MalformedURLException e) {
            sendMessage(Level.WARNING, "Malformed resource URL to resource: " + resource);
        }
        return url;
    }
    
    public void warning(TransformerException arg0) throws TransformerException {
        System.err.println("Warning: " + arg0.getLocalizedMessage());
    }


    public void error(TransformerException arg0) throws TransformerException {
        System.err.println("Error: " + arg0.getLocalizedMessage());
    }


    public void fatalError(TransformerException arg0) throws TransformerException {
        System.err.println("Fatal: " + arg0.getLocalizedMessage());        
    }

}
