package org_pef_dtbook2pef.setups.sv_SE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;
import org_pef_dtbook2pef.system.tasks.DebugTask;
import org_pef_dtbook2pef.system.tasks.ValidatorTask;
import org_pef_dtbook2pef.system.tasks.XsltTask;
import org_pef_dtbook2pef.system.tasks.layout.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMasterConfigurator;
import org_pef_dtbook2pef.system.tasks.layout.text.FilterLocale;
import org_pef_dtbook2pef.system.tasks.layout.text.RegexFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.writers.TextMediaWriter;

/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author joha
 *
 */
public class SwedishTextSystem implements TaskSystem {
	private URL resourceBase;
	private String config;
	
	public SwedishTextSystem(URL resourceBase, String config) {
		this.resourceBase = resourceBase;
		this.config = config;
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters) throws TaskSystemException {
		URL dtbook2flow;
		URL flowWsNormalizer;
		URL configURL;
		URL flowValidationURL;
		URL inputSchURL;
		try {
			inputSchURL = new URL(resourceBase, "sv_SE/validation/basic.sch");
			dtbook2flow = new URL(resourceBase, "sv_SE/definers/dtbook2flow_sv_SE.xsl");
			flowValidationURL = new URL(resourceBase, "sv_SE/validation/flow.xsd");
			flowWsNormalizer = new URL(resourceBase, "sv_SE/preprocessing/remove-whitespace.xsl");
			configURL = new URL(resourceBase, config);
		} catch (MalformedURLException e) {
			throw new TaskSystemException(e);
		}

		Properties p = new Properties();
		try {
			p.loadFromXML(configURL.openStream());
		} catch (FileNotFoundException e) {
			throw new TaskSystemException("Configuration file not found: " + config, e);
		} catch (InvalidPropertiesFormatException e) {
			throw new TaskSystemException("Configuration file could not be parsed: " + config, e);
		} catch (IOException e) {
			throw new TaskSystemException("IOException while reading configuration file: " + config, e);
		}
		// GUI parameters should take precedence
		p.putAll(parameters);

		HashMap h = new HashMap();
		h.putAll(p);

		int flowWidth = Integer.parseInt(p.getProperty("cols", "28"));
		int pageHeight = Integer.parseInt(p.getProperty("rows", "29"));
		int innerMargin = Integer.parseInt(p.getProperty("inner-margin", "5"));
		int outerMargin = Integer.parseInt(p.getProperty("outer-margin", "2"));
		float rowgap = Float.parseFloat(p.getProperty("rowgap", "0"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		// Check input conformance 
		setup.add(new ValidatorTask("Conformance checker", inputSchURL));

		// Redefines dtbook as FLOW input
		setup.add(new XsltTask("DTBook to FLOW converter", dtbook2flow, null, h));

		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("FLOW whitespace normalizer", flowWsNormalizer, null, h));

		File debug = new File("D:\\debug.xml");
		setup.add(new DebugTask("Debug output to " + debug, debug));

		// Check that the result from the previous step is OK
		setup.add(new ValidatorTask("FLOW validator", flowValidationURL));

		// Layout FLOW as text
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		factory.setDefault(new RegexFilter("\\u200B", ""));
		TextMediaWriter paged = new TextMediaWriter(p, "UTF-8");
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer.Builder(paged).
										addLayoutMaster("main",
												new BaseLayoutMaster(
													new LayoutMasterConfigurator(flowWidth+innerMargin+outerMargin, pageHeight).
														innerMargin(innerMargin).
														outerMargin(outerMargin).
														rowSpacing((rowgap/4)+1)
													)
												).
										addLayoutMaster("front", 
												new BaseLayoutMaster(
													new LayoutMasterConfigurator(flowWidth+innerMargin+outerMargin, pageHeight).
														innerMargin(innerMargin).
														outerMargin(outerMargin).
														rowSpacing((rowgap/4)+1)
													)
												).
										addLayoutMaster("plain",
												new BaseLayoutMaster(
													new LayoutMasterConfigurator(flowWidth+innerMargin+outerMargin, pageHeight).
														innerMargin(innerMargin).
														outerMargin(outerMargin).
														rowSpacing((rowgap/4)+1)
													)
												).
										setStringFilterFactory(factory).
										build();
		setup.add(new LayoutEngineTask("FLOW to Text converter", flow, flow));

		return setup;
	}

}