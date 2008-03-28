/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package se_tpb_speechgen2.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.daisy.util.execution.Command;
import org.daisy.util.execution.ExecutionException;

/**
 * A class providing the ability to merge and mp3-encode files in a concurrent
 * mode.
 * 
 * @author Martin Blomberg
 * 
 */
public class AudioConcatQueue implements Runnable {

	/**
	 * Queue poison, represents "End of input".
	 */
	public final FileBunch poison = new FileBunch(null, null, null);

	/**
	 * Number of files merged so far.
	 */
	private int filesMerged = 0;

	/**
	 * Queue for merge jobs.
	 */
	private LinkedBlockingQueue<FileBunch> queue;

	/**
	 * Thread synchronization, barrier to signal when done.
	 */
	private CountDownLatch signal;

	/**
	 * Maximum number of files to merge, if more: divide and conquer.
	 */
	private int maxTempFiles = 200;

	/**
	 * Represents a "merge job".
	 * 
	 * @author Martin Blomberg
	 * 
	 */
	class FileBunch {
		private List<File> inputFiles;
		private File wavFile;
		private File mp3File;

		FileBunch(List<File> inputFiles, File wavFile, File mp3File) {
			this.inputFiles = inputFiles;
			this.wavFile = wavFile;
			this.mp3File = mp3File;
		}

		List<File> getInputFiles() {
			return inputFiles;
		}

		File getMp3File() {
			return mp3File;
		}

		File getWavFile() {
			return wavFile;
		}
	}

	/**
	 * Constructs a new queue for audio concatenation.
	 * 
	 * @param inputFiles
	 *            the list of input files to merge. The files will be deleted
	 *            once merged.
	 * @param outWav
	 *            the target file for the wav concatenation. Must not be
	 *            <code>null</code>.
	 * @param outMp3
	 *            the mp3 output file. If <code>null</code>,
	 *            <code>outputWav</code> will be the result of this run,
	 *            otherwise the output will be represented by
	 *            <code>outputMp3</code> and <code>outputWav</code> will be
	 *            deleted together with the files in <code>inputFiles</code>,
	 * @param signal
	 *            a <code>CountDownLatch</code> to give the caller (the thread
	 *            that started this thread) an "i've finished"-signal.
	 */
	public AudioConcatQueue(CountDownLatch signal) {
		if (signal == null) {
			String msg = "CountDownLatch may not be null!";
			throw new IllegalArgumentException(msg);
		}

		this.signal = signal;
		queue = new LinkedBlockingQueue<FileBunch>();
	}

	/**
	 * Returns the number of files merged so far.
	 * 
	 * @return the number of files merged so far.
	 */
	public int numFilesMerged() {
		return filesMerged;
	}

	/**
	 * Add audio to merge.
	 * 
	 * @param inputFiles
	 *            The wav snips.
	 * @param wavFile
	 *            the resulting wav file.
	 */
	public void addAudio(List<File> inputFiles, File wavFile) {
		addAudio(inputFiles, wavFile, null);
	}

	/**
	 * Add audio to merge.
	 * 
	 * @param inputFiles
	 *            The wav snips.
	 * @param wavFile
	 *            the resulting wav file.
	 * @param mp3File
	 *            the resulting mp3 file.
	 */
	public void addAudio(List<File> inputFiles, File wavFile, File mp3File) {
		FileBunch fb = new FileBunch(inputFiles, wavFile, mp3File);
		queue.add(fb);
	}

	/**
	 * Tells this queue that no more elements are going to be added. So, just
	 * finish what you are doing and then exit.
	 * 
	 */
	public void finish() {
		DEBUG("Adds queue poison!");
		queue.add(this.poison);
	}

	/**
	 * A method to call to abort this process. The queue clears its content and
	 * adds the poison to end soon.
	 */
	public void abort() {
		queue.clear();
		queue.add(this.poison);
	}

