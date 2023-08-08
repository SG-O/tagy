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

package de.sg_o.app.tagy;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.settings.SettingsConfiguration;
import com.github.weisj.darklaf.settings.ThemeSettings;
import de.sg_o.app.tagy.ui.ProjectsUI;
import de.sg_o.lib.tagy.db.DB;
import io.objectbox.exception.DbSchemaException;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.prefs.Preferences;

import static com.github.weisj.darklaf.LafManager.getPreferredThemeStyle;

public class Init {
    private static final Preferences prefs = Preferences.userRoot().node("de/sg-o/app/tagy");
    public static final List<ResourceBundle> FORM_TEXT = getResourceBundles("translations/formText");

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

        String language = prefs.get("language", "en");
        Locale.setDefault(new Locale(language));

        String lastUsed = prefs.get("lastOpenedDb", null);
        if (lastUsed != null) {
            File lastUsedFile = new File(lastUsed);
            try {
                DB.initDb(lastUsedFile, false);
            } catch (DbSchemaException e) {
                System.out.println(e.getMessage());
            }
        }

        ProjectsUI projectsUI = new ProjectsUI();
        SwingUtilities.invokeLater(() -> projectsUI.setVisible(true));
    }

    public static List<ResourceBundle> getResourceBundles(String baseName) {
        HashSet<ResourceBundle> resourceBundles = new HashSet<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                resourceBundles.add(ResourceBundle.getBundle(baseName, locale));
            } catch (MissingResourceException ignore) {
            }
        }

        return Collections.unmodifiableList(new ArrayList<>(resourceBundles));
    }
}
