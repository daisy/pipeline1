/*
 * #### - ################################
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
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.daisy.dmfc.core.DirClassLoader;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import se_tpb_xmldetection.UnsupportedDocumentTypeException;

/**
 * @author Linus Ericson 
 * @author Martin Blomberg
 */
public class Annonsator extends Transformer implements ErrorListener {

    protected final static Pattern dtdPattern = Pattern.compile("<!DOCTYPE\\s+\\w+(\\s+((SYSTEM\\s+(\"[^\"]*\"|'[^']*')|PUBLIC\\s+(\"[^\"]*\"|'[^']*')\\s+(\"[^\"]*\"|'[^']*'))))?\\s*(\\[.*\\]\\s*)?>");
    
    private AnnonsatorSettingsResolver resolver = null;
    
    private Class resourceLoader = null;
    
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
            resolver = AnnonsatorSettingsResolver.getInstance(); 
            
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(input));
            URL configUrl = null;
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.DTD) {
                    configUrl = parseDoctype(((DTD)event).getDocumentTypeDeclaration());
                    break;
                } else if (event.isStartElement()) {
                    StartElement se = event.asStartElement();
                    System.err.println(se.getName().getNamespaceURI());
                    configUrl = resolver.resolve(se.getName().getNamespaceURI());
                    break;
                }
            }            
            reader.close();
            AnnonsatorXSLTBuilder xsltBuilder = new AnnonsatorXSLTBuilder(configUrl, getResource("attributes.template"), getResource("text.template"));
            xsltBuilder.setOutputFile(xslOutput);
            xsltBuilder.printToFile();
            Document xslt = xsltBuilder.getTemplate();
            sendMessage(Level.FINE, i18n("STYLESHEET_CREATED"));
            DOMSource xsltSource = new DOMSource(xslt);            
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlreader = sp.getXMLReader();
            xmlreader.setEntityResolver(CatalogEntityResolver.getInstance());
            SAXSource xmlSource = new SAXSource(xmlreader, new InputSource(new FileInputStream(input)));
            Result result = new StreamResult(new FileOutputStream(output));
            //Stylesheet.apply(xmlSource, xsltSource, result);
            Stylesheet.apply(xmlSource, xsltSource, result, "net.sf.saxon.TransformerFactoryImpl", parameters, this);
        } catch (FileNotFoundException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new TransformerRunException(e.getMessage(), e);
        } catch (UnsupportedDocumentTypeException e) {
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
        }
        
        return true;
    }
    
    public URL getResource(String resource) {
        if (resourceLoader == null) {
	        ClassLoader cl = this.getClass().getClassLoader();
	        if (cl instanceof DirClassLoader) {
	            DirClassLoader dcl = (DirClassLoader)cl;
	            //System.err.println("dcl");
	            cl = new DirClassLoader(dcl.getClassDir(), dcl.getResourceDir().getParentFile());
	        } else {
	            //System.err.println("cl");
	        }
	        try {
	            resourceLoader = Class.forName(this.getClass().getPackage().getName() + ".DummyClass", true, cl);
	        } catch (ClassNotFoundException e) {
	           return null;
	        }
        }
        return resourceLoader.getResource(resource);
    }
    
    protected URL parseDoctype(String doctype) throws UnsupportedDocumentTypeException, IOException {
        Matcher matcher = dtdPattern.matcher(doctype);
        if (matcher.matches()) {
            if (matcher.group(3).startsWith("PUBLIC")) {
                String pub = matcher.group(5);
                String sys = matcher.group(6);
                pub = pub.substring(1, pub.length() - 1);
                sys = sys.substring(1, sys.length() - 1);
                return resolver.resolve(pub, sys);                
            } 
            String sys = matcher.group(4);                        
            sys = sys.substring(1, sys.length() - 1);
            return resolver.resolve(null, sys);            
        } 
        throw new UnsupportedDocumentTypeException("Cannot parse doctype declaration");        
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
