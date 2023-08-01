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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.customComponents.Icons;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.FileInfoList;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.util.Export;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;

public class FileInfoUI extends JFrame {
    private JButton nextButton;
    private JTable files;
    private JButton previousButton;
    private JLabel page;
    private JButton editButton;
    private JButton dataButton;
    private JButton exportButton;
    private JPanel contentPane;

    private final Project project;


    public FileInfoUI(FileInfoList fileInfoList, Project project) {
        this.project = project;
        previousButton.setIcon(Icons.NAVIGATE_BEFORE_16);
        nextButton.setIcon(Icons.NAVIGATE_NEXT_16);
        setContentPane(contentPane);
        setTitle("Projects");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        files.setModel(fileInfoList);
        files.getTableHeader().setResizingAllowed(false);
        files.getTableHeader().setReorderingAllowed(false);
        files.setShowGrid(false);
        files.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        files.getColumnModel().getColumn(0).setPreferredWidth(500);
        files.getColumnModel().getColumn(1).setPreferredWidth(100);
        files.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        RightUI rightUI = new RightUI();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setUI(rightUI);
        files.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);

        setMinimumSize(new Dimension(640, 300));

        pack();
        setLocationRelativeTo(null);

        previousButton.addActionListener(e -> {
            fileInfoList.setCurrentPage(fileInfoList.getCurrentPage() - 1);
            page.setText(String.valueOf(fileInfoList.getCurrentPage() + 1));
        });

        nextButton.addActionListener(e -> {
            fileInfoList.setCurrentPage(fileInfoList.getCurrentPage() + 1);
            page.setText(String.valueOf(fileInfoList.getCurrentPage() + 1));
        });

        dataButton.addActionListener(e -> {
            int[] selectedRow = files.getSelectedRows();
            if (selectedRow.length < 1) return;
            int selected = selectedRow[0];
            if (selected < 0 || selected >= fileInfoList.getFiles().size()) return;
            FileInfo fileInfo = fileInfoList.getFiles().get(selected);
            if (fileInfo == null) return;
            MetaData metaData = MetaData.queryOrCreate(fileInfo, project);
            MetaViewerUI metaView = new MetaViewerUI(metaData.toString());
            metaView.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int[] selectedRow = files.getSelectedRows();
            if (selectedRow.length < 1) return;
            int selected = selectedRow[0];
            if (selected < 0 || selected >= fileInfoList.getFiles().size()) return;
            FileInfo fileInfo = fileInfoList.getFiles().get(selected);
            if (fileInfo == null) return;
            MetaData metaData = MetaData.queryOrCreate(fileInfo, project);
            AnnotateUI metaView = new AnnotateUI(project, metaData);
            metaView.setVisible(true);
        });

        exportButton.addActionListener(e -> export());

    }

    private void export() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML encoded", "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("YAML encoded", "yaml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON encoded", "json"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                return;
            }

            FileFilter filter = fileChooser.getFileFilter();
            if (filter instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter) filter;
                if (fileNameExtensionFilter.getExtensions().length > 0) {
                    String extension = fileNameExtensionFilter.getExtensions()[0];
                    if (!selectedFile.getName().endsWith("." + extension)) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
                    }
                }
            }

            //noinspection ResultOfMethodCallIgnored
            selectedFile.getParentFile().mkdirs();

            Export export = new Export(selectedFile);
            export.export(project);
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
        contentPane.setLayout(new GridLayoutManager(3, 4, new Insets(8, 8, 8, 8), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(620, -1), null, null, 0, false));
        files = new JTable();
        scrollPane1.setViewportView(files);
        editButton = new JButton();
        editButton.setText("Edit");
        contentPane.add(editButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        previousButton = new JButton();
        previousButton.setText("");
        previousButton.setToolTipText("Previous");
        panel1.add(previousButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(11, 30), null, 0, false));
        page = new JLabel();
        page.setHorizontalAlignment(0);
        page.setHorizontalTextPosition(0);
        page.setText("1");
        panel1.add(page, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, -1), null, null, 0, false));
        nextButton = new JButton();
        nextButton.setText("");
        nextButton.setToolTipText("Next");
        panel1.add(nextButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        dataButton = new JButton();
        dataButton.setText("Inspect");
        contentPane.add(dataButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setText("Export");
        contentPane.add(exportButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private static class RightUI extends BasicLabelUI {
        @Override
        protected String layoutCL(
                JLabel label, FontMetrics fontMetrics, String text, Icon icon,
                Rectangle viewR, Rectangle iconR, Rectangle textR) {
            String s = super.layoutCL(label, fontMetrics, new StringBuilder(text).reverse().toString(), icon, viewR, iconR, textR);
            return new StringBuilder(s).reverse().toString();
        }
    }
}
