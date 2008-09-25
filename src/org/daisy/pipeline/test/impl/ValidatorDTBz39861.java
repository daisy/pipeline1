package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class ValidatorDTBz39861 extends PipelineTest {

	public ValidatorDTBz39861(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--validatorInputFile=" + mDataInputDir + "/dtb/z3986-2005/06-speechgen.opf");
		mParameters.add("--validatorTimeTolerance=100");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should report no errors.";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Z3986DTBValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
