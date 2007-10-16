package int_daisy_mixedContentNormalizer.dom;

import org.daisy.util.i18n.CharUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Markus Gylling
 */
public class DOMUtil {

	/**
	 * @return true if test is a descendant of parent
	 */
	static boolean isDescendant(Node test, Node target) {
		Node parent = test.getParentNode();
		if(parent==null)return false;
		else if(parent.equals(target)) return true;
		return isDescendant(parent,target);
	}
	
	/**
	 * return true if e has at least on child of type NODE_TEXT
	 */
	static boolean hasTextChild(Element e) {
		if(e.hasChildNodes()) {
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node c = e.getChildNodes().item(i);
				if(c.getNodeType() == Node.TEXT_NODE && !CharUtils.isXMLWhiteSpace(c.getNodeValue())) return true;
			}
		}
		return false;
	}

}
