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
package org.daisy.pipeline.transformers;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;

/**
 * Transform a XML document using XSLT. The XSLTRunner class is an internal
 * transformer class of DMFC.
 * <p>
 * The Transformer reads four arguments:
 * </p>
 * <ul> 
 * <li><code>xml</code> - The URI of a XML document</li>
 * <li><code>xslt</code> - The URI of a XSLT Stylesheet</li>
 * <li><code>out</code> - The URI of the output document</li>
 * <li><code>factory</code> - The fully qualified name of the TransformerFactory to use.
 * If the <code>factory</code> parameter is not specified, the default
 * TransformerFactory will be used.</li>
 * </ul>
 * <p>
 * Any remaining parameters will be sent as parameters to the xslt.
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
        
        // xml
        sendMessage(Level.FINE, i18n("XSLT_READING_XML", xmlFileName));
        
        // xslt
        sendMessage(Level.FINE, i18n("XSLT_READING_XSLT", xsltFileName));
        
        // factory
        if (factory != null) {
	        sendMessage(Level.FINER, i18n("XSLT_USING_FACTORY", factory));
	    }
        
        // result
        sendMessage(Level.FINE, i18n("XSLT_WRITING_OUT", outFileName));
		
		try {			
		    Stylesheet.apply(xmlFileName, xsltFileName, outFileName, factory, parameters, CatalogEntityResolver.getInstance());
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
        return true;
    }

}
