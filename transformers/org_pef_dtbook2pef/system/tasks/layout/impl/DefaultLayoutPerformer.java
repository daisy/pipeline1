package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Flow;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformerException;
import org_pef_dtbook2pef.system.tasks.layout.flow.Leader;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.flow.SequenceProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.SpanProperties;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriter;
import org_pef_dtbook2pef.system.tasks.layout.page.PagedMediaWriterException;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CompoundField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.CurrentPageField;
import org_pef_dtbook2pef.system.tasks.layout.page.field.MarkerReferenceField;
import org_pef_dtbook2pef.system.tasks.layout.text.FilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.system.tasks.layout.utils.BlockHandler;
import org_pef_dtbook2pef.system.tasks.layout.utils.LayoutTools;

/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author joha
 * TODO: fix recursive keep problem
 * TODO check flow-file validity
 */
public class DefaultLayoutPerformer implements Flow, LayoutPerformer {
	private static final Character SPACE_CHAR = ' '; //'\u2800'
	private int leftMargin;
	private int rightMargin;
	private FlowStruct flowStruct;
	private Stack<BlockProperties> context;
	private boolean firstRow;
	//private int currentListNumber;
	//private BlockProperties.ListType currentListType;
	//private Leader currentLeader;
	private HashMap<String, LayoutMaster> masters;
	private final StringFilter filters;
	private final PagedMediaWriter writer;
	private BlockHandler bh;
	//private int currentBlockIndent;

	public static class Builder {
		//required
		PagedMediaWriter writer;
		//optional
		HashMap<String, LayoutMaster> masters;
		FilterFactory filtersFactory;
	
		/**
		 * Create a new DefaultlayoutPerformer.Builder with the supplied PagedMediaWriter
		 * @param writer the PagedMediaWriter to use for output.
		 */
		public Builder(PagedMediaWriter writer) {
			this.writer = writer;
			masters = new HashMap<String, LayoutMaster>();
			setStringFilterFactory(null);
		}
		
		/**
		 * Set text filter handler, may not be null
		 * @param filters
		 */
		public Builder setStringFilterFactory(FilterFactory filters) {
			this.filtersFactory = filters;
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
		this.filters = builder.filtersFactory.getDefault();
		this.writer = builder.writer;
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		//this.currentListType = BlockProperties.ListType.NONE;
		//this.currentLeader = null;
		this.flowStruct = new FlowStruct(); //masters
		this.bh = new BlockHandler(filters);
		//this.currentBlockIndent = 0;
	}

	/**
	public abstract int getWidth();
	public abstract void close();
	public abstract int availableRows();
	public abstract void addRow(CharSequence chars);
	public abstract void newPage();*/

	
	//TODO Handle SpanProperites
	public void addChars(CharSequence c, SpanProperties p) {
		addChars(c);
	}
	/*
	public void addChars(CharSequence c) {
		assert context.size()!=0;
		String chars = filters.filter(c.toString());
		int available = masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getFlowWidth() - rightMargin;
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
				chars = newRow("", chars, available, leftMargin, p.getFirstLineIndent());
			}
			firstRow = false;
		} else {
			Row r = flowStruct.getCurrentSequence().getCurrentGroup().popRow();
			chars = newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), chars, available);
		}
		while (LayoutTools.length(chars.toString())>0) {
			String c2 = newRow("", chars, available, leftMargin, p.getTextIndent());
			//c2 = c2.replaceFirst("\\A\\s*", ""); // remove leading white space from input
			if (c2.length()>=chars.length()) {
				System.out.println(c2);
			}
			chars = c2;
		}
	}

	private String newRow(String contentBefore, String chars, int available, int margin, int indent) {
		int thisIndent = indent - LayoutTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString();
		return newRow(null, margin, preText, "", chars, available);
	}

	//TODO: check leader functionality
	private String newRow(ArrayList<Marker> r, int margin, String preContent, String preTabText, String postTabText, int available) {

		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^

		int preTextIndent = LayoutTools.length(preContent);
		int preContentPos = margin+preTextIndent;
		int preTabPos = preContentPos+LayoutTools.length(preTabText);
		int postTabTextLen = LayoutTools.length(postTabText);
		int maxLenText = available-(preContentPos);

		int width = masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getFlowWidth();
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
				flowStruct.getCurrentSequence().getCurrentGroup().pushRow(row);

				preContent = LayoutTools.fill(SPACE_CHAR, context.peek().getTextIndent());
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

		flowStruct.getCurrentSequence().getCurrentGroup().pushRow(nr);
		return bp.getTail();
	}
*/


