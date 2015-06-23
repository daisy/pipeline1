package org_pef_text.text2pef;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.daisy.braille.pef.TextHandler;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

/**
 * Transformer wrapper for TextParser 
 * 
 * @author  Joel Hakansson, TPB
 * @version 3 sep 2008
 * @since 1.0
 */
public class Text2PEF extends Transformer {

	/**
	 * Default constructor
	 * @param inListener
	 * @param isInteractive
	 */
	public Text2PEF(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}
	
	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		File input = new File(parameters.get("input"));
		File output = new File(parameters.get("output"));
		String mode = parameters.get("mode");
		String title = parameters.get("title");
		String author = parameters.get("author");
		String identifier = parameters.get("identifier");
		String language = parameters.get("language");
		//TODO: Implement support to set date
		String date = parameters.get("date");
		if (title==null) title="";
		if (author==null) author="";
		if (identifier==null) identifier="";
		progress(0);
		try {
			TextHandler.Builder builder = new TextHandler.Builder(input, output);
			if (mode!=null && !"".equals(mode) && !"detect".equals(mode)) {
				builder.converterId(mode);
			}
			if (title!=null && !"".equals(title)) {
				builder.title(title);
			}
			if (author!=null && !"".equals(author)) {
				builder.author(author);
			}
			if (identifier!=null && !"".equals(identifier)) {
				builder.identifier(identifier);
			}
			if (language!=null && !"".equals(language)) {
				builder.language(language);
			}
			TextHandler tp = builder.build();
			tp.parse();
		} catch (UnsupportedEncodingException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TransformerRunException(e.getMessage(), e);
		} finally {
			
		}
		progress(1);
		return true;
	}

}
