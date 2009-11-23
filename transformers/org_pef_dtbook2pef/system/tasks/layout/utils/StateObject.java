package org_pef_dtbook2pef.system.tasks.layout.utils;

public class StateObject {
	public enum State {UNOPENED, OPEN, CLOSED}
	private State state;
	private String type;
	
	public StateObject(String type) {
		this.type = type;
		state = State.UNOPENED;
	}
	
	public StateObject() {
		this("Object");
	}
	
	/**
	 * Open the object
	 */
	public void open() {
		state = State.OPEN;
	}
	
	/**
	 * Close the object
	 */
	public void close() {
		state = State.CLOSED;
	}
	
	public boolean isClosed() {
		return state == State.CLOSED;
	}
	
	public boolean isOpen() {
		return state == State.OPEN;
	}

	/**
	 * Assert that the object is open
	 * @throws IllegalStateException if the object is not open
	 */
	public void assertOpen() throws IllegalStateException {
		if (state != State.OPEN) {
			throw new IllegalStateException(type + " is not open.");
		}
	}
	
	/**
	 * Assert that the object is not open
	 * @throws IllegalStateException if the object is open
	 */
	public void assertNotOpen() throws IllegalStateException {
		if (state == State.OPEN) {
			throw new IllegalStateException(type + " is already open.");
		}
	}
	
	/**
	 * Assert that the object has been closed
	 * @throws IllegalStateException if the object is not closed
	 */
	public void assertClosed() throws IllegalStateException {
		if (state != State.CLOSED) {
			throw new IllegalStateException(type + " is not closed.");
		}
	}
	
	/**
	 * Assert that the object has never been opened
	 * @throws IllegalStateException if the object is not unopened
	 */
	public void assertUnopened() throws IllegalStateException {
		if (state != State.UNOPENED) {
			throw new IllegalStateException(type + " has already been opened.");
		}
	}

}
