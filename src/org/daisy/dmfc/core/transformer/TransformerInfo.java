/*
 * Created on 2005-mar-16
 */
package org.daisy.dmfc.core.transformer;

import java.util.Collection;

/**
 * @author LINUSE
 */
public interface TransformerInfo {
	/**
	 * @return the name of the Transformer
	 */
	public String getName();

	/**
	 * @return a description of the Transformer
	 */
	public String getDescription();
	
	/**
	 * @return a collection of paramters that the Transfomer accepts as input
	 */
	public Collection getParameters();	
}
