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
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriterException;

/**
 * breaks row flow into pages
 * @author joha
 *
 */
public class LayoutEngineTask extends InternalTask  {
	private final Flow performer;
	private LayoutPerformer paginator;
	private PagedMediaWriter writer;
	
	public LayoutEngineTask(String name, Flow flow, LayoutPerformer layoutPerformer, PagedMediaWriter writer) {
		super(name);
		this.performer = flow;
		this.paginator = layoutPerformer;
		this.writer = writer;
	}

	@Override
	public void execute(File input, File output)
			throws TransformerRunException {
		try {
			FileOutputStream os = new FileOutputStream(output);
			writer.open(os);
			paginator.open(writer);
			performer.open(paginator);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, new FlowHandler(performer));
			performer.close();
			paginator.close();
			writer.close();
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException while runing task.", e);
		} catch (FileNotFoundException e) {
			throw new TransformerRunException("FileNotFoundException while runing task. ", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException while runing task. ", e);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException("ParserConfigurationException while runing task. ", e);
		} catch (PagedMediaWriterException e) {
			throw new TransformerRunException("Could not open media writer.", e);
		}
	}

}
 