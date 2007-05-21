package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.util.file.EFolder;

/**
 * @author Romain Deltour
 * 
 */
public class TransformersTocTab extends TocTab {

    @Override
    protected EFolder getRootDir() {
        return PipelineUtil.getDir(PipelineUtil.TRANS_DOC_DIR_PATH);
    }

    @Override
    protected String getTitle() {
        return "Transformers";
    }

    @Override
    protected String getToolTipText() {
        return "Transformers documentation contents";
    }
}
