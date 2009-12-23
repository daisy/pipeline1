package org.daisy.util.dtb.build;

/**
 * SMIL text media structure
 * @author jpritchett@rfbd.org
 */
public class SmilStructureText extends SmilStructureMedia {

	/**
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param src The URI of the referenced media object
	 */
	public SmilStructureText(String smilID, String src) {
		super("text", smilID);		// Element name is hard-wired
		this.src = src;
	}

	/**
	 * @param smilID The XML id to use in the output XML for this structure
	 * @param className The value for the class attribute in the output XML
	 * @param src The URI of the referenced media object
	 */
	public SmilStructureText(String smilID, String className, String src) {
		super("text", smilID, className, src);
	}
}
