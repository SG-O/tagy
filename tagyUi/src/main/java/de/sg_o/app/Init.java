/*
 *
 *      Copyright (C) 2023 Joerg Bayer (SG-O)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.sg_o.app;

import com.couchbase.lite.CouchbaseLiteException;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.settings.SettingsConfiguration;
import com.github.weisj.darklaf.settings.ThemeSettings;
import de.sg_o.app.ui.ProjectsUI;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.NewDB;

import javax.swing.SwingUtilities;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.prefs.Preferences;

import static com.github.weisj.darklaf.LafManager.getPreferredThemeStyle;

public class Init {
    private static final Preferences prefs = Preferences.userRoot().node("de/sg-o/app/tagy");

    public static void main(String[] args) {
        LafManager.themeForPreferredStyle(getPreferredThemeStyle());
        LafManager.install();

        ThemeSettings settings = ThemeSettings.getInstance();
        byte[] serializedSettings = prefs.getByteArray("themeSettings", null);
        if (serializedSettings != null) {
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(serializedSettings))) {
                SettingsConfiguration config = (SettingsConfiguration) in.readObject();
                settings.setConfiguration(config);
            } catch (IOException | ClassNotFoundException ignore) {
            }
        }
        settings.apply();

        try {
            Thread.sleep(100); //Make sure theme is loaded and applied
        } catch (InterruptedException ignore) {
        }

        String lastUsed = prefs.get("lastOpenedDb", null);
        if (lastUsed != null) {
            File lastUsedFile = new File(lastUsed);
            NewDB.initDb(lastUsedFile, false);
        }

        ProjectsUI projectsUI = new ProjectsUI();
        SwingUtilities.invokeLater(() -> projectsUI.setVisible(true));
    }
}
