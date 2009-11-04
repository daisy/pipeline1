package org_pef_dtbook2pef.system.tasks.layout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.daisy.pipeline.exception.TransformerRunException;
import org.xml.sax.SAXException;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.tasks.layout.flow.Flow;
import org_pef_dtbook2pef.system.tasks.layout.flow.FlowHandler;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformerException;

/**
 * breaks row flow into pages
 * @author joha
 *
 */
public class LayoutEngineTask extends InternalTask  {
	private final Flow flow;
	private LayoutPerformer lp;
	
	public LayoutEngineTask(String name, Flow flow, LayoutPerformer lp) {
		super(name);
		this.flow = flow;
		this.lp = lp;
	}

	@Override
	public void execute(File input, File output)
			throws TransformerRunException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, new FlowHandler(flow));
			FileOutputStream os = new FileOutputStream(output);
			lp.layout(os);
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException while runing task.", e);
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException while runing task. ", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException while runing task. ", e);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException("ParserConfigurationException while runing task. ", e);
		} catch (LayoutPerformerException e) {
			throw new TransformerRunException("Exception in layout performer.", e);
		}
	}

}
 