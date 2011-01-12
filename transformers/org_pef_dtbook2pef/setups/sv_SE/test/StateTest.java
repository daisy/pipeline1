package org_pef_dtbook2pef.setups.sv_SE.test;

import org_pef_dtbook2pef.system.utils.StateObject;
import org_pef_dtbook2pef.test.unit.Test;
import org_pef_dtbook2pef.test.unit.TestResult;

public class StateTest implements Test {
	
	public StateTest() {
		
	}

	public String getName() {
		return "State Object";
	}

	public TestResult runTest() {
		StateObject so = new StateObject();
		boolean success = true;
		try {
			so.assertUnopened();
		} catch (IllegalStateException e) {
			success = false;
		}
		so.open();
		try {
			so.assertUnopened();
			success = false;
		} catch (IllegalStateException e) {
			// this is correct in this state
		}
		return new TestResult(this, null, success, "State object tests performed");
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
