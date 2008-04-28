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

package se_tpb_dtbSplitterMerger;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * A convenience class that can be used for parsing xml files. 
 * <br><br>
 * The default <code>org.xml.sax.EntityResolver</code> for <code>se_tpb_dtbSplitterMerger.DtbParsingInitializer</code> 
 * is <code>org.daisy.util.xml.catalog.CatalogEntityResolver</code> that holds the pathways to the local copies of 
 * the dtd files for Daisy 2.02 and Daisy 3 books.
 * <br><br>
 * The default <code>org.xml.sax.ErrorHandler</code> is the {@link se_tpb_dtbSplitterMerger.DtbErrorHandler}.
 * 
 * @see CatalogEntityResolver
 * @see DtbErrorHandler 
 * @author Piotr Kiernicki
 */
public class DtbParsingInitializer {
	
	DtbTransformationReporter reportGenerator = null;
	/*
	 * A List field for files that have been parsed 
	 * so as to avoid multiple validation of the same document.
	 *  
	 * Every parsing error is reported to the DtbTransformationReporter and 
	 * multiple validation would cause sending replicated error messages.
	 */
	private static List<File> validatedFiles = new ArrayList<File>();
	private EntityResolver resolver = null;
	
	public DtbParsingInitializer(DtbTransformationReporter reportGen){
		this.reportGenerator = reportGen;
		try {
            this.setEntityResolver(CatalogEntityResolver.getInstance());
        } catch (CatalogExceptionNotRecoverable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	
	/**
	 * <p>
	 * If multiple parsing of the same file occurs,
	 * <code>parseDocWithDOM</code> validates the document 
	 * only during the very first parsing of it.
	 * </p>
	 * <p>
	 * Sets the default {@link org.daisy.util.xml.catalog.CatalogEntityResolver} 
	 * and {@link se_tpb_dtbSplitterMerger.DtbErrorHandler} for the parser.
	 * </p>
	 * @throws XmlParsingException 
     * @see  org.daisy.util.xml.catalog.CatalogEntityResolver
     * @see  DtbErrorHandler
	 */
 	public Document parseDocWithDOM(File docFile) throws XmlParsingException{
 		Document doc = null;
		DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docB = docBF.newDocumentBuilder();
			DtbParsingInitializer.setValidation(docB, docFile);
			
			docB.setEntityResolver(this.resolver);
			docB.setErrorHandler(new DtbErrorHandler(docFile.getAbsolutePath(), this.reportGenerator));
			
			InputSource input = new InputSource(new FileInputStream(docFile));
			doc = docB.parse(input);
		} catch (Exception pce) {
            throw new XmlParsingException(docFile.getAbsolutePath()); 
		}
		
		return doc;

	}
	

	

	private static void setValidation(Object parser, File docFile) {
		boolean validate = false;
		 
		if(!DtbParsingInitializer.validatedFiles.contains(docFile)){
			//the file has not been validated
			validate = true;
			//add it to the collection
			DtbParsingInitializer.validatedFiles.add(docFile);
		}
		if(parser instanceof DocumentBuilderFactory){
			((DocumentBuilderFactory)parser).setValidating(validate);	
		}else if(parser instanceof SAXParserFactory){
			((SAXParserFactory)parser).setValidating(validate);
		}
		
	}

	public void setEntityResolver(EntityResolver resolver){
		this.resolver = resolver;
	}
		
}
