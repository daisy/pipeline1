/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
		Command.execute((arg.toArray(new String[arg.size()])));
	}

}
