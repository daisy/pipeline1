/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

/**
 * @author Romain Deltour
 * 
 */
public class PreferencesUtil {

    private static IScopeContext[] contexts = new IScopeContext[] {
            new InstanceScope(), new ConfigurationScope(), new DefaultScope() };

    private PreferencesUtil() {
    }
    
    public static void initDefaults() {
        Platform.getPreferencesService().getRootNode();
    }

    public static String get(String key, String def) {
        return Platform.getPreferencesService().getString(GuiPlugin.ID, key,
                def, contexts);
    }

    public static boolean getBoolean(String key, boolean def) {
        return Platform.getPreferencesService().getBoolean(GuiPlugin.ID, key,
                def, contexts);
    }

    public static byte[] getByteArray(String key, byte[] def) {
        return Platform.getPreferencesService().getByteArray(GuiPlugin.ID, key,
                def, contexts);
    }

    public static double getDouble(String key, double def) {
        return Platform.getPreferencesService().getDouble(GuiPlugin.ID, key,
                def, contexts);
    }

    public static float getFloat(String key, float def) {
        return Platform.getPreferencesService().getFloat(GuiPlugin.ID, key,
                def, contexts);
    }

    public static int getInt(String key, int def) {
        return Platform.getPreferencesService().getInt(GuiPlugin.ID, key, def,
                contexts);
    }

    public static long getLong(String key, long def) {
        return Platform.getPreferencesService().getLong(GuiPlugin.ID, key, def,
                contexts);
    }

    public static void put(String key, String value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.put(key, value);
    }

    public static void putBoolean(String key, boolean value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putBoolean(key, value);
    }

    public static void putByteArray(String key, byte[] value,
            IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putByteArray(key, value);
    }

    public static void putDouble(String key, double value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putDouble(key, value);
    }

    public static void putFloat(String key, float value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putFloat(key, value);
    }

    public static void putInt(String key, int value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putInt(key, value);
    }

    public static void putLong(String key, long value, IScopeContext scope) {
        Preferences node = scope.getNode(GuiPlugin.ID);
        if (node != null)
            node.putLong(key, value);
    }
}
