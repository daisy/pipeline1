package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class Xhtml2DTBook1 extends PipelineTest {

	public Xhtml2DTBook1(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--inputFile=" + mDataInputDir + "/xhtml/valentinhauy.html");
		mParameters.add("--outputFile=" + mDataOutputDir + "/Xhtml2Dtbook1/output01.xml");
		mParameters.add("--userCss=" + mDataInputDir + "/dtbook/dtbook.2005.basic.css");		
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "Should output a valid DTBook with dtbook.2005.basic.css added. Input XHTML css should not be in output folder. ";
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
