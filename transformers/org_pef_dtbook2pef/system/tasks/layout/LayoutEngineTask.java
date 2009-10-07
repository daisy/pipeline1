package org_pef_dtbook2pef.system.tasks.layout;

import java.io.File;
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
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultPEFOutput;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaOutput;
import org_pef_dtbook2pef.system.tasks.layout.text.CombinationFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilterFactory;

/**
 * breaks row flow into pages
 * @author joha
 *
 */
public class LayoutEngineTask extends InternalTask  {
	private final Flow flow;
	private PagedMediaOutput paged;
	private LayoutPerformer lp;
	
	public LayoutEngineTask(String name, Flow flow, LayoutPerformer lp, PagedMediaOutput paged) {
		super(name);
		this.flow = flow;
		this.lp = lp;
		this.paged = paged;
	}

	@Override
	public void execute(File input, File output, HashMap<String, String> options)
			throws TransformerRunException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, new FlowHandler(flow));
			paged.open(output);
			lp.layout(paged);
			paged.close();
		} catch (SAXException e) {
			throw new TransformerRunException("SAXException while runing task.", e);
		} catch (IOException e) {
			throw new TransformerRunException("IOException while runing task. ", e);
		} catch (ParserConfigurationException e) {
			throw new TransformerRunException("ParserConfigurationException while runing task. ", e);
		}
	}
	/*
	public DefaultLayoutPerformer generateTitlePage(int pageWidth, int pageHeight, ArrayList<StringFilter> sf, String author, String title, String volume) {
		CoverLayoutMaster clm = new CoverLayoutMaster(pageWidth, pageHeight, sf);
		clm.setFooter(volume);
		masters.put("title-master", clm);
		DefaultLayoutPerformer fi = new DefaultLayoutPerformer(masters);
		SequenceProperties.Builder b1 = new SequenceProperties.Builder("title-master");
		fi.newSequence(b1.build());
		BlockProperties.Builder b2 = new BlockProperties.Builder();
		fi.startBlock(b2.build());
		fi.addChars(author);
		fi.endBlock();
		fi.startBlock(b2.build());
		fi.addChars(title);
		fi.endBlock();
		return fi;
	}
*/
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		run1();
	}
	
	private static void run1() throws SAXException, IOException, ParserConfigurationException {
		HashMap<String, LayoutMaster> masters = new HashMap<String, LayoutMaster>();
		HashMap<String, String> p = new HashMap<String, String>();
		Properties prop = new Properties();
		StringFilterFactory factory = StringFilterFactory.newInstance();
		prop.putAll(p);
		int width = 30;
		int height = 29;
		BodyLayoutMaster wi = new BodyLayoutMaster(width, height, 5, 2);
		masters.put("main", wi);
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer.Builder().
		addLayoutMaster("main", new BodyLayoutMaster(width, height, 5, 2)).
		addLayoutMaster("front", new FrontLayoutMaster(width, height, 5, 2)).
		setStringFilterFactory(factory).build();
		DefaultPEFOutput paged = new DefaultPEFOutput(prop);
		LayoutEngineTask ft = new LayoutEngineTask("FLOW to PEF converter", flow, flow, paged);
		try {
			ft.execute(new File("C:\\temp\\flow.xml"), new File("C:\\temp\\res.txt"), p);
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
 