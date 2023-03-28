/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

/**
 * A set of static utility methods for GUI debugging.
 * 
 * @author Romain Deltour
 * 
 */
public class DebugUtil {
	/**
	 * Prints the children tree of the given composite to
	 * <code>System.err</code>.
	 * 
	 * @param parent
	 *            an SWT composite
	 */
	public static void printChildTree(Composite parent) {
		printChildTree(parent, 0);
	}

	private static void printChildTree(Composite parent, int indent) {
		if (parent == null) {
			return;
		}
		for (Control control : parent.getChildren()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				sb.append(' ');
			}
			sb.append(toStringObject(control));
			System.err.println(sb.toString());
			if (control instanceof Composite) {
				printChildTree((Composite) control, indent + 4);
			}
		}
	}

	/**
	 * Prints the TAB-list of the given composite to <code>System.err</code>.
	 * 
	 * @param parent
	 *            an SWT composite
	 */
	public static void printTabList(Composite parent) {
		printTabList(parent, 0);
	}

	private static void printTabList(Composite parent, int indent) {
		if (parent == null) {
			return;
		}
		for (Control control : parent.getTabList()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				sb.append(' ');
			}
			sb.append(toStringObject(control));
			System.err.println(sb.toString());
			if (control instanceof Composite) {
				printTabList((Composite) control, indent + 4);
			}
		}
	}

	/**
	 * Returns a string representing the given object similar to the default
	 * implementation of the {@link Object#toString()} method (i.e.
	 * <code><i>simple_class_name</i>[<i>hex_hashcode</i>]</code>)
	 * 
	 * @param obj
	 *            an object the string representation of which is returned.
	 * @return a string representation of the given object as
	 *         <code><i>simple_class_name</i>[<i>hex_hashcode</i>]</code>
	 */
	public static String toStringObject(Object obj) {

		StringBuilder sb = new StringBuilder();
		sb.append(obj.getClass().getSimpleName());
		sb.append('[');
		sb.append(Integer.toHexString(obj.hashCode()));
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Hooks some SWT events occurring in the given display and prints some
	 * tracing information.
	 * <p>
	 * Currently trace {@link SWT#FocusIn} and {@link SWT#Traverse} events.
	 * </p>
	 * 
	 * @param display
	 *            a display to watch for focus and tab-traversal events
	 */
	public static void traceEvents(final Display display) {
		Listener focusListener = new Listener() {
			public void handleEvent(Event event) {
				Control focused = display.getFocusControl();
				System.err.println("Focused: " + toStringObject(focused));
				// System.err.println("Parent TabList:");
				// printTabList(focused.getParent(), 2);
			}
		};
		Listener traverseListener = new Listener() {

			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ARROW_NEXT:
					System.err.println("Traversed Arrow Next: "
							+ toStringObject(event.widget));
					break;
				case SWT.TRAVERSE_ARROW_PREVIOUS:
					System.err.println("Traversed Arrow Prev: "
							+ toStringObject(event.widget));
					break;
				case SWT.TRAVERSE_PAGE_NEXT:
					System.err.println("Traversed Page Next: "
							+ toStringObject(event.widget));
					break;
				case SWT.TRAVERSE_PAGE_PREVIOUS:
					System.err.println("Traversed Page Prev: "
							+ toStringObject(event.widget));
					break;
				case SWT.TRAVERSE_TAB_NEXT:
					System.err.println("Traversed TAB Next: "
							+ toStringObject(event.widget));
					break;
				case SWT.TRAVERSE_TAB_PREVIOUS:
					System.err.println("Traversed TAB Prev: "
							+ toStringObject(event.widget));
					break;
				default:
					System.err.println("Traversed " + event.detail
							+ toStringObject(event.widget));
					break;
				}
				if (event.widget instanceof TabFolder) {
					TabFolder folder = (TabFolder) event.widget;
					System.err.println("tabfolder sel = "
							+ folder.getSelectionIndex());
				}
				if ((event.widget instanceof Control)
						&& (((Control) event.widget).getParent() instanceof TabFolder)) {
					TabFolder folder = (TabFolder) ((Control) event.widget)
							.getParent();
					System.err.println("tabfolder sel = "
							+ folder.getSelectionIndex());
				}
			}

		};
		display.addFilter(SWT.FocusIn, focusListener);
		// display.addFilter(SWT.Traverse, traverseListener);
	}

	private DebugUtil() {
	}
}
