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
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaOutput;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CompoundField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CurrentPageField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.MarkerReferenceField;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilterHandler;
import org_pef_dtbook2pef.system.tasks.layout.utils.BreakPointHandler;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;
import org_pef_dtbook2pef.system.tasks.layout.utils.BreakPointHandler.BreakPoint;

/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author joha
 * TODO: implement initial-page-number attribute
 * TODO: content-before (list-item) does align properly with nested block elements (aligns against left margin) 
 * TODO: fix recursive keep problem
 */
public class DefaultLayoutPerformer implements Flow, LayoutPerformer {
	private static final Character SPACE_CHAR = ' '; //'\u2800'
	private int leftMargin;
	private int rightMargin;
	private FlowStruct flowStruct;
	private Stack<BlockProperties> context;
	private boolean firstRow;
	private int currentListNumber;
	private BlockProperties.ListType currentListType;
	private Leader currentLeader;
	private HashMap<String, LayoutMaster> masters;
	private StringFilterHandler filters;

	public static class Builder {
		HashMap<String, LayoutMaster> masters;
		StringFilterHandler filters;
		
		public Builder() {
			masters = new HashMap<String, LayoutMaster>();
			setTextFilterHandler(null);
		}
		
		/**
		 * Set text filter handler, may be null
		 * @param filters
		 */
		public Builder setTextFilterHandler(StringFilterHandler filters) {
			if (filters == null) {
				this.filters = new StringFilterHandler();
			} else {
				this.filters = filters;
			}
			return this;
		}
		
		public Builder addLayoutMaster(String key, LayoutMaster value) {
			masters.put(key, value);
			return this;
		}

		public DefaultLayoutPerformer build() {
			return new DefaultLayoutPerformer(this);
		}
	}
	
	/**
	 * Create a new flow
	 * @param flowWidth the width of the flow, in chars
	 */
	private DefaultLayoutPerformer(Builder builder) {
		this.masters = builder.masters;
		this.filters = builder.filters;
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		this.currentListType = BlockProperties.ListType.NONE;
		this.currentLeader = null;
		this.flowStruct = new FlowStruct(); //masters
	}

	/**
	public abstract int getWidth();
	public abstract void close();
	public abstract int availableRows();
	public abstract void addRow(CharSequence chars);
	public abstract void newPage();*/

