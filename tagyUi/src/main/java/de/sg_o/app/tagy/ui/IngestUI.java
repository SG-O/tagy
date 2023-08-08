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

package de.sg_o.app.tagy.ui;

import com.github.weisj.darklaf.theme.ColorPalette;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.tagy.customComponents.Icons;
import de.sg_o.lib.tagy.data.DataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

public class IngestUI extends JDialog {
    private final Preferences prefs = Preferences.userRoot().node("de/sg-o/app/tagy");

    private JButton ingestButton;
    private JButton cancelButton;
    private JButton addButton;
    private JButton removeButton;
    private JTable directories;
    private JPanel contentPane;
    private JButton saveButton;
    private JScrollPane directoriesScrollPane;
    private JButton clear;

    private final DataManager dataManager;

    public IngestUI(DataManager dataManager) {
        addButton.setIcon(Icons.ADD_20);
        removeButton.setIcon(Icons.REMOVE_20);
        this.dataManager = dataManager;
        setTitle(getMessageFromBundle("translations/formText", "form.title.ingest"));
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(saveButton);
        directories.setModel(dataManager);
        directories.getTableHeader().setResizingAllowed(false);
        directories.getTableHeader().setReorderingAllowed(false);
        directories.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        directories.setShowGrid(false);
        directories.getColumnModel().getColumn(0).setPreferredWidth(200);
        directories.getColumnModel().getColumn(1).setPreferredWidth(100);
        directories.getColumnModel().getColumn(2).setPreferredWidth(400);
        directories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pack();
        setLocationRelativeTo(null);

        addButton.addActionListener(e -> addDirectory());
        addButton.setMnemonic(KeyEvent.VK_PLUS);

        removeButton.addActionListener(e -> dataManager.removeDirectory(directories.getSelectedRow()));
        removeButton.setMnemonic(KeyEvent.VK_MINUS);

        saveButton.addActionListener(e -> save());
        saveButton.setMnemonic(KeyEvent.VK_ENTER);

        ingestButton.addActionListener(e -> ingest());
        ingestButton.setMnemonic(KeyEvent.VK_I);

        cancelButton.addActionListener(e -> onCancel());
        cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);

        clear.setBackground(ColorPalette.RED);
        clear.addActionListener(e -> clear());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        getRootPane().registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void save() {
        dataManager.save();
        dispose();
    }

    private void ingest() {
        dataManager.save();
        dataManager.ingest();
        dispose();
    }

    private void clear() {
        dataManager.clear();
        dataManager.save();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void addDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        String lastUsed = prefs.get("lastOpenedDirectory", System.getProperty("user.home"));
        fileChooser.setCurrentDirectory(new File(lastUsed));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }
            prefs.put("lastOpenedDirectory", selectedFile.getAbsolutePath());
            dataManager.addDirectory(selectedFile);
        }
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
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(8, 8, 8, 8), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(160, 34), null, 0, false));
        cancelButton = new JButton();
        this.$$$loadButtonText$$$(cancelButton, this.$$$getMessageFromBundle$$$("translations/formText", "button.cancel"));
        cancelButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.cancel"));
        panel2.add(cancelButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        this.$$$loadButtonText$$$(saveButton, this.$$$getMessageFromBundle$$$("translations/formText", "button.save"));
        saveButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.save"));
        panel2.add(saveButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ingestButton = new JButton();
        this.$$$loadButtonText$$$(ingestButton, this.$$$getMessageFromBundle$$$("translations/formText", "button.save.ingest"));
        ingestButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.ingest"));
        panel2.add(ingestButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clear = new JButton();
        this.$$$loadButtonText$$$(clear, this.$$$getMessageFromBundle$$$("translations/formText", "button.removeIngested"));
        clear.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.removeIngested"));
        panel1.add(clear, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(800, -1), null, null, 0, false));
        directoriesScrollPane = new JScrollPane();
        panel3.add(directoriesScrollPane, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        directories = new JTable();
        directoriesScrollPane.setViewportView(directories);
        addButton = new JButton();
        addButton.setHideActionText(true);
        addButton.setText("");
        addButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.add"));
        panel3.add(addButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setHideActionText(true);
        removeButton.setText("");
        removeButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.remove"));
        panel3.add(removeButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
