package org_pef_dtbook2pef.test.junit;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

public class JUnitTestRunner {

	public void run(Collection<org_pef_dtbook2pef.test.unit.Test> tc) {
		for (org_pef_dtbook2pef.test.unit.Test t : tc) {
			run(t);
		}		
	}
	
	public void run(org_pef_dtbook2pef.test.unit.Test t) {
		org_pef_dtbook2pef.test.unit.TestResult tr = t.runTest();
		assertEquals(tr.getResultMessage(), tr.getResult(), t.getExpected());
	}
	
}
