package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class ValidatorConfigurable2 extends PipelineTest {

	public ValidatorConfigurable2(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--validatorInputFile=" + mDataInputDir + "/pef/poem.pef");
		mParameters.add("--validatorInputSchemas=" + mDataInputDir + "/pef/pef-2008-1.rng");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Validate a pef document and return valid";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("ConfigurableValidator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
