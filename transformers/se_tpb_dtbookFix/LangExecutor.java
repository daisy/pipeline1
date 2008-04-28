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
package se_tpb_dtbookFix;

import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import net.sf.saxon.event.MessageEmitter;

import org.daisy.pipeline.core.transformer.TransformerDelegateListener;
import org.daisy.pipeline.exception.TransformerRunException;
import org.xml.sax.EntityResolver;

public class LangExecutor extends XSLTExecutor {


	public LangExecutor(Map<String, String> parameters, URL xslt,
			String niceName, TransformerDelegateListener transformer, URIResolver uriResolver,
			EntityResolver entityResolver, MessageEmitter emitter) {
		super(parameters, xslt, 
				new String[]{"2005-1", "2005-2", "2005-3"}, niceName, 
				transformer, uriResolver, entityResolver, emitter);
	}

	@Override
	void execute(Source source, Result result) throws TransformerRunException {
		String lang = mParameters.get("documentLanguage");
		if (lang==null) { lang = ""; }
		if ("".equals(lang)) {
			mParameters.put("jvmDefaultLocale", Locale.getDefault().toString().replace('_', '-'));
		} else {
			mParameters.put("documentLanguage", lang);
		}
		super.execute(source, result);
	}

}
