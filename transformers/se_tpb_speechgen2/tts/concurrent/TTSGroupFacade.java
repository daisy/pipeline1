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

import se_tpb_speechgen2.tts.TTSInput;
import se_tpb_speechgen2.tts.TTSOutput;

/**
 * The TTSRunner's view of the TTSGroup. Basically just an input provider
 * and an ouput receiver.
 * @author Martin Blomberg
 *
 */
public interface TTSGroupFacade {
	
	/**
	 * Returns the next TTSInput from the queue.
	 * @return the next TTSInput from the queue.
	 */
	public TTSInput getNextInput();
	
	/**
	 * Receives the TTSOutput containing the processed
	 * contents from a TTSInput.
	 * @param out the tts output.
	 */
	public void addOutput(TTSOutput out);
	
	/**
	 * Called by a TTSRunner when a slave has terminated abnormally.
	 * @param slave the terminated slave.
	 * @param myLastInput the slaves last input.
	 */
	public void slaveTerminated(TTSAdapter slave, TTSInput myLastInput);
}
