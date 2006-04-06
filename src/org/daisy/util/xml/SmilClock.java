package org.daisy.util.xml;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A <code>SmilClock</code> object is a wrapper for a SMIL clock value (time)
 * <pre>
 * Versions:
 * 0.1.0 (09/02/2003)
 * - Implemented string parsing
 * - Implemented both toString() methods
 * 0.1.1 (10/02/2003)
 * - Added static method to get/set tolerance for equals() and compareTo() methods
 * - Modified equals() and compareTo() to take tolerance value into account
 * 0.2.0 (10/04/2003)
 * - Added support for npt= formats
 * - Fixed bug in SmilClock(double) constructor
 * - Fixed nasty bug in SmilClock(String) constructor
 * 1.0.1 (11/01/2004)
 * - Fixed bug in milliseconds parsing in SmilClock(String s); now handles values with more/less than 3 digits
 * - Fixed bug in toString(int format) that caused milliseconds to lose leading zeroes
 * 1.0.2 (11/06/2005) Markus
 * - Added optimization: patterns compiled and static
 * 1.0.3 (21/06/2005) Markus 
 * - Added secondsValueRounded
 * 1.0.4 (10/02/2006) Linus
 * - Fixed locale bug in toString: now using DecimalFormat instead of NumberFormat
 * </pre>
 * @author James Pritchett
 */
public class SmilClock implements Comparable {
//	TODO move this to a more appropriate package
	private static Pattern fullClockPattern = Pattern.compile("(npt=)?(\\d+):([0-5]\\d):([0-5]\\d)([.](\\d+))?");
	private static Pattern partialClockPattern  = Pattern.compile("(npt=)?([0-5]\\d):([0-5]\\d)([.](\\d+))?");
	private static Pattern timecountClockPattern  = Pattern.compile("(npt=)?(\\d+([.]\\d+)?)(h|min|s|ms)?");
	
	/**
	 @param s A string representation of the SMIL clock value in any accepted format
	 @throws NumberFormatException if the string is not a legal SMIL clock value format
	 */
	public SmilClock(String s) throws NumberFormatException {		
		Matcher m;
		double d;
		
		/*
		 This uses regular expressions to parse the given string.  It tries each of the three
		 formats (full, partial, timecount) and throws an exception if none of them match.  It uses
		 regular expression groupings to capture the various numeric portions of the string
		 at parse-time, which it then uses to calculate the milliseconds value.
		 */
		
		//test for timecount clock value
		m = timecountClockPattern.matcher(s.trim());
		if (m.matches()) {
			d = Double.parseDouble(m.group(2));         // Save the number (with fraction)
			if (m.group(4) == null) {
				this.msecValue = (long)(d * 1000);
			}
			else if (m.group(4).equals("ms")) {
				this.msecValue = (long)d;               // NOTE:  This will truncate fraction
			}
			else if (m.group(4).equals("s")) {
				this.msecValue = (long)(d * 1000);
			}
			else if (m.group(4).equals("min")) {
				this.msecValue = (long)(d * 60000);
			}
			else if (m.group(4).equals("h")) {
				this.msecValue = (long)(d * 3600000);
			}
			return;
		}

		
		//test for a full clock value
		m = fullClockPattern.matcher(s.trim());
		if (m.matches()) {
			this.msecValue =
				(Long.parseLong(m.group(2)) * 3600000) +
				(Long.parseLong(m.group(3)) * 60000) +
				(Long.parseLong(m.group(4)) * 1000) +
				((m.group(6) != null) ? Math.round(Double.parseDouble(m.group(5)) * 1000) : 0);
			return;
		}
		
		//test for partial clock value
		m = partialClockPattern.matcher(s.trim());
		if (m.matches()) {
			this.msecValue =
				(Long.parseLong(m.group(2)) * 60000) +
				(Long.parseLong(m.group(3)) * 1000) +
				((m.group(5) != null) ? Math.round(Double.parseDouble(m.group(4)) * 1000) : 0);
			return;
		}
				
		// If we got this far, s is not a legal SMIL clock value
		throw new NumberFormatException("Invalid SMIL clock value format: " + s.trim());
	}
	
	/**
	 @param msec Time value in milliseconds
	 */
	public SmilClock(long msec) {
		this.msecValue = msec;
	}
	
	/**
	 @param sec Time value in seconds
	 */
	public SmilClock(double sec) {
		this.msecValue = (long) (sec * 1000);
	}
	
	/**
	 Returns clock value in full clock value format (default)
	 @return String in full clock value format (HH:MM:SS.mmm)
	 */
	public String toString() {
		return this.toString(SmilClock.FULL);
	}
	
