package org_pef_dtbook2pef.test.unit.impl;

import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class FilterTest implements Test {
	private StringFilter filter;
	private final String input;
	private final String expected;
	
	public FilterTest(StringFilter filter, String input, String expOutput) {
		this.input = input;
		this.expected = expOutput;
		this.filter = filter;
	}

	public String getInputAsString() {
		return input;
	}

	public String getName() {
		return "Filter Test";
	}

	public TestResult runTest() {
		String res = filter.filter(input);
		TestResult tr = new TestResult(this, res, expected.equals(res), input);
		StringBuffer sb = new StringBuffer();
		sb.append("'"+ input +"'");
		if (!tr.success()) { 
			sb.append(" -> '" + res + "'");	
		} else {
			sb.append(" -> '" + res + "'");
		}
		tr.setResultMessage(sb.toString());
		return tr;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getInput() {
		return input;
	}

}
