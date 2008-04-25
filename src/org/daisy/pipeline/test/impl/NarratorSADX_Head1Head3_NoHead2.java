package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.EFolder;

public class NarratorSADX_Head1Head3_NoHead2 extends PipelineTest {

	public NarratorSADX_Head1Head3_NoHead2(EFolder dataInputDir, EFolder dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/sadx/Head1Head3_NoHead2.xml");
		mParameters.add("--outputPath=" + mDataOutputDir
				+ "/NarratorSADX-Head1Head3_NoHead2/");
		return mParameters;
	}

	@Override
	public String getResultDescription() {
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if ("DTBookFix_NarratorValidation.taskScript".equals(scriptName)) {
			return true;
		}
		return false;
	}

}
