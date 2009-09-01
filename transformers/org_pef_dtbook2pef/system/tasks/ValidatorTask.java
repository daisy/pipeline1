package org_pef_dtbook2pef.system.tasks;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org_pef_dtbook2pef.system.InternalTask;

public class ValidatorTask extends InternalTask implements ErrorHandler {
	private URL schema;
	

	public ValidatorTask(String name, URL schema) {
		super(name);
		this.schema = schema;
	}

	@Override
	public void execute(File input, File output, HashMap<String, String> options)
			throws TransformerRunException {
		try {
			SimpleValidator sv = new SimpleValidator(schema, this);
			boolean ret = sv.validate(input.toURI().toURL());
			FileUtils.copy(input, output);
			if (ret) {
				return;
			}
		} catch (SAXException e) {
			throw new TransformerRunException("Input validation failed: ", e);
		} catch (TransformerException e) {
			throw new TransformerRunException("Input validation failed: ", e);
		} catch (ValidationException e) {
			throw new TransformerRunException("Input validation failed: ", e);
		} catch (MalformedURLException e) {
			throw new TransformerRunException("Input validation failed: ", e);
		} catch (IOException e) {
			throw new TransformerRunException("Input validation failed: ", e);
		}
		throw new TransformerRunException("Input validation failed.");
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
		throw new SAXException(exception);
		// TODO Auto-generated method stub
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
		throw new SAXException(exception);
		// TODO Auto-generated method stub
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
		// TODO Auto-generated method stub
	}

}
