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

import org_pef_dtbook2pef.setups.common.InputDetectorTaskSystem;
import org_pef_dtbook2pef.setups.sv_SE.tasks.VolumeCoverPageTask;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.TaskSystemException;
import org_pef_dtbook2pef.system.tasks.ValidatorTask;
import org_pef_dtbook2pef.system.tasks.XsltTask;
import org_pef_dtbook2pef.system.tasks.layout.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.PageStruct;
import org_pef_dtbook2pef.system.tasks.layout.text.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.text.FilterLocale;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.layout.utils.TextBorder;
import org_pef_dtbook2pef.system.tasks.layout.writers.PEFMediaWriter;

/**
 * <p>Transforms a DTBook 2005-3 into Swedish braille in PEF 2008-1 format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * 
 * <p>This TaskSystem consists of the following steps:</p>
 * <ol>
	 * <li>Conformance checker.
	 * 		Checks that the input meets some basic requirements.</li>
	 * <li>DTBook to FLOW converter.
	 * 		Inserts braille characters preceding
	 * 		numbers and capital letters, inserts braille markers where inline structural
	 * 		markup such as <tt>em</tt> and <tt>strong</tt> occur and converts the DTBook
	 * 		structure into a flow definition, similar to XSL-FO.</li>
	 * <li>FLOW whitespace normalizer</li>
	 * <li>FLOW to PEF converter.
	 * 		Translates all characters into braille, and puts the text flow onto pages.</li>
	 * <li>Volume splitter.
	 * 		The output from the preceding step is a single volume that is split into volumes.</li>
	 * <li>Cover page adder</li>
	 * <li>Braille finalizer.
	 * 		Replaces any remaining non-braille characters, e.g. spaces and hyphens, with
	 * 		braille characters.</li>
	 * <li>Meta data finalizer</li>

 * </ol>
 * <p>The result should be validated against the PEF Relax NG schema using int_daisy_validator.</p>
 * @author joha
 * TODO: hyphenation "sjuttonåringar", "blårött" och "jättestor".
 * TODO: clean
 * TODO: remove "sv_SE/preprocessing"
 */
public class SwedishBrailleSystem implements TaskSystem {
	private URL resourceBase;
	private String config;
	private InputDetectorTaskSystem inputDetector;
	
	public SwedishBrailleSystem(URL resourceBase, String config) {
		this.resourceBase = resourceBase;
		this.config = config;
		this.inputDetector = new InputDetectorTaskSystem(resourceBase, "sv_SE/config/", "common/config/");
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters) throws TaskSystemException {
		//URL markersXsl;
		//URL dtbook2flow;
		URL flowWsNormalizer;
		URL volumeSplitter;
		URL brailleFinalizer;
		URL metaFinalizer;
		URL configURL;
		URL flowValidationURL;
		//URL inputSchURL;
		
		try {
			//inputSchURL = new URL(resourceBase, "sv_SE/validation/basic.sch");
			//markersXsl = new URL(resourceBase, "sv_SE/preprocessing/add-structure-markers.xsl");
			//dtbook2flow = new URL(resourceBase, "sv_SE/definers/dtbook2flow_sv_SE_braille.xsl");
			flowValidationURL = new URL(resourceBase, "common/validation/flow.xsd");
			flowWsNormalizer = new URL(resourceBase, "common/preprocessing/flow-whitespace-normalizer.xsl");
			volumeSplitter = new URL(resourceBase, "common/splitters/simple-splitter.xsl");
			brailleFinalizer = new URL(resourceBase, "common/renderers/braille-finalizer.xsl");
			metaFinalizer = new URL(resourceBase, "common/renderers/meta-finalizer.xsl");
			configURL = new URL(resourceBase, config);
		} catch (MalformedURLException e) {
			throw new TaskSystemException(e);
		}

		Properties p = new Properties();
		p.put("l10nrearjacketcopy", "Baksidestext");
		p.put("l10imagedescription", "Bildbeskrivning");
		p.put("l10ncolophon", "Tryckuppgifter");
		p.put("l10ncaption", "Bildtext");

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
		boolean duplex = "true".equals(p.getProperty("duplex", "true"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();

		p.put("page-height", pageHeight);
		p.put("page-width", flowWidth+innerMargin+outerMargin);
		p.put("row-spacing", (rowgap/4)+1);

		HashMap h = new HashMap();
		h.putAll(p);

		setup.addAll(inputDetector.compile(h));

		// Check input conformance 
		//setup.add(new ValidatorTask("Conformance checker", inputSchURL));

		// Add braille markers
		//setup.add(new XsltTask("Markers injector", markersXsl, null, h));
		
		// Add braille markers and redefines dtbook as FLOW input
		//setup.add(new XsltTask("DTBook to FLOW converter", dtbook2flow, null, h));

		// Whitespace normalizer TransformerFactoryConstants.SAXON8
		setup.add(new XsltTask("FLOW whitespace normalizer", flowWsNormalizer, null, h));

		// Check that the result from the previous step is OK
		setup.add(new ValidatorTask("FLOW validator", flowValidationURL));
		
		//setup.add(new DebugTask("Debug", new File("D:\\debug.xml")));

		// Layout FLOW as PEF
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		PEFMediaWriter paged = new PEFMediaWriter(p);
		factory.setDefault(sv_SE);
		PageStruct paginator = new PageStruct(factory.getDefault());
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer.Builder().
										setStringFilterFactory(factory).
										build();
		setup.add(new LayoutEngineTask("FLOW to PEF converter", flow, paginator, paged));

		// Split result into volumes
		setup.add(new XsltTask("Volume splitter", volumeSplitter, null, h));

		// Add a title page first in each volume
    	TextBorder tb = new TextBorder.Builder(flowWidth+innerMargin).
    						topLeftCorner(LayoutTools.fill(' ', innerMargin) + "\u280F").
    						topBorder("\u2809").
    						topRightCorner("\u2839").
    						leftBorder(LayoutTools.fill(' ', innerMargin) + "\u2807  ").
    						rightBorder("  \u2838").
    						bottomLeftCorner(LayoutTools.fill(' ', innerMargin) + "\u2827").
    						bottomBorder("\u2824").
    						bottomRightCorner("\u283c").
    						alignment(TextBorder.Align.CENTER).
    						build();
		setup.add(new VolumeCoverPageTask("Cover page adder", factory.getDefault(), tb, new File(parameters.get("input")), pageHeight));

		// Finalizes character data on rows
		HashMap<String, Object> finalizerOptions = new HashMap<String, Object>();
		finalizerOptions.put("finalizer-input", " \u00a0-\u00ad");
		finalizerOptions.put("finalizer-output", "\u2800\u2800\u2824\u2824");
		setup.add(new XsltTask("Braille finalizer", brailleFinalizer, null, finalizerOptions));

		// Finalize meta data from input file
		setup.add(new XsltTask("Meta data finalizer", metaFinalizer, null, h));

		return setup;
	}

}