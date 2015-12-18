package int_daisy_mixedContentNormalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.junit.Test;
import org.hamcrest.Matcher.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class MixedContentNormalizerTest {

	@Test
	public void testNormalizer_01() throws TransformerRunException, TransformerDisabledException, IOException {
		
		// Use same line endings for all os environments, golden master created using *nix-style line endings
		System.setProperty("line.separator", "\n");

		Map<String,String> parameters = new HashMap<String, String>();
		 
		String testNormalizeOutputName = "test/int_daisy_mixedContentNormalizer/test_normalize_output.xml";
		String testNormalizeInputName = "test/int_daisy_mixedContentNormalizer/test_normalize_input.xml";
        String testGoldenMasterName = "test/int_daisy_mixedContentNormalizer/golden_master_test_normalize.xml";
		parameters.put("input", testNormalizeInputName);
		parameters.put("output", testNormalizeOutputName);
		parameters.put("addSyncPoints", "true");
		parameters.put("implementation", "dom");
		
		MixedContentNormalizer n = new MixedContentNormalizer(null, false);
		n.execute(parameters);
		
		File goldenMaster = new File(testGoldenMasterName);
		File testOutput = new File(testNormalizeOutputName);
		assertThat(FileUtils.contentEquals(goldenMaster, testOutput), is(Boolean.TRUE));
	}
}
