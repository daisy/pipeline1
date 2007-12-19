package org.daisy.util.xml.validation.jaxp;

import javax.xml.transform.TransformerFactory;

import com.thaiopensource.validate.schematron.SchematronSchemaReaderFactory;

/**
 * Mimics James Clarks <code>com.thaiopensource.validate.schematron.SaxonSchemaReaderFactory</code>
 * but uses the qualified names of the Saxon 8/9 series.
 * 
 * @author Markus Gylling
 */
public class SaxonSchematronSchemaReaderFactory extends SchematronSchemaReaderFactory {
	@Override
	public TransformerFactory newTransformerFactory() {
		net.sf.saxon.TransformerFactoryImpl fac = new net.sf.saxon.TransformerFactoryImpl();
		
		try{
			fac.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
		}catch (Exception e){
			e.printStackTrace();
		}
		return fac;			
	}
}
