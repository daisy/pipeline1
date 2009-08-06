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
package org.daisy.util.xml.xslt;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import org.daisy.util.file.FilenameOrFileURI;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.daisy.util.xml.catalog.CatalogURIResolver;

/**
 * A cache of precompiled stylesheets.
 * 
 * @author Linus Ericson
 */
public class TransformerCache {

	private Map<URI, Templates> cache = new ConcurrentHashMap<URI, Templates>();

	/**
	 * Get a compiled stylesheet. If the stylesheet at the specified location
	 * already exists in the cache, the precompiled version will be used.
	 * Otherwise a stylesheet will be compiled.
	 * 
	 * @param fileOrFileUri
	 *            filename or file URI to the stylesheet
	 * @param factory
	 *            the factory to use for the stylesheet compilation
	 * @param errorListener
	 *            an ErrorListener
	 * @return a compiled stylesheet
	 * @throws XSLTException
	 */
	public Transformer get(String fileOrFileUri, String factory,
			ErrorListener errorListener) throws XSLTException {
		Templates entry = cache.get(FilenameOrFileURI.toURI(fileOrFileUri));
		if (entry == null) {
			entry = Stylesheet.createTemplate(fileOrFileUri, factory,
					errorListener);
			cache.put(FilenameOrFileURI.toURI(fileOrFileUri), entry);
		}
		try {
			Transformer res = entry.newTransformer();
			res.setURIResolver(new CatalogURIResolver());
			return res;
		} catch (TransformerConfigurationException e) {
			throw new XSLTException(e.getMessage(), e);
		} catch (CatalogExceptionNotRecoverable e) {
			throw new XSLTException(e.getMessage(), e);
		}
	}

	/**
	 * Get a compiled stylesheet. If the stylesheet at the specified location
	 * already exists in the cache, the precompiled version will be used.
	 * Otherwise a stylesheet will be compiled.
	 * 
	 * @param fileOrFileUri
	 *            filename or file URI to the stylesheet
	 * @param factory
	 *            the factory to use for the stylesheet compilation
	 * @return a compiled stylesheet
	 * @throws XSLTException
	 */
	public Transformer get(String fileOrFileUri, String factory)
			throws XSLTException {
		return get(fileOrFileUri, factory, null);
	}

}