	public void addChars(CharSequence chars) {
		if (context.size()==0) return; // TODO: Fix this, it's really the xml handler that outputs character where there shouldn't be any...
		chars = filters.filter(chars.toString());
		int available = masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getPageWidth() - rightMargin;
		BlockProperties p = context.peek();
		if (available < 1) {
			throw new RuntimeException("Cannot continue layout: No space left for characters.");
		}
		// process first row, is it a new block or should we continue the current row?
		if (firstRow) {
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
				listNumber = filters.filter(listNumber);
				chars = newRow(listNumber, chars, available, leftMargin, p.getFirstLineIndent());
			} else {
				chars = newRow(chars, available, leftMargin, p.getFirstLineIndent());
			}
			firstRow = false;
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

	public void insertMarker(Marker m) {
		flowStruct.getCurrentSequence().getCurrentGroup().addMarker(m);
	}

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
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());		
		context.push(p);
		leftMargin += p.getLeftMargin();
		rightMargin += p.getRightMargin();
		firstRow = true;
	}
	
	public void endBlock() {
		BlockProperties p = context.pop();
		flowStruct.getCurrentSequence().getCurrentGroup().addSpaceAfter(p.getBottomMargin());
		if (context.size()>0) {
			FlowGroup c = flowStruct.getCurrentSequence().newFlowGroup();
			c.setKeepType(context.peek().getKeepType());
			c.setKeepWithNext(context.peek().getKeepWithNext());
		}
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		firstRow = true;
	}

	public void newSequence(SequenceProperties p) {
		flowStruct.newSequence(p);
	}

	public void insertLeader(Leader leader) {
		currentLeader = leader;
	}
	
	public void newLine() {
		BlockProperties p = context.peek();
		int thisMargin = leftMargin + p.getTextIndent();
		flowStruct.getCurrentSequence().getCurrentGroup().pushRow(new Row(LayoutTools.fill(SPACE_CHAR, thisMargin)));
	}

	private int getKeepHeight(FlowGroup[] groupA, int gi) {
		int keepHeight = groupA[gi].getSpaceBefore()+groupA[gi].toArray().length;
		if (groupA[gi].getKeepWithNext()>0 && gi+1<groupA.length) {
			keepHeight += groupA[gi].getSpaceAfter()+groupA[gi+1].getSpaceBefore()+groupA[gi].getKeepWithNext();
			switch (groupA[gi+1].getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(groupA, gi+1);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}
	
	public PagedMediaOutput layout(PagedMediaOutput pm) {
		PageStruct ps = new PageStruct(); //masters
		for (FlowSequence seq : flowStruct.toArray()) {
			ps.newSection(masters.get(seq.getSequenceProperties().getMasterName())); //seq.getSequenceProperties().getMasterName(), 
			ps.newPage();
			FlowGroup[] groupA = seq.toArray();
			for (int gi = 0; gi<groupA.length; gi++) {
				int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (groupA[gi].getBreakBeforeType()) {
					case PAGE:
						if (ps.countRowsOnCurrentPage()>0) {
							ps.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (groupA[gi].getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(groupA, gi);
						if (ps.countRowsOnCurrentPage()>0 && keepHeight>height-ps.countRowsOnCurrentPage() && keepHeight<=height) {
							ps.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (groupA[gi].getSpaceBefore()+groupA[gi].getSpaceAfter()>=height) {
					throw new RuntimeException("Group margins too large to fit on an empty page.");
				} else if (groupA[gi].getSpaceBefore()+1>height-ps.countRowsOnCurrentPage()) {
					ps.newPage();
				}
				for (int i=0; i<groupA[gi].getSpaceBefore();i++) {
					ps.newRow(new Row(""));
				}
				ps.insertMarkers(groupA[gi].getGroupMarkers());
				for (Row row : groupA[gi].toArray()) {
					ps.newRow(row);
				}
				if (groupA[gi].getSpaceAfter()>=height-ps.countRowsOnCurrentPage()) {
					ps.newPage();
				} else {
					for (int i=0; i<groupA[gi].getSpaceAfter();i++) {
						ps.newRow(new Row(""));
					}
				}
			}
		}
		/*
		ArrayList<TabStopString> test = new ArrayList<TabStopString>();
		test.add(new TabStopString(filters.filter("te1"), 2));
		test.add(new TabStopString(filters.filter("te2"), 15, TabStopString.Alignment.CENTER));
		test.add(new TabStopString(filters.filter("te3"), 30, TabStopString.Alignment.RIGHT, filters.filter(".")));*/
		for (PageSequence s : ps.getSequenceArray()) {
			for (Page p : s.getPages()) {
				LayoutMaster lm = s.getLayoutMaster();
				ArrayList<Row> header = new ArrayList<Row>();
				int pagenum = p.getPageIndex()+1;
				for (ArrayList<Object> row : lm.getHeader(pagenum)) {
					header.add(new Row(distribute(row, lm.getPageWidth(), " ", p)));
					//header.add(new Row(LayoutTools.distribute(test)));
				}
				ArrayList<Row> footer = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getFooter(pagenum)) {
					footer.add(new Row(distribute(row, lm.getPageWidth(), " ", p)));
				}
				p.setHeader(header);
				p.setFooter(footer);
			}
		}

		for (PageSequence s : ps.getSequenceArray()) {
			pm.newSection(s.getLayoutMaster());
			for (Page p1 : s.getPages()) {
				pm.newPage();
				for (Row row : p1.getRows()) {
					pm.newRow(row.getChars());
				}
			}
		}
		return pm;
	}

	private String resolveCurrentPageField(CurrentPageField f, Page p) {
		int pagenum = p.getPageIndex() + 1;
		return f.style(pagenum);
	}
	
	private String resolveCompoundField(CompoundField f, Page p) {
		StringBuffer sb = new StringBuffer();
		for (Object f2 : f) {
			sb.append(resolveField(f2, p));
		}
		return sb.toString();
	}
	
	private String resolveField(Object field, Page p) {
		if (field instanceof CompoundField) {
			return resolveCompoundField((CompoundField)field, p);
		} else if (field instanceof MarkerReferenceField) {
			MarkerReferenceField f2 = (MarkerReferenceField)field;
			return findMarker(p, f2);
		} else if (field instanceof CurrentPageField) {
			return resolveCurrentPageField((CurrentPageField)field, p);
		} else {
			return field.toString();
		}
	}

	private String distribute(ArrayList<Object> chunks, int width, String padding, Page p) {
		ArrayList<String> chunkF = new ArrayList<String>();
		for (Object f : chunks) {
			chunkF.add(filters.filter(resolveField(f, p)));
		}
		return LayoutTools.distribute(chunkF, width, padding, LayoutTools.DistributeMode.EQUAL_SPACING);
		
		/*
		int chunksLength = 0;
		ArrayList<String> chunkF = new ArrayList<String>();
		for (String s : chunks) {
			String fs = filters.filter(s);
			chunksLength += fs.length();
			chunkF.add(fs);
		}
		int totalSpace = (width-chunksLength);
		int parts = chunks.size()-1;
		double target = totalSpace/(double)parts;
		int used = 0;
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<chunkF.size(); i++) {
			if (i>0) {
				int spacing = (int)Math.round(i * target) - used;
				used += spacing;
				sb.append(LayoutTools.fill(fillpattern, spacing));
			}
			sb.append(chunkF.get(i));
		}
		assert sb.length()==width;
		return sb.toString();*/
	}
	
	public String findMarker(Page page, MarkerReferenceField markerRef) {
		int dir = 1;
		int index = 0;
		int count = 0;
		ArrayList<Marker> m = page.getMarkers();
		if (markerRef.getSearchDirection() == MarkerReferenceField.MarkerSearchDirection.BACKWARD) {
			dir = -1;
			index = m.size()-1;
		}
		while (count < m.size()) {
			Marker m2 = m.get(index);
			if (m2.getName().equals(markerRef.getName())) {
				return m2.getValue();
			}
			index += dir; 
			count++;
		}
		int nextPage = page.getPageIndex() + dir;
		if (markerRef.getSearchScope() == MarkerReferenceField.MarkerSearchScope.SEQUENCE && nextPage < page.getParent().getPages().size() && nextPage >= 0) {
			Page next = page.getParent().getPages().get(nextPage);
			return findMarker(next, markerRef);
		}
		return "";
	}
}
