package org_pef_dtbook2pef.system.tasks.layout.flow;

import java.util.ArrayList;

public class Row {
	private String chars;
	private ArrayList<Marker> markers;
	private ArrayList<String> anchors;
	private int leftMargin;
	/*
	private int spaceBefore;
	private int spaceAfter;
	*/
	
	public Row(String chars) {
		this.chars = chars;
		this.markers = new ArrayList<Marker>();
		this.anchors = new ArrayList<String>();
		this.leftMargin = 0;
		/*
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		*/
	}
	
	public Row() {
		this("");
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
	
	public void addAnchor(String ref) {
		anchors.add(ref);
	}
	
	public void addMarkers(ArrayList<Marker> m) {
		markers.addAll(m);
	}

	public ArrayList<Marker> getMarkers() {
		return markers;
	}
	
	public ArrayList<String> getAnchors() {
		return anchors;
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
