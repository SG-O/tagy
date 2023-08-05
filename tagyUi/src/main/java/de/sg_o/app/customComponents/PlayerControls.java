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

package de.sg_o.app.customComponents;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.annotator.Input;
import org.jetbrains.annotations.NotNull;
import uk.co.caprica.vlcj.media.VideoTrackInfo;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerControls {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private JPanel controlsPane;
    private JSlider progress;
    private JButton inButton;
    private JButton rewindButton;
    private JButton playPauseButton;
    private JButton nextFrameButton;
    private JButton outButton;
    private JButton rewind10Button;
    private JButton skip10Button;
    private JLabel position;
    private JLabel total;

    private boolean progressClicked = false;

    private final MediaPlayer mediaPlayerComponent;
    private final Input in;
    private final Input out;

    public PlayerControls(@NotNull MediaPlayer mediaPlayerComponent, Input in, Input out) {
        inButton.setIcon(Icons.START_20);
        rewindButton.setIcon(Icons.FAST_REWIND_20);
        rewind10Button.setIcon(Icons.REPLAY_10_20);
        playPauseButton.setIcon(Icons.PAUSE_20);
        nextFrameButton.setIcon(Icons.NAVIGATE_NEXT_20);
        skip10Button.setIcon(Icons.FORWARD_10_20);
        outButton.setIcon(Icons.LAST_PAGE_20);

        fixButtonKeypress(inButton);
        fixButtonKeypress(rewindButton);
        fixButtonKeypress(rewind10Button);
        fixButtonKeypress(playPauseButton);
        fixButtonKeypress(nextFrameButton);
        fixButtonKeypress(skip10Button);
        fixButtonKeypress(outButton);


        this.mediaPlayerComponent = mediaPlayerComponent;
        this.in = in;
        this.out = out;

        rewindButton.addActionListener(e -> mediaPlayerComponent.controls().setPosition(0.0f));
        rewind10Button.addActionListener(e -> mediaPlayerComponent.controls().skipTime(-10000));
        playPauseButton.addActionListener(e -> playPause());
        nextFrameButton.addActionListener(e -> {
            List<VideoTrackInfo> videoTracks = mediaPlayerComponent.media().info().videoTracks();
            int track = mediaPlayerComponent.video().track();
            if (videoTracks.size() > track && track >= 0) {
                int frameRate = videoTracks.get(track).frameRate();
                if (frameRate == 0) frameRate = 30;
                float msPerFrame = 1000.0f / frameRate;
                long delta = Math.round(msPerFrame);
                mediaPlayerComponent.controls().skipTime(delta);
            }
        });
        skip10Button.addActionListener(e -> mediaPlayerComponent.controls().skipTime(10000));
        inButton.addActionListener(e -> in());
        outButton.addActionListener(e -> out());
        progress.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                progressClicked = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                float pro = (float) progress.getValue() / 10000.0f;
                mediaPlayerComponent.controls().setPosition(pro);
                progressClicked = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayerComponent), 0L, 100L, TimeUnit.MILLISECONDS);
    }

    private void fixButtonKeypress(JButton button) {
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "none");
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "none");
    }

    public void in() {
        if (in == null) return;
        in.setValue(mediaPlayerComponent.status().time());
    }

    public void out() {
        if (out == null) return;
        out.setValue(mediaPlayerComponent.status().time());
    }

    public void playPause() {
        if (mediaPlayerComponent.status().isPlaying()) {
            mediaPlayerComponent.controls().pause();
        } else {
            mediaPlayerComponent.controls().play();
        }
    }

    public JPanel getControlsPane() {
        return controlsPane;
    }

    public void close() {
        executorService.shutdown();
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
        controlsPane = new JPanel();
        controlsPane.setLayout(new GridLayoutManager(2, 11, new Insets(0, 0, 0, 0), -1, -1));
        progress = new JSlider();
        progress.setMaximum(10000);
        progress.setPaintTicks(false);
        progress.setPaintTrack(true);
        progress.setValue(0);
        progress.setValueIsAdjusting(false);
        controlsPane.add(progress, new GridConstraints(0, 1, 1, 9, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inButton = new JButton();
        inButton.setText("");
        inButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.inPosition"));
        controlsPane.add(inButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rewindButton = new JButton();
        rewindButton.setText("");
        rewindButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.rewind"));
        controlsPane.add(rewindButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        playPauseButton = new JButton();
        playPauseButton.setText("");
        playPauseButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.playPause"));
        controlsPane.add(playPauseButton, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextFrameButton = new JButton();
        nextFrameButton.setText("");
        nextFrameButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.nextFrame"));
        controlsPane.add(nextFrameButton, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outButton = new JButton();
        outButton.setText("");
        outButton.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.outPosition"));
        controlsPane.add(outButton, new GridConstraints(1, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rewind10Button = new JButton();
        rewind10Button.setText("");
        rewind10Button.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.rewindTen"));
        controlsPane.add(rewind10Button, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        skip10Button = new JButton();
        skip10Button.setText("");
        skip10Button.setToolTipText(this.$$$getMessageFromBundle$$$("translations/formText", "button.tooltip.skipTen"));
        controlsPane.add(skip10Button, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        controlsPane.add(spacer1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        controlsPane.add(spacer2, new GridConstraints(1, 9, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        position = new JLabel();
        position.setHorizontalAlignment(4);
        position.setHorizontalTextPosition(4);
        position.setText("00:00:00");
        controlsPane.add(position, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 17), null, 0, false));
        total = new JLabel();
        total.setHorizontalAlignment(2);
        total.setHorizontalTextPosition(2);
        total.setText("00:00:00");
        controlsPane.add(total, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
    public JComponent $$$getRootComponent$$$() {
        return controlsPane;
    }

    private final class UpdateRunnable implements Runnable {

        private final MediaPlayer mediaPlayer;

        private UpdateRunnable(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void run() {
            mediaPlayer.status().rate();
            final long time = mediaPlayer.status().time();
            final long length = mediaPlayer.status().length();
            final int timePosition = (int) (mediaPlayer.status().position() * 10000.0f);
            final boolean isPlaying = mediaPlayer.status().isPlaying();

            SwingUtilities.invokeLater(() -> {
                updateTime(time, position);
                updateTime(length, total);
                updatePosition(timePosition);
                updatePlayPauseButton(isPlaying);
            });
        }
    }

    private void updateTime(long millis, JLabel label) {
        String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        label.setText(s);
    }

    private void updatePosition(int value) {
        if (progressClicked) return;
        progress.setValue(value);
    }

    private void updatePlayPauseButton(boolean playing) {
        if (playing) {
            playPauseButton.setIcon(Icons.PAUSE_20);
        } else {
            playPauseButton.setIcon(Icons.PLAY_ARROW_20);
        }
    }
}