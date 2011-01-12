package org_pef_dtbook2pef.test.unit.impl;

import java.util.ArrayList;
import java.util.List;

import org_pef_dtbook2pef.system.tasks.layout.flow.Position;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class PositionTest implements Test {
	
	private final String input;
	private final Position expected;
	private final boolean isRel;
	private final double value;
	private final int width;
	private final int absolute;
	private final static ArrayList<Test> tests;
	
	static {
		tests = new ArrayList<Test>();
		tests.add(new PositionTest("33%", true, 0.33d, 30, 10));
		tests.add(new PositionTest("15", false, 15d, 30, 15));
		tests.add(new PositionTest("20%", true, 0.2d, 28, 6));
		tests.add(new PositionTest("0%", true, 0d, 28, 0));
		tests.add(new PositionTest("100%", true, 1d, 28, 28));
	}
	
	public PositionTest(String input, boolean isRel, double value, int width, int absolute) {
		this.input = input;
		this.isRel = isRel;
		this.value = value;
		this.width = width;
		this.absolute = absolute;
		this.expected = new Position(value, isRel);
	}

	public String getName() {
		return "Position Test";
	}

	public TestResult runTest() {
		Position p = Position.parsePosition(input);
		return new TestResult(this, p, p.isRelative()==isRel && p.getValue()==value && p.makeAbsolute(width)==absolute, input);
	}
	
	/**
	 * Some tests included in this test
	 * @return example tests
	 */
	public static List<Test> getTestCollection() {
		return tests;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getInput() {
		return input;
	}

}
