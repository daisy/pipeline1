package org.daisy.util.xml;

public class ClipTime {

	private final Double timeInMs;

	public ClipTime() {
		timeInMs = null;
	}
	
	public ClipTime(double timeInMs) {
		this.timeInMs = new Double(timeInMs);
	}

	public double getTimeInMs() {
		if(notSet()) {
			return 0;
		} else {
			return timeInMs;
		}
	}

	public ClipTime roundedToMilliSeconds() {
		return new ClipTime(Math.round(this.getTimeInMs()));
	}

	public ClipTime floorToMilliSeconds() {
		return new ClipTime(Math.floor(this.getTimeInMs()));
	}

	public boolean notSet() {
		if(this.timeInMs == null) {
			return true;
		} else {
			return false;
		}
	}

	public ClipTime add(ClipTime timeToAdd) {
		return new ClipTime(this.getTimeInMs() + timeToAdd.getTimeInMs());
	}

	public ClipTime subtract(ClipTime timeToSubtract) {
		return new ClipTime(this.getTimeInMs() - timeToSubtract.getTimeInMs());
	}
}
