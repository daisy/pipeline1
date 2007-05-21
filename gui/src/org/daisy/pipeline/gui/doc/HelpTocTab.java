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
        return "Help";
    }

    @Override
    protected String getToolTipText() {
        return "Help contents";
    }
}
