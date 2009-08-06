/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.xml.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogURIResolver;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Some utility functions for applying XSLT stylesheets. This class contains
 * a set of convenince functions that makes it a one-liner to apply a stylesheet
 * in Java.
 * @author Linus Ericson
 */
public class Stylesheet {
    
    /**
     * Apply an XSLT stylesheet to an XML document. To use a specific TransformerFactory
     * implementation, specify the <code>factory</code> parameter using a fully qualified
     * class name. If the <code>factory</code> parameter is set to <code>null</code>,
     * the TransformerFactory specified by the <code>javax.xml.transform.TransformerFactory</code>
     * system property is used.
     *  
     * @param xml the XML <code>Source</code>.
     * @param xslt the XSLT <code>Source</code>.
     * @param result the <code>Result</code>.
     * @param factory the name of the <code>TransformerFactory</code> to use.
     * @param parameters a <code>Map</code> containing parameters that are sent to the stylesheet.
     * @param errorListener an ErrorListener 
     * @throws XSLTException
     * @see javax.xml.transform.Source
     * @see javax.xml.transform.Result
     */
    public static void apply(Source xml, Source xslt, Result result, String factory, Map<String,Object> parameters, ErrorListener errorListener) throws XSLTException{        		
		try {
		    // Create factory
		    String property = "javax.xml.transform.TransformerFactory";
		    String oldFactory = System.getProperty(property);
		    if (factory != null) {
		        System.setProperty(property, factory);
		    }		    
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			
			URIResolver resolver = new CatalogURIResolver();
			
			try {
				transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
			} catch (IllegalArgumentException iae) {
				
			}
			
			if (errorListener != null) {
			    transformerFactory.setErrorListener(errorListener);
			}
			
			// Reset old factory property
			if (oldFactory != null) {
				System.setProperty(property, oldFactory);
			} else {
				System.clearProperty(property);
			}
						
						
			// Create transformer
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer(xslt);

            // Set any parameters to the XSLT
            if (parameters != null) {
	            for (Iterator<Map.Entry<String,Object>> it = parameters.entrySet().iterator(); it.hasNext(); ) {
	                Map.Entry<String,?> paramEntry = it.next();
	                transformer.setParameter(paramEntry.getKey(), paramEntry.getValue());
	            }
            }
            transformer.setURIResolver(resolver); 
            			
            //Create a SAXSource, hook up an entityresolver
	        if(xml.getSystemId()!=null && xml.getSystemId().length()>0) {
	        	try{
		            SAXSource saxSource = null;
					if(!(xml instanceof SAXSource)) {
						File input = FilenameOrFileURI.toFile(xml.getSystemId());
						Map<String,Object> features = new HashMap<String, Object>();
						Map<String,Object> properties = new HashMap<String, Object>();
						features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.FALSE);		
						SAXParser parser = SAXParserPool.getInstance().acquire(features, properties);                
				        saxSource = new SAXSource(parser.getXMLReader(), new InputSource(new FileInputStream(input)));        
				        saxSource.setSystemId(input.toString());
					}else{
						saxSource = (SAXSource) xml;
					}
					if(saxSource.getXMLReader().getEntityResolver()==null) {
						saxSource.getXMLReader().setEntityResolver(CatalogEntityResolver.getInstance());
					}	
					xml = saxSource;
	        	}catch (Exception e) {
					e.printStackTrace();
				}
            }
            // Perform transformation            
            transformer.transform(xml, result);
            
        } catch (TransformerConfigurationException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        } catch (TransformerException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        } catch (CatalogExceptionNotRecoverable e) {
            throw new XSLTException(e.getMessage(), e);
        } 
    }
    
    /**
     * Apply an XSLT transformation using a precompiled stylesheet.
     * @param xml the XML Source
     * @param xslt the precompiled stylesheet
     * @param result the Result
     * @param parameters any parameters to the stylesheet
     * @throws XSLTException
     */
    public static void apply(Source xml, Transformer xslt, Result result, Map<String,Object> parameters) throws XSLTException{             
        try {            
            // Set any parameters to the XSLT
            if (parameters != null) {
                for (Iterator<Map.Entry<String,Object>> it = parameters.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String,Object> paramEntry = it.next();
                    xslt.setParameter(paramEntry.getKey(), paramEntry.getValue());
                }
            }
            // Perform transformation            
            xslt.transform(xml, result);
        } catch (TransformerConfigurationException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        } catch (TransformerException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        }
    }
    
    /**
     * Apply an XSLT stylesheet to an XML document. To use a specific TransformerFactory
     * implementation, specify the <code>factory</code> parameter using a fully qualified
     * class name. If the <code>factory</code> parameter is set to <code>null</code>,
     * the TransformerFactory specified by the <code>javax.xml.transform.TransformerFactory</code>
     * system property is used.
     *  
     * @param xml the XML <code>Source</code>.
     * @param xslt the XSLT <code>Source</code>.
     * @param result the <code>Result</code>.
     * @param factory the name of the <code>TransformerFactory</code> to use.
     * @param parameters a <code>Map</code> containing parameters that are sent to the stylesheet. 
     * @throws XSLTException
     * @see javax.xml.transform.Source
     * @see javax.xml.transform.Result
     */
    public static void apply(Source xml, Source xslt, Result result, String factory, Map<String,Object> parameters) throws XSLTException{        		
		apply(xml, xslt, result, factory, parameters, null);
    }
        
    /**
     * Apply an XSLT stylesheet to an XML document. Overloaded method. This is
     * the same as calling
     * <pre>
     * apply(xml, xslt, result, null, null)
     * </pre>
     * @param xml the XML <code>Source</code>.
     * @param xslt the XSLT <code>Source</code>.
     * @param result the <code>Result</code>.
     * @throws XSLTException
     * @see #apply(Source, Source, Result, String, Map)
     */
    public static void apply(Source xml, Source xslt, Result result) throws XSLTException {
        apply(xml, xslt, result, null, null);
    }
        
    /**
     * Apply an XSLT stylesheet to an XML document. The input and output documents
     * can be specified using file URIs (i.e. <code>file://path/to/file</code>) or
     * regular filenames. If the <code>factory</code> parameter is set to
     * <code>null</code>, the TransformerFactory specified by the
     * <code>javax.xml.transform.TransformerFactory</code> system property is
     * used. If <code>resolver</code> is <code>null</code>, the default entity
     * resolver will be used.
     * @param xmlFile a file URI or a filename to the XML file.
     * @param xsltFile a file URI or a filename to the XSLT stylesheet.
     * @param outFile a file URI or a filename to the output doucment.
     * @param factory the name of the <code>TransformerFactory</code> to use.
     * @param parameters a <code>Map</code> containing parameters that are sent to the stylesheet.
     * @param resolver an entity resolver
     * @throws XSLTException
     * @see #apply(Source, Source, Result, String, Map)
     */
    public static void apply(String xmlFile, String xsltFile, String outFile, String factory, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try {
            SAXParser parser = parserFactory.newSAXParser();        
            XMLReader reader = parser.getXMLReader();
            if (resolver != null) {
                reader.setEntityResolver(resolver);
            }
            
            Source xmlSource = new SAXSource(reader, new InputSource(new FileInputStream(FilenameOrFileURI.toFile(xmlFile))));
            Source xsltSource = new SAXSource(reader, new InputSource(new FileInputStream(FilenameOrFileURI.toFile(xsltFile))));
            xmlSource.setSystemId(FilenameOrFileURI.toURI(xmlFile).toString());
            xsltSource.setSystemId(FilenameOrFileURI.toURI(xsltFile).toString());
	        Result outResult = new StreamResult(FilenameOrFileURI.toFile(outFile).toURI().toString());	        
	        apply(xmlSource, xsltSource, outResult, factory, parameters);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new XSLTException(e.getMessage(), e);
        }
    }
    
    public static void apply(String xmlFile, URL xsltUrl, String outFile, String factory, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try {
            SAXParser parser = parserFactory.newSAXParser();        
            XMLReader reader = parser.getXMLReader();
            if (resolver != null) {
                reader.setEntityResolver(resolver);
            }
            
            Source xmlSource = new SAXSource(reader, new InputSource(new FileInputStream(FilenameOrFileURI.toFile(xmlFile))));            
            Source xsltSource = new SAXSource(reader, new InputSource(xsltUrl.openStream()));
            xmlSource.setSystemId(FilenameOrFileURI.toURI(xmlFile).toString());
            xsltSource.setSystemId(xsltUrl.toString());
	        Result outResult = new StreamResult(FilenameOrFileURI.toFile(outFile).toURI().toString());	        
	        apply(xmlSource, xsltSource, outResult, factory, parameters);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (IOException e) {
        	throw new XSLTException(e.getMessage(), e);
		}
    }
    
    public static void apply(String xmlFile, Transformer xslt, String outFile, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try {
            Source xmlSource = file2source(FilenameOrFileURI.toFile(xmlFile), resolver);
            Result outResult = file2result(FilenameOrFileURI.toFile(outFile));            
            apply(xmlSource, xslt, outResult, parameters);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        }    
    }
    
    /**
     * Apply an XSLT stylesheet to an XML document. Overloaded method. This is
     * the same as calling
     * <pre>
     * apply(xmlFile, xsltFile, outFile, null, null)
     * </pre>
     * @param xmlFile a file URI or a filename to the XML file.
     * @param xsltFile a file URI or a filename to the XSLT stylesheet.
     * @param outFile a file URI or a filename to the output doucment.
     * @throws XSLTException
     * @see #apply(String, String, String, String, Map, EntityResolver)
     */
    public static void apply(String xmlFile, String xsltFile, String outFile) throws XSLTException {        
        apply(xmlFile, xsltFile, outFile, null, null, null);
    }

    /**
     * Apply a stylesheet.
     * @param xmlFile the string representation of a file or a file URI to the input.
     * @param xsltFile the string representation of a file or a file URI to the stylesheet.
     * @param outDom an output DOMResult
     * @param factory the stylesheet transformer factory to use
     * @param parameters any parameters to the stylesheet
     * @param resolver an entity resolver
     * @throws XSLTException
     */
    public static void apply(String xmlFile, String xsltFile, DOMResult outDom, String factory, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        try {
            Source xml = file2source(FilenameOrFileURI.toFile(xmlFile), resolver);
            Source xslt = file2source(FilenameOrFileURI.toFile(xsltFile), resolver);
            apply(xml, xslt, outDom, factory, parameters, null);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        }
    }
    /**
     * Apply a stylesheet.
     * @param xmlFile the string representation of a file or a file URI to the input.
     * @param xslt a precompiled stylesheet.
     * @param outDom an output DOMResult
     * @param parameters any parameters to the stylesheet
     * @param resolver an entity resolver
     * @throws XSLTException
     */
    public static void apply(String xmlFile, Transformer xslt, DOMResult outDom, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        try {
            Source xml = file2source(FilenameOrFileURI.toFile(xmlFile), resolver);
            apply(xml, xslt, outDom, parameters);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        }
    }
    
    /**
     * Apply a styleshet.
     * @param xmlDom an input DOMSource.
     * @param xsltFile the string representation of a file or a file URI to the stylesheet.
     * @param outDom an output DOMResult 
     * @param factory the stylesheet transformer factory to use
     * @param parameters any parameters to the stylesheet
     * @param resolver an entity resolver
     * @throws XSLTException
     */
    public static void apply(DOMSource xmlDom, String xsltFile, DOMResult outDom, String factory, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
       try {
           Source xslt = file2source(FilenameOrFileURI.toFile(xsltFile), resolver);
           apply(xmlDom, xslt, outDom, factory, parameters, null);
       } catch (ParserConfigurationException e) {
           throw new XSLTException(e.getMessage(), e);
       } catch (SAXException e) {
           throw new XSLTException(e.getMessage(), e);
       }
    }
    
    /**
     * Apply a stylesheet.
     * @param xmlDom an input DOMSource.
     * @param xsltFile the string representation of a file or a file URI to the stylesheet.
     * @param outFile the string representation of a file or a file URI to the output file.
     * @param factory the stylesheet transformer factory to use
     * @param parameters any parameters to the stylesheet
     * @param resolver an entity resolver
     * @throws XSLTException
     */
    public static void apply(DOMSource xmlDom, String xsltFile, String outFile, String factory, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        try {
            Source xslt = file2source(FilenameOrFileURI.toFile(xsltFile), resolver);
            Result result = file2result(FilenameOrFileURI.toFile(outFile));
            apply(xmlDom, xslt, result, factory, parameters, null);
        } catch (ParserConfigurationException e) {
            throw new XSLTException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new XSLTException(e.getMessage(), e);
        }
    }
    /**
     * Apply a stylesheet.
     * @param xmlDom an input DOMSource.
     * @param xslt a precompiled stylesheet.
     * @param outFile the string representation of a file or a file URI to the output file.
     * @param parameters any parameters to the stylesheet
     * @param resolver an entity resolver
     * @throws XSLTException
     */
    public static void apply(DOMSource xmlDom, Transformer xslt, String outFile, Map<String,Object> parameters, EntityResolver resolver) throws XSLTException {
        Result result = file2result(FilenameOrFileURI.toFile(outFile));
        apply(xmlDom, xslt, result, parameters);
    }
    
    public static void apply(DOMSource xmlDom, Transformer xslt, StringBuffer outBuffer, Map<String,Object> parameters, @SuppressWarnings("unused")
	EntityResolver resolver) throws XSLTException {
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);
        apply(xmlDom, xslt, result, parameters);
        outBuffer.append(sw.getBuffer());
    }
    
    public static Templates createTemplate(String fileOrFileUri,
			String factory, ErrorListener errorListener) throws XSLTException {
		// Create factory
		String property = "javax.xml.transform.TransformerFactory";
		String oldFactory = System.getProperty(property);
		if (factory != null) {
			System.setProperty(property, factory);
		}
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		try {
			transformerFactory.setAttribute(
					"http://saxon.sf.net/feature/version-warning",
					Boolean.FALSE);
		} catch (IllegalArgumentException iae) {}

		if (errorListener != null) {
			transformerFactory.setErrorListener(errorListener);
		}

		// Reset old factory property
		System.setProperty(property, (oldFactory == null ? "" : oldFactory));

		// Create transformer template
		Source xslt;
		try {
			xslt = file2source(FilenameOrFileURI.toFile(fileOrFileUri),
					CatalogEntityResolver.getInstance());
			return transformerFactory.newTemplates(xslt);
		} catch (ParserConfigurationException e) {
			throw new XSLTException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new XSLTException(e.getMessage(), e);
		} catch (TransformerConfigurationException e) {
			throw new XSLTException(e.getMessage(), e);
		}
	}
    
    /*package*/ static Source file2source(File xml, EntityResolver resolver) throws ParserConfigurationException, SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        SAXParser parser = parserFactory.newSAXParser();        
        XMLReader reader = parser.getXMLReader();
        if (resolver != null) {
            reader.setEntityResolver(resolver);
        }
        Source xmlSource = new SAXSource(reader, new InputSource(xml.toString()));
        xmlSource.setSystemId(xml.toURI().toString());
        return xmlSource;
    }
    
    /*package*/ static Result file2result(File xml) {
        Result result = new StreamResult(xml);
        return result;
    }
}