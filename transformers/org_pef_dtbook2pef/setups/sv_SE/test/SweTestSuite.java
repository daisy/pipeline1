package org_pef_dtbook2pef.setups.sv_SE.test;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.setups.sv_SE.test.FlowTest.Mode;
import org_pef_dtbook2pef.system.tasks.layout.text.CaseFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.CombinationFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.FilterLocale;
import org_pef_dtbook2pef.system.tasks.layout.text.RegexFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.brailleFilters.BrailleFilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.text.brailleFilters.sv_SE.CapitalizationMarkers;
import org_pef_dtbook2pef.test.junit.JUnitTestRunner;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestRunner;
import org_pef_dtbook2pef.test.unit.impl.EvaluateTest;
import org_pef_dtbook2pef.test.unit.impl.FilterTest;
import org_pef_dtbook2pef.test.unit.impl.PositionTest;

public class SweTestSuite extends JUnitTestRunner {

	public static void main(String[] args) throws Exception {
		SweTestSuite sts = new SweTestSuite();
		sts.run();
	}
	
	public void run() {
		TestRunner tss = new TestRunner(System.out, false);

		tss.runTestBatch("Position", PositionTest.getTestCollection());
		tss.runTestBatch("Locale", getLocaleTest());
		tss.runTestBatch("Translation", getTranslationTests());
		tss.runTestBatch("Capitalization", getCapsTest());
		tss.runTestBatch("BreakPoint", getBreakPointTest());
		tss.runTestBatch("Flow", getFlowTests());
		//runNumberTest(out);
		tss.runTestBatch("Evaluate", EvaluateTest.getTestCollection());
		tss.runTest(new StateTest());
		tss.printSummary();
	}
	
	@org.junit.Test
	public void runTranslationTests() {
		run(getTranslationTests());
	}
	
	@org.junit.Test
	public void runCapsTests() {
		run(getCapsTest());
	}

	@org.junit.Test
	public void runLocaleTests() {
		run(getLocaleTest());
	}
	
	@org.junit.Test
	public void runBreakPointTests() {
		run(getBreakPointTest());
	}
	
	@org.junit.Test
	public void runFlowTests() {
		run(getFlowTests());
	}
	
	public Collection<Test> getFlowTests() {
		ArrayList<Test> a = new ArrayList<Test>();
		String dir = "resource-files/";
		a.add(new FlowTest(
				this.getClass().getResource(dir + "alice-input.xml"), 
				this.getClass().getResource(dir + "alice-expected.txt"), Mode.TEXT));
		a.add(new FlowTest(
				this.getClass().getResource(dir + "alice-input.xml"), 
				this.getClass().getResource(dir + "alice-expected.xml"), Mode.PEF));
		return a;
	}

