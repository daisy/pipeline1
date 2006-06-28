package se_tpb_speechgenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

import se_tpb_dtbAudioEncoder.EncodingException;


/**
 * A class providing the ability to merge and mp3-encode 
 * files in a concurrent mode.
 * 
 * @author Martin Blomberg
 *
 */
public class WavConcatWorker implements Runnable {
	
	private List inputFiles;
	private File wavFile;
	private File mp3File;
	private CountDownLatch signal;
	private int maxTempFiles = 400; // the maximum number of open files
	
	
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
	public WavConcatWorker(List inputFiles, File outWav, File outMp3, CountDownLatch signal) {
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
			System.exit(1);
		}		
		if (mp3File != null) {
			try {
				mp3encode(wavFile, mp3File);
			} catch (EncodingException e) {
				e.printStackTrace();
				System.exit(1);
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
	private void merge(List inputFiles, File outputWav) throws IOException {
		// the divide and conquer approach is made to avoid 
		// the "too many open files"-exception from Java.
		if (inputFiles.size() > maxTempFiles) {
			// first half of the list
			List files = new ArrayList();
			File firstMerge = File.createTempFile("wavmerge", ".wav");
			for (int i = 0; i < inputFiles.size() / 2; i++) {
				files.add(inputFiles.get(i));
			}
			merge(files, firstMerge);
			
			// second half of the list
			List moreFiles = new ArrayList();
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
				File f = (File) inputFiles.get(i);
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
	 * @throws EncodingException
	 * @throws ExecutionException
	 */
	private void mp3encode(File inputWav, File outputMp3) throws EncodingException, ExecutionException {
		String lameCommand = System.getProperty("dmfc.lame.path");
		
		String inputFilename = inputWav.getAbsolutePath();
		if (inputFilename.contains(" ")) {
			inputFilename = "\"" + inputFilename + "\"";
		}
		
		String outputFilename = outputMp3.getAbsolutePath();
		if (outputFilename.contains(" ")) {
			outputFilename = "\"" + outputFilename + "\"";
		}
		
		String cmd[] = {
				lameCommand,
				"--quiet", "-h", "-m", "m", "-a", "-cbr", "-b", "32", "--resample", "22.50",
				inputFilename,
				outputFilename
				};
		int exitVal = Command.execute(cmd);
		if (exitVal != 0) {
			System.err.println("Unable to encode using the following command:");
			System.err.println(cmd.toString());
			System.err.println("Exit value: " + exitVal);
			System.exit(1);
		}
		if (!wavFile.delete()) {
			wavFile.deleteOnExit();
		}
	}
}