	private long getTotalSize(FileBunch fb) {
		List<File> inputFiles = fb.getInputFiles();
		long size = 0;
		for (Iterator<File> it = inputFiles.iterator(); it.hasNext();) {
			File f = (File) it.next();
			size += f.length();
		}
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {

			// if the merge queue is empty, sleep for a
			// while and then check again.
			long sleepTime = 5 * 1000; // 5 seconds
			while (true) {
				FileBunch fb;

				boolean sleep = false;
				synchronized (queue) {
					if (queue.size() > 0) {
						fb = (FileBunch) queue.poll();
						long time = System.currentTimeMillis();
						// time to end?
						if (this.poison == fb) {
							DEBUG("Reads queue poison!");
							do {
								signal.countDown();
							} while (signal.getCount() > 0);
							DEBUG("Returns!");
							return;
						}

						DEBUG("#files to merge: " + fb.getInputFiles().size());
						merge(fb);
						filesMerged++;

						time = System.currentTimeMillis() - time;
						double prettyTime = (double) time / 1000;

						DEBUG("Number of merged files: " + filesMerged
								+ ", left in queue: " + queue.size());
						DEBUG("Job took " + prettyTime + " seconds");

					} else {
						// queue was empty, go to sleep
						sleep = true;
					}
				}

				if (sleep) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
			} // while
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Merges the files in {@code fb}.
	 * 
	 * @param fb
	 *            the {@link FileBunch} to merge.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws ExecutionException
	 */
	private void merge(FileBunch fb) throws IOException,
			UnsupportedAudioFileException, ExecutionException {
		merge(fb.getInputFiles(), fb.getWavFile());
		if (fb.getMp3File() != null) {
			mp3encode(fb.getWavFile(), fb.getMp3File());
		}
	}

	/**
	 * Merges the files in <code>inputFiles</code> to produce
	 * <code>outputWav</code> that contains all audio in a sequence. The files
	 * in <code>inputFiles</code> will be deleted after a merge. If
	 * {@code mp3File} is not {@code null}, {@code wavFile} will be encoded
	 * into {@code mp3File} and then deleted.
	 * 
	 * @param inputFiles
	 *            list of .wav files to merge.
	 * @param wavFile
	 *            the resulting .wav file containing all audio.
	 * @param mp3File
	 *            the resulting .mp3 file if encoding is done. If
	 *            {@code mp3File} is null, no encoding will be attempted and the
	 *            merged .wav file will be the result.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws ExecutionException
	 */
	public void merge(List<File> inputFiles, File wavFile, File mp3File)
			throws IOException, UnsupportedAudioFileException,
			ExecutionException {
		FileBunch fb = new FileBunch(inputFiles, wavFile, mp3File);
		merge(fb);
	}

	/**
	 * Merges the files in <code>inputFiles</code> to produce
	 * <code>outputWav</code> that contains all audio in a sequence. The files
	 * in <code>inputFiles</code> will be deleted after a merge.
	 * 
	 * @param inputFiles
	 *            a list containing instances of <code>java.io.File</code>.
	 * @param outputWav
	 *            a <code>java.io.File</code> pointing to the desired result.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private void merge(List<File> inputFiles, File outputWav)
			throws IOException, UnsupportedAudioFileException {
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
				File f = (File) inputFiles.get(i);
				if (!f.delete()) {
					f.deleteOnExit();
				}
			}
		}
	}

	/**
	 * Uses lame to encode <code>inputWav</code> as mp3 and store the result
	 * as <code>outputMp3</code>. <code>inputWav</code> will be deleted
	 * once the encoding is done.
	 * 
	 * @param inputWav
	 *            the input wav file.
	 * @param outputMp3
	 *            the desired output mp3 file.
	 * @throws ExecutionException
	 */
	private void mp3encode(File inputWav, File outputMp3)
			throws ExecutionException {
		String lameCommand = System.getProperty("dmfc.lame.path");

		String inputFilename = inputWav.getAbsolutePath();
		String outputFilename = outputMp3.getAbsolutePath();

		String cmd[] = { lameCommand, "--quiet", "-h", "-m", "m", "-a", "-cbr",
				"-b", "32", "--resample", "22.50", inputFilename,
				outputFilename };

		int exitVal = Command.execute(cmd);
		if (exitVal != 0) {
			String str = "";
			for (int i = 0; i < cmd.length; i++) {
				str += " " + cmd[i];
			}
			System.err.println("Unable to encode using lame:");
			System.err.println(str);
			System.err.println("Exit value: " + exitVal);
			String msg = "Unable to encode using lame, lame exit code: "
					+ exitVal;
			throw new ExecutionException(msg);
		}
		if (!inputWav.delete()) {
			inputWav.deleteOnExit();
		}
	}

	/**
	 * Prints conditional ({@code org.daisy.debug}-property) debug messages.
	 * 
	 * @param msg
	 *            the message
	 */
	private static void DEBUG(String msg) {
		if (System.getProperty("org.daisy.debug") != null) {
			String prefix = "DEBUG [se_tpb_speechgen2.audio.AudioConcatQueue]: ";
			System.err.println(prefix + msg);
		}
	}
}
