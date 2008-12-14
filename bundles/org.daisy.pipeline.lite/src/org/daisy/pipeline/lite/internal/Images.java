package org.daisy.pipeline.lite.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Images {
	public static final String PIPELINE_LOGO = "pipeline-icon.gif";
	public static final String MESSAGE_DEBUG = "message-debug.gif";
	public static final String MESSAGE_ERROR = "message-error.gif";
	public static final String MESSAGE_INFO_FINER = "message-info-finer.gif";
	public static final String MESSAGE_INFO = "message-info.gif";
	public static final String MESSAGE_WARNING = "message-warning.gif";
	private static ImageRegistry registry;

	public static Image getImage(String key) {
		return getRegistry().get(key);
	}

	private static ImageRegistry getRegistry() {
		if (registry == null) {
			registry = new ImageRegistry();
			init();
		}
		return registry;
	}

	private static void init() {
		registry.put(PIPELINE_LOGO, new Image(Display.getDefault(),
				Images.class.getResourceAsStream(PIPELINE_LOGO)));
		registry.put(MESSAGE_DEBUG, new Image(Display.getDefault(),
				Images.class.getResourceAsStream(MESSAGE_DEBUG)));
		registry.put(MESSAGE_ERROR, new Image(Display.getDefault(),
				Images.class.getResourceAsStream(MESSAGE_ERROR)));
		registry.put(MESSAGE_INFO, new Image(Display.getDefault(), Images.class
				.getResourceAsStream(MESSAGE_INFO)));
		registry.put(MESSAGE_INFO_FINER, new Image(Display.getDefault(),
				Images.class.getResourceAsStream(MESSAGE_INFO_FINER)));
		registry.put(MESSAGE_WARNING, new Image(Display.getDefault(),
				Images.class.getResourceAsStream(MESSAGE_WARNING)));
	}

}
