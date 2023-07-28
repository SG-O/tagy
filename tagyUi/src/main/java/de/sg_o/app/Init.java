package de.sg_o.app;

import com.couchbase.lite.CouchbaseLiteException;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.settings.SettingsConfiguration;
import com.github.weisj.darklaf.settings.ThemeSettings;
import de.sg_o.app.ui.ProjectsUI;
import de.sg_o.lib.tagy.db.DB;

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
            try {
                DB.initDb(lastUsedFile, false);
            } catch (CouchbaseLiteException e) {
                System.out.println("Failed to open db: " + e.getMessage());
            }
        }

        ProjectsUI projectsUI = new ProjectsUI();
        SwingUtilities.invokeLater(() -> projectsUI.setVisible(true));
    }
}
