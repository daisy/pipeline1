package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.pipeline.gui.PipelineGuiPlugin;

public class ScriptManager {
    public static final String SCRIPT_DIR = "scripts";
    private static ScriptManager instance;
    private File scriptDir;
    private Map<String, ScriptHandler> scriptHandlerMap;

    private ScriptManager() {
        scriptDir = PipelineGuiPlugin.getResourceFile(SCRIPT_DIR);
        scriptHandlerMap = new HashMap<String, ScriptHandler>();
        populateScriptHandlerMap();
    }

    public static ScriptManager getDefault() {
        if (instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }

    public File getScriptDir() {
        return scriptDir;
    }
    
    public ScriptHandler getScript(String path) {
        return scriptHandlerMap.get(path);
    }

    // TODO old GUI code
    private void populateScriptHandlerMap() {
        // mg: this is not recursive, ie only allows one level subdirs
        // nor supports scripts as direct descendants of main scriptdir
        // remember that users may add their own scrips so this needs
        // improvement
        // if we dont need the categories sort here, we can just use EFolder
        // Collection scripts = scriptDirAsEFolder.getFiles((deep=true,
        // ".*\.xml", false);
        // also to avoid getting most of those 999 exceptions from createScript
        // in developer mode
        // when the file is not a script

        // For each file in the subdirectory, create a ScriptHandler object.
        // create an hashMap to hold the script handlers
        // key = filename
        // value = scripthandler of the file

        File[] arrayFiles = null;

        // System.out.println("is scriptdir set? " + scriptDirectory.getPath());

        if (scriptDir.isDirectory()) {
            // Find list of files in directory
            arrayFiles = scriptDir.listFiles();
        }
        // for each directory, again list files.
        // create a scripthandler object from file (not directory)
        // add to the script handler hashmap

        for (int i = 0; i < arrayFiles.length; i++) {
            File categoryDir = (File) arrayFiles[i];
            // System.out.println("Name of category " + categoryDir.getName());

            if (categoryDir.isDirectory()) {
                File[] arCatFiles = categoryDir.listFiles();

                // create script handlers for each file in subdirectory
                for (int j = 0; j < arCatFiles.length; j++) {
                    File toSH = (File) arCatFiles[j];
                    // System.out.println(" Name of file in category " +
                    // toSH.getName());

                    // mg: all System.out.println statements should be
                    // surrounded by a
                    // debug test clause, will make the app snappier
                    // I have started using:
                    // if(System.getProperty("org.daisy.debug")!=null) {
                    // System.out.println("blah");
                    // }
                    // so its easily switched on/off without excessive code

                    try {
                        // TODO fake method
                        ScriptHandler sh = new ScriptHandler();
                        // ScriptHandler sh = dmfc.createScript(toSH);
                        // add to HashMap
                        // key, name of file
                        // value: ScriptHandler object
                        scriptHandlerMap.put(toSH.getPath(), sh);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // catch(ValidationException ve){
                    // ve.getMessage();
                    // ve.printStackTrace();
                    // }
                    // catch(MIMEException me){
                    // me.getMessage();
                    // me.printStackTrace();
                    // }
                    // catch(ScriptException se){
                    // //add error messages to be thrown to GUI
                    // se.printStackTrace();
                    // }
                }
            }
        }
    }
}
