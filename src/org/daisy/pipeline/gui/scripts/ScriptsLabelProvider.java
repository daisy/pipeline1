package org.daisy.pipeline.gui.scripts;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class ScriptsLabelProvider extends LabelProvider implements ILabelProvider {

    private ScriptManager scriptMan;

    public ScriptsLabelProvider() {
        scriptMan = ScriptManager.getDefault();
    }

    /**
     * Returns the text for the label of the given script file.
     * 
     * There is a hierarchy in the file structure that organises the.
     * transformers by type:
     * <ul>
     * <li>Directories does not have associated script handler objects, they
     * must be labelled by their name.</li>
     * <li>Script file does have an associated script handler, they are
     * labelled with teh script name retrieved from the handler. </li>
     * </ul>
     * 
     * @param file A File in the script file tree.
     */
    @Override
    public String getText(Object file) {
        String text;
        String path = ((File) file).getPath();
        ScriptHandler script = scriptMan.getScript(path);
        if (script != null) {
            text = script.getName();
        } else {
            text = ((File) file).getName();
        }
        return text;
    }
}
