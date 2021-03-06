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
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.daisy.util.exception.ExceptionTransformer;
import org.daisy.util.xml.validation.SchemaLanguageConstants;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * 
 * @author Markus Gylling
 */
public abstract class AbstractSchemaFactory extends javax.xml.validation.SchemaFactory {

	private ErrorHandler errorHandler = null;
	private LSResourceResolver resourceResolver = null;
	private EntityResolver entityResolver = null;
	protected String schemaLanguage = null;	

	/* 
	 * This is for the reflection instantiation that the SchemaFactoryFinder performs.
	 */
 	public AbstractSchemaFactory() {
 		super();
 	}
	
 	/*package*/ AbstractSchemaFactory(String schemaLanguage) {
 		super();
 	}
	 	 	
	public Schema newSchema(File schema) throws SAXException {
		try {
			//our preferred way to represent the schema docs
			//is URL since we must expect jarness
			return newSchema(schema.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new SAXException(e.getMessage(),e);
		} catch (SAXException e) {
			throw e;
		}
	}

	public Schema newSchema(URL schema) throws SAXException {		
		Schema ret = null;
		try {
			if(this.schemaLanguage == SchemaLanguageConstants.RELAXNG_NS_URI) {
				ret = new RelaxNGSchema(schema, this);
			}else if(this.schemaLanguage == SchemaLanguageConstants.SCHEMATRON_NS_URI){			
				ret = new SchematronSchema(schema, this);			
			}else if(this.schemaLanguage == SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI){			
				ret = new ISOSchematronSchema(schema, this);
			}	
		} catch (Exception e) {
			throw ExceptionTransformer.newSAXParseException(e);
		}
		return ret;				
	}

	public Schema newSchema(Source schema) throws SAXException {	
		return this.newSchema(new Source[]{schema});
	}
	
	public Schema newSchema(Source[] schemas) throws SAXException {
		Schema ret = null;
		try {
			if(this.schemaLanguage == SchemaLanguageConstants.RELAXNG_NS_URI) {
				ret = new RelaxNGSchema(schemas, this);
			}else if(this.schemaLanguage == SchemaLanguageConstants.SCHEMATRON_NS_URI){
				ret = new SchematronSchema(schemas, this);
			}else if(this.schemaLanguage == SchemaLanguageConstants.ISO_SCHEMATRON_NS_URI){
				ret = new ISOSchematronSchema(schemas, this);
			}else if(this.schemaLanguage == SchemaLanguageConstants.NVDL_NS_URI){
				ret = new NVDLSchema(schemas, this);	
			}	
		} catch (Exception e) {
			throw ExceptionTransformer.newSAXParseException(e);
		}
		return ret;
	}
		
	public Schema newSchema() throws SAXException {		
		throw new SAXException("use File or URL calls for now");
	}
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public LSResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	public void setResourceResolver(LSResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
		if(this.resourceResolver instanceof EntityResolver){
			this.entityResolver = (EntityResolver)this.resourceResolver;
		}
	}
	
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}	
	
	@SuppressWarnings("unused")
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);	                
	}

	@SuppressWarnings("unused")
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);	         
	}
	
	@SuppressWarnings("unused")
	public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");        
       	throw new SAXNotRecognizedException(name);	 
	}

	@SuppressWarnings("unused")
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) throw new NullPointerException("the name parameter is null");
        throw new SAXNotRecognizedException(name);
    }
}
