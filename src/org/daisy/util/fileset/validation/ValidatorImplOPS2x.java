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
package org.daisy.util.fileset.validation;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;

import org.daisy.util.fileset.Fileset;
import org.daisy.util.fileset.FilesetType;
import org.daisy.util.fileset.util.FilesetConstants;
import org.daisy.util.fileset.validation.delegate.impl.InnerDocURICheckerDelegate;
import org.daisy.util.fileset.validation.delegate.impl.InterDocURICheckerDelegate;
import org.daisy.util.fileset.validation.exception.ValidatorException;
import org.daisy.util.fileset.validation.exception.ValidatorNotSupportedException;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.pool.StAXEventFactoryPool;

/**
 * A Fileset Validator for OPS 2.0 publications. Note - this validator is not for epub archives, check
 * ValidatorImplEpub for that.
 * @author Markus Gylling
 */
public class ValidatorImplOPS2x extends ValidatorImplAbstract implements Validator {

	ValidatorImplOPS2x() {
		super(FilesetType.OPS_20);
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(org.daisy.util.fileset.interfaces.Fileset)
	 */
	public void validate(Fileset fileset) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(fileset);
		validate();
	}


	/*
	 * (non-Javadoc)
	 * @see org.daisy.util.fileset.validation.ValidatorImplAbstract#validate(java.net.URI)
	 */
	public void validate(URI manifest) throws ValidatorException, ValidatorNotSupportedException {
		setStaticResources();
		super.validate(manifest);
		validate();
	}
	
	/**
	 * Perform additional validation other than that done through the call 
	 * to super.validate (which executes any schemas and delegates registered on super)
	 */
	@SuppressWarnings("unused")
	private void validate() throws ValidatorException, ValidatorNotSupportedException {

	}
	
	/**
	 * Set validation resources (schemas, delegates) that are hardcoded for this Fileset type.
	 */
	private void setStaticResources() throws ValidatorException{
		try{
			//schemas:			
			setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("opf20.rng"),"org.daisy.util.fileset.impl.Opf20FileImpl");			
			//for dtbook we need to use the three-param set method since there are different schemas for different version of the grammar
			setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("dtbook-2005-1.rng"),"org.daisy.util.fileset.impl.Z3986DtbookFileImpl", getDtbookVersionAttr("2005-1"));
			setSchema(CatalogEntityResolver.getInstance().resolveEntityToURL("dtbook-2005-2.rng"),"org.daisy.util.fileset.impl.Z3986DtbookFileImpl", getDtbookVersionAttr("2005-2"));
			
			//delegates:								
			setDelegate(new InterDocURICheckerDelegate());									
			setDelegate(new InnerDocURICheckerDelegate());
									 
		}catch (Exception e) {
			throw new ValidatorException(e.getMessage(),e);
		}
	}
	
	private Set<Attribute> getDtbookVersionAttr(String value) {
		Set<Attribute> set = new HashSet<Attribute>();		
		XMLEventFactory xef = null;
		QName dtbookVersionAttr = new QName(FilesetConstants.NAMESPACEURI_DTBOOK_Z2005, "version");
		try{
			xef = StAXEventFactoryPool.getInstance().acquire();
			set.add(xef.createAttribute(dtbookVersionAttr, value));
		}finally{
			StAXEventFactoryPool.getInstance().release(xef);
		}
		return set;
	}
}
