package org.daisy.pipeline.gui.util;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.util.file.EFile;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

/**
 * @author Romain Deltour
 * 
 */
public class HtmlFileFilter extends FileFileFilter {

    public HtmlFileFilter(boolean acceptDir) {
        super(acceptDir);
    }

    @Override
    protected boolean acceptEFile(EFile file) {
        Peeker peeker = null;
        try {
            peeker = PeekerPool.getInstance().acquire();
            PeekResult result = peeker.peek(file);
            return "html".equals(result.getRootElementLocalName());
        } catch (Exception e) {
            GuiPlugin.get().error(
                    "Couldn't peek in file " + file.getAbsolutePath(), e);
        }
        return false;
    }

}
