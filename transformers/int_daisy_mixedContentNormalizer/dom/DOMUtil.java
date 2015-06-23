/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
		else if(parent.equals(target)) return true; //TODO equality, sb == in DOM?
		return isDescendant(parent,target);
	}
	
	/**
	 * return true if e has at least on child of type NODE_TEXT which 
	 * is not only XML whitespace
	 */
	static boolean hasTextChild(Element e) {
		if(e.hasChildNodes()) {
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node c = e.getChildNodes().item(i);
				if(c.getNodeType() == Node.TEXT_NODE 
						&& !CharUtils.isXMLWhiteSpace(c.getNodeValue())) return true;
			}
		}
		return false;
	}

}
