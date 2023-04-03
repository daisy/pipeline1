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
package org.daisy.pipeline.scripts.ui;

import java.util.HashSet;
import java.util.Set;

import org.daisy.pipeline.core.script.ScriptParameter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Holds the widgets used to edit a script parameter with a particular data
 * type.
 * <p>
 * This is the abstract base class for all data type adapters:
 * <ul>
 * <li>{@link BooleanAdapter} - to edit boolean values</li>
 * <li>{@link DirectoryAdapter} - to edit directory selection</li>
 * <li>{@link EnumAdapter} - to edit choice enumeration</li>
 * <li>{@link FileAdapter} - to edit single file selection</li>
 * <li>{@link FilesAdapter} - to edit multiple file selection</li>
 * <li>{@link IntegerAdapter} - to edit integer values</li>
 * <li>{@link StringAdapter} - to edit strings</li>
 * </ul>
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public abstract class DatatypeAdapter {

	/**
	 * The main control used to edit the parameter
	 */
	protected Control control;
	/**
	 * The script parameter edited by this adapter
	 */
	protected ScriptParameter param;
	/**
	 * The set of listeners to the parameter value
	 */
	protected final Set<DatatypeAdapterValueListener> valueListeners;

	/**
	 * Creates the adapter.
	 * <p>
	 * This constructor hooks the value change notification to the appropriate
	 * SWT event.
	 * </p>
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public DatatypeAdapter(Composite parent, ScriptParameter param) {
		this.param = param;
		this.control = createControl(parent);
		this.valueListeners = new HashSet<DatatypeAdapterValueListener>();
		hookValueListener();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's value is modified, by sending it one of the messages
	 * defined in the {@link DatatypeAdapterValueListener} interface.
	 * 
	 * @param listener
	 *            The listener which should be notified.
	 */
	public void addValueListener(DatatypeAdapterValueListener listener) {
		valueListeners.add(listener);
	}

	/**
	 * Adjust the layout of the internal widgets to the number of columns in the
	 * GridLayout of the parent widget.
	 * 
	 * @param numCol
	 *            The number of columns in the parent GridLayout.
	 */
	public abstract void adjustLayout(int numCol);

	/**
	 * Create the internal widgets and returns the main control.
	 * 
	 * @param parent
	 *            The parent composite of this adapter's widgets.
	 * @return The main control used to edit the parameter.
	 */
	protected abstract Control createControl(Composite parent);

	/**
	 * Notifies the {@link DatatypeAdapterValueListener}s that the value of the
	 * main control changed.
	 */
	protected final void fireValueChanged() {
		for (DatatypeAdapterValueListener listener : valueListeners) {
			listener.valueChanged(this);
		}
	}

	/**
	 * Returns the main control used to edit the underlying parameter.
	 * 
	 * @return the main control used to edit the underlying parameter.
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Return the number of internal widgets used in this adapter.
	 * 
	 * @return the number of internal widgets used in this adapter.
	 */
	public abstract int getNumberOfControls();

	/**
	 * Returns the script parameter to be edited by this adapter.
	 * 
	 * @return the script parameter to be edited by this adapter.
	 */
	public ScriptParameter getParameter() {
		return param;
	}

	/**
	 * Returns the current value of the main control.
	 * 
	 * @return the current value of the main control.
	 */
	public abstract String getValue();

	/**
	 * Hooks the value change notification to the appropriate SWT event.
	 */
	protected abstract void hookValueListener();

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the receiver's value is modified.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified.
	 */
	public void removeValueListener(DatatypeAdapterValueListener listener) {
		valueListeners.remove(listener);
	}

	/**
	 * Sets the value of the main control to <code>value</code>.
	 * 
	 * @param value
	 *            The new value to set to the main control.)
	 */
	public abstract void setValue(String value);
}
