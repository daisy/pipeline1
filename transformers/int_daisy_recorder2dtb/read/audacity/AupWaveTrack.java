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
package int_daisy_recorder2dtb.read.audacity;

import java.util.LinkedList;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.TransformerDelegateListener;

/**
 * An AupWaveTrack consists of a list of AupAudioFile
 * @author Markus Gylling
 */
public class AupWaveTrack extends LinkedList<AupBlockFile> {
				
	private TransformerDelegateListener mListener;

	public AupWaveTrack(TransformerDelegateListener listener) {
		super();
		this.mListener = listener;
	}

	/**
	 * Get the duration of this wave track, expressed in seconds.
	 */
	public double getDurationSeconds() {
		double s = 0;
		for(AupBlockFile af : this) {
			s += af.getDurationSeconds();
		}		
		return s; 		
	}
	
	/**
	 * Get the audiofile in which the inparam time (double seconds) occurs,
	 * relative to the timeline of the current wavetrack
	 */
	public AupBlockFile getAudioFile(double seconds) {
		double s = 0;
		for(AupBlockFile af:this) {
			s+= af.getDurationSeconds();
			if(seconds<=s) return af;
		}
		String msg = "Warning: did not find a AupBlockFile that includes " 
				+ Double.toString(seconds) + "; total wavetrack time is " + Double.toString(this.getDurationSeconds()) +". Returning last AupBlockFile.";
		
		mListener.delegateMessage(this, msg, MessageEvent.Type.DEBUG, MessageEvent.Cause.INPUT, null);
		
		return this.getLast();		
	}
	
	private static final long serialVersionUID = -1212438683565057569L;
}
