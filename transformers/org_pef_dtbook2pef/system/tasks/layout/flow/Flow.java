package org_pef_dtbook2pef.system.tasks.layout.flow;



/**
 * Flow is an interface designed to convert blocks of text into rows and pages. 
 * @author joha
 *
 */
public interface Flow {

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
	 * Insert a marker at the current position in the flow
	 * @param m the marker to insert
	 */
	public void insertMarker(Marker m);
	
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
	 * Start a new line, continue block
	 */
	public void newLine();

}