	public static Collection<Test> getTranslationTests() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);
		ArrayList<Test> a = new ArrayList<Test>();
		// 1.2
		a.add(new FilterTest(filter, "2009", "⠼⠃⠚⠚⠊"));
		// 2.1
		a.add(new FilterTest(filter, "Hon köpte smör, te och ost.", "⠠⠓⠕⠝ ⠅⠪⠏⠞⠑ ⠎⠍⠪⠗⠂ ⠞⠑ ⠕⠉⠓ ⠕⠎⠞⠄"));
		a.add(new FilterTest(filter, "Kommer du?", "⠠⠅⠕⠍⠍⠑⠗ ⠙⠥⠢"));
		a.add(new FilterTest(filter, "Hör upp!", "⠠⠓⠪⠗ ⠥⠏⠏⠖"));
		a.add(new FilterTest(filter, "Hon sa: DN:s redaktion är stor.", "⠠⠓⠕⠝ ⠎⠁⠒ ⠠⠠⠙⠝⠒⠎ ⠗⠑⠙⠁⠅⠞⠊⠕⠝ ⠜⠗ ⠎⠞⠕⠗⠄"));
		a.add(new FilterTest(filter, "Skillnaden mellan arbets- och vilodagar blev mindre skarp; hon kunde tillåta sig vilodagar mitt i veckan.", "⠠⠎⠅⠊⠇⠇⠝⠁⠙⠑⠝ ⠍⠑⠇⠇⠁⠝ ⠁⠗⠃⠑⠞⠎- ⠕⠉⠓ ⠧⠊⠇⠕⠙⠁⠛⠁⠗ ⠃⠇⠑⠧ ⠍⠊⠝⠙⠗⠑ ⠎⠅⠁⠗⠏⠆ ⠓⠕⠝ ⠅⠥⠝⠙⠑ ⠞⠊⠇⠇⠡⠞⠁ ⠎⠊⠛ ⠧⠊⠇⠕⠙⠁⠛⠁⠗ ⠍⠊⠞⠞ ⠊ ⠧⠑⠉⠅⠁⠝⠄"));
		a.add(new FilterTest(filter, "M/S Kronan", "⠠⠍⠌⠠⠎ ⠠⠅⠗⠕⠝⠁⠝"));
		a.add(new FilterTest(filter, "0,55 liter/mil", "⠼⠚⠂⠑⠑ ⠇⠊⠞⠑⠗⠌⠍⠊⠇"));
		a.add(new FilterTest(filter, "månadsskiftet april/maj", "⠍⠡⠝⠁⠙⠎⠎⠅⠊⠋⠞⠑⠞ ⠁⠏⠗⠊⠇⠌⠍⠁⠚"));
		a.add(new FilterTest(filter, "\"Vill du leka?\"", "⠰⠠⠧⠊⠇⠇ ⠙⠥ ⠇⠑⠅⠁⠢⠰"));
		a.add(new FilterTest(filter, "Det var Iris' blommor.", "⠠⠙⠑⠞ ⠧⠁⠗ ⠠⠊⠗⠊⠎⠐ ⠃⠇⠕⠍⠍⠕⠗⠄"));
		a.add(new FilterTest(filter, "\"Vad betyder 'abstrus'?\" frågade han.", "⠰⠠⠧⠁⠙ ⠃⠑⠞⠽⠙⠑⠗ ⠐⠁⠃⠎⠞⠗⠥⠎⠐⠢⠰ ⠋⠗⠡⠛⠁⠙⠑ ⠓⠁⠝⠄"));
		// 2.2
		a.add(new FilterTest(filter, "Anne-Marie har gul- och vitrandig kjol.", "⠠⠁⠝⠝⠑-⠠⠍⠁⠗⠊⠑ ⠓⠁⠗ ⠛⠥⠇- ⠕⠉⠓ ⠧⠊⠞⠗⠁⠝⠙⠊⠛ ⠅⠚⠕⠇⠄"));
		a.add(new FilterTest(filter, "Ett febrilt sysslande med \u2013 ingenting alls.", "⠠⠑⠞⠞ ⠋⠑⠃⠗⠊⠇⠞ ⠎⠽⠎⠎⠇⠁⠝⠙⠑ ⠍⠑⠙ ⠤⠤ ⠊⠝⠛⠑⠝⠞⠊⠝⠛ ⠁⠇⠇⠎⠄"));
		a.add(new FilterTest(filter, "\u2013 Vad heter hunden?", "⠤⠤ ⠠⠧⠁⠙ ⠓⠑⠞⠑⠗ ⠓⠥⠝⠙⠑⠝⠢"));
		a.add(new FilterTest(filter, "Han tog tåget Stockholm\u2013Göteborg.", "⠠⠓⠁⠝ ⠞⠕⠛ ⠞⠡⠛⠑⠞ ⠠⠎⠞⠕⠉⠅⠓⠕⠇⠍⠤⠤⠠⠛⠪⠞⠑⠃⠕⠗⠛⠄"));
		// 2.3.1
		a.add(new FilterTest(filter, "Synskadades Riksförbund (SRF)", "⠠⠎⠽⠝⠎⠅⠁⠙⠁⠙⠑⠎ ⠠⠗⠊⠅⠎⠋⠪⠗⠃⠥⠝⠙ ⠦⠠⠠⠎⠗⠋⠴"));
		a.add(new FilterTest(filter, "Rapporter a) från förbundsmötet b) kassaärenden", "⠠⠗⠁⠏⠏⠕⠗⠞⠑⠗ ⠁⠴ ⠋⠗⠡⠝ ⠋⠪⠗⠃⠥⠝⠙⠎⠍⠪⠞⠑⠞ ⠃⠴ ⠅⠁⠎⠎⠁⠜⠗⠑⠝⠙⠑⠝"));
		// 2.3.2
		a.add(new FilterTest(filter, "Kravet har ställts från olika grupper (bl.a. [högskole]studerande och deltidsarbetande) men det har alltid avvisats.", "⠠⠅⠗⠁⠧⠑⠞ ⠓⠁⠗ ⠎⠞⠜⠇⠇⠞⠎ ⠋⠗⠡⠝ ⠕⠇⠊⠅⠁ ⠛⠗⠥⠏⠏⠑⠗ ⠦⠃⠇⠄⠁⠄ ⠷⠓⠪⠛⠎⠅⠕⠇⠑⠾⠎⠞⠥⠙⠑⠗⠁⠝⠙⠑ ⠕⠉⠓ ⠙⠑⠇⠞⠊⠙⠎⠁⠗⠃⠑⠞⠁⠝⠙⠑⠴ ⠍⠑⠝ ⠙⠑⠞ ⠓⠁⠗ ⠁⠇⠇⠞⠊⠙ ⠁⠧⠧⠊⠎⠁⠞⠎⠄"));
		a.add(new FilterTest(filter, "Red Port [räd pårt]", "⠠⠗⠑⠙ ⠠⠏⠕⠗⠞ ⠷⠗⠜⠙ ⠏⠡⠗⠞⠾"));
		// COULDDO 2.3.4 
		// 2.3.5
		a.add(new FilterTest(filter, "{1, 3, 5} utläses mängden av talen ett, tre och fem.", "⠠⠷⠼⠁⠂ ⠼⠉⠂ ⠼⠑⠠⠾ ⠥⠞⠇⠜⠎⠑⠎ ⠍⠜⠝⠛⠙⠑⠝ ⠁⠧ ⠞⠁⠇⠑⠝ ⠑⠞⠞⠂ ⠞⠗⠑ ⠕⠉⠓ ⠋⠑⠍⠄"));
		// 2.4.1 (ex 2) COULDDO ex 1, 3
		a.add(new FilterTest(filter, "se § 7\u20139", "⠎⠑ ⠬ ⠼⠛⠤⠤⠼⠊"));
		// 2.4.2
		a.add(new FilterTest(filter, "Almqvist & Wiksell","⠠⠁⠇⠍⠟⠧⠊⠎⠞ ⠯ ⠠⠺⠊⠅⠎⠑⠇⠇"));
		// 2.4.3 COULDDO ex 2, 3
		a.add(new FilterTest(filter, "Lars Gustafsson *1936", "⠠⠇⠁⠗⠎ ⠠⠛⠥⠎⠞⠁⠋⠎⠎⠕⠝ ⠔⠼⠁⠊⠉⠋"));
		// 2.4.4
		a.add(new FilterTest(filter, "tryck #21#","⠞⠗⠽⠉⠅ ⠘⠼⠼⠃⠁⠘⠼"));
		// 2.4.5, 2.4.7
		a.add(new FilterTest(filter, "emil_emilsson@hotmail.com","⠑⠍⠊⠇⠘⠤⠑⠍⠊⠇⠎⠎⠕⠝⠘⠷⠓⠕⠞⠍⠁⠊⠇⠄⠉⠕⠍"));
		// 2.4.6
		a.add(new FilterTest(filter, "C:\\WINDOWS\\system\\loginw31.dll","⠠⠉⠒⠘⠌⠠⠠⠺⠊⠝⠙⠕⠺⠎⠘⠌⠎⠽⠎⠞⠑⠍⠘⠌⠇⠕⠛⠊⠝⠺⠼⠉⠁⠄⠙⠇⠇"));
		// 2.4.8
		a.add(new FilterTest(filter, "alk|a","⠁⠇⠅⠸⠁"));
		// 2.4.9
		a.add(new FilterTest(filter, "subst. ~n ~ar","⠎⠥⠃⠎⠞⠄ ⠘⠒⠝ ⠘⠒⠁⠗"));
		// 2.5
		a.add(new FilterTest(filter, "Priset var 500 €.","⠠⠏⠗⠊⠎⠑⠞ ⠧⠁⠗ ⠼⠑⠚⠚ ⠘⠑⠄"));
		// COULDDO 2.6, 2.7
		// 3.2.1
		a.add(new FilterTest(filter, "Han heter Hans och bror hans heter Bror.","⠠⠓⠁⠝ ⠓⠑⠞⠑⠗ ⠠⠓⠁⠝⠎ ⠕⠉⠓ ⠃⠗⠕⠗ ⠓⠁⠝⠎ ⠓⠑⠞⠑⠗ ⠠⠃⠗⠕⠗⠄"));
		// 3.2.2
		a.add(new FilterTest(filter, "SAV","⠠⠠⠎⠁⠧"));
		a.add(new FilterTest(filter, "IKEAs katalog","⠠⠠⠊⠅⠑⠁⠱⠎ ⠅⠁⠞⠁⠇⠕⠛"));
		a.add(new FilterTest(filter, "Svenska ISBN-centralen","⠠⠎⠧⠑⠝⠎⠅⠁ ⠠⠠⠊⠎⠃⠝⠱-⠉⠑⠝⠞⠗⠁⠇⠑⠝"));
		// 3.2.3
		a.add(new FilterTest(filter, "LO/TCO/SACO:s Brysselkontor","⠠⠠⠠⠇⠕⠌⠞⠉⠕⠌⠎⠁⠉⠕⠱⠒⠎ ⠠⠃⠗⠽⠎⠎⠑⠇⠅⠕⠝⠞⠕⠗"));
		a.add(new FilterTest(filter, "SYNSKADADES RIKSFÖRBUND","⠠⠠⠠⠎⠽⠝⠎⠅⠁⠙⠁⠙⠑⠎ ⠗⠊⠅⠎⠋⠪⠗⠃⠥⠝⠙⠱"));
		a.add(new FilterTest(filter, "kWh, JämO","⠅⠠⠺⠓⠂ ⠠⠚⠜⠍⠠⠕"));
		a.add(new FilterTest(filter, "Lösenord: oVbEGj","⠠⠇⠪⠎⠑⠝⠕⠗⠙⠒ ⠕⠠⠧⠃⠠⠑⠠⠛⠚"));
		// 3.3.1
		a.add(new FilterTest(filter, "Lösenord: lvb57j","⠠⠇⠪⠎⠑⠝⠕⠗⠙⠒ ⠇⠧⠃⠼⠑⠛⠱⠚"));
		a.add(new FilterTest(filter, "\u066d \u2607 \u2639 \u263a \u00ba", "⠘⠦⠎⠞⠚⠜⠗⠝⠁⠘⠴ ⠘⠦⠃⠇⠊⠭⠞⠘⠴ ⠘⠦⠒⠦⠘⠴ ⠘⠦⠒⠴⠘⠴ ⠬⠕"));
		a.add(new FilterTest(filter, "Negativa tal: -45645",""));
		a.add(new FilterTest(filter, "\u2665","⠘⠦⠓⠚⠜⠗⠞⠁⠘⠴"));
		a.add(new FilterTest(filter, "",""));
		return a;
	}
	
	public static Collection<Test> getCapsTest() {
		ArrayList<Test> a = new ArrayList<Test>();
		CombinationFilter m = new CombinationFilter();
		m.add(new RegexFilter("([\\d]+[\\d,\\.]*)", "\u283c$1"));
		m.add(new RegexFilter("([\\d])([a-j])", "$1\u2831$2"));
		m.add(new CapitalizationMarkers());
		m.add(new CaseFilter(CaseFilter.Mode.LOWER_CASE));
		
		a.add(new FilterTest(m, "Det första exemplet är SAV, bara ett ord.", "⠠det första exemplet är ⠠⠠sav, bara ett ord."));
		a.add(new FilterTest(m, "Det andra exemplet är SAV VAS, en ordgrupp.", "⠠det andra exemplet är ⠠⠠⠠sav vas⠱, en ordgrupp."));
		a.add(new FilterTest(m, "LO/TCO / SACO:s Brysselkontor", "⠠⠠⠠lo/tco / saco⠱:s ⠠brysselkontor"));
		a.add(new FilterTest(m, "LO-TCO-SACO:s Brysselkontor", "⠠⠠⠠lo-tco-saco⠱:s ⠠brysselkontor"));
		a.add(new FilterTest(m, "LO/TCO/SACO5", "⠠⠠⠠lo/tco⠱/⠠⠠saco⠼5"));
		a.add(new FilterTest(m, "aaa/LO/TCO/sas", "aaa/⠠⠠⠠lo/tco⠱/sas"));
		a.add(new FilterTest(m, "dddLO/TCO5", "ddd⠠l⠠o/⠠⠠tco⠼5"));
		a.add(new FilterTest(m, "dddLO TCO5", "ddd⠠l⠠o ⠠⠠tco⠼5"));
		a.add(new FilterTest(m, "LO/TCO5ddc", "⠠⠠lo/⠠⠠tco⠼5⠱ddc"));
		a.add(new FilterTest(m, "LO-TCO-", "⠠⠠⠠lo-tco⠱-"));
		a.add(new FilterTest(m, "LO-/TCO-kongressen", "⠠⠠⠠lo-/tco⠱-kongressen"));
		a.add(new FilterTest(m, "TCO-", "⠠⠠tco⠱-"));
		a.add(new FilterTest(m, "IKEAs katalog", "⠠⠠ikea⠱s katalog"));
		a.add(new FilterTest(m, "IKEA4gf katalog", "⠠⠠ikea⠼4⠱gf katalog"));
		a.add(new FilterTest(m, "Svenska ISBN-centralen", "⠠svenska ⠠⠠isbn⠱-centralen"));
		a.add(new FilterTest(m, "SYNSKADADES RIKSFÖRBUND", "⠠⠠⠠synskadades riksförbund⠱"));
		a.add(new FilterTest(m, "kWh, JämO", "k⠠wh, ⠠jäm⠠o"));
		a.add(new FilterTest(m, "Lösenord: oVbEGj", "⠠lösenord: o⠠vb⠠e⠠gj"));
		a.add(new FilterTest(m, "Lösenord: oVb4EGF", "⠠lösenord: o⠠vb⠼4⠠e⠠g⠠f"));
		a.add(new FilterTest(m, "Det är sant: DETTA ÄR ETT TEST. Inget annat.", "⠠det är sant: ⠠⠠⠠detta är ett test⠱. ⠠inget annat."));
		a.add(new FilterTest(m, "Flera R O L I G A exempel.", "⠠flera ⠠r ⠠o ⠠l ⠠i ⠠g ⠠a exempel."));
		a.add(new FilterTest(m, "Test av STORA Bokstäver med flera ORD I RAD, samt andra varianter, t.ex. lösenord som oRoXVQ5q och S P Ä R R A D text.", "⠠test av ⠠⠠stora ⠠bokstäver med flera ⠠⠠⠠ord i rad⠱, samt andra varianter, t.ex. lösenord som o⠠ro⠠x⠠v⠠q⠼5q och ⠠s ⠠p ⠠ä ⠠r ⠠r ⠠a ⠠d text."));
		a.add(new FilterTest(m, "Flera R-O-L-I-G-A exempel.", "⠠flera ⠠⠠⠠r-o-l-i-g-a⠱ exempel."));
		a.add(new FilterTest(m, "SIFFROR I 10 GRUPPER.", "⠠⠠siffror ⠠i ⠼10 ⠠⠠grupper."));
		a.add(new FilterTest(m, "SIFFROR I FLER ÄN 10 GRUPPER.", "⠠⠠⠠siffror i fler än⠱ ⠼10 ⠠⠠grupper."));
		a.add(new FilterTest(m, "Svenska ISBN-Centralen", "⠠svenska ⠠⠠isbn⠱-⠠centralen"));
		a.add(new FilterTest(m, "SAV:::::VAS", "⠠⠠sav:::::⠠⠠vas"));
		a.add(new FilterTest(m, "SAV/VAS", "⠠⠠⠠sav/vas⠱"));
		a.add(new FilterTest(m, "UmUB", "⠠um⠠u⠠b"));
		a.add(new FilterTest(m, "(ISBN)", "(⠠⠠isbn)"));
		a.add(new FilterTest(m, "TVå inledande versaler", "⠠t⠠vå inledande versaler"));
		a.add(new FilterTest(m, "(vanligen förkortad SFSV) \u2013", "(vanligen förkortad ⠠⠠sfsv) –"));
		a.add(new FilterTest(m, "T.EX.", "⠠t.⠠⠠ex."));
		a.add(new FilterTest(m, "PC, men PC-apparat.", "⠠⠠pc, men ⠠p⠠c-apparat."));
		a.add(new FilterTest(m, "B. KORTA CITAT", "⠠b. ⠠⠠⠠korta citat⠱"));
		a.add(new FilterTest(m, "IV", "⠠⠠iv"));
		a.add(new FilterTest(m, "STRINDBERG, AUGUST", ""));
		a.add(new FilterTest(m, "GPS-klocka", "⠠⠠gps⠱-klocka"));
		a.add(new FilterTest(m, "CAN'T PLAY BINGO WITH NO LIGHTS!", ""));
		return a;
	}

	public static Collection<Test> getLocaleTest() {
		ArrayList<Test> a = new ArrayList<Test>();
		a.add(new LocaleTest("sv-SE", "sv", true));
		a.add(new LocaleTest("sv-FI", "da-DK", false));
		a.add(new LocaleTest("sv-SE-test", "sv-SE-test", true));
		a.add(new LocaleTest("sv-SE-test", "sv-SE-test-test", false));
		return a;
	}

	public static Collection<Test> getBreakPointTest() {
		ArrayList<Test> a = new ArrayList<Test>();
		a.add(new BreakpointTest("citat/blockcitat20", 17, "citat/blockcitat2", "0", true));
		a.add(new BreakpointTest("citat/blockcitat20", 1, "c", "itat/blockcitat20", true));
		a.add(new BreakpointTest("citat/blockcitat20", 0, "", "citat/blockcitat20", false));
		a.add(new BreakpointTest("citat blockcitat20", 5, "citat ", "blockcitat20", false));
		a.add(new BreakpointTest("citat blockcitat20", 4, "cita", "t blockcitat20", true));
		a.add(new BreakpointTest("citat/blockcitat20", 35, "citat/blockcitat20", "", false));
		a.add(new BreakpointTest("citat-blockcitat20", 12, "citat-", "blockcitat20", false));
		a.add(new BreakpointTest("at the ev­i­dence on the ev­i­dence", 11, "at the evi-", "dence on the ev­i­dence", false));
		a.add(new BreakpointTest("Negative number: -154", 19, "Negative number: ", "-154", false));
		a.add(new BreakpointTest("Negative numbers - odd! (and even)", 18, "Negative numbers - ", "odd! (and even)", false));
		a.add(new BreakpointTest("Negative numbers - odd! (and even)", 17, "Negative numbers ", "- odd! (and even)", false));
		return a;
	}

/*
    public void runNumberTest() {
    	for (int i = 0; i<100; i++) {
    		ps.println(i + " " + SwedishVolumeCoverPage.intToText(i));
    	}
    }*/
    


/*	public void testIsa(FilterLocale l1, FilterLocale l2) {
		System.out.println(l1 + (l1.isA(l2) ? " is a " : " is not a ")  + l2);
	}*/


	/*
	ArrayList<TabStopString> test = new ArrayList<TabStopString>();
	test.add(new TabStopString(filters.filter("te1"), 2));
	test.add(new TabStopString(filters.filter("te2"), 15, TabStopString.Alignment.CENTER));
	test.add(new TabStopString(filters.filter("te3"), 30, TabStopString.Alignment.RIGHT, filters.filter(".")));
	*/

	/*
	 * hyphtest:
	 * sjuttonåringar
	 * blårött
	 * jättestor
	 * */
}
