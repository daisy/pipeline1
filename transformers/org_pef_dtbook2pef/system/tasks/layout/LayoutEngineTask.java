package org_pef_dtbook2pef.system.tasks.layout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.daisy.pipeline.exception.TransformerRunException;
import org.xml.sax.SAXException;
import org_pef_dtbook2pef.setups.sv_SE.definers.BodyLayoutMaster;
import org_pef_dtbook2pef.setups.sv_SE.definers.FrontLayoutMaster;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.tasks.layout.flow.Flow;
import org_pef_dtbook2pef.system.tasks.layout.flow.FlowHandler;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformerException;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMasterConfigurator;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.text.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.writers.PEFMediaWriter;

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

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		run1();
	}
	
	private static void run1() throws SAXException, IOException, ParserConfigurationException {
		HashMap<String, String> p = new HashMap<String, String>();
		Properties prop = new Properties();
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		prop.putAll(p);
		int width = 28;
		int height = 29;
		PEFMediaWriter paged = new PEFMediaWriter(prop);
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer.Builder(paged).
			addLayoutMaster("main", new BodyLayoutMaster(new LayoutMasterConfigurator(width, height).innerMargin(5).outerMargin(2).rowSpacing(1))).
			addLayoutMaster("front", new FrontLayoutMaster(new LayoutMasterConfigurator(width, height).innerMargin(5).outerMargin(2).rowSpacing(1))).
			setStringFilterFactory(factory).build();
		LayoutEngineTask ft = new LayoutEngineTask("FLOW to PEF converter", flow, flow);
		try {
			ft.execute(new File("C:\\temp\\flow.xml"), new File("C:\\temp\\res.txt"));
		} catch (TransformerRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	private static void run2() throws FileNotFoundException, UnsupportedEncodingException {
		
		HashMap<String, LayoutMaster> masters = new HashMap<String, LayoutMaster>();
		BodyLayoutMaster wi = new BodyLayoutMaster(30, 29, new ArrayList<StringFilter>());
		masters.put("main", wi);
		LayoutEngineTask ft = new LayoutEngineTask("Internal Layout Engine", masters);
		PrintStream ps = new PrintStream(new File("C:\\temp\\res.txt"), "UTF-8");
		DefaultLayoutPerformer fs = ft.generateTitlePage(30, 29, new ArrayList<StringFilter>(), "förf", "titel", "volume 1 av 23");
		PageStruct pst = new PageStruct(masters);
		//ft.writePEF(ps, fs.layout(pst), new HashMap<String, String>());
	}*/
}
 