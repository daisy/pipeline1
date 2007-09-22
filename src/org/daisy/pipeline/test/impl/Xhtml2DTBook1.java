package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class Xhtml2DTBook1 extends PipelineTest {

	public Xhtml2DTBook1(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {
		mParameters.add("--inputFile=" + mDataInputDir + "/xhtml/daisy_202.html");
		mParameters.add("--outputFile=" + mDataOutputDir + "/Xhtml2Dtbook/output01.xml");		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return null;
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("Xhtml2Dtbook.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