	// Using BlockHandler
	public void addChars(CharSequence c) {

		assert context.size()!=0;
		bh.setBlockProperties(context.peek());
		bh.setWidth(masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getFlowWidth() - rightMargin);
		ArrayList<Row> ret;
		if (firstRow) {
			ret = bh.layoutBlock(c, leftMargin, masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()));
			firstRow = false;
		} else {
			Row r = flowStruct.getCurrentSequence().getCurrentGroup().popRow();
			ret = bh.appendBlock(c, leftMargin, r, masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()));
		}
		for (Row r : ret) {
			flowStruct.getCurrentSequence().getCurrentGroup().pushRow(r);
		}

	}
	// END Using BlockHandler
	

	
	public void insertMarker(Marker m) {
		flowStruct.getCurrentSequence().getCurrentGroup().addMarker(m);
	}

	public void startBlock(BlockProperties p) {
		//assert currentLeader == null;
		assert bh.getCurrentLeader() == null;
		if (context.size()>0) {
			
			//currentListType = context.peek().getListType();
			//bh.setCurrentListType(context.peek().getListType());
			//bh.setCurrentListType(context.peek().getListType());
			
			/*if (currentListType!=BlockProperties.ListType.NONE) {
				currentListNumber = context.peek().nextListNumber();
			}*/
			
			//if (bh.getCurrentListType()!=BlockProperties.ListType.NONE) {
				//bh.setCurrentListNumber(context.peek().nextListNumber());
			//}
			//currentBlockIndent += context.peek().getBlockIndent();
			bh.addToBlockIndent(context.peek().getBlockIndent());
			if (context.peek().getListType()!=BlockProperties.ListType.NONE) {
				String listLabel;
				switch (context.peek().getListType()) {
				case OL:
					listLabel = context.peek().nextListNumber()+""; break;
				case UL:
					listLabel = "•";
					break;
				case PL: default:
					listLabel = "";
				}
				bh.setListItem(listLabel, context.peek().getListType());
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
		/*if (currentLeader!=null) {
			addChars("");
		}*/
		// BlockHandler
		if (bh.getCurrentLeader()!=null || bh.getListItem()!=null) {
			addChars("");
		}
		// BlockHandler
		BlockProperties p = context.pop();
		flowStruct.getCurrentSequence().getCurrentGroup().addSpaceAfter(p.getBottomMargin());
		if (context.size()>0) {
			FlowGroup c = flowStruct.getCurrentSequence().newFlowGroup();
			c.setKeepType(context.peek().getKeepType());
			c.setKeepWithNext(context.peek().getKeepWithNext());
			//currentListType = context.peek().getListType();
			//bh.setCurrentListType(context.peek().getListType());
			//currentBlockIndent -= context.peek().getBlockIndent();
			bh.subtractFromBlockIndent(context.peek().getBlockIndent());
		} else {
			//TODO: what else need to be done when there is no context stack?
			
			//currentListType = ListType.NONE;
			//bh.setCurrentListType(ListType.NONE);
		}
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		firstRow = true;
	}

	public void newSequence(SequenceProperties p) {
		flowStruct.newSequence(p);
	}

	public void insertLeader(Leader leader) {
		//currentLeader = leader;
		bh.setCurrentLeader(leader);
	}
	
	public void newLine() {
		Row r = new Row("");
		r.setLeftMargin(leftMargin + context.peek().getTextIndent());
		flowStruct.getCurrentSequence().getCurrentGroup().pushRow(r);
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
	
	public void layout(OutputStream os) throws LayoutPerformerException {
		try {
			writer.open(os);
		} catch (PagedMediaWriterException e) {
			throw new LayoutPerformerException("Could not open stream.", e);
		}
		PageStruct ps = new PageStruct(); //masters
		for (FlowSequence seq : flowStruct.toArray()) {
			ps.newSection(masters.get(seq.getSequenceProperties().getMasterName()), seq.getSequenceProperties().getInitialPageNumber()-1); //seq.getSequenceProperties().getMasterName(), 
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
					throw new LayoutPerformerException("Group margins too large to fit on an empty page.");
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
					header.add(new Row(distribute(row, lm.getFlowWidth(), " ", p)));
				}
				p.setHeader(header);
				ArrayList<Row> footer = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getFooter(pagenum)) {
					footer.add(new Row(distribute(row, lm.getFlowWidth(), " ", p)));
				}
				p.setFooter(footer);
			}
		}
		
		int rowNo = 1;
		for (PageSequence s : ps.getSequenceArray()) {
			LayoutMaster lm = s.getLayoutMaster();
			writer.newSection(lm);
			for (Page p : s.getPages()) {
				writer.newPage();
				int pagenum = p.getPageIndex()+1;
				for (Row row : p.getRows()) {
					if (row.getChars().length()>0) {
						int margin = ((pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin()) + row.getLeftMargin();
						String chars = row.getChars().replaceAll("\\s*\\z", "");
						int rowWidth = LayoutTools.length(chars)+row.getLeftMargin();
						String r = 	LayoutTools.fill(SPACE_CHAR, margin) + chars;
						if (rowWidth>lm.getFlowWidth()) {
							throw new LayoutPerformerException("Row no " + rowNo + " is too long (" + rowWidth + "/" + lm.getFlowWidth() + ") '" + chars + "'");
						}
						writer.newRow(r);
					} else {
						writer.newRow();
					}
					rowNo++;
				}
			}
		}
		writer.close();
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
