package org.daisy.pipeline.lite.internal;

import org.daisy.pipeline.core.event.MessageEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MessageLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof MessageEvent) {
			switch ((((MessageEvent) element).getType())) {
			case DEBUG:
				return Images.getImage(Images.MESSAGE_DEBUG);
			case ERROR:
				return Images.getImage(Images.MESSAGE_ERROR);
			case INFO:
				return Images.getImage(Images.MESSAGE_INFO);
			case INFO_FINER:
				return Images.getImage(Images.MESSAGE_INFO_FINER);
			case WARNING:
				return Images.getImage(Images.MESSAGE_WARNING);
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof MessageEvent) {
			MessageEvent me = (MessageEvent) element;
			return me.toString(true, false, true, true);
		}
		return super.getText(element);
	}

}
