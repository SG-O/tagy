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
import de.sg_o.app.customComponents.Viewer;
import de.sg_o.app.customComponents.WrapLayout;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class AnnotateUI extends JFrame {
    private JPanel variables;
    private JButton doneButton;
    private JLabel errorMessage;
    private JScrollPane valueScrollPane;
    private JLabel fileName;
    private JPanel viewerHolder;
    private JPanel contentPane;
    private final Project project;
    private ArrayList<Input> inputs;

    private final DataManager dataManager;
    private MetaData metaData;
    private Viewer viewer;

    public AnnotateUI(@NotNull Project project, MetaData toView) throws HeadlessException {
        super();
        this.project = project;
        $$$setupUI$$$();
        setContentPane(contentPane);
        setTitle("Annotation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        dataManager = new DataManager(this.project);


        doneButton.addActionListener(e -> {
            if (metaData == null) {
                if (toView != null) {
                    this.dispose();
                } else {
                    showNext(null);
                }
                return;
            }
            for (Input input : inputs) {
                try {
                    Tag tag = input.getTag();
                    if (tag == null) continue;
                    metaData.addTag(tag);
                } catch (InputException ex) {
                    errorMessage.setText(ex.toString());
                    errorMessage.revalidate();
                    errorMessage.repaint();
                    return;
                }
            }
            errorMessage.setText("OK");
            metaData.save();
            System.out.println(metaData);
            if (toView != null) {
                this.dispose();
            } else {
                showNext(null);
            }

        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                inputs = Input.parseProject(project);
                for (Input input : inputs) {
                    variables.add(input.getModule());
                }
                viewer = new Viewer(inputs);
                viewerHolder.add(viewer, BorderLayout.CENTER);
                showNext(toView);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                viewer.close();
            }
        });

        contentPane.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent evt) {
                contentPane.revalidate();
                contentPane.repaint();
            }
        });

        setMinimumSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(null);
    }

    public void showNext(MetaData toView) {
        if (toView != null) {
            metaData = toView;
        } else {
            FileInfo fileInfo = dataManager.getNextFile();
            if (fileInfo == null) {
                for (Input input : inputs) {
                    input.reset(null);
                }
                errorMessage.setText("No more files that need annotating");
                fileName.setText("");
                metaData = null;
                return;
            }
            metaData = new MetaData(fileInfo, project);
        }
        fileName.setText(metaData.getReference().getId());
        viewer.display(metaData.getReference());

        HashMap<String, Tag> tags = metaData.getTags();

        for (Input input : inputs) {
            Tag tag = null;
            if (tags.containsKey(input.getTagDefinition().getKey())) {
                tag = tags.get(input.getTagDefinition().getKey());
            }
            input.reset(tag);
        }

        revalidate();
        repaint();
    }

    private void createUIComponents() {
        variables = new JPanel();
        variables.setLayout(new WrapLayout(WrapLayout.LEFT, 5, 5));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(4, 3, new Insets(8, 8, 8, 8), -1, -1));
        doneButton = new JButton();
        doneButton.setText("Done");
        contentPane.add(doneButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        errorMessage = new JLabel();
        errorMessage.setText("OK");
        contentPane.add(errorMessage, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        valueScrollPane = new JScrollPane();
        valueScrollPane.setHorizontalScrollBarPolicy(31);
        contentPane.add(valueScrollPane, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 180), null, new Dimension(-1, 350), 0, false));
        valueScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        valueScrollPane.setViewportView(variables);
        fileName = new JLabel();
        fileName.setText("FileName");
        contentPane.add(fileName, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewerHolder = new JPanel();
        viewerHolder.setLayout(new BorderLayout(0, 0));
        contentPane.add(viewerHolder, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, 100), new Dimension(640, 480), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
