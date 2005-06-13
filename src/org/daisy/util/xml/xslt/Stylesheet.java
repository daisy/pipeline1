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
package org.daisy.util.xml.xslt;

import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.file.FilenameOrFileURI;

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
     * @throws XSLTException
     * @see javax.xml.transform.Source
     * @see javax.xml.transform.Result
     */
    public static void apply(Source xml, Source xslt, Result result, String factory, Map parameters) throws XSLTException{        		
		try {
		    // Create factory
		    String property = "javax.xml.transform.TransformerFactory";
		    String oldFactory = System.getProperty(property);
		    if (factory != null) {
		        System.setProperty(property, factory);
		    }
			TransformerFactory transformerFactory = TransformerFactory.newInstance();			
			
			// Reset old factory property
			System.setProperty(property, (oldFactory==null?"":oldFactory));			
					   
			// Create transformer
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer(xslt);

            // Set any parameters to the XSLT
            if (parameters != null) {
	            for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
	                Map.Entry paramEntry = (Map.Entry)it.next();
	                transformer.setParameter((String)paramEntry.getKey(), paramEntry.getValue());
	            }
            }
                        
            // Perform transformation            
            transformer.transform(xml, result);
        } catch (TransformerConfigurationException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        } catch (TransformerException e) {
            throw new XSLTException(e.getMessageAndLocation(), e);            
        }
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
     * regular filenames.
     * @param xmlFile a file URI or a filename to the XML file.
     * @param xsltFile a file URI or a filename to the XSLT stylesheet.
     * @param outFile a file URI or a filename to the output doucment.
     * @param factory the name of the <code>TransformerFactory</code> to use.
     * @param parameters a <code>Map</code> containing parameters that are sent to the stylesheet.
     * @throws XSLTException
     * @see #apply(Source, Source, Result, String, Map)
     */
    public static void apply(String xmlFile, String xsltFile, String outFile, String factory, Map parameters) throws XSLTException {
        Source xmlSource = new StreamSource(FilenameOrFileURI.toFile(xmlFile));
        Source xsltSource = new StreamSource(FilenameOrFileURI.toFile(xsltFile));
        Result outResult = new StreamResult(FilenameOrFileURI.toFile(outFile));
        apply(xmlSource, xsltSource, outResult, factory, parameters);
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
     * @see #apply(String, String, String, String, Map)
     */
    public static void apply(String xmlFile, String xsltFile, String outFile) throws XSLTException {        
        apply(xmlFile, xsltFile, outFile, null, null);
    }
}