	/**
	 Returns clock value in specified format
	 @param format Format code (FULL, PARTIAL, TIMECOUNT)
	 @return String with value in named format
	 */
	public String toString(int format) {
		long hr;
		long min;
		long sec;
		long msec;
		long tmp;
		
		String s;
		
		NumberFormat nfInt = NumberFormat.getIntegerInstance();
		nfInt.setMinimumIntegerDigits(2);
		NumberFormat nfMsec = NumberFormat.getIntegerInstance();
		nfMsec.setMinimumIntegerDigits(3);
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
		dfSymbols.setDecimalSeparator('.');
		DecimalFormat dfDouble = new DecimalFormat("0.000", dfSymbols);
		dfDouble.setMaximumFractionDigits(3);
		dfDouble.setGroupingUsed(false);
		
		// Break out all the pieces ...
		msec = this.msecValue % 1000;
		tmp = (this.msecValue - msec) / 1000;
		sec = tmp % 60;
		tmp = (tmp - sec) / 60;
		min = tmp % 60;
		hr = (tmp - min) / 60;
		
		switch(format) {
		case FULL:
			if (msec > 0) {
				s = hr + ":" + nfInt.format(min) + ":" + nfInt.format(sec) + "." + nfMsec.format(msec);
			}
			else {
				s = hr + ":" + nfInt.format(min) + ":" + nfInt.format(sec);
			}
			break;
		case PARTIAL:
			// KNOWN BUG:  This will return misleading results for clock values > 59:59.999
			// WORK AROUND:  Caller is responsible for testing that this is an appropriate format
			if (msec > 0) {
				s = nfInt.format(min) + ":" + nfInt.format(sec) + "." + nfMsec.format(msec);
			}
			else {
				s = nfInt.format(min) + ":" + nfInt.format(sec);
			}
			break;
		case TIMECOUNT:
			s = dfDouble.format((double)this.msecValue / 1000);
			break;
		case TIMECOUNT_MSEC:
			s = dfDouble.format((double)this.msecValue) + "ms";
			break;
		case TIMECOUNT_SEC:
			s = dfDouble.format((double)this.msecValue / 1000) + "s";
			break;
		case TIMECOUNT_MIN:
			s = dfDouble.format((double)this.msecValue / 60000) + "min";
			break;
		case TIMECOUNT_HR:
			s = dfDouble.format((double)this.msecValue / 3600000) + "h";
			break;
		default:
			throw new NumberFormatException("Unknown SMIL clock format code: " + format);
		}
		return s;
	}
	
	/**
	 Returns clock value in milliseconds
	 @return clock value in milliseconds
	 */
	public long millisecondsValue() {
		return this.msecValue;
	}
	
	/**
	 Returns clock value in seconds
	 @return clock value in seconds
	 */
	public double secondsValue() {
		return (double) this.msecValue / 1000;
	}
	
	/**
	 Returns clock value in seconds, rounded to full seconds
	 @return clock value in seconds, rounded to full seconds
	 */
	public long secondsValueRounded() {
		return Math.round((double) this.msecValue / 1000);
	}
	
	// implement equals() so we can test values for equality
	public boolean equals(Object otherObject) {
		if (this == otherObject) return true;       // Objects are identical
		if (otherObject == null) return false;      // There ain't nuthin' like a null ...
		if (getClass() != otherObject.getClass()) return false;     // No class-mixing, either
		try{
			SmilClock other = (SmilClock)otherObject;   // Cast it, then compare, using tolerance		
			if (Math.abs(other.msecValue - this.msecValue) <= msecTolerance) {
				return true;
			}
		}catch (ClassCastException cce) {
			//do nothing
		}
		return false;		
	}
	
	// implement Comparable interface so we can sort and compare values
	public int compareTo(Object otherObject) throws ClassCastException {
		SmilClock other = (SmilClock)otherObject;           // Hope for the best!		
		if (Math.abs(other.msecValue - this.msecValue) <= msecTolerance) return 0;
		if (this.msecValue < other.msecValue) return -1;
		return 1;
	}
	
	// Static methods
	
	/**
	 Sets tolerance for comparisons and equality testing.
	 <p>When comparing two values, if they differ by less than the given tolerance,
	 they will be evaluated as equal to one another.</p>
	 @param msec Tolerance value in milliseconds
	 */
	public static void setTolerance(long msec) {
		msecTolerance = msec;
	}
	
	/**
	 Returns tolerance setting
	 @return Current tolerance value in milliseconds
	 */
	public static long getTolerance() {
		return msecTolerance;
	}
	
	// Type codes for the different SMIL clock value formats
	public static final int FULL = 1;
	public static final int PARTIAL = 2;
	public static final int TIMECOUNT = 3;          // Default version (no metric)
	public static final int TIMECOUNT_MSEC = 4;
	public static final int TIMECOUNT_SEC = 5;
	public static final int TIMECOUNT_MIN = 6;
	public static final int TIMECOUNT_HR = 7;
	
	private long msecValue;         // All values stored in milliseconds
	private static long msecTolerance;
}