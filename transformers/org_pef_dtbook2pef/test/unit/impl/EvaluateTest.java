package org_pef_dtbook2pef.test.unit.impl;

import java.util.ArrayList;
import java.util.Collection;

import org_pef_dtbook2pef.system.tasks.layout.utils.Expression;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class EvaluateTest implements Test {
	private final String input;
	private final Object result;
	
	public EvaluateTest(String input, Object result) {
		this.input = input;
		this.result = result;
	}

	public String getName() {
		return "Evaluate";
	}

	public TestResult runTest() {
		Expression e = new Expression();
		Object ret = e.evaluate(input);
		return new TestResult(this, ret, result.equals(ret), input + " -> " + ret + " (" +ret.getClass() + ")");
	}
	
	/**
	 * Some tests included in this test
	 * @return example tests
	 */
	public static Collection<Test> getTestCollection() {
		ArrayList<Test> a = new ArrayList<Test>();
		a.add(new EvaluateTest("(+ 7 3)", 10d));
		a.add(new EvaluateTest("(+ 7 3) (+ 4 11)", 15d));
		a.add(new EvaluateTest("(* 4 (+ 1 1 1) 2)", 24d));
		a.add(new EvaluateTest("( % (+ (* 12  2) 1) 2)", 1d));
		a.add(new EvaluateTest("(/ 50 5)", 10d));
		a.add(new EvaluateTest("(= 50 5)", false));
		a.add(new EvaluateTest("(= 5.000d 5f 5)", true));
		a.add(new EvaluateTest("(= 5 5 5 1)", false));
		a.add(new EvaluateTest("(< 5 6 7)", true));
		a.add(new EvaluateTest("(< 100 6)", false));
		a.add(new EvaluateTest("(< 6 6)", false));
		a.add(new EvaluateTest("(<= 6 6)", true));
		a.add(new EvaluateTest("(> 6 6)", false));
		a.add(new EvaluateTest("(>= 6 6)", true));
		a.add(new EvaluateTest("(& (= 1 1) (= 2 2))", true));
		a.add(new EvaluateTest("(| (= 1 0) (= 2 2))", true));
		a.add(new EvaluateTest("(& (= 1 1) (= 1 2))", false));
		a.add(new EvaluateTest("(| (= 1 0) (= 2 1))", false));
		a.add(new EvaluateTest("(+ (if (= 1 0) 18 17) 0)", 17d));
		a.add(new EvaluateTest("(if (< 1 3) 18 17)", 18d));
		a.add(new EvaluateTest("(now \"yyyy\")", "2011")); // "" is not required in this case // stupid test
		a.add(new EvaluateTest("(set var 3) (set var1 12) (round (* var var1))", 36));
		return a;
	}

	public Object getExpected() {
		return result;
	}

	public Object getInput() {
		return input;
	}
	
}
