package org_pef_dtbook2pef.system.tasks.layout.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org_pef_dtbook2pef.system.tasks.layout.flow.BlockProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.Flow;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformer;
import org_pef_dtbook2pef.system.tasks.layout.flow.LayoutPerformerException;
import org_pef_dtbook2pef.system.tasks.layout.flow.Leader;
import org_pef_dtbook2pef.system.tasks.layout.flow.Marker;
import org_pef_dtbook2pef.system.tasks.layout.flow.Row;
import org_pef_dtbook2pef.system.tasks.layout.flow.SequenceProperties;
import org_pef_dtbook2pef.system.tasks.layout.flow.SpanProperties;
import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;
import org_pef_dtbook2pef.system.tasks.layout.text.FilterFactory;
import org_pef_dtbook2pef.system.tasks.layout.text.StringFilter;
import org_pef_dtbook2pef.system.tasks.layout.utils.BlockHandler;
import org_pef_dtbook2pef.system.tasks.layout.utils.StateObject;

/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author joha
 * TODO: fix recursive keep problem
 * TODO: Remove builder
 * TODO: Implement SpanProperites
 * TODO: Implement floating elements
 */
public class DefaultLayoutPerformer implements Flow {
	private int leftMargin;
	private int rightMargin;
	private FlowStruct flowStruct;
	private Stack<BlockProperties> context;
	private boolean firstRow;
	private HashMap<String, LayoutMaster> masters;
	private final StringFilter filters;
	private LayoutPerformer paginator;
	private StateObject state;

	private BlockHandler bh;

	public static class Builder {
		//required

		//optional
		FilterFactory filtersFactory;

		/**
		 * Create a new DefaultlayoutPerformer.Builder with the supplied PagedMediaWriter
		 * @param writer the PagedMediaWriter to use for output.
		 */
		public Builder() {
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

/*
		public Builder addLayoutMaster(String key, LayoutMaster value) {
			masters.put(key, value);
			return this;
		}*/

		public DefaultLayoutPerformer build() {
			return new DefaultLayoutPerformer(this);
		}
	}

	/**
	 * Create a new flow
	 * @param flowWidth the width of the flow, in chars
	 */
	private DefaultLayoutPerformer(Builder builder) {
		this.masters = new HashMap<String, LayoutMaster>();
		this.filters = builder.filtersFactory.getDefault();
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		this.flowStruct = new FlowStruct(); //masters
		this.bh = new BlockHandler(filters);
		this.state = new StateObject();
	}

	public void open(LayoutPerformer paginator) {
		state.assertUnopened();
		state.open();
		this.paginator = paginator;
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}

	//TODO Handle SpanProperites
	public void addChars(CharSequence c, SpanProperties p) {
		state.assertOpen();
		addChars(c);
	}

	// Using BlockHandler
	public void addChars(CharSequence c) {
		state.assertOpen();
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
		state.assertOpen();
		flowStruct.getCurrentSequence().getCurrentGroup().addMarker(m);
	}

	public void startBlock(BlockProperties p) {
		state.assertOpen();
		assert bh.getCurrentLeader() == null;
		if (context.size()>0) {
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
		state.assertOpen();
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
			bh.subtractFromBlockIndent(context.peek().getBlockIndent());
		}
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		firstRow = true;
	}

	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		flowStruct.newSequence(p);
	}

	public void insertLeader(Leader leader) {
		state.assertOpen();
		//currentLeader = leader;
		if (bh.getCurrentLeader()!=null) {
			addChars("");
		}
		bh.setCurrentLeader(leader);
	}
	
	public void newLine() {
		state.assertOpen();
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
	
	public void close() throws IOException {
		state.assertOpen();
		for (FlowSequence seq : flowStruct.toArray()) {
			paginator.newSequence(masters.get(seq.getSequenceProperties().getMasterName()), seq.getSequenceProperties().getInitialPageNumber()-1); //seq.getSequenceProperties().getMasterName(), 
			paginator.newPage();
			FlowGroup[] groupA = seq.toArray();
			for (int gi = 0; gi<groupA.length; gi++) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (groupA[gi].getBreakBeforeType()) {
					case PAGE:
						if (paginator.getPageInfo().countRows()>0) {
							paginator.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (groupA[gi].getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(groupA, gi);
						if (paginator.getPageInfo().countRows()>0 && keepHeight>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows() && keepHeight<=paginator.getPageInfo().getFlowHeight()) {
							paginator.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (groupA[gi].getSpaceBefore()+groupA[gi].getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()) {
					throw new IOException("Layout exception: ", new LayoutPerformerException("Group margins too large to fit on an empty page."));
				} else if (groupA[gi].getSpaceBefore()+1>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				}
				for (int i=0; i<groupA[gi].getSpaceBefore();i++) {
					paginator.newRow(new Row(""));
				}
				paginator.insertMarkers(groupA[gi].getGroupMarkers());
				for (Row row : groupA[gi].toArray()) {
					paginator.newRow(row);
				}
				if (groupA[gi].getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				} else {
					for (int i=0; i<groupA[gi].getSpaceAfter();i++) {
						paginator.newRow(new Row(""));
					}
				}
			}
		}
		state.close();
	}
	/*
	private PageStruct setHeadersAndFooters(PageStruct ps) throws LayoutPerformerException {
		for (PageSequence s : ps.getSequenceArray()) {
			LayoutMaster lm = s.getLayoutMaster();
			for (Page p : s.getPages()) {
				int pagenum = p.getPageIndex()+1;
				/*
				ArrayList<Row> header = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getHeader(pagenum)) {
					try {
						header.add(new Row(distribute(row, lm.getFlowWidth(), " ", p)));
					} catch (LayoutToolsException e) {
						throw new LayoutPerformerException("Error while rendering header", e);
					}
				}*/
				//p.setHeader(header);
	/*
				p.setHeader(renderFields(lm, p, lm.getHeader(pagenum)));
				/*
				ArrayList<Row> footer = new ArrayList<Row>();
				for (ArrayList<Object> row : lm.getFooter(pagenum)) {
					try {
						footer.add(new Row(distribute(row, lm.getFlowWidth(), " ", p)));
					} catch (LayoutToolsException e) {
						throw new LayoutPerformerException("Error while rendering footer", e);
					}
				}*/
				//p.setFooter(footer);
	/*
				p.setFooter(renderFields(lm, p, lm.getFooter(pagenum)));
			}
		}
		return ps;
	}*/

	public void endFloat() {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}

	public void insertAnchor(String ref) {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}

	public void startFloat(String id) {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}
}
