package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.util.file.EFolder;


/**
 * @author Romain Deltour
 * 
 */
public class HelpTocTab extends TocTab {

    @Override
    protected EFolder getRootDir() {
        return PipelineUtil.getDir(PipelineUtil.USER_DOC_DIR_PATH);
    }

    @Override
    protected String getTitle() {
        return Messages.tab_help;
    }

    @Override
    protected String getToolTipText() {
        return Messages.tab_help_tooltip;
    }
}
