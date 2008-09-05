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
package se_tpb_speechgen2.tts.concurrent;

import java.io.IOException;

import org.daisy.pipeline.exception.TransformerRunException;

import se_tpb_speechgen2.tts.TTSException;
import se_tpb_speechgen2.tts.TTSInput;
import se_tpb_speechgen2.tts.TTSOutput;

/**
 * Class polling the tts input-queue for next input and uses an 
 * implementation of TTSAdapter to generate audio for that input. 
 * Quits when null is returned from the queue.
 * 
 * @author Martin Blomberg
 *
 */
public class TTSRunner implements Runnable {

	private TTSGroupFacade mCallback;	// a callback-handle to the tts group
	private TTSAdapter mSlave;			// the working slave


	/**
	 * Constructs an instance given a group facade and a tts adapter.
	 * @param tgf
	 * @param instance
	 */
	public TTSRunner(TTSGroupFacade tgf, TTSAdapter instance) {
		mCallback = tgf;
		mSlave = instance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		TTSInput in = null;
		long duration = 0;
			while ((in = mCallback.getNextInput()) != null) {
				try {
				if (in.isAnnouncement()) {
					duration = mSlave.read(in.getAnnouncements(), in.getQName(), in.getFile());		
				} else {
					duration = mSlave.read(in.getSyncPoint(), in.getFile());
				}
				mCallback.addOutput(new TTSOutput(in.getFile(), in.getNumber(), duration));
				}  catch (Throwable t) {
					mCallback.slaveTerminated(mSlave, in, t);
				}
			}
	}

	/**
	 * Closes the slave.
	 * @throws TTSException
	 */
	public void close() throws TTSException {
		try {
			mSlave.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Thread.currentThread().getName() + "";
	}
}
