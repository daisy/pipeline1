package org.daisy.util.swt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class AnimatedGifPlayer extends Thread {
	Display display;
	GC gc;
	Color background;
	ImageLoader loader;
	ImageData[] imageDataArray;
	Image image;
	boolean useGIFBackground = false;
	Point controlSize;
	boolean isDisposed = false;

	public AnimatedGifPlayer(String fileName, Control control, Display display) {
		try {
			init(new FileInputStream(fileName), control, display);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(""); //$NON-NLS-1$
		}
	}

	public AnimatedGifPlayer(InputStream stream, Control control,
			Display display) {
		init(stream, control, display);
	}

	private void init(InputStream stream, Control control, Display display) {
		this.display = display;
		controlSize = control.getSize();
		gc = new GC(control);
		background = control.getBackground();
		loader = new ImageLoader();
		imageDataArray = loader.load(stream);
		if (imageDataArray.length <= 1) {
			throw new IllegalArgumentException("not a gif"); //$NON-NLS-1$
		}
		setDaemon(true);
		control.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {

				controlSize = ((Control) event.widget).getSize();
			}
		});
	}

	@Override
	public void run() {
		/*
		 * Create an off-screen image to draw on, and fill it with the shell
		 * background.
		 */
		Image offscreenImage = new Image(display, loader.logicalScreenWidth,
				loader.logicalScreenHeight);
		GC offscreenGC = new GC(offscreenImage);
		offscreenGC.setBackground(background);
		offscreenGC.fillRectangle(0, 0, loader.logicalScreenWidth,
				loader.logicalScreenHeight);

		try {
			/*
			 * Create the first image and draw it on the off-screen image.
			 */
			int imageDataIndex = 0;
			ImageData imageData = imageDataArray[imageDataIndex];
			if ((image != null) && !image.isDisposed()) {
				image.dispose();
			}
			image = new Image(display, imageData);
			offscreenGC.drawImage(image, 0, 0, imageData.width,
					imageData.height, imageData.x, imageData.y,
					imageData.width, imageData.height);

			/*
			 * Now loop through the images, creating and drawing each one on the
			 * off-screen image before drawing it on the shell.
			 */
			int repeatCount = loader.repeatCount;
			while (!isDisposed
					&& ((loader.repeatCount == 0) || (repeatCount > 0))) {
				switch (imageData.disposalMethod) {
				case SWT.DM_FILL_BACKGROUND:
					/*
					 * Fill with the background color before drawing.
					 */
					Color bgColor = null;
					if (useGIFBackground && (loader.backgroundPixel != -1)) {
						bgColor = new Color(display, imageData.palette
								.getRGB(loader.backgroundPixel));
					}
					offscreenGC.setBackground(bgColor != null ? bgColor
							: background);
					offscreenGC.fillRectangle(imageData.x, imageData.y,
							imageData.width, imageData.height);
					if (bgColor != null) {
						bgColor.dispose();
					}
					break;
				case SWT.DM_FILL_PREVIOUS:
					/*
					 * Restore the previous image before drawing.
					 */
					offscreenGC.drawImage(image, 0, 0, imageData.width,
							imageData.height, imageData.x, imageData.y,
							imageData.width, imageData.height);
					break;
				}

				imageDataIndex = (imageDataIndex + 1) % imageDataArray.length;
				imageData = imageDataArray[imageDataIndex];
				image.dispose();
				image = new Image(display, imageData);
				offscreenGC.drawImage(image, 0, 0, imageData.width,
						imageData.height, imageData.x, imageData.y,
						imageData.width, imageData.height);

				/* Draw the off-screen image to the shell. */
				int x = controlSize.x / 2 - imageData.width / 2;
				int y = controlSize.y / 2 - imageData.height / 2;
				gc.drawImage(offscreenImage, x, y);

				/*
				 * Sleep for the specified delay time (adding commonly-used
				 * slow-down fudge factors).
				 */
				int ms = imageData.delayTime * 10;
				if (ms < 20) {
					ms += 30;
				}
				if (ms < 30) {
					ms += 10;
				}
				try {
					Thread.sleep(ms);
				} catch (InterruptedException e) {
					isDisposed = true;
					break;
				}

				/*
				 * If we have just drawn the last image, decrement the repeat
				 * count and start again.
				 */
				if (imageDataIndex == imageDataArray.length - 1) {
					repeatCount--;
				}
			}
		} catch (SWTException ex) {
			// TODO log the error
			System.out.println("There was an error animating the GIF"); //$NON-NLS-1$
		} finally {
			if ((offscreenImage != null) && !offscreenImage.isDisposed()) {
				offscreenImage.dispose();
			}
			if ((offscreenGC != null) && !offscreenGC.isDisposed()) {
				offscreenGC.dispose();
			}
			if ((image != null) && !image.isDisposed()) {
				image.dispose();
			}
		}
	}

	public void dispose() {
		isDisposed = true;
	}

	public boolean isUseGIFBackground() {
		return useGIFBackground;
	}

	public void setUseGIFBackground(boolean useGIFBackground) {
		this.useGIFBackground = useGIFBackground;
	}

	/**
	 * This is a usage example. It will display a 300x300 splash shell from an
	 * animated GIF.
	 * 
	 * @param display
	 *            the display to open the splash in
	 * @param gif
	 *            a input stream from the GIF
	 * @return the splash shell
	 */
	public static Shell showSplash(Display display, InputStream gif) {
		Shell splash = new Shell(SWT.ON_TOP);
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - 300) / 2;
		int y = (displayRect.height - 300) / 2;
		splash.setBounds(x, y, 300, 300);
		splash.setBackground(splash.getBackground());
		splash.open();
		final AnimatedGifPlayer player = new AnimatedGifPlayer(gif, splash,
				display);
		player.start();
		splash.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				player.dispose();
			}
		});
		return splash;
	}
}
