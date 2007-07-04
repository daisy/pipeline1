package se_tpb_wordml2dtbook;

import java.io.File;
import java.util.ArrayList;

import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

public class ImageConverter {

	public ImageConverter() { }
	
	public void convert(File input, File output) throws ExecutionException {
		String converter = System.getProperty("pipeline.imageMagick.converter.path");
		//String converter = "C:\\Program\\ImageMagick-6.3.4-Q16\\convert.exe";
		ArrayList<String> arg = new ArrayList<String>();
		arg.add(converter);
		arg.add(input.getAbsolutePath());
		arg.add("-scale");
		arg.add("600>");
		arg.add(output.getAbsolutePath());
		Command.execute((String[])(arg.toArray(new String[arg.size()])));
	}

}
