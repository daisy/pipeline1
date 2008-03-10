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
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.xslt.Stylesheet;
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
		String lang = ((String)mParameters.get("documentLanguage"));
		if (lang==null) { lang = ""; }
		if ("".equals(lang)) {
			mParameters.put("jvmDefaultLocale", Locale.getDefault().toString().replace('_', '-'));
		} else {
			mParameters.put("documentLanguage", lang);
		}
		super.execute(source, result);
	}

}
