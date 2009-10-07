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
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilterFactory;
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
	private StringFilter filters;

	public static class Builder {
		HashMap<String, LayoutMaster> masters;
		StringFilterFactory filtersFactory;
		
		public Builder() {
			masters = new HashMap<String, LayoutMaster>();
			setStringFilterFactory(null);
		}
		
		/**
		 * Set text filter handler, may be null
		 * @param filters
		 */
		public Builder setStringFilterFactory(StringFilterFactory filters) {
			if (filters == null) {
				this.filtersFactory = StringFilterFactory.newInstance();
			} else {
				this.filtersFactory = filters;
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
		this.filters = builder.filtersFactory.newStringFilter();
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
						listNumber = "•";
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
			chars = newRow(r.getMarkers(), r.getChars().toString(), chars, available); // indent already added, don't add it again
		}
		while (LayoutTools.length(chars.toString())>0) {
			chars = newRow(chars, available, leftMargin, p.getTextIndent());
		}
	}

	private CharSequence newRow(CharSequence chars, int available, int margin, int indent) {
		return newRow("", chars, available, margin, indent);
	}
	
	private CharSequence newRow(String contentBefore, CharSequence chars, int available, int margin, int indent) {
		int thisIndent = indent - LayoutTools.length(contentBefore);
		String preText = LayoutTools.fill(SPACE_CHAR, margin).toString() + contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString();
		assert thisIndent >= 0;
		return newRow(null, preText, chars, available);
	}

	//TODO: in order to implement leader properly, margin (space chars) and proper text must be separated - or leader functionality moved to addChars
	private CharSequence newRow(ArrayList<Marker> r, String preText, CharSequence chars, int available) {
		String charsStr = chars.toString();
		/*
		 * om tabläget paserats {
		 * 	ny rad
		 *  försök igen
		 * } annars {
		 * 	om höger {
		 * 		om hela strängen får plats {
		 * 			space(strängen.length-av) + strängen
		 * 		} else {
		 * 			ignorera tabläget
		 * 		}
		 * 	}
		 * om vänster {
		 * 	space(till tabläget) + bryt som vanligt
		 * } om center {
		 *  bryt som vanligt (av) och space(det som blir över)
		 * }
		 * }
		 * 
		 */
		int width = masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getPageWidth();
		int col = LayoutTools.length(preText);
		int maxLen = available-(col);

		String tabSpace = "";
		if (currentLeader!=null) {
			/*
			if (currentLeader.getPosition().makeAbsolute(width)>=col) {
				flowStruct.getCurrentSequence().getCurrentGroup().pushRow(new Row(preText));
				preText = "";
				breakPoint = available;
				col = 0;
			}*/
			int leaderPos = currentLeader.getPosition().makeAbsolute(width);
			String leaderPattern = filters.filter(currentLeader.getPattern());
			switch (currentLeader.getAlignment()) {
				case LEFT:
					// if leader position hasn't been passed yet
					if (col<leaderPos) {
						tabSpace = LayoutTools.fill(leaderPattern, leaderPos-col);
					} else {
						//FIXME: ignore, or what?
					}
					break;
				case RIGHT:
					if (LayoutTools.length(charsStr)<maxLen) {
						if (col<leaderPos) {
							tabSpace = LayoutTools.fill(leaderPattern, leaderPos-col-LayoutTools.length(charsStr));
						} else {
							//FIXME: ignore, or what?
						}
					} else {
						//FIXME: ignore, or what?
					}
					break;
				case CENTER:
					if (col<leaderPos) {
						tabSpace = LayoutTools.fill(leaderPattern, leaderPos-col-(LayoutTools.length(charsStr)/2));
					} else {
						//FIXME: ignore, or what?
					}
					break;
			}
			// }
			// discard
			currentLeader = null;
		}
		maxLen -= LayoutTools.length(tabSpace);
		BreakPoint bp = null;
		Row nr = null;
		if (tabSpace.length()>0) { // break only on the new text
			BreakPointHandler bph = new BreakPointHandler(charsStr);
			bp = bph.nextRow(available-LayoutTools.length(preText + tabSpace));
			if (bp.isHardBreak()) {
				// FIXME: recalculate breakpoint on a new row
				throw new RuntimeException("Not implemented");
			} else {
				nr = new Row(preText + tabSpace + bp.getHead().replaceAll("\u00ad", ""));
			}
		} else { // break on the entire string, mainly to fix any trailing white space in preText
			BreakPointHandler bph = new BreakPointHandler(preText + charsStr);
			bp = bph.nextRow(available);
			nr = new Row(bp.getHead().replaceAll("\u00ad", ""));
		}
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
		assert currentLeader == null;
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
		if (currentLeader!=null) {
			// current leader finishes block, output now
			addChars("");
		}
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
			LayoutMaster lm = s.getLayoutMaster();
			for (Page p : s.getPages()) {
				int pagenum = p.getPageIndex()+1;
				ArrayList<Row> header = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getHeader(pagenum)) {
					header.add(new Row(distribute(row, lm.getPageWidth(), " ", p)));
				}
				p.setHeader(header);
				ArrayList<Row> footer = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getFooter(pagenum)) {
					footer.add(new Row(distribute(row, lm.getPageWidth(), " ", p)));
				}
				p.setFooter(footer);
			}
		}

		for (PageSequence s : ps.getSequenceArray()) {
			LayoutMaster lm = s.getLayoutMaster();
			pm.newSection(lm);
			for (Page p : s.getPages()) {
				pm.newPage();
				int pagenum = p.getPageIndex()+1;
				for (Row row : p.getRows()) {
					if (row.getChars().length()>0) {
						pm.newRow(LayoutTools.fill(SPACE_CHAR, (pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin()) + row.getChars());
					} else {
						pm.newRow(row.getChars());
					}
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
