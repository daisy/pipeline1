package org_pef_dtbook2pef.setups.sv_SE.test;

import org_pef_dtbook2pef.system.tasks.layout.text.FilterLocale;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class LocaleTest implements Test {
	private final String input;
	private final String ref;
	private final boolean isa;
	
	public LocaleTest(String input, String ref, boolean isa) {
		this.input = input;
		this.ref = ref;
		this.isa = isa;
	}

	public String getName() {
		return "Locale";
	}

	public TestResult runTest() {
		FilterLocale inLoc = FilterLocale.parse(input);
		FilterLocale refLoc = FilterLocale.parse(ref);
		boolean ok = inLoc.isA(refLoc)==isa;
		return new TestResult(this, null, ok, inLoc + (inLoc.isA(refLoc) ? " is a " : " is not a ") + ref);
	}

	public Object getExpected() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getInput() {
		// TODO Auto-generated method stub
		return null;
	}
}