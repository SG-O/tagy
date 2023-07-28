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

package de.sg_o.app.ui;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.customComponents.Icons;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.ProjectManager;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.values.User;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class ProjectsUI extends JFrame {
    private JButton annotate;
    private JButton edit;
    private JButton create;
    private JButton open;
    private JButton newDb;
    private JList<String> projectList;
    private JButton settingsButton;
    private JButton ingestDataButton;
    private JButton inspectButton;
    private JPanel contentPane;

    private final ProjectManager projectManager = new ProjectManager();
    private final Preferences prefs = Preferences.userRoot().node("de/sg-o/app/tagy");

    public ProjectsUI() throws HeadlessException {
        super();

        annotate.setIcon(Icons.PLAY_ARROW_24);
        edit.setIcon(Icons.EDIT_24);
        create.setIcon(Icons.ADD_24);
        open.setIcon(Icons.FOLDER_OPEN_24);
        newDb.setIcon(Icons.NOTE_ADD_24);
        settingsButton.setIcon(Icons.SETTINGS_24);
        ingestDataButton.setIcon(Icons.SCAN_24);
        inspectButton.setIcon(Icons.FIND_IN_PAGE_24);

        setContentPane(contentPane);
        setTitle("Projects");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        open.addActionListener(e -> {
            try {
                fileOpen();
            } catch (CouchbaseLiteException ignored) {
            }
        });
        newDb.addActionListener(e -> {
            try {
                fileCreate();
            } catch (CouchbaseLiteException ignored) {
            }
        });
        annotate.addActionListener(e -> annotate());
        edit.addActionListener(e -> edit());
        ingestDataButton.addActionListener(e -> ingestData());
        create.addActionListener(e -> {
            Database db = DB.getDb();
            if (db == null) return;
            CreateProjectUI createProjectUI = new CreateProjectUI(projectManager);
            createProjectUI.setVisible(true);
        });
        settingsButton.addActionListener(e -> {
            Settings settings = new Settings();
            settings.setVisible(true);
        });
        inspectButton.addActionListener(e -> inspectData());
        projectList.setModel(projectManager);

        updateProjectList();
        setMinimumSize(new Dimension(500, 400));
    }

    private void fileCreate() throws CouchbaseLiteException {
        JFileChooser fileChooser = new JFileChooser();
        String lastUsed = prefs.get("lastOpenedDb", System.getProperty("user.home"));
        fileChooser.setCurrentDirectory(new File(lastUsed).getParentFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "Tagy Database (*_tagy.cblite2)";
            }

            public boolean accept(File f) {
                return f.isDirectory();
            }
        });
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }
            if (!selectedFile.getName().endsWith("_tagy.cblite2")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + "_tagy.cblite2");
            }
            //noinspection ResultOfMethodCallIgnored
            selectedFile.getParentFile().mkdirs();
            prefs.put("lastOpenedDb", selectedFile.getAbsolutePath());
            DB.closeDb();
            DB.initDb(selectedFile, true);
        }
        updateProjectList();
    }

    private void fileOpen() throws CouchbaseLiteException {
        JFileChooser fileChooser = new JFileChooser();
        String lastUsed = prefs.get("lastOpenedDb", System.getProperty("user.home"));
        fileChooser.setCurrentDirectory(new File(lastUsed).getParentFile());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }
            prefs.put("lastOpenedDb", selectedFile.getAbsolutePath());
            DB.closeDb();
            DB.initDb(selectedFile, false);
        }
        updateProjectList();
    }

    private void updateProjectList() {
        Database db = DB.getDb();
        if (db != null) {
            setTitle("Projects - " + db.getName() + ".cblite2");
            annotate.setEnabled(true);
            edit.setEnabled(true);
            ingestDataButton.setEnabled(true);
            inspectButton.setEnabled(true);
            create.setEnabled(true);
        } else {
            annotate.setEnabled(false);
            edit.setEnabled(false);
            ingestDataButton.setEnabled(false);
            inspectButton.setEnabled(false);
            create.setEnabled(false);
        }
        if (projectManager.getSize() > 0) {
            projectList.setSelectedIndex(0);
        }
        projectList.revalidate();
        projectList.repaint();
    }

    private void inspectData() {
        String selectedProject = projectManager.getElementAt(projectList.getSelectedIndex());
        if (selectedProject == null) {
            return;
        }

        Project project = new Project(selectedProject, User.getLocalUser());
        FileInfoUI fileInfoUI = new FileInfoUI(new DataManager(project).getFileInfoList(20), project);
        fileInfoUI.setVisible(true);
    }

    private void ingestData() {
        String selectedProject = projectManager.getElementAt(projectList.getSelectedIndex());
        if (selectedProject == null) {
            return;
        }

        Project project = new Project(selectedProject, User.getLocalUser());
        IngestUI ingest = new IngestUI(new DataManager(project));
        ingest.setVisible(true);
    }

    private void edit() {
        String selectedProject = projectManager.getElementAt(projectList.getSelectedIndex());
        if (selectedProject == null) {
            return;
        }

        Project project = new Project(selectedProject, User.getLocalUser());

        EditorUI editorUI = new EditorUI(project);
        editorUI.setVisible(true);
    }

    private void annotate() {
        String selectedProject = projectManager.getElementAt(projectList.getSelectedIndex());
        if (selectedProject == null) {
            return;
        }

        Project project = new Project(selectedProject, User.getLocalUser());

        AnnotateUI annotateUI = new AnnotateUI(project, null);
        annotateUI.setVisible(true);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 6, new Insets(8, 8, 8, 8), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(1, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, 200), null, null, 0, false));
        projectList = new JList();
        scrollPane1.setViewportView(projectList);
        annotate = new JButton();
        annotate.setText("");
        annotate.setToolTipText("Annotate");
        contentPane.add(annotate, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        open = new JButton();
        open.setText("");
        open.setToolTipText("Open");
        contentPane.add(open, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        create = new JButton();
        create.setText("");
        create.setToolTipText("Add");
        contentPane.add(create, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newDb = new JButton();
        newDb.setText("");
        newDb.setToolTipText("New");
        contentPane.add(newDb, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edit = new JButton();
        edit.setText("");
        edit.setToolTipText("Edit");
        contentPane.add(edit, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsButton = new JButton();
        settingsButton.setText("");
        settingsButton.setToolTipText("Settings");
        contentPane.add(settingsButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ingestDataButton = new JButton();
        ingestDataButton.setText("");
        ingestDataButton.setToolTipText("IngestData");
        contentPane.add(ingestDataButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inspectButton = new JButton();
        inspectButton.setText("");
        inspectButton.setToolTipText("Inspect");
        contentPane.add(inspectButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
