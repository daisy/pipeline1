package org_pef_dtbook2pef.setups.en_US;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org_pef_dtbook2pef.setups.common.InputDetectorTaskSystem;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;
import org_pef_dtbook2pef.system.tasks.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.ValidatorTask;
import org_pef_dtbook2pef.system.tasks.XsltTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.PageStruct;
import org_pef_dtbook2pef.system.tasks.layout.text.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.text.RegexFilter;
import org_pef_dtbook2pef.system.tasks.layout.writers.TextMediaWriter;

/**
 * <p>Transforms a DTBook 2005-3 into text format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * @author joha
 *
 */
public class DefaultTextSystem implements TaskSystem {
	private URL resourceBase;
	private String config;
	private InputDetectorTaskSystem inputDetector;
	private final String name;
	
	public DefaultTextSystem(URL resourceBase, String config, String name) {
		this.resourceBase = resourceBase;
		this.config = config;
		this.inputDetector = new InputDetectorTaskSystem(resourceBase, "en_US/config/", "common/config/");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters) throws TaskSystemException {
		URL flowWsNormalizer;
		URL configURL;
		URL flowValidationURL;
		try {
			flowValidationURL = new URL(resourceBase, "common/validation/flow.xsd");
			flowWsNormalizer = new URL(resourceBase, "common/preprocessing/flow-whitespace-normalizer.xsl");
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

		int flowWidth = Integer.parseInt(p.getProperty("cols", "28"));
		int pageHeight = Integer.parseInt(p.getProperty("rows", "29"));
		int innerMargin = Integer.parseInt(p.getProperty("inner-margin", "5"));
		int outerMargin = Integer.parseInt(p.getProperty("outer-margin", "2"));
		float rowgap = Float.parseFloat(p.getProperty("rowgap", "0"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		p.put("page-height", pageHeight);
		p.put("page-width", flowWidth+innerMargin+outerMargin);
		p.put("row-spacing", (rowgap/4)+1);

		HashMap h = new HashMap();
		h.putAll(p);
		
		setup.addAll(inputDetector.compile(h));

		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("FLOW whitespace normalizer", flowWsNormalizer, null, h));

		// Check that the result from the previous step is OK
		setup.add(new ValidatorTask("FLOW validator", flowValidationURL));

		// Layout FLOW as text
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		factory.setDefault(new RegexFilter("\\u200B", ""));
		TextMediaWriter paged = new TextMediaWriter(p, "UTF-8");
		PageStruct paginator = new PageStruct(factory.getDefault());
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer(factory);
		setup.add(new LayoutEngineTask("FLOW to Text converter", flow, paginator, paged));

		return setup;
	}

}