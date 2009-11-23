package org_pef_dtbook2pef.system.tasks.layout.flow;

import java.io.Closeable;

import org_pef_dtbook2pef.system.tasks.layout.page.LayoutMaster;

/**
 * Flow is an interface designed to convert blocks of text into rows and pages. 
 * @author joha
 *
 */
public interface Flow extends Closeable {
	
	/**
	 * Open 
	 * @param performer
	 */
	public void open(LayoutPerformer performer);

	/**
	 * Start a new Sequence at the current position in the flow.
	 * @param p the SequenceProperties for the new Sequence
	 */
	public void newSequence(SequenceProperties p);

	/**
	 * Start a new block with the supplied BlockProperties.
	 * 
	 * @param p the BlockProperties of the new block
	 */
	public void startBlock(BlockProperties p);

	/**
	 * End the current block
	 */
	public void endBlock();
	
	/**
	 * Start a floating element
	 * @param id the identifier of the floating element
	 */
	public void startFloat(String id);
	
	/**
	 * End the floating element
	 */
	public void endFloat();

	/**
	 * Insert a marker at the current position in the flow
	 * @param m the marker to insert
	 */
	public void insertMarker(Marker m);
	
	/**
	 * Insert an anchor at the current position in the flow
	 * @param ref anchor name
	 */
	public void insertAnchor(String ref);
	
	/**
	 * Insert a leader at the current position in the flow
	 * @param leader the leader to insert
	 */
	public void insertLeader(Leader leader);

	/**
	 * Add chars to flow
	 * @param chars the characters to add to the flow
	 */
	public void addChars(CharSequence chars);
	
	/**
	 * Add chars to flow
	 * @param chars the characters to add to the flow
	 * @param p the SpanProperties for the characters 
	 */
	public void addChars(CharSequence chars, SpanProperties p);
	
	/**
	 * Start a new line, continue block
	 */
	public void newLine();
	
	/**
	 * 
	 * @param name
	 * @param master
	 */
	public void addLayoutMaster(String name, LayoutMaster master);

}
