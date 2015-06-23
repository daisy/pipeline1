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