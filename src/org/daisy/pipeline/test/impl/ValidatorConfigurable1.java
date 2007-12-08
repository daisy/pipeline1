package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class ValidatorConfigurable1 extends PipelineTest {

	public ValidatorConfigurable1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--validatorInputFile=" + mDataInputDir + "/dtbook/hauy_valid.xml");
		mParameters.add("--validatorInputSchemas=" + mDataInputDir + "/schema/foobar.sch");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Test should validate a dtbook document and return missing 'dc:FooBar' because of the added bogus schematron";
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
