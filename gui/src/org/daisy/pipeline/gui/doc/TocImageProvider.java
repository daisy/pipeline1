/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.doc;

import java.io.File;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.util.file.EFolder;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class TocImageProvider {

    EFolder root;

    public TocImageProvider(EFolder root) {
        super();
        this.root = root;
    }

    public Image getImage(Object element) {
        if (element instanceof File) {
            File file = (File) element;
            if (file.isDirectory()) {
                if (root != null && root.equals(file.getParentFile())) {
                    return GuiPlugin.getImage(IIconsKeys.HELP_TOC_SECTION);
                } else {
                    return GuiPlugin.getImage(IIconsKeys.HELP_TOC_SUBSECTION);
                }
            } else {
                return GuiPlugin.getImage(IIconsKeys.HELP_TOC_ITEM);
            }
        }
        return null;
    }
}
