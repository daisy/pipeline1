package org.daisy.pipeline.gui.messages;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.AbstractTableField;
import org.daisy.pipeline.gui.util.Category;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
class MessageField extends AbstractTableField {

    @Override
    public Image getImage(Object element) {
        if (element instanceof MessageEvent) {
            switch ((((MessageEvent) element).getType())) {
            case DEBUG:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_DEBUG);
            case ERROR:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_ERROR);
            case INFO:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_INFO);
            case WARNING:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_WARNING);
            }
        }
        if (element instanceof Category) {
            return GuiPlugin.getImage(IIconsKeys.TREE_CATEGORY);
        }
        return null;
    }

    @Override
    public String getHeaderText() {
        return "Message";
    }

    @Override
    public String getText(Object element) {
        if (element instanceof MessageEvent) {
            return ((MessageEvent) element).getMessage();
        }
        if (element instanceof Category) {
            return ((Category) element).getName();
        }
        if (element instanceof Location) {
            Location loc = (Location) element;
            StringBuilder locString = new StringBuilder();
            String sysId = loc.getSystemId();
            try {
                URI uri = new URI(sysId);
                File path = new File(uri.getPath());
                if (sysId != null && sysId.length() > 0) {
                    if (loc.getLineNumber() > -1) {
                        locString.append("At line ");
                        locString.append(loc.getLineNumber());
                        if (loc.getColumnNumber() > -1) {
                            locString.append(", column ");
                            locString.append(loc.getColumnNumber());
                        }
                    }
                    locString.append(" in file '");
                    locString.append(path.getName());
                    locString.append("' [");
                    locString.append(sysId);
                    locString.append(']');
                }
            } catch (URISyntaxException e) {
                GuiPlugin.get().error("Couldn't create URI from SystemID", e);
                locString.append("!err!");
            }
            return locString.toString();
        }
        return super.getText(element);
    }

    @Override
    public int getWeight() {
        return 5;
    }

}
