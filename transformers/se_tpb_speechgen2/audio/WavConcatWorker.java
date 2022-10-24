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
package se_tpb_speechgen2.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;


/**
 * A class providing the ability to merge and mp3-encode 
 * files in a concurrent mode.
 * 
 * @author Martin Blomberg
 *
 */
public class WavConcatWorker implements Runnable {
	
	private List<File> inputFiles;	// a list of input files to concatenate.
	private File wavFile;			// the resulting wav file.
	private File mp3File;			// the optional mp3 encoding of wavFile.
	private CountDownLatch signal;	// thread synchronization: signal when done!
	private int maxTempFiles = 400; // the maximum number of open files per run. If more, divide and conquer!
	
	
	/**
	 * @param inputFiles the list of input files to merge. The files will 
	 * be deleted once merged.
	 * @param outWav the target file for the wav concatenation. 
	 * Must not be <code>null</code>.
	 * @param outMp3 the mp3 output file. If <code>null</code>, 
	 * <code>outputWav</code> will be the result of this run, otherwise
	 * the output will be represented by <code>outputMp3</code> and <code>outputWav</code>
	 * will be deleted together with the files in <code>inputFiles</code>, 
	 * @param signal a <code>CountDownLatch</code> to give the caller (the thread
	 * that started this thread) an "i've finished"-signal.
	 */
	public WavConcatWorker(List<File> inputFiles, File outWav, File outMp3, CountDownLatch signal) {
		this.inputFiles = inputFiles;
		this.wavFile = outWav;
		this.mp3File = outMp3;
		this.signal = signal;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			merge(inputFiles, wavFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}		
		if (mp3File != null) {
			try {
				mp3encode(wavFile, mp3File);
			} catch (ExecutionException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
			
		signal.countDown();
	}
	
	/**
	 * Merges the files in <code>inputFiles</code> to produce 
	 * <code>outputWav</code> that contains all audio in a sequence. The 
	 * files in <code>inputFiles</code> will be deleted after a merge.
	 * @param inputFiles a list containing instances of <code>java.io.File</code>.
	 * @param outputWav a <code>java.io.File</code> pointing to the desired result.
	 * @throws IOException
	 */
	private void merge(List<File> inputFiles, File outputWav) throws IOException {
		// the divide and conquer approach is made to avoid 
		// the "too many open files"-exception from Java.
		if (inputFiles.size() > maxTempFiles) {
			// first half of the list
			List<File> files = new ArrayList<File>();
			File firstMerge = File.createTempFile("wavmerge", ".wav");
			for (int i = 0; i < inputFiles.size() / 2; i++) {
				files.add(inputFiles.get(i));
			}
			merge(files, firstMerge);
			
			// second half of the list
			List<File> moreFiles = new ArrayList<File>();
			File secondMerge = File.createTempFile("wavmerge", ".wav");
			for (int i = inputFiles.size() / 2; i < inputFiles.size(); i++) {
				moreFiles.add(inputFiles.get(i));
			}
			merge(moreFiles, secondMerge);
			
			// wrap it all up
			files.clear();
			files.add(firstMerge);
			files.add(secondMerge);
			merge(files, outputWav);
			
		} else {		
			// base case:
			AudioConcat.concat(inputFiles, outputWav);
			for (int i = 0; i < inputFiles.size(); i++) {
				File f = inputFiles.get(i);
				if (!f.delete()) {
					f.deleteOnExit();
				}
			}
		}
	}
	
	
	/**
	 * Uses lame to encode <code>inputWav</code> as mp3 and store the result 
	 * as <code>outputMp3</code>. <code>inputWav</code> will be deleted once the
	 * encoding is done.
	 * @param inputWav the input wav file.
	 * @param outputMp3 the desired output mp3 file.
	 * @throws ExecutionException
	 */
	private void mp3encode(File inputWav, File outputMp3) throws  ExecutionException {
		String lameCommand = System.getProperty("pipeline.lame.path");
		
		String inputFilename = inputWav.getAbsolutePath();
		String outputFilename = outputMp3.getAbsolutePath();
				
		String cmd[] = {
				lameCommand,
				"--quiet", "-h", "-m", "m", "-a", "--cbr", "-b", "32", "--resample", "22.50",
				inputFilename,
				outputFilename
				};
		
		int exitVal = Command.execute(cmd);
		if (exitVal != 0) {
			String str = "";
			for (int i = 0; i < cmd.length; i++) {
				str += " " + cmd[i];
			}
			System.err.println("Unable to encode using lame:");
			System.err.println(str);
			System.err.println("Exit value: " + exitVal);
			String msg = "Unable to encode using lame, lame exit code: " + exitVal;
			throw new ExecutionException(msg);
		}
		if (!wavFile.delete()) {
			wavFile.deleteOnExit();
		}
	}
}
