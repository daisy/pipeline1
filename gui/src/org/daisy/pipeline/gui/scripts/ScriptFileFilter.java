package org.daisy.pipeline.gui.scripts;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.FileFileFilter;
import org.daisy.util.file.EFile;
import org.daisy.util.xml.peek.PeekResult;
import org.daisy.util.xml.peek.Peeker;
import org.daisy.util.xml.peek.PeekerPool;

/**
 * @author Romain Deltour
 * 
 */
public class ScriptFileFilter extends FileFileFilter {

    public ScriptFileFilter(boolean acceptDir) {
        super(acceptDir);
    }

    @Override
    protected boolean acceptEFile(EFile file) {
        Peeker peeker = null;
        try {
            peeker = PeekerPool.getInstance().acquire();
            PeekResult result = peeker.peek(file);
            return "taskScript".equals(result.getRootElementLocalName());
        } catch (Exception e) {
            GuiPlugin.get().error(
                    "Couldn't peek in file " + file.getAbsolutePath(), e);
        }
        return false;
    }
}
