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
