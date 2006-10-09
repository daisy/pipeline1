/**
 *DAISY Multi-Format Converter, or DAISY Pipeline Graphical User Interface.
Copyright (C) 2006 DAISY Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

public class FormAttachmentsHelper {

	FormData fa;
	
	public FormAttachmentsHelper(){}
	
	public void setFormData(FormData fd, int topa, int topb, int lefta, int leftb, 
			int bottoma, int bottomb, int righta, int rightb){
	
		this.fa =fd;
		fa.top = new FormAttachment(topa, topb);
		fa.left = new FormAttachment(lefta, leftb);
		fa.bottom = new FormAttachment(bottoma, bottomb);
		fa.right = new FormAttachment(righta,rightb);
	}
}
