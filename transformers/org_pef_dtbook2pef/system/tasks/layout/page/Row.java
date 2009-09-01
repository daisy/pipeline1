package org_pef_dtbook2pef.system.tasks.layout.page;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;


public class Row {
	private CharSequence chars;
	private ArrayList<Marker> markers;
	/*
	private int spaceBefore;
	private int spaceAfter;
	private int leftMargin;*/
	
	public Row(CharSequence chars) {
		this.chars = chars;
		this.markers = new ArrayList<Marker>();
		/*
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.leftMargin = 0;*/
	}

	public CharSequence getChars() {
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

	/*
	public void setLeftMargin(int value) {
		leftMargin = value;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

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
