package org_pef_dtbook2pef.test.unit;

import java.io.PrintStream;
import java.util.Collection;

public class TestRunner {
	private PrintStream ps;
	private int testCount;
	private int failCount;
	private boolean reportSuccessful;
	
	public TestRunner(PrintStream ps, boolean reportSuccessful) {
		this.ps = ps;
		this.testCount = 0;
		this.failCount = 0;
		this.reportSuccessful = reportSuccessful;
	}
	
	public void runTestBatch(String batchName, Collection<Test> ta) {
		runTestBatch(batchName, ta, reportSuccessful);
	}
	
	public void runTestBatch(String batchName, Collection<Test> ta, boolean outputSuccessful) {
		ps.println("--- Running " + batchName + " tests ---");
		int c=0;
		int f=0;
		for (Test t : ta) {
			TestResult res = t.runTest();
			if (!res.success()) {
				f++;
			}
			c++;
			String report = reportTestResult(res.getTest().getName(), res.success(), res.getResultMessage());
			if (outputSuccessful || !res.success()) {
				ps.println("  " + report);
			}
		}
		ps.println(batchName + " results: " + "completed " + c + " tests" + (f>0? (", " + f + " failed"): ", all OK"));
		ps.println();
	}
	
	public void runTest(Test t) {
		ps.println("--- Running " + t.getName() + " test --- ");
		TestResult res = t.runTest();	
		ps.println(reportTestResult(res.getTest().getName(), res.success(), res.getResultMessage()));
		ps.println();
	}
	
	public void printSummary() {
		ps.println("Test run completed!");
		ps.println("Completed " +testCount +" tests.");
		ps.println(failCount+ " tests failed.");
	}

	private String reportTestResult(String action, boolean ok, String desc) {
		testCount++;
		if (!ok) { failCount++; }
		return action + " " + (ok ? "OK": "FAILED") + ": " + desc; 
	}

}