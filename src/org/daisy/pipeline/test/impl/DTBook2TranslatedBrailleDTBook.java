package org.daisy.pipeline.test.impl;

import java.util.List;

import org.daisy.pipeline.test.PipelineTest;
import org.daisy.util.file.Directory;

public class DTBook2TranslatedBrailleDTBook extends PipelineTest {

	public DTBook2TranslatedBrailleDTBook(Directory dataInputDir,
			Directory dataOutputDir) {
		super(dataInputDir, dataOutputDir);
	}

	@Override
	public List<String> getParameters() {
		mParameters.add("--input=" + mDataInputDir
				+ "/dtbook/dontworrybehappy.xml");
		mParameters
				.add("--output="
						+ mDataOutputDir
						+ "/DTBook2TranslatedBrailleDTBook/dontworrybehappyTranslated.xml");
		return mParameters;
	}

	@Override
	public String getResultDescription() {
		return "";
	}

	@Override
	public boolean supportsScript(String scriptName) {
		return ("DTBookToTranslatedBrailleDTBook.taskScript".equals(scriptName));
	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub

	}

}
