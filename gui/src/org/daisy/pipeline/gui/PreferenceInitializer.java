package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public PreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        try {
            PreferencesUtil.put(PipelineUtil.PATH_TO_TEMP_DIR, File
                    .createTempFile("dont", "care").getParent(),
                    new DefaultScope());
        } catch (IOException e) {
            GuiPlugin.get().warn("Couldn't find the default temp directory", e);
        }
    }

}
