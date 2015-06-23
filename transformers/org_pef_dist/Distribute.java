package org_pef_dist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.transformer.Transformer;
import org.daisy.pipeline.exception.TransformerRunException;

public class Distribute extends Transformer {
	private final static int BUF_SIZE = 2048;
	/**
	 * Default constructor
	 * @param inListener
	 * @param isInteractive
	 */
	public Distribute(InputListener inListener, Boolean isInteractive) {
		super(inListener, isInteractive);
	}

	@Override
	protected boolean execute(Map<String, String> parameters)
			throws TransformerRunException {
		progress(0);
		File inputFile = new File(parameters.get("input"));
		File source=new File(new File(getTransformerDirectory(), "lib"), "dist.jar");
		File outputFile=new File(parameters.get("output"));

		ZipInputStream zis=null;
		ZipOutputStream zos=null;
		try {
			zis = new ZipInputStream(new FileInputStream(source));
			zos = new ZipOutputStream(new FileOutputStream(outputFile));
			ZipEntry e;
			while((e=zis.getNextEntry())!=null) {
				zos.putNextEntry(e);
				copyFile(zis, zos);
				zos.closeEntry();
				e=null;
			}
			zos.putNextEntry(new ZipEntry("www/book.pef"));
			FileInputStream is = new FileInputStream(inputFile);
			copyFile(is, zos);
			zos.closeEntry();
			zos.flush();
			zos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		progress(1);
		return true;
	}
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		int read;
		while ((read=in.read(buf, 0, BUF_SIZE))!=-1) {
			out.write(buf, 0, read);
		}
	}

}
