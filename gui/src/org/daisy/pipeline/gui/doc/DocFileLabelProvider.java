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
package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.XhtmlTitleProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class DocFileLabelProvider extends LabelProvider {
    private TocImageProvider imageProvider;
    private Map<File, String> titleMap;

    public DocFileLabelProvider(TocImageProvider imageProvider) {
        super();
        this.imageProvider = imageProvider;
        this.titleMap = new HashMap<File, String>();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof File) {
            File file = (File) element;
            String title = titleMap.get(file);
            if (title == null) {
                if (file.isFile()) {
                    try {
                        title = XhtmlTitleProvider.getTitle(file.toURI()
                                .toURL());
                    } catch (Exception e) {
                        GuiPlugin.get().error(
                                "Couldn't fetch xhtml title of " + file, e); //$NON-NLS-1$
                    }
                }
                if (title == null || title.length() == 0) {
                    title = file.getName();
                }
                titleMap.put(file, title);
            }
            return title;
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        return imageProvider.getImage(element);
    }

}
