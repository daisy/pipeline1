package org_pef_dtbook2pef.system.tasks;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.MessageEvent.Type;
import org.daisy.util.file.FileUtils;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.InternalTaskException;

/**
 * <p>This tasks validates the input file against the given schema and copies the original file to 
 * the output file, to conform with the contract of an {@link InternalTask}.</p>
 * <p>The tasks throws an exception if anything goes wrong.</p>
 * <p>Input file type requirement: XML</p>
 * @author Joel Håkansson, TPB
 *
 */
public class ValidatorTask extends InternalTask implements ErrorHandler {
	private URL schema;
	private boolean error = false;

	public ValidatorTask(String name, URL schema) {
		super(name);
		this.schema = schema;
	}

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		try {
			SimpleValidator sv = new SimpleValidator(schema, this);
			boolean ret = sv.validate(input.toURI().toURL());
			FileUtils.copy(input, output);
			if (ret && !error) {
				return;
			}
		} catch (SAXException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (TransformerException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (ValidationException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (MalformedURLException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		} catch (IOException e) {
			throw new InternalTaskException("Input validation failed: ", e);
		}
		throw new InternalTaskException("Input validation failed.");
	}

	public void error(SAXParseException exception) throws SAXException {
		EventBus.getInstance().publish(new MessageEvent(this, exception.getMessage().replaceAll("\\s+", " "), Type.ERROR));
		error = true;
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw new SAXException(exception);
	}

	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(exception.toString());
	}

}
