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

import javax.xml.validation.SchemaFactory;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.sax.SAXParseExceptionMessageFormatter;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author Markus Gylling
 */
public abstract class AbstractValidator extends javax.xml.validation.Validator implements ErrorHandler {
	protected ErrorHandler errorHandler = null;
	protected LSResourceResolver resourceResolver = null;
	protected EntityResolver entityResolver = null;
	boolean configSuccess = true;

	AbstractValidator(){
		super();
		errorHandler = this;
		try {
			//default value is not null 
			resourceResolver = CatalogEntityResolver.getInstance();
			entityResolver = CatalogEntityResolver.getInstance();
		} catch (CatalogExceptionNotRecoverable e) {
			
		}
	}
	/**
	 * Reset this Validator to its original configuration.
	 */
	public void reset() {
		errorHandler = this;
		try { 
			resourceResolver = CatalogEntityResolver.getInstance();
			entityResolver = CatalogEntityResolver.getInstance();
		} catch (CatalogExceptionNotRecoverable e) {
			
		}	
	}

	/**
	 * If they are not null, propagates ErrorHandler and ResourceResolver from the SchemaFactory that originated the Schema.
	 */
	public void propagateHandlers(SchemaFactory handlerCarrier) {
		if(handlerCarrier.getErrorHandler()!=null) this.setErrorHandler(handlerCarrier.getErrorHandler());
		if(handlerCarrier.getResourceResolver()!=null) this.setResourceResolver(handlerCarrier.getResourceResolver());
	}
	
		
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;		
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	public void setResourceResolver(LSResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;		
		if(this.resourceResolver instanceof EntityResolver){
			this.entityResolver = (EntityResolver)this.resourceResolver;
		}
	}

	public LSResourceResolver getResourceResolver() {
		return this.resourceResolver;		
	}

	/**
	 * Extension to javax.xml.validation.Validator
	 */
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	/**
	 * Extension to javax.xml.validation.Validator
	 */
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will receive error notifications if the
	 * user has not registered another errorhandler.
	 */
	@SuppressWarnings("unused")
	public void warning(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Warning", spe));
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will receive error notifications if the
	 * user has not registered another errorhandler.
	 */	
	@SuppressWarnings("unused")
	public void error(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Error", spe));
	}

	/**
	 * Extension to javax.xml.validation.Validator.
	 * This is the org.xml.sax.ErrorHandler impl
	 * that will receive error notifications if the
	 * user has not registered another errorhandler.
	 */
	@SuppressWarnings("unused")
	public void fatalError(SAXParseException spe) throws SAXException {
		System.err.println(SAXParseExceptionMessageFormatter.formatMessage("Fatal error", spe));		
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