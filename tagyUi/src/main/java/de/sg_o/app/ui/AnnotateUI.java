package de.sg_o.app.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.customComponents.PlayerControls;
import de.sg_o.app.customComponents.WrapLayout;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.def.Parameter;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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
    private JPanel viewer;
    private JPanel contentPane;
    private final Project project;
    private ArrayList<Input> inputs;

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;

    private final DataManager dataManager;
    private MetaData metaData;
    private PlayerControls controls;


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
                createViewer();
                showNext(toView);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if (controls != null) {
                    controls.close();
                }
                mediaPlayerComponent.mediaPlayer().release();
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
        mediaPlayerComponent.mediaPlayer().media().play(metaData.getReference().getId());

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

    private void createViewer() {
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent("-vv");
        viewer.add(mediaPlayerComponent, BorderLayout.CENTER);
        Input in = null;
        Input out = null;
        Input length = null;

        for (Input input : inputs) {
            if (input.getTagDefinition().getParameter() == Parameter.IN) {
                in = input;
            } else if (input.getTagDefinition().getParameter() == Parameter.OUT) {
                out = input;
            } else if (input.getTagDefinition().getParameter() == Parameter.LENGTH) {
                length = input;
            }
        }

        Input finalLength = length;
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (finalLength != null) {
                    finalLength.setValue(mediaPlayer.status().length());
                }
            }
        });

        controls = new PlayerControls(mediaPlayerComponent.mediaPlayer(), in, out);

        viewer.add(controls.getControlsPane(), BorderLayout.SOUTH);
        viewer.revalidate();
        viewer.repaint();
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
        viewer = new JPanel();
        viewer.setLayout(new BorderLayout(0, 0));
        contentPane.add(viewer, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(100, 100), new Dimension(640, 480), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
