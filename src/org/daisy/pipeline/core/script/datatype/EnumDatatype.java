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
package org.daisy.pipeline.core.script.datatype;

import java.util.List;

/**
 * A datatype for enumeration values.
 * @author Linus Ericson
 */
public class EnumDatatype extends Datatype {

	private List<EnumItem> items;
	
	/**
	 * Constructor.
	 * @param items a list of EnumItem this enum should contain
	 */
	public EnumDatatype(List<EnumItem> items) {
		super(Type.ENUM);
		this.items = items;
	}

	/**
	 * Gets the list of enum items.
	 * @return a list of enum items contained in this EnumDatatype
	 */
	public List<EnumItem> getItems() {
		return items;
	}

	@Override
	public void validate(String value) throws DatatypeException {
		for (EnumItem item : items) {
			if (item.getValue().equals(value)) {
				return;
			}
		}
		throw new DatatypeException("Value '" + value + "' does not match any items in this enum.");
	}

}
