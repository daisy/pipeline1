package org_pef_dtbook2pef.test.unit;


public interface Test {
	
	/**
	 * Get the name of this test type
	 * @return returns the name of the test type
	 */
	public String getName();
	
	/**
	 * Run the test
	 * @return returns the test result
	 */
	public TestResult runTest();
	
	public Object getExpected();
	
	public Object getInput();

}