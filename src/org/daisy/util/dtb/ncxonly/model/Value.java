package org.daisy.util.dtb.ncxonly.model;

/**
 * The value (textual) of a model item
 * @author Markus Gylling
 */
public class Value {
	private String mText = null;
	private Direction mDirection = null;

	public Value(String text) {
		this (text, Direction.LTR);
	}
	
	public Value(String text, Direction dir) {
		mText = text;
		mDirection = dir;
	}
	
	@Override
	public String toString() {
		return mText;
	}
	
	public enum Direction {
		LTR,
		RTL;
	}
	
}
