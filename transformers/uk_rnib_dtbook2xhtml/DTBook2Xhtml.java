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
package uk_rnib_dtbook2xhtml;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.xslt.Stylesheet;
import org.daisy.util.xml.xslt.XSLTException;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;

public class DTBook2Xhtml extends Transformer {

	public DTBook2Xhtml(InputListener inListener, Set eventListeners, Boolean isInteractive) {
		super(inListener, eventListeners, isInteractive);
	}

	protected boolean execute(Map parameters) throws TransformerRunException {
		String xml = (String)parameters.remove("xml");
		String factory = (String)parameters.remove("factory");
		String out = (String)parameters.remove("out");
		
		URL url = Stylesheets.get("dtbook2xhtml.xsl");
		
		try {			
		    Stylesheet.apply(xml, url, out, factory, parameters, CatalogEntityResolver.getInstance());
        } catch (XSLTException e) {
            throw new TransformerRunException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new TransformerRunException(e.getMessage(), e);
		}
		
		return true;
	}

}
