package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;

import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.util.file.EFolder;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Romain Deltour
 * 
 */
public class ScriptTocTab extends TocTab {

    @Override
    public URI getURI() {
        IStructuredSelection sel = (IStructuredSelection) getViewer()
                .getSelection();
        File file = (File) sel.getFirstElement();
        if (file != null && file.isFile()) {
            Script script = ScriptManager.getDefault().getScript(file.toURI());
            if (script != null) {
                return script.getDocumentation();
            }
        }
        return null;
    }

    @Override
    protected boolean contains(File file) {
        return convertToTocFile(file) != null;
    }

    @Override
    protected File convertToFile(Object object) {
        Script script = null;
        if (object instanceof Script) {
            script = (Script) object;
        }
        if (object instanceof JobInfo) {
            script = ((JobInfo) object).getJob().getScript();
        }
        if (script != null) {
            URI uri = ScriptManager.getDefault().getURI(script);
            try {
                return new File(uri);
            } catch (Exception e) {
                GuiPlugin.get().error(
                        "Couldn't fetch the script file for "
                                + script.getNicename(), e);
            }
        }
        return null;
    }

    @Override
    protected File convertToTocFile(File file) {
        if (file == null) {
            return null;
        }
        // Test if the file is a script file
        if (ScriptManager.getDefault().getScript(file.toURI()) != null) {
            return file;
        }
        // Test if the file is a doc script file
        for (Script script : ScriptManager.getDefault().getScripts()) {
            if (file.toURI().equals(script.getDocumentation())) {
                try {
                    return new File(ScriptManager.getDefault().getURI(script));
                } catch (IllegalArgumentException e) {
                    GuiPlugin.get().error(
                            "Couldn't create File from script URI", e);
                    break;
                }
            }
        }
        return null;
    }

    @Override
    protected FileFilter createFileFilter() {
        return new ScriptFileFilter(true);
    }

    @Override
    protected IBaseLabelProvider createLabelProvider() {
        return new DocScriptLabelProvider(new TocImageProvider(getRootDir()));
    }

    @Override
    protected EFolder getRootDir() {
        return PipelineUtil.getScriptDir();
    }

    @Override
    protected String getTitle() {
        return "Scripts";
    }

    @Override
    protected String getToolTipText() {
        return "Scripts documentation contents";
    }

}
