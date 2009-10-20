package org_pef_dtbook2pef.system.tasks.layout.utils;

import java.util.ArrayList;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Leader;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.impl.Row;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;

public class BlockHandler {
	private static final Character SPACE_CHAR = ' ';
	private final StringFilter filters;
	//private int currentListNumber;
	//private BlockProperties.ListType currentListType;
	private Leader currentLeader;
	private ArrayList<Row> ret;
	private BlockProperties p;
	private int available;
	private ListItem item;
	private int blockIndent;
	
	public class ListItem {
		private int number;
		private BlockProperties.ListType type;
		
		public ListItem(int number, BlockProperties.ListType type) {
			this.number = number;
			this.type = type;
		}
		
		public int getNumber() {
			return number;
		}
		
		public BlockProperties.ListType getType() {
			return type;
		}
	}
	
	public BlockHandler(StringFilter filters) {
		this.filters = filters;
		this.currentLeader = null;
		//this.currentListType = BlockProperties.ListType.NONE;
		//this.currentListNumber = 0;
		this.ret = new ArrayList<Row>();
		this.p = new BlockProperties.Builder().build();
		this.available = 0;
		this.item = null;
	}
	/*
	public void setCurrentListType(BlockProperties.ListType type) {
		currentListType = type;
	}
	
	public BlockProperties.ListType getCurrentListType() {
		return currentListType;
	}
	
	public void setCurrentListNumber(int value) {
		currentListNumber = value;
	}
	
	public int getCurrentListNumber() {
		return currentListNumber;
	}
	*/
	
	public void setListItem(int number, BlockProperties.ListType type) {
		item = new ListItem(number, type);
	}
	
	public ListItem getListItem() {
		return item;
	}

	public void setBlockProperties(BlockProperties p) {
		this.p = p;
	}
	
	public BlockProperties getBlockProperties() {
		return p;
	}
	
	public void setWidth(int value) {
		available = value;
	}
	
	public int getWidth() {
		return available;
	}
	
	public void setCurrentLeader(Leader l) {
		currentLeader = l;
	}
	
	public Leader getCurrentLeader() {
		return currentLeader;
	}
	
	public void setBlockIndent(int value) {
		this.blockIndent = value;
	}
	
	public ArrayList<Row> layoutBlock(CharSequence c, int leftMargin, LayoutMaster master) {
		return layoutBlock(c, leftMargin, true, null, master);
	}
	
	public ArrayList<Row> appendBlock(CharSequence c, int leftMargin, Row r, LayoutMaster master) {
		return layoutBlock(c, leftMargin, false, r, master);
	}

	private ArrayList<Row> layoutBlock(CharSequence c, int leftMargin, boolean firstRow, Row r, LayoutMaster master) {
		ret = new ArrayList<Row>();
		String chars = filters.filter(c.toString());
		// process first row, is it a new block or should we continue the current row?
		if (firstRow) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listNumber;
				switch (item.getType()){ //currentListType) {
					case OL:
						listNumber = "" + item.getNumber(); //currentListNumber;
						break;
					case UL:
						listNumber = "•";
						break;
					default:
						listNumber = "";
				}
				listNumber = filters.filter(listNumber);
				item = null;
				chars = newRow(listNumber, chars, available, leftMargin, p.getFirstLineIndent(), master, p);
			} else {
				chars = newRow("", chars, available, leftMargin, p.getFirstLineIndent(), master , p);
			}
		} else {
			chars = newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), chars, available, master, p);
		}
		while (LayoutTools.length(chars.toString())>0) {
			String c2 = newRow("", chars, available, leftMargin, p.getTextIndent(), master, p);
			//c2 = c2.replaceFirst("\\A\\s*", ""); // remove leading white space from input
			if (c2.length()>=chars.length()) {
				System.out.println(c2);
			}
			chars = c2;
		}
		return ret;
	}

	private String newRow(String contentBefore, String chars, int available, int margin, int indent, LayoutMaster master, BlockProperties p) {
		int thisIndent = indent + blockIndent - LayoutTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString();
		return newRow(null, margin, preText, "", chars, available, master, p);
	}

	//TODO: check leader functionality
	private String newRow(ArrayList<Marker> r, int margin, String preContent, String preTabText, String postTabText, int available, LayoutMaster master, BlockProperties p) {

		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^

		int preTextIndent = LayoutTools.length(preContent);
		int preContentPos = margin+preTextIndent;
		int preTabPos = preContentPos+LayoutTools.length(preTabText);
		int postTabTextLen = LayoutTools.length(postTabText);
		int maxLenText = available-(preContentPos);
		if (maxLenText<1) {
			throw new RuntimeException("Cannot continue layout: No space left for characters.");
		}

		int width = master.getFlowWidth();
		String tabSpace = "";
		if (currentLeader!=null) {
			int leaderPos = currentLeader.getPosition().makeAbsolute(width);
			int offset = leaderPos-preTabPos;
			int align = 0;
			switch (currentLeader.getAlignment()) {
				case LEFT:
					align = 0;
					break;
				case RIGHT:
					align = postTabTextLen;
					break;
				case CENTER:
					align = postTabTextLen/2;
					break;
			}
			if (preTabPos>leaderPos || offset - align < 0) { // if tab position has been passed or if text does not fit within row, try on a new row
				Row row = new Row(preContent + preTabText);
				row.setLeftMargin(margin);
				if (r!=null) {
					row.addMarkers(r);
					r = null;
				}
				ret.add(row);

				preContent = LayoutTools.fill(SPACE_CHAR, p.getTextIndent());
				preTextIndent = LayoutTools.length(preContent);
				preTabText = "";
				preContentPos = margin+preTextIndent;
				preTabPos = preContentPos;
				maxLenText = available-(preContentPos);
				offset = leaderPos-preTabPos;
			}
			if (offset - align > 0) {
				String leaderPattern = filters.filter(currentLeader.getPattern());
				tabSpace = LayoutTools.fill(leaderPattern, offset - align);
			} // else: leader position has been passed on an empty row or text does not fit on an empty row, ignore
		}

		maxLenText -= LayoutTools.length(tabSpace);

		BreakPoint bp = null;
		Row nr = null;
		
		if (tabSpace.length()>0) { // there is a tab...
			maxLenText -= preTabText.length();
			BreakPointHandler bph = new BreakPointHandler(postTabText);
			bp = bph.nextRow(maxLenText);
			nr = new Row(preContent + preTabText + tabSpace + bp.getHead());
		} else { // no tab
			BreakPointHandler bph = new BreakPointHandler(preTabText + postTabText);
			bp = bph.nextRow(maxLenText);
			nr = new Row(preContent + bp.getHead());
		}
		// discard leader
		currentLeader = null;

		assert nr != null;
		if (r!=null) {
			nr.addMarkers(r);
		}
		nr.setLeftMargin(margin);

		ret.add(nr);
		return bp.getTail();
	}
}