/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.util.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A set of XPath convenience functions. This class contains a set of convenience
 * functions to make it easier to use the XPath utilities of Java.
 * @author Linus Ericson
 */
public class XPathUtils {

    /**
     * Evaluates an XPath expression and returns the {@link String} value
     * of the result, similar to the <code>value-of</code> function in XSLT.
     * If there is an error in the XPath syntax, an empty string will be
     * returned.
     * @param node the {@link Node} to evaluate the expression on
     * @param xpath the XPath expression to evaluate
     * @return a {@link String}.
     */
    public static String valueOf(Node node, String xpath) {
        XPath xpathObj = XPathFactory.newInstance().newXPath();
	    try {
            return (String)xpathObj.evaluate(xpath, node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {            
            return "";
        }
    }

    /**
     * Evaluates an XPath expression and returns the result as a {@link NodeList}.
     * @param node the {@link Node} to evaluate the expression on
     * @param xpath the XPath expression to evaluate
     * @return a {@link NodeList}, or <code>null</code> if there is an
     * error in the XPath expression.
     */
    public static NodeList selectNodes(Node node, String xpath) {
        XPath xpathObj = XPathFactory.newInstance().newXPath();
        try {
            return (NodeList)xpathObj.evaluate(xpath, node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {            
            return null;
        }
    }

    /**
     * Evaluates an XPath expression and returns the result as a {@link Node}.
     * Only a single <code>Node</code> will be returned.
     * @param node the {@link Node} to evaluate the expression on
     * @param xpath the XPath expression to evaluate
     * @return a single {@link Node}, or <code>null</code> if there is an
     * error in the XPath expression.
     */
    public static Node selectSingleNode(Node node, String xpath) {
        XPath xpathObj = XPathFactory.newInstance().newXPath();
        try {
            return (Node)xpathObj.evaluate(xpath, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {            
            return null;
        }
    }
}
