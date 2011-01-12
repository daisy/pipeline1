package org_pef_dtbook2pef.test.junit;

import org.junit.Test;

public class JUnitTestSuite extends JUnitTestRunner {
	
	@Test
	public void testEvaluate() {
		run(org_pef_dtbook2pef.test.unit.impl.EvaluateTest.getTestCollection());
	}
	
	@Test
	public void testPosition() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection());
	}
	
	@Test
	public void testPosition1() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection().get(0));
	}
	
	@Test
	public void testPosition2() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection().get(1));
	}
	
	@Test
	public void testPosition3() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection().get(2));
	}
	
	@Test
	public void testPosition4() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection().get(3));
	}
	
	@Test
	public void testPosition5() {
		run(org_pef_dtbook2pef.test.unit.impl.PositionTest.getTestCollection().get(4));
	}

}
