/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package se_tpb_speechgen2.tts.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import se_tpb_speechgen2.tts.TTS;
import se_tpb_speechgen2.tts.TTSAnnouncement;
import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.TTSInput;
import se_tpb_speechgen2.tts.TTSOutput;
import se_tpb_speechgen2.tts.TTSSyncPoint;

/**
 * This is an implementation of the TTS interface. It covers the case
 * with several tts instances speaking the same language. 
 * 
 * TTSGroup first recieves the input. That input is added to a queue.
 * When no more input will be added, TTSGroup can start deliver its
 * output, i e. the synthesized audio.
 * 
 * Calling <code>start()</code> will initialize all slaves and put
 * them to work before their first output is actually requested. However,
 * if more input is added, an exception will be thrown.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSGroup implements TTS, TTSGroupFacade {

	// housekeeping
	private int spCounter = 0;					// number of sync points to process				
	private Map<TTSRunner, Thread> mSlaves = 	// the slaves (runners and threads)
		new HashMap<TTSRunner, Thread>();
	private List<TTSAdapter> mTTSInstances;		// list of ttsadapters created by the TTSBuilder.
	
	private Queue<TTSInput> mTTSInput = 		// input queue where new jobs are put by the transformer
		new LinkedList<TTSInput>();
	private Map<Integer, TTSOutput> mTTSOutput = // queue-number system for output, deliver output in... 
		new HashMap<Integer, TTSOutput>();		 // ...the same order as input
	private Integer mOutputIndex = null;		// output queue counter
	private static boolean DEBUG = false;
	
	
	/**
	 * Constructs a new instance given a list of tts adapters to run
	 * concurrently.
	 * 
	 * @param ttsInstances the tts process wrappers.
	 */
	public TTSGroup(List<TTSAdapter> ttsInstances) {
		// check for errornous input
		if (null == ttsInstances) {
			String msg = ttsInstances + " not a legal argument for TTSGroup(List)";
			throw new IllegalArgumentException(msg);
		}
		
		if (ttsInstances.size() == 0) {
			String msg = ttsInstances + " not a legal argument for TTSGroup(List), size = " + (ttsInstances.size());
			throw new IllegalArgumentException(msg);
		}
		
		for (Iterator it = ttsInstances.iterator(); it.hasNext(); ) {
			TTSAdapter tmp = (TTSAdapter) it.next();
			if (null == tmp) {
				String msg = tmp + " is not a valid TTSInstance.";
				throw new IllegalArgumentException(msg);
			}
		}
		
		mTTSInstances = ttsInstances;
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#start()
	 */
	public void start()  {
		init();
	}
		
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#addAnnouncements(java.util.List, javax.xml.namespace.QName, java.io.File)
	 */
	public void addAnnouncements(List announcement, QName attrName, File outputFile) { 		
		addInput(new TTSAnnouncement(announcement, attrName, outputFile, spCounter++));
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#addSyncPoint(org.w3c.dom.Document, java.io.File)
	 */
	public void addSyncPoint(Document scope, File outputFile) {
		addInput(new TTSSyncPoint(scope, outputFile, spCounter++));
	}
	
	/**
	 * Puts the TTSInput in the queue.
	 * @param in the tts input.
	 */
	private void addInput(TTSInput in) {
		if (mOutputIndex != null) {
			String msg = "Not legal to add input after output " +
					"has been requested: " + 
					(in.isAnnouncement() ? "Announcement" : "SyncPoint");
			throw new IllegalStateException(msg);
		}
		mTTSInput.add(in);
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#close()
	 */
	public void close() throws IOException, TTSException {
		if (0 == mSlaves.size()) {
			DEBUG("Number of slaves: " + mSlaves.size()); 
			return;
		}
		
		List<Exception> exceptions = new ArrayList<Exception>();
		
		DEBUG("Await slave termination...");
		int i = 0;
		for (Iterator iter = mSlaves.keySet().iterator(); iter.hasNext();) {
			
			DEBUG("Processing slave " + i);
			TTSRunner element = (TTSRunner) iter.next();
			
			try {
				element.close();
			} catch (TTSException e) {
				exceptions.add(e);
			}
			DEBUG("Terminated slave " + i);
			
			Thread thread = (Thread) mSlaves.get(element);
			
			DEBUG("Await termination of slave " + i);
			boolean success = true;
			try {
				thread.join();
			} catch (InterruptedException e) {
				success = false;
				DEBUG("Exception joining slave " + i);
				e.printStackTrace();
			}
			
			DEBUG("Slave " + i + " terminated? " + success);
			i++;
		}
		
		mSlaves.clear();
		DEBUG("TTS terminated, all slaves too.");
		
		if (exceptions.size() > 0) {
			String msg = "Exceptions occurred while generating the book. I better stop now than " +
					"returning an invalid book.";
			DEBUG(msg);
			throw (TTSException) exceptions.get(0);
		}
		
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#getNext()
	 */
	public synchronized TTSOutput getNext() {
		// first request
		if (null == mOutputIndex) {
			init();
		}
		
		
		// no more results to wait for
		if (!hasNext()) {
			return null;
		}
		
		// wait for the "right" output to arrive
		while (null == mTTSOutput.get(mOutputIndex)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		TTSOutput to = (TTSOutput) mTTSOutput.remove(mOutputIndex);
		mOutputIndex = new Integer(mOutputIndex.intValue() + 1);
		
		notifyAll();
		return to;
	}

	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.TTS#hasNext()
	 */
	public synchronized boolean hasNext() {
		// we haven't started yet!
		if (null == mOutputIndex) {
			return true;
		}
		
		return mOutputIndex.intValue() < spCounter;
	}
	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSGroupFacade#getNextInput()
	 */
	public synchronized TTSInput getNextInput() {
		// TODO:
		// this could be done smarter.
		// I was thinking of a BlockingPriorityQueue
		// that is filled with "poison" when all jobs
		// are finished.
		return (TTSInput) mTTSInput.poll();
	}
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSGroupFacade#addOutput(se_tpb_speechgen2.tts.TTSOutput)
	 */
	public synchronized void addOutput(TTSOutput out) {
		Integer key = new Integer(out.getNumber());
		mTTSOutput.put(key, out);
		notifyAll();
	}
	
	
	/* (non-Javadoc)
	 * @see se_tpb_speechgen2.tts.concurrent.TTSGroupFacade#slaveTerminated(se_tpb_speechgen2.tts.concurrent.TTSInstance, se_tpb_speechgen2.tts.TTSInput)
	 */
	public synchronized void slaveTerminated(TTSAdapter slave, TTSInput myLastInput) {
		// TODO
		DEBUG("#slaveTerminated: ");
		
		// makes hasNext() return false;
		spCounter = -1;
		
		// makes the currently blocking call receive "something"
		mTTSOutput.put(mOutputIndex, new TTSOutput(myLastInput.getFile(), myLastInput.getNumber(), 0));

		// no more work 
		mTTSInput.clear();
		
		// wake up the master thread
		notifyAll();
	}
	
	
	
	/**
	 * Initializes the TTSGroup, variables and so on.
	 */
	private void init() {
		if (mOutputIndex != null) {
			return;
		}
		
		mOutputIndex = new Integer(0);
		int i = 0;
		for (Iterator<TTSAdapter> it = mTTSInstances.iterator(); it.hasNext(); ) {
			TTSRunner slave = new TTSRunner(this, it.next());
			Thread wrapper = new Thread(slave, "TTS Slave-" + i++);
			mSlaves.put(slave, wrapper);
			wrapper.start();
		}
	}

	private void DEBUG(String msg) {
		if (DEBUG) {
			String base = "DEBUG (" + getClass().getName() + "): ";
			System.err.println(base + msg);
		}
	}
}
