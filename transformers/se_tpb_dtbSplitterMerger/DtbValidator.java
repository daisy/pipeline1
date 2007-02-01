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

package se_tpb_dtbSplitterMerger;
/*
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple validation tool. Error messages are sent to the {@link DtbTransformationReporter}.
 *  
 * @author Piotr Kiernicki
 */
public class DtbValidator extends DefaultHandler {
	
	private DtbTransformationReporter reportGenerator = null;
	
	public DtbValidator(DtbTransformationReporter reportGen){
		this.reportGenerator = reportGen;
	}
	
    public void dtdSaxValidate(File docFile)throws XmlParsingException {
        
        Map features = new HashMap();
        SAXParser saxParser = null;
        try{
            features.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
            features.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.TRUE);            
            saxParser = SAXParserPool.getInstance().acquire(features,null);
            saxParser.getXMLReader().setContentHandler(new DefaultHandler());
            saxParser.getXMLReader().setErrorHandler(new DtbErrorHandler(docFile.getAbsolutePath(), this.reportGenerator));
            saxParser.getXMLReader().setEntityResolver(CatalogEntityResolver.getInstance());
            InputSource input = new InputSource(new FileInputStream(docFile));
            saxParser.getXMLReader().parse(input);         
        }catch (Exception e) {
            throw new XmlParsingException(docFile.getAbsolutePath());        
        }finally{
            try {
                SAXParserPool.getInstance().release(saxParser,features,null);
            } catch (PoolException e) {

            }
        }
    }

}
