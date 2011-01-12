package org_pef_dtbook2pef.test.unit;


public class TestResult {
	private final Test test;
	private final boolean success;
	private String resultMessage;
	private Object result;
	
	/**
	 * Create a new TestResult for the supplied test
	 * @param test the test that the result is for
	 * @param success true if the test was successful, false otherwise
	 * @param resultMessage a descriptive result message
	 */
	public TestResult(Test test, Object result, boolean success, String resultMessage) {
		this.test = test;
		this.success = success;
		this.resultMessage = resultMessage;
		this.result = result;
	}
	
	/**
	 * Get the test for this TestResult
	 * @return returns the test
	 */
	public Test getTest() {
		return test;
	}

	/**
	 * Get test status
	 * @return returns true if test was successful, false otherwise
	 */
	public boolean success() {
		return success;
	}
	
	public Object getResult() {
		return result;
	}

	/**
	 * Get a descriptive message for the test result
	 * @return returns a descriptive message for the test result
	 */
	public String getResultMessage() {
		return resultMessage;
	}
	
	/**
	 * Set the message for this test result
	 * @param msg the result message
	 */
	public void setResultMessage(String msg) {
		resultMessage = msg;
	}

}