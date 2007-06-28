package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    private IScopeContext defaultScope = new DefaultScope();

    public PreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        initCommonDefaults();
        if (System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$
            initWindowsDefaults();
        } else if (System.getProperty("os.name").startsWith("Mac OS X")) { //$NON-NLS-1$ //$NON-NLS-2$
            initMacDefaults();
        } else if (System.getProperty("os.name").startsWith("Linux") //$NON-NLS-1$ //$NON-NLS-2$
                || System.getProperty("os.name").startsWith("LINUX")) { //$NON-NLS-1$ //$NON-NLS-2$
            initLinuxDefaults();
        }

    }

    private void initCommonDefaults() {
        // set the default temp dir
        try {
            PreferencesUtil.put(PipelineUtil.PATH_TO_TEMP_DIR, File
                    .createTempFile("dont", "care").getParent(), //$NON-NLS-1$ //$NON-NLS-2$
                    defaultScope);
        } catch (IOException e) {
            GuiPlugin.get().warn("Couldn't find the default temp directory", e); //$NON-NLS-1$
        }
    }

    private void initLinuxDefaults() {
        setPrefPath(PipelineUtil.PATH_TO_LAME, "/usr/bin/lame");//$NON-NLS-1$
        setPrefPath(PipelineUtil.PATH_TO_PYTHON, "/usr/bin/python");//$NON-NLS-1$
    }

    private void initMacDefaults() {
        setPrefPath(PipelineUtil.PATH_TO_LAME, "/sw/bin/lame");//$NON-NLS-1$
        setPrefPath(PipelineUtil.PATH_TO_PYTHON, "/usr/bin/python");//$NON-NLS-1$
    }

    private void initWindowsDefaults() {
        setPrefPath(PipelineUtil.PATH_TO_LAME, "C:\\lame\\lame.exe");//$NON-NLS-1$
        setPrefPath(PipelineUtil.PATH_TO_PYTHON, "C:\\Python25\\python.exe");//$NON-NLS-1$
    }

    private void setPrefPath(String pref, String path) {
        File file = new File(path);
        if (file.exists()) {
            PreferencesUtil.put(pref, path, defaultScope);
        } else {
            PreferencesUtil.put(pref, "", defaultScope);//$NON-NLS-1$
        }
    }
}
