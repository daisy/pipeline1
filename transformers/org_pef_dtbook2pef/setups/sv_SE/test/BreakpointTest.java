package org_pef_dtbook2pef.setups.sv_SE.test;

import org_pef_dtbook2pef.system.tasks.layout.utils.BreakPoint;
import org_pef_dtbook2pef.system.tasks.layout.utils.BreakPointHandler;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class BreakpointTest implements Test {
	private final String input;
	private final BreakPoint expected;
	private final int breakpoint;
	
	public BreakpointTest(String input, int breakpoint, String expHead, String expTail, boolean expHard) {
		this(input, breakpoint, new BreakPoint(expHead, expTail, expHard));
	}
	
	public BreakpointTest(String input, int breakpoint, BreakPoint expected) {
		this.input = input;
		this.breakpoint = breakpoint;
		this.expected = expected;
	}

	public String getName() {
		return "Break point";
	}

	public TestResult runTest() {
		BreakPointHandler bph = new BreakPointHandler(input);
		BreakPoint bp = bph.nextRow(breakpoint);
		boolean ok = bp.equals(expected);
		TestResult tr = new TestResult(this, bp, ok, "'" + input + "' @" + breakpoint + " -> '" + bp.getHead() + "', '" + (bp.getTail()) + "', " + (bp.isHardBreak() ? "hard break":"soft break") + "");
		return tr;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getInput() {
		return input;
	}
}
