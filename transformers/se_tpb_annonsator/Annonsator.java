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
package se_tpb_annonsator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.settings.SettingsResolver;
import org.daisy.util.xml.settings.SettingsResolverException;
import org.daisy.util.xml.settings.UnsupportedDocumentTypeException;
import org.daisy.util.xml.stax.StaxEntityResolver;
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
    public Annonsator(InputListener inListener, Boolean isInteractive) {
        super(inListener, isInteractive);
    }

    protected boolean execute(Map<String,String> parameters) throws TransformerRunException {
        String input = parameters.remove("input");
        String output = parameters.remove("output");
        String xslOutput = parameters.remove("xslOutput");
        
        //JW 2011-06-20 change file for announcements by a voluntary parameter 
        String filename = "type.xml";
        String pathToTypeFile = parameters.remove("catalogFile");
        if (pathToTypeFile != null) filename = pathToTypeFile;
     
        
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
        	System.out.println("filename for catalogfile in tpb_annonsator" +filename);
            resolver = SettingsResolver.getInstance(filename, this.getClass()); 
            //mg20071203: set entityresolver
            factory.setXMLResolver(new StaxEntityResolver(CatalogEntityResolver.getInstance()));
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
            this.sendMessage(i18n("STYLESHEET_CREATED"), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM);
                        
            // Perform transformation
            DOMSource xsltSource = new DOMSource(xslt);            
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlreader = sp.getXMLReader();
            xmlreader.setEntityResolver(CatalogEntityResolver.getInstance());
            SAXSource xmlSource = new SAXSource(xmlreader, new InputSource(new FileInputStream(input)));
            //create outputs parent dir 
            File outputFile = new File(output);
            FileUtils.createDirectory(outputFile.getParentFile());
            Result result = new StreamResult(new FileOutputStream(outputFile));
            // Attribute version of stylesheet uses XSLT 2.0 
            Map<String,Object> xslParams = new HashMap<String,Object>();
            xslParams.putAll(parameters);
            Stylesheet.apply(xmlSource, xsltSource, result, "net.sf.saxon.TransformerFactoryImpl", xslParams, this);
            
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
            sendMessage("Malformed resource URL to resource: " + resource, MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT);
        }
        return url;
    }
    
    @SuppressWarnings("unused")
	public void warning(TransformerException arg0) throws TransformerException {
        System.err.println("Warning: " + arg0.getLocalizedMessage());
    }

    @SuppressWarnings("unused")
    public void error(TransformerException arg0) throws TransformerException {
        System.err.println("Error: " + arg0.getLocalizedMessage());
    }

    @SuppressWarnings("unused")
    public void fatalError(TransformerException arg0) throws TransformerException {
        System.err.println("Fatal: " + arg0.getLocalizedMessage());        
    }

}
