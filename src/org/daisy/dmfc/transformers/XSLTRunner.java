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
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;

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
     * @param a_inputListener an input listener
     * @param a_eventListeners a set of event listeners
     * @param a_interactive specified whether the Transformer should be run in interactive mode
     */
    public XSLTRunner(InputListener a_inputListener, Set a_eventListeners, Boolean a_interactive) {
        super(a_inputListener, a_eventListeners, a_interactive);
    }

    public boolean execute(Map a_parameters) throws TransformerRunException {
        String _xmlFileName = (String)a_parameters.get("xml");
        String _xsltFileName = (String)a_parameters.get("xslt");
        String _outFileName = (String)a_parameters.get("out");
        String _factory = (String)a_parameters.get("factory");
        
        Source _xml = new StreamSource(_xmlFileName);
		Source _xslt = new StreamSource(_xsltFileName);		
		try {
		    String _property = "javax.xml.transform.TransformerFactory";
		    String _oldFactory = System.getProperty(_property);
		    if (_factory != null) {
		        System.setProperty(_property, _factory);
		    }
			TransformerFactory _transformerFactory = TransformerFactory.newInstance();			
			if (_factory != null) {
			    System.setProperty(_property, (_oldFactory==null?"":_oldFactory));
			}
					    
            javax.xml.transform.Transformer _transformer = _transformerFactory.newTransformer(_xslt);
            _transformer.transform(_xml, new StreamResult(new File(_outFileName)));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new TransformerRunException(e.getMessage(), e);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new TransformerRunException(e.getMessage(), e);
        }
        return false;
    }

}
