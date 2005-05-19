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
package org.daisy.dmfc.transformers;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.file.FilenameOrFileURI;

/**
 * Transform a XML document using XSLT. The XSLTRunner class is an internal
 * transformer class of DMFC.
 * <p>
 * The Transformer reads four arguments:
 * <ul> 
 * <li><code>xml</code> - The URI of a XML document</li>
 * <li><code>xslt</code> - The URI of a XSLT Stylesheet</li>
 * <li><code>out</code> - The URI of the output document</li>
 * <li><code>factory</code> - The fully qualified name of the TransformerFactory to use.
 * If the <code>factory</code> parameter is not specified, the default
 * TransformerFactory will be used.</li>
 * </ul>
 * </p>
 * @author Linus Ericson
 */
public class XSLTRunner extends Transformer {

    /**
     * Constructs a new XSLTRunner transformer.
     * @param inputListener an input listener
     * @param eventListeners a set of event listeners
     * @param interactive specified whether the Transformer should be run in interactive mode
     */
    public XSLTRunner(InputListener inputListener, Set eventListeners, Boolean interactive) {
        super(inputListener, eventListeners, interactive);
    }

    protected boolean execute(Map parameters) throws TransformerRunException {
        String xmlFileName = (String)parameters.remove("xml");
        String xsltFileName = (String)parameters.remove("xslt");
        String outFileName = (String)parameters.remove("out");
        String factory = (String)parameters.remove("factory");
        
        // Set input files
        sendMessage(Level.FINE, i18n("XSLT_READING_XML", xmlFileName));
        Source xml = new StreamSource(FilenameOrFileURI.toFile(xmlFileName));
        sendMessage(Level.FINE, i18n("XSLT_READING_XSLT", xsltFileName));
		Source xslt = new StreamSource(FilenameOrFileURI.toFile(xsltFileName));
		
		try {
		    String property = "javax.xml.transform.TransformerFactory";
		    String oldFactory = System.getProperty(property);
		    if (factory != null) {
		        System.setProperty(property, factory);
		    }
			TransformerFactory _transformerFactory = TransformerFactory.newInstance();			
			
			// Reset old factory
			System.setProperty(property, (oldFactory==null?"":oldFactory));			
					    
            javax.xml.transform.Transformer transformer = _transformerFactory.newTransformer(xslt);
            
            // Set any parameters to the XSLT
            for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry paramEntry = (Map.Entry)it.next();
                transformer.setParameter((String)paramEntry.getKey(), paramEntry.getValue());
            }
            
            // Set output file
            File outFile = FilenameOrFileURI.toFile(outFileName);            
            StreamResult sr = new StreamResult(outFile);
            sr.setSystemId(outFile.toURI().toString());
            
            // Perform transformation
            sendMessage(Level.FINE, i18n("XSLT_WRITING_OUT", outFile.toString()));
            transformer.transform(xml, sr);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new TransformerRunException(e.getMessage(), e);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new TransformerRunException(e.getMessage(), e);
        }
        return true;
    }

}
