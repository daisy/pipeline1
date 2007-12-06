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
