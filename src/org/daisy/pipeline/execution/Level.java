package org.daisy.pipeline.execution;

/**
 * Represents levels of severity, as used in Pipeline messages for instance.
 * 
 * @author Romain Deltour
 * 
 */
public enum Level implements Comparable<Level> {
	DEBUG, INFO_FINER, INFO, WARNING, ERROR
};