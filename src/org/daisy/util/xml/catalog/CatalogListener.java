package org.daisy.util.xml.catalog;

/**
 * Retrieve information on entities not contained in a catalog
 * @author Markus Gylling
 */
public interface CatalogListener {

	/**
	 * This callback is made when the Catalog to which this 
	 * listener is registered could not resolve this entity 
	 * to a catalog-local resource.
	 */
	public void entityNotSupported(String entity);
				
}
