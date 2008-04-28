/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
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
package org.daisy.util.dtb.ncxonly.model;

import java.util.LinkedList;
import java.util.List;

/**
 * One item in the list of a Model.
 * <p>An Item has a Semantic, a textual Value, and a list (1-n) of AudioClip.</p>
 * @author Markus Gylling
 */
public class Item {
	private Semantic mSemantic = null;
	private Value mValue = null;
	private List<AudioClip> mAudioClips = null;
	
	public Item(Semantic semantic, Value value, List<AudioClip> clips) {
		mSemantic = semantic;
		mValue = value;
		mAudioClips = new LinkedList<AudioClip>();		
		mAudioClips.addAll(clips);
	}
	
	public Semantic getSemantic() {
		return mSemantic;
	}

	public void setSemantic(Semantic semantic) {
		mSemantic = semantic;
	}

	public Value getValue() {
		return mValue;
	}

	public  void setValue(Value value) {
		mValue = value;
	}

	public List<AudioClip> getAudioClips() {
		return mAudioClips;
	}

	public void addAudioClip(AudioClip clip) {
		mAudioClips.add(clip);
	}
		
	public double getDurationSeconds() {
		double s = 0;
		for(AudioClip ac : mAudioClips) {
			s+=ac.getDurationSeconds();			
		}
		return s;
	}
	
	
	
	
}
