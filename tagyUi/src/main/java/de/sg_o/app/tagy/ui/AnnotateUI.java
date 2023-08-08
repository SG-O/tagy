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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.tagy.annotator.Input;
import de.sg_o.app.tagy.annotator.InputHolder;
import de.sg_o.app.tagy.customComponents.Viewer;
import de.sg_o.app.tagy.customComponents.WrapLayout;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.exceptions.InputException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

public class AnnotateUI extends JFrame {
    private JPanel variables;
    private JButton doneButton;
    private JLabel errorMessage;
    private JScrollPane valueScrollPane;
    private JLabel fileName;
    private JPanel viewerHolder;
    private JPanel contentPane;
    private final Project project;

    private static final Preferences prefs = Preferences.userRoot().node("de/sg-o/app/tagy");

    private final DataManager dataManager;
    private final InputHolder inputHolder;
    private MetaData metaData;
    private final MetaData toView;
    private Viewer viewer;

    public AnnotateUI(@NotNull Project project, MetaData toView) throws HeadlessException {
        super();
        this.project = project;
        this.toView = toView;
        $$$setupUI$$$();
        setContentPane(contentPane);
        contentPane.setFocusTraversalKeysEnabled(false);
        setTitle(getMessageFromBundle("translations/formText", "form.title.annotation"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        dataManager = new DataManager(this.project);
        inputHolder = new InputHolder(this.project);

        doneButton.setMnemonic(KeyEvent.VK_ENTER);
        doneButton.addActionListener(e -> doneAction());

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        KeyEventPostProcessor keyEventPostProcessor = e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                AnnotateUI.this.doneAction();
                return true;
            }
            return false;
        };

        manager.addKeyEventPostProcessor(keyEventPostProcessor);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                ArrayList<Input> inputs = inputHolder.getInputs();
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
                manager.removeKeyEventPostProcessor(keyEventPostProcessor);
            }
        });

        contentPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                contentPane.revalidate();
                contentPane.repaint();
            }
        });

        setMinimumSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(null);
    }

    private void doneAction() {
        if (metaData == null) {
            if (toView != null) {
                this.dispose();
            } else {
                showNext(null);
            }
            return;
        }
        try {
            metaData = inputHolder.getData();
        } catch (InputException ex) {
            errorMessage.setText(ex.toString());
            errorMessage.revalidate();
            errorMessage.repaint();
            return;
        }
        errorMessage.setText("OK");
        metaData.save();
        System.out.println(metaData);
        if (toView != null) {
            this.dispose();
        } else {
            showNext(null);
        }
    }

    public void showNext(MetaData toView) {
        if (toView != null) {
            metaData = toView;
        } else {
            FileInfo fileInfo = dataManager.getNextFile();
            if (fileInfo == null) {
                inputHolder.reset();
                errorMessage.setText(this.$$$getMessageFromBundle$$$("translations/formText", "message.noMoreFiles"));
                fileName.setText("");
                metaData = null;
                return;
            }
            metaData = MetaData.openOrCreate(fileInfo, project);
        }
        fileName.setText(metaData.getFileReference());
        viewer.display(metaData.resolveFileReference());

        inputHolder.setData(metaData);

        revalidate();
        repaint();
    }

    private void createUIComponents() {
        variables = new JPanel();
        valueScrollPane = new JScrollPane();
        if (prefs.getBoolean("singleRow", false)) {
            variables.setLayout(new BoxLayout(variables, BoxLayout.X_AXIS));
            valueScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            valueScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        } else {
            variables.setLayout(new WrapLayout(WrapLayout.LEFT, 5, 5));
            valueScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            valueScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        valueScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
        this.$$$loadButtonText$$$(doneButton, this.$$$getMessageFromBundle$$$("translations/formText", "button.done"));
        contentPane.add(doneButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        errorMessage = new JLabel();
        errorMessage.setText("OK");
        contentPane.add(errorMessage, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
