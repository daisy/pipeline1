package org_pef_dtbook2pef.setups.sv_SE;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org_pef_dtbook2pef.DTBook2PEF;
import org_pef_dtbook2pef.setups.sv_SE.definers.BodyLayoutMaster;
import org_pef_dtbook2pef.setups.sv_SE.definers.FrontLayoutMaster;
import org_pef_dtbook2pef.setups.sv_SE.tasks.VolumeCoverPageTask;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.tasks.XsltTask;
import org_pef_dtbook2pef.system.tasks.layout.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultPEFOutput;
import org_pef_dtbook2pef.system.tasks.layout.text.Locale2;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.layout.utils.TextBorder;

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
 *
 */
public class SwedishBrailleSystem implements TaskSystem {
	private DTBook2PEF t;
	
	public SwedishBrailleSystem(DTBook2PEF t) {
		this.t = t;
	}

	public ArrayList<InternalTask> compile(Map<String, String> parameters) {
		Properties p = new Properties();
		p.putAll(parameters);
		int width = Integer.parseInt(p.getProperty("cols", "28"));
		int height = Integer.parseInt(p.getProperty("rows", "29"));
		int innerMargin = Integer.parseInt(p.getProperty("inner-margin", "0"));
		int outerMargin = Integer.parseInt(p.getProperty("outer-margin", "0"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		
		//TODO: add input conformance test
		// Check input conformance 
		//setup.add(new ValidatorTask("Conformance checker",
		//		t.getResource("setups/sv_SE/validation/basic.sch")));
		/*
		// Add braille markers based on text contents
		StringFilterHandler filters = new StringFilterHandler();
		// Remove redundant whitespace
		filters.add(new RegexFilter("(\\s+)", " "));
		// Remove zero width space
		filters.add(new RegexFilter("\\u200B", ""));
		// One or more digit followed by zero or more digits, commas or periods
		filters.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
		// Add upper case marker to the beginning of any upper case sequence
		filters.add(new RegexFilter("(\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
		// Add another upper case marker if the upper case sequence contains more than one character
		filters.add(new RegexFilter("(\\u2820\\p{Lu}\\u00ad*\\p{Lu}[\\p{Lu}\\u00ad]*)", "\u2820$1"));
		// Change case to lower case
		filters.add(new CaseFilter(CaseFilter.Mode.LOWER_CASE));
		// Transcode characters
		
		filters.add(new CharFilter(
				t.getResource("setups/sv_SE/text/default-table.xml")));
*/

		// Add to setup
		//setup.add(new TextNodeTask("Text to Braille processor (Swedish)", filters));
		
		// Add braille markers
		/*setup.add(new XsltTask("Braille structure markers injector",
				t.getResource("setups/sv_SE/preprocessing/add-structure-markers.xsl"), null));*/
		
		// Add braille markers and redefines dtbook as FLOW input
		setup.add(new XsltTask("DTBook to FLOW converter",
				t.getResource("setups/sv_SE/definers/dtbook2flow_sv_SE.xsl"), null));
		
		// Whitespace normalizer
		setup.add(new XsltTask("FLOW whitespace normalizer", 
				t.getResource("setups/sv_SE/preprocessing/remove-whitespace.xsl"), null));

		// Layout FLOW as PEF
		Locale2 sv_SE = Locale2.parse("sv-SE");
		StringFilterFactory factory = StringFilterFactory.newInstance();
		factory.setDefault(sv_SE);
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer.Builder().
										addLayoutMaster("main", new BodyLayoutMaster(width, height, innerMargin, outerMargin)).
										addLayoutMaster("front", new FrontLayoutMaster(width, height, innerMargin, outerMargin)).
										setStringFilterFactory(factory).
										build();
		DefaultPEFOutput paged = new DefaultPEFOutput(p);
		setup.add(new LayoutEngineTask("FLOW to PEF converter", flow, flow, paged));
		
		// Split result into volumes 
		setup.add(new XsltTask("Volume splitter", t.getResource("setups/common/splitters/simple-splitter.xsl"), null));

		// Add a title page first in each volume
    	TextBorder tb = new TextBorder.Builder(width+innerMargin).
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
		setup.add(new VolumeCoverPageTask("Cover page adder", factory.newStringFilter(), tb));

		// Finalizes character data on rows
		setup.add(new XsltTask("Braille finalizer", 
				t.getResource("setups/common/renderers/braille-finalizer.xsl"), null));

		// Finalize meta data from input file
		setup.add(new XsltTask("Meta data finalizer", t.getResource("setups/common/renderers/meta-finalizer.xsl"), null));

		return setup;
	}

}