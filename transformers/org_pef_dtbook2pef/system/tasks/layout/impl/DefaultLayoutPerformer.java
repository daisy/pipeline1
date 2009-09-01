package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Flow;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.flow.Leader;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.flow.SequenceProperties;
import org_pef_dtbook2pef.system.tasks.layout.impl.BreakPointHandler.BreakPoint;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.Page;
import org_pef_dtbook2pef.system.tasks.layout.page.PageSequence;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaOutput;
import org_pef_dtbook2pef.system.tasks.layout.page.Row;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;

/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author joha
 *
 */
public class DefaultLayoutPerformer implements Flow, LayoutPerformer {
	private static final Character SPACE_CHAR = ' '; //'\u2800'
	private int leftMargin;
	private int rightMargin;
	private FlowStruct flowStruct;
	private Stack<BlockProperties> context;
	private boolean newRow;
	private int currentListNumber;
	private BlockProperties.ListType currentListType;
	private Leader currentLeader;
	private HashMap<String, LayoutMaster> masters;

	
	/**
	 * Create a new flow
	 * @param flowWidth the width of the flow, in chars
	 */
	public DefaultLayoutPerformer(HashMap<String, LayoutMaster> masters) {
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		this.flowStruct = new FlowStruct(masters);
		this.currentListType = BlockProperties.ListType.NONE;
		this.currentLeader = null;
		this.masters = masters;
	}

	/**
	public abstract int getWidth();
	public abstract void close();
	public abstract int availableRows();
	public abstract void addRow(CharSequence chars);
	public abstract void newPage();*/

	/**
	 * Add chars to flow
	 * @param chars the characters to add to the flow
	 */
	public void addChars(CharSequence chars) {
		if (context.size()==0) return; // TODO: Fix this, it's really the xml handler that outputs character where there shouldn't be any...
		int available = flowStruct.getLayoutMaster(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getPageWidth() - rightMargin;
		BlockProperties p = context.peek();
		if (available < 1) {
			throw new RuntimeException("Cannot continue layout: No space left for characters.");
		}
		// process first row, is it a new block or should we continue the current row?
		if (newRow) {
			// add to left margin
			if (currentListType!=BlockProperties.ListType.NONE) {
				String listNumber;
				switch (currentListType) {
					case OL:
						listNumber = "" + currentListNumber;
						break;
					case UL:
						listNumber = "*";
						break;
					default:
						listNumber = "";
				}
				chars = newRow(listNumber, chars, available, leftMargin, p.getFirstLineIndent());
			} else {
				chars = newRow(chars, available, leftMargin, p.getFirstLineIndent());
			}
			newRow = false;
		} else {
			Row r = flowStruct.getCurrentSequence().getCurrentGroup().popRow();
			CharSequence cs = r.getChars();
			chars = cs.toString() + chars;
			chars = newRow(r.getMarkers(), chars, available, 0, 0); // indent already added, don't add it again
		}
		while (chars.length()>0) {
			chars = newRow(chars, available, leftMargin, p.getTextIndent());
		}
	}

	private CharSequence newRow(ArrayList<Marker> m, CharSequence chars, int available, int margin, int indent) {
		return newRow(m, "", chars, available ,margin, indent);
	}

	private CharSequence newRow(CharSequence chars, int available, int margin, int indent) {
		return newRow(null, "", chars, available, margin, indent);
	}
	
	private CharSequence newRow(String contentBefore, CharSequence chars, int available, int margin, int indent) {
		return newRow(null, contentBefore, chars, available, margin, indent);
	}

	private CharSequence newRow(ArrayList<Marker> r, String contentBefore, CharSequence chars, int available, int margin, int indent) {
		// Calculate breakpoint
		int breakPoint = available-(margin+indent);
		String charsStr = chars.toString();
		/*
		String head;
		String tail;
		assert charsStr.length()==charsStr.codePointCount(0, charsStr.length());
		if (charsStr.length()>breakPoint) {
			int strPos = -1;
			int len = 0;
			for (char c : charsStr.toCharArray()) {
				strPos++;
				switch (c) {
					case '\u00ad': 
						break;
					default:
						len++;
				}
				if (len>=breakPoint) {
					break;
				}
			}
			assert strPos<charsStr.length();
			
			int tailStart;
			
			/*if (strPos>=charsStr.length()-1) {
				head = charsStr.substring(0, strPos);
				System.out.println(head);
				tailStart = strPos;
			} else *//*
			// check next character to see if it can be removed.
			if (strPos==charsStr.length()-1) {
				head = charsStr.substring(0, strPos+1);
				tailStart = strPos+1;
			} else if (charsStr.charAt(strPos+1)==' ') {
				head = charsStr.substring(0, strPos+1);
				tailStart = strPos+2;
			} else { // back up
				int i=strPos;
whileLoop:		while (i>=0) {
					switch (charsStr.charAt(i)) {
						case ' ' : case '-' : case '\u00ad' : 
							break whileLoop;
					}
					i--;
				}
				if (i<0) { // no breakpoint found, break hard 
					head = charsStr.substring(0, strPos+1);
					tailStart = strPos+1;
				} else if (charsStr.charAt(i)==' ') { // ignore space at breakpoint
					head = charsStr.substring(0, i);
					tailStart = i+1;
				} else if (charsStr.charAt(i)=='\u00ad'){ // convert soft hyphen to hard hyphen 
					head = charsStr.substring(0, i) + '-';
					tailStart = i+1;
				} else {
					head = charsStr.substring(0, i+1);
					tailStart = i+1;
				}
			}
			if (charsStr.length()>tailStart) {
				tail = charsStr.substring(tailStart);
			} else {
				tail = "";
			}
		} else {
			head = charsStr;
			tail = "";
		}*/
		BreakPointHandler bph = new BreakPointHandler(charsStr);
		BreakPoint bp = bph.nextRow(breakPoint);
		int thisIndent = indent - contentBefore.length();
		Row nr = new Row(LayoutTools.fill(SPACE_CHAR, margin).toString() + contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString() + bp.getHead().replaceAll("\u00ad", ""));
		//nr.setLeftMargin(margin);
		if (r!=null) {
			nr.addMarkers(r);
		}
		flowStruct.getCurrentSequence().getCurrentGroup().pushRow(nr);
		return bp.getTail();
	}

	/**
	 * Insert a marker at the current position in the flow
	 * @param name a name for the marker
	 * @param value a value for the marker
	 */
	public void insertMarker(Marker m) {
		flowStruct.getCurrentSequence().getCurrentGroup().addMarker(m);
	}

	/**
	 * Begin a new block with the supplied BlockProperties.
	 * 
	 * @param p the BlockProperties of the new block
	 */
	public void startBlock(BlockProperties p) {
		if (context.size()>0) {
			currentListType = context.peek().getListType();
			if (currentListType!=BlockProperties.ListType.NONE) {
				currentListNumber = context.peek().nextListNumber();
			}
		}
		FlowGroup c = flowStruct.getCurrentSequence().newFlowGroup();
		c.addSpaceBefore(p.getTopMargin());
		c.setBreakBeforeType(p.getBreakBeforeType());
		context.push(p);
		leftMargin += p.getLeftMargin();
		rightMargin += p.getRightMargin();
		newRow = true;
	}
	
	/**
	 * End
	 */
	public void endBlock() {
		BlockProperties p = context.pop();
		flowStruct.getCurrentSequence().getCurrentGroup().addSpaceAfter(p.getBottomMargin());
		if (context.size()>0) {
			flowStruct.getCurrentSequence().newFlowGroup();
		}
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		newRow = true;
	}

	/*
	public FlowStruct getStruct() {
		return flowStruct;
	}*/

	public void newSequence(SequenceProperties p) {
		flowStruct.newSequence(p);
	}

	@Override
	public void insertLeader(Leader leader) {
		currentLeader = leader;
	}

	public PagedMediaOutput layout(PagedMediaOutput pm) {
		PageStruct ps = new PageStruct(masters);
		for (FlowSequence seq : flowStruct.toArray()) {
			ps.newSection(seq.getSequenceProperties().getMasterName(), flowStruct.getLayoutMaster(seq.getSequenceProperties().getMasterName()));
			ps.newPage();
			for (FlowGroup group : seq.toArray()) {
				switch (group.getBreakBeforeType()) {
					case PAGE:
						if (ps.countRowsOnCurrentPage()>0) {
							ps.newPage();
						}
						break;
					case AUTO:default:;
				}
				int height = ps.getCurrentLayoutMaster().getFlowHeight();
				if (group.getSpaceBefore()+group.getSpaceAfter()>=height) {
					throw new RuntimeException("Group margins too large to fit on an empty page.");
				} else if (group.getSpaceBefore()+1>height-ps.countRowsOnCurrentPage()) {
					ps.newPage();
				}
				for (int i=0; i<group.getSpaceBefore();i++) {
					ps.newRow(new Row(""));
				}
				ps.insertMarkers(group.getGroupMarkers());
				for (Row row : group.toArray()) {
					ps.newRow(row);
				}
				if (group.getSpaceAfter()>=height-ps.countRowsOnCurrentPage()) {
					ps.newPage();
				} else {
					for (int i=0; i<group.getSpaceAfter();i++) {
						ps.newRow(new Row(""));
					}
				}
			}
		}
		for (PageSequence s : ps.getSequenceArray()) {
			for (Page p : s.getPages()) {
				p.setHeader(s.getLayoutMaster().getHeader(p));
				p.setFooter(s.getLayoutMaster().getFooter(p));
			}
		}

		for (PageSequence s : ps.getSequenceArray()) {
			pm.newSection(s.getLayoutMaster());
			for (Page p1 : s.getPages()) {
				pm.newPage();
				for (Row row : p1.getRows()) {
					pm.newRow(row);
				}
			}
		}
		return pm;
	}

}
