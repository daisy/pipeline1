package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;


public class Row {
	private String chars;
	private ArrayList<Marker> markers;
	private int leftMargin;
	/*
	private int spaceBefore;
	private int spaceAfter;
	*/
	
	public Row(String chars) {
		this.chars = chars;
		this.markers = new ArrayList<Marker>();
		this.leftMargin = 0;
		/*
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		*/
	}

	public String getChars() {
		return chars;
	}
	
	public void appendChars(CharSequence c) {
		chars = chars.toString() + c;
	}

	public void addMarker(Marker m) {
		markers.add(m);
	}
	
	public void addMarkers(ArrayList<Marker> m) {
		markers.addAll(m);
	}

	public ArrayList<Marker> getMarkers() {
		return markers;
	}

	public void setLeftMargin(int value) {
		leftMargin = value;
	}

	public int getLeftMargin() {
		return leftMargin;
	}
/*
	public int getSpaceBefore() {
		return spaceBefore;
	}
	
	public int getSpaceAfter() {
		return spaceAfter;
	}
	
	public void setSpaceBefore(int value) {
		spaceBefore = value;
	}
	
	public void setSpaceAfter(int value) {
		spaceAfter = value;
	}
	*/

}
