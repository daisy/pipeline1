package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class NarratorSADX_EmptyHeadingOnly extends PipelineTest {

	public NarratorSADX_EmptyHeadingOnly(Directory dataInputDir, Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir + "/sadx/EmptyHeadingOnly.xml");
		mParameters.add("--outputPath=" + mDataOutputDir
				+ "/NarratorSADX-EmptyHeadingOnly/");
		return mParameters;
	}

	@Override
	public String getResultDescription() {
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		if ("Narrator-DtbookToDaisy202.taskScript".equals(scriptName)) {
			return true;
		}
		return false;
	}

}
