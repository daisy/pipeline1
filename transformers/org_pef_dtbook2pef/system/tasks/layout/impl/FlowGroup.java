package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;

public class FlowGroup {
	private Stack<Row> rows;
	private int spaceBefore;
	private int spaceAfter;
	private ArrayList<Marker> groupMarkers;
	private BlockProperties.BreakBeforeType breakBefore;
	private BlockProperties.KeepType keep;
	private int keepWithNext;
	
	public FlowGroup() {
		this.rows = new Stack<Row>();
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.groupMarkers = new ArrayList<Marker>();
		this.breakBefore = BlockProperties.BreakBeforeType.AUTO;
		this.keep = BlockProperties.KeepType.AUTO;
		this.keepWithNext = 0;
	}
	
	public void pushRow(Row row) {
		rows.push(row);
	}
	
	public Row popRow() {
		return rows.pop();
	}
	
	public void addMarker(Marker m) {
		if (isEmpty()) {
			groupMarkers.add(m);
		} else {
			rows.peek().addMarker(m);
		}
	}
	
	/**
	 * Get markers that are not attached to a row, i.e. markers that proceeds any text contents
	 * @return returns markers that proceeds this FlowGroups text contents
	 */
	public ArrayList<Marker> getGroupMarkers() {
		return groupMarkers;
	}
	
	public Row[] toArray() {
		Row[] ret = new Row[rows.size()];
		return rows.toArray(ret);
	}
	
	public int getSpaceBefore() {
		return spaceBefore;
	}
	
	public int getSpaceAfter() {
		return spaceAfter;
	}
	
	public BlockProperties.BreakBeforeType getBreakBeforeType() {
		return breakBefore;
	}
	
	public BlockProperties.KeepType getKeepType() {
		return keep;
	}
	
	public int getKeepWithNext() {
		return keepWithNext;
	}
	
	public void addSpaceBefore(int spaceBefore) {
		this.spaceBefore += spaceBefore;
	}
	
	public void addSpaceAfter(int spaceAfter) {
		this.spaceAfter += spaceAfter;
	}
	
	public void setBreakBeforeType(BlockProperties.BreakBeforeType breakBefore) {
		this.breakBefore = breakBefore;
	}
	
	public void setKeepType(BlockProperties.KeepType keep) {
		this.keep = keep;
	}
	
	public void setKeepWithNext(int keepWithNext) {
		this.keepWithNext = keepWithNext;
	}
	
	public boolean isEmpty() {
		return rows.size()==0;
	}

}
