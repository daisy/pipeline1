package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class UnicodeNormalizer1 extends PipelineTest {

	public UnicodeNormalizer1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + mDataInputDir + "/dtbook/dontworrybehappy.xml");
		mParameters.add("--output=" + mDataOutputDir + "/normalizer/");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "perform normalization";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("UnicodeNormalizer.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
