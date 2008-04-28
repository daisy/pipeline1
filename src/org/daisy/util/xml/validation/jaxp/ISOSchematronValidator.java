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
package org.daisy.util.xml.validation.jaxp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.xslt.stylesheets.Stylesheets;
import org.xml.sax.SAXException;

/**
 *  This implementation uses Jing. It allows compound schema input (sch embedded in RNG or WXS).
 *  @author Markus Gylling
 */
public class ISOSchematronValidator extends AbstractValidator implements ErrorListener, URIResolver {
	private Set<Transformer> compiledSchemas = null;
	private ISOSchematronSchema schema = null;	
	
	/*package*/ ISOSchematronValidator(ISOSchematronSchema schema) {
		super();
		this.schema = schema;
	}

	/*package*/ boolean initialize() {
		compiledSchemas = new HashSet<Transformer>();
		TransformerFactory tfac = TransformerFactory.newInstance();
		tfac.setURIResolver(this);
		tfac.setErrorListener(this);
		
		try {			
			URL skeletonURL = Stylesheets.get("iso_svrl.xsl");
			Transformer skeleton = tfac.newTransformer(new StreamSource(skeletonURL.openStream()));
			skeleton.setErrorListener(this);
			
			//for each schema registered in the Schema object, create the intermediary XSL
			for (int i = 0; i < schema.sources.length; i++) {
				File temp = TempFile.create();
				StreamResult r = new StreamResult(temp);
				skeleton.transform(schema.sources[i], r);
				Transformer compiled = tfac.newTransformer(new StreamSource(temp));
				compiled.setErrorListener(this);				
				compiledSchemas.add(compiled);		
				temp.delete();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		} 
		return true;
	}
	
	@SuppressWarnings("unused")
	public void validate(Source source, Result result) throws SAXException, IOException {		
		try {			
			for(Transformer t : compiledSchemas) {				
				t.transform(source, result);
			}			
		} catch (TransformerException e) {
			throw ExceptionTransformer.newSAXException(e);
		}
	}

	public void validate(Source source) throws SAXException, IOException {				
		this.validate(source, new SAXResult(new ISOSchematronSVRLHandler(this.errorHandler, source)));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	@SuppressWarnings("unused")
	public void error(TransformerException te) throws TransformerException {		
		try {
			this.errorHandler.error(ExceptionTransformer.newSAXParseException(te));
		} catch (SAXException e) {
			e.printStackTrace();
		} 		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	@SuppressWarnings("unused")
	public void fatalError(TransformerException te) throws TransformerException {
		try {
			this.errorHandler.error(ExceptionTransformer.newSAXParseException(te));
		} catch (SAXException e) {
			e.printStackTrace();
		} 		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	@SuppressWarnings("unused")
	public void warning(TransformerException te) throws TransformerException {
		try {
			this.errorHandler.error(ExceptionTransformer.newSAXParseException(te));
		} catch (SAXException e) {
			e.printStackTrace();
		} 		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unused")
	public Source resolve(String href, String base) throws TransformerException {
		/*
		 * The first time this is called is when the SRVL XSL looks
		 * for the skeleton reference
		 */
		URL url = Stylesheets.get(href);
		if(url!=null) {
			try {
				StreamSource ss = new StreamSource(url.openStream());
				ss.setSystemId(url.toExternalForm());
				return ss;
			} catch (IOException e) {
				throw new TransformerException(e.getMessage(),e);				
			}
		}	
		
		return null;
		
	}
}
