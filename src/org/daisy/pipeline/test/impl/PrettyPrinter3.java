package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class PrettyPrinter3 extends PipelineTest {

	public PrettyPrinter3(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}
	
	@Override
	public List<String> getParameters() {		
		mParameters.add("--input=" + "D:\\data\\documents\\daisyware\\dmfc\\sourceforge.webpages\\index.html");
		mParameters.add("--output=" + "D:\\data\\documents\\daisyware\\dmfc\\sourceforge.webpages\\ppout\\");
		return mParameters;
	}

	@Override
	public String getResultDescription() {		
		return "pretty printer";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if("PrettyPrinter.taskScript".equals(scriptName)) {
			return true;
		}		
		return false;
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		
	}

}
