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
package int_daisy_mixedContentNormalizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 *
 * @author Markus Gylling
 */
public abstract class AbstractSyncPointLocator {
		
	protected TransformerDelegateListener mTransformer  = null;
	protected int mSyncPointCount = 0;
		
	public AbstractSyncPointLocator(TransformerDelegateListener tdl) {				
		mTransformer = tdl;		
	}

	/**
	 * Locate sync points in inparam source, and return the result.
	 * <p>Sync points are identified through an attribute identified in config, typically smil:sync="true"</p>
	 */
	public abstract Result locate(Source source) throws TransformerRunException;
	    
	/**
	 * Retrieve the number of sync points that were identified.
	 */
	public int getNumberOfSyncPoints() {
		return mSyncPointCount;
	}
					
}
