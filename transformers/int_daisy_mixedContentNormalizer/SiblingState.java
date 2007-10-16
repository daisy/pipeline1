package int_daisy_mixedContentNormalizer;

/**
 * Describe the state of a set of XML siblings.
 * <p>Sibling state is balanced if:</p>
 * <ul>
 * <li>The siblings are only text</li>
 * <li>The siblings are only ignorable elements (empty elements or elements marked as ignorable in config)</li>
 * <li>The siblings are text and ignorable elements </li>
 * <li>The siblings are only non-ignorable elements.</li>
 * </ul>
 * <p>Sibling state is unbalanced if:</p>
 * <ul>
 * <li>Non-ignorable elements occur in combination with text and/or ignorable elements.</li>
 * </ul>
 * <p>XML Whitespace Nodes are not included in the weighting.</p> 
 * @author Markus Gylling
 */

public enum SiblingState {
	BALANCED,			
	UNBALANCED			
}