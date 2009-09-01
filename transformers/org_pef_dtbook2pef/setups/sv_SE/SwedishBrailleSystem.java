package org_pef_dtbook2pef.setups.sv_SE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org_pef_dtbook2pef.DTBook2PEF;
import org_pef_dtbook2pef.setups.sv_SE.definers.BodyLayoutMaster;
import org_pef_dtbook2pef.setups.sv_SE.definers.FrontLayoutMaster;
import org_pef_dtbook2pef.setups.sv_SE.tasks.VolumeCoverPageTask;
import org_pef_dtbook2pef.system.InternalTask;
import org_pef_dtbook2pef.system.TaskSystem;
import org_pef_dtbook2pef.system.tasks.ValidatorTask;
import org_pef_dtbook2pef.system.tasks.XsltTask;
import org_pef_dtbook2pef.system.tasks.layout.LayoutEngineTask;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultLayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.impl.DefaultPEFOutput;
import org_pef_dtbook2pef.system.tasks.layout.page.BaseLayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.textnode.TextNodeTask;
import org_pef_dtbook2pef.system.tasks.textnode.filters.CaseFilter;
import org_pef_dtbook2pef.system.tasks.textnode.filters.CharFilter;
import org_pef_dtbook2pef.system.tasks.textnode.filters.RegexFilter;
import org_pef_dtbook2pef.system.tasks.textnode.filters.StringFilterHandler;

/**
 * <p>Transforms a DTBook 2005-3 into Swedish braille in PEF 2008-1 format.
 * The input DTBook should be hyphenated (using SOFT HYPHEN U+00AD) at all
 * breakpoints prior to conversion.</p>
 * 
 * <p>This TaskSystem consists of the following steps:</p>
 * <ol>
	 * <li>Conformance checker.
	 * 		Checks that the input meets some basic requirements.</li>
	 * <li>Text to Braille processor (Swedish).
	 * 		Inserts braille characters preceding numbers and capital
	 * 		letters as well as translating all characters into braille.</li>
	 * <li>Braille structure markers injector.
	 * 		Inserts braille markers where inline structural
	 * 		markup such as <tt>em</tt> and <tt>strong</tt> occur.</li>
	 * <li>DTBook to FLOW converter.
	 * 		Converts the DTBook structure into a flow definition, similar to XSL-FO.</li>
	 * <li>FLOW to PEF converter.
	 * 		Puts the text flow onto pages.</li>
	 * <li>Braille finalizer.
	 * 		Replaces any remaining non-braille characters, e.g. spaces and hyphens, with
	 * 		braille characters.</li>
	 * <li>Volume splitter.
	 * 		The output from the preceding step is a single volume that is split into volumes.</li>
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

	@Override
	public ArrayList<InternalTask> compile(Map<String, String> parameters) {
		Properties p = new Properties();
		p.putAll(parameters);
		int width = Integer.parseInt(p.getProperty("cols", "30"));
		int height = Integer.parseInt(p.getProperty("rows", "29"));

		ArrayList<InternalTask> setup = new ArrayList<InternalTask>();
		
		// Check input conformance 
		setup.add(new ValidatorTask("Conformance checker",
				t.getResource("setups/sv_SE/validation/basic.sch")));
		
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

		// Add to setup
		setup.add(new TextNodeTask("Text to Braille processor (Swedish)", filters));
		
		// Add braille markers
		setup.add(new XsltTask("Braille structure markers injector",
				t.getResource("setups/sv_SE/preprocessing/add-structure-markers.xsl"), null));
		
		// Redefines dtbook as FLOW input
		setup.add(new XsltTask("DTBook to FLOW converter",
				t.getResource("setups/sv_SE/definers/dtbook2flow.xsl"), null));

		// Layout FLOW as PEF
		HashMap<String, LayoutMaster> masters = new HashMap<String, LayoutMaster>();
		BodyLayoutMaster m1 = new BodyLayoutMaster(width, height, filters);
		BaseLayoutMaster m2 = new BaseLayoutMaster(width, height, 0, 0, filters);
		FrontLayoutMaster m3 = new FrontLayoutMaster(width, height, filters);
		masters.put("main", m1);
		masters.put("title", m2);
		masters.put("front", m3);
		DefaultLayoutPerformer flow = new DefaultLayoutPerformer(masters);
		DefaultPEFOutput paged = new DefaultPEFOutput(p);
		setup.add(new LayoutEngineTask("FLOW to PEF converter", flow, flow, paged));
		
		// Split result into volumes 
		setup.add(new XsltTask("Volume splitter", t.getResource("setups/common/splitters/simple-splitter.xsl"), null));

		// Add a title page first in each volume
		setup.add(new VolumeCoverPageTask("Cover page adder", filters));

		// Finalizes character data on rows
		setup.add(new XsltTask("Braille finalizer", 
				t.getResource("setups/common/renderers/braille-finalizer.xsl"), null));

		return setup;
	}

}