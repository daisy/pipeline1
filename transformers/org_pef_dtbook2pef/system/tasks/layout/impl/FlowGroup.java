package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;

public class FlowGroup {
	private Stack<Row> rows;
	private int spaceBefore;
	private int spaceAfter;
	private ArrayList<Marker> groupMarkers;
	private BlockProperties.BreakBeforeType breakBefore;
	
	public FlowGroup() {
		this.rows = new Stack<Row>();
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.groupMarkers = new ArrayList<Marker>();
		this.breakBefore = BlockProperties.BreakBeforeType.AUTO;
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
	
	public void addSpaceBefore(int spaceBefore) {
		this.spaceBefore += spaceBefore;
	}
	
	public void addSpaceAfter(int spaceAfter) {
		this.spaceAfter += spaceAfter;
	}
	
	public void setBreakBeforeType(BlockProperties.BreakBeforeType breakBefore) {
		this.breakBefore = breakBefore;
	}
	
	public boolean isEmpty() {
		return rows.size()==0;
	}

}
