package org.daisy.pipeline.gui.util.jface;

import java.util.Set;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.graphics.Image;

public interface ICompositeLabelProvider extends IBaseLabelProvider {
    public Set<String> getImageKeys();

    public Set<String> getIntKeys();

    public Set<String> getTextKeys();

    public int getInt(String key, Object element);

    public Image getImage(String key, Object element);

    public String getText(String key, Object element);
}
