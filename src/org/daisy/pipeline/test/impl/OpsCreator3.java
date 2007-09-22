package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class OpsCreator3 extends PipelineTest {

	public OpsCreator3(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		//mParameters.add("--input=" + mDataInputDir + "/xhtml/multi-language-unicode.html;" + mDataInputDir + "/xhtml/daisy_202.html");
		mParameters.add("--input=" + mDataInputDir + "/xhtml/daisy_202.html");
		mParameters.add("--output=" + mDataOutputDir + "/OpsCreator3/hauy.epub");				
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("OPSCreator.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
