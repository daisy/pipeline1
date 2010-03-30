package org_pef_text.pef2text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Provides a volume writer for Braillo embossers.
 * Note that, this volume writer assumes LineBreaks.Type.DOS
 * and Padding.BEFORE.
 * @author Joel Håkansson, TPB
 *
 */
public abstract class BrailloVolumeWriter implements VolumeWriter, EmbosserProperties {
	public final static byte[] ffSeq = new byte[]{'\r', '\n', 0x0c}; 

	public abstract List<? extends List<Byte>> reorder(List<? extends List<Byte>> pages);
	public abstract byte[] getHeader(int pages) throws IOException;
	public abstract byte[] getFooter(int pages) throws IOException;

	public boolean write(List<? extends List<Byte>> input, File out) throws IOException {
		FileOutputStream os = new FileOutputStream(out);

		List<? extends List<Byte>> pages = reorder(input);
		
		int len = pages.size();
		os.write(getHeader(len));
		//write contents
		// debug: int j = 1;
		for (List<Byte> page : pages) {
			byte[] b = new byte[page.size()];
			for (int i=0; i<page.size(); i++) {
				b[i] = page.get(i);
			}
			// debug: os.write(("---- page --- " + j + " [").getBytes());
			os.write(b);
			// debug: os.write("]--- page ---- ".getBytes());
			// debug: j++;
		}
		os.write(getFooter(len));
		os.close();
		return true;
	}

	public boolean supportsVolumes() {
		return true;
	}

}
