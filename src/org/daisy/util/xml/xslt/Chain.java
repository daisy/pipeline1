/*
 * org.daisy.util - The DAISY java utility library
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

package org.daisy.util.xml.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/**
 * Creates a chain of XSLT transformations. The transformations are applied in
 * the same order as they are added.
 * @author Linus Ericson
 */
public class Chain {
    Collection chain = new ArrayList();
    String factoryImpl = null;
    EntityResolver resolver = null;
    XMLReader reader = null;
    
    /**
     * Constructs a new XSLT chain. This constructor does noth specify what
     * TransformerFactory implementation to use. The implementation defined by
     * the 'javax.xml.transform.TransformerFactory' system property will be
     * used.
     */
    public Chain() {        
    }

    /**
     * Constructs a new XSLT chain. This constructor specifies what
     * TransformerFactory to use for the XSLT transformations.
     * @param factory a fully qualified class name of a TransformerFactory implementation
     * @param entityResolver the entity resolver to use.
     */
    public Chain(String factory, EntityResolver entityResolver) {
        factoryImpl = factory;
        resolver = entityResolver;
    }
    
    /**
     * Adds a stylesheet to the chain.
     * @param source a stylesheet
     */
    public void addStylesheet(Source source) {
        chain.add(source);
    }
    
    /**
     * Adds a stylesheet to the chain.
     * @param xslt
     * @see #addStylesheet(Source)
     */
    public void addStylesheet(File xslt) {
        addStylesheet(new StreamSource(xslt));
    }
    
    /**
     * mg: force the instance to use this xmlreader
     */
    public void setXMLReader(XMLReader xr) {
    	this.reader = xr;
    }
    
    /**
     * Applies the stylesheet chain to an XML doucment.
     * @param xml the XML document
     * @param result the result of the transformation chain
     * @throws XSLTException
     */
    public void applyChain(InputSource xml, Result result) throws XSLTException {
        if (chain.size() == 0) {
            throw new XSLTException("No stylesheets to apply.");
        }
        try {
        	if(reader == null) {
        		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        		parserFactory.setNamespaceAware(true);                     		
        		SAXParser parser = parserFactory.newSAXParser();
        		reader = parser.getXMLReader();
        	}
        	
            if (resolver != null) {
                reader.setEntityResolver(resolver);
            }
            
            // Create transformer factory using correct implementation
		    String property = "javax.xml.transform.TransformerFactory";
		    String oldFactory = System.getProperty(property);
		    if (factoryImpl != null) {
		        System.setProperty(property, factoryImpl);
		    }
            SAXTransformerFactory transformerFactory = (SAXTransformerFactory)TransformerFactory.newInstance();
			try {
				transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
			} catch (IllegalArgumentException iae) {
				
			}
            // Reset old transformer factory property
			System.setProperty(property, (oldFactory==null?"":oldFactory));	
            
			// Setup the filter chaining
            XMLFilter parentFilter = null;
            for (Iterator it = chain.iterator(); it.hasNext(); ) {
                Source xsltSource = (Source)it.next();
                XMLFilter filter = transformerFactory.newXMLFilter(xsltSource);                
                if (resolver != null) {
                    filter.setEntityResolver(resolver);
                }
                
                if (parentFilter == null) {
                    filter.setParent(reader);
                } else {
                    filter.setParent(parentFilter);
                }
                parentFilter = filter;
            }
            
            // Create transformer
            Transformer transformer = transformerFactory.newTransformer();
            SAXSource xmlSource = new SAXSource(parentFilter, xml);

            // Apply transformation
            transformer.transform(xmlSource, result);
            
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (TransformerConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new XSLTException(e.getMessage(), e);
        }        
    }
    
    /**
     * Applies the stylesheet chain to an XML document.
     * @param xml a XML file
     * @param result a result file
     * @throws XSLTException
     * @see #applyChain(InputSource, Result)
     */
    public void applyChain(File xml, File result) throws XSLTException {
        try {
            applyChain(new InputSource(new FileInputStream(xml)), new StreamResult(result));
        }
        catch (FileNotFoundException e) {
            throw new XSLTException(e.getMessage(), e);
        }        
    }
    
    public static InputSource saxSourceToInputSource(SAXSource source) {    	
        return source.getInputSource();
    }
    
    public static InputSource streamSourceToInputSource(StreamSource source) {
        InputSource inputSource = new InputSource(source.getSystemId());
        inputSource.setByteStream(source.getInputStream());
        inputSource.setCharacterStream(source.getReader());
        inputSource.setPublicId(source.getPublicId());
        return inputSource;
    }
    
    /*
    public static void main(String args[]) {                
        try {
            Chain chain = new Chain("net.sf.saxon.TransformerFactoryImpl");        
            chain.addStylesheet(new File("C:\\temp\\sheet.xsl"));
            chain.addStylesheet(new File("C:\\temp\\sheet2.xsl"));
            chain.applyChain(new File("D:\\books\\tb50-nodoctype.xml"), new File("D:\\books\\chaintest.xml"));
        } catch (XSLTException e) {
            System.err.println("Nu bidde det fel");
        }
    }
    */
}
