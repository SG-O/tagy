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

package de.sg_o.app.tagy.customComponents;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.sg_o.app.tagy.annotator.Input;
import de.sg_o.lib.tagy.exceptions.InputException;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.elements.PlayBin;
import org.freedesktop.gstreamer.event.SeekFlags;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.EnumSet;
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

    private final PlayBin playBin;
    private final Input in;
    private final Input out;

    private final SectionedSliderUI sectionedSliderUI;

    public PlayerControls(@NotNull PlayBin playBin, Input in, Input out) {
        inButton.setIcon(Icons.START_20);
        rewindButton.setIcon(Icons.FAST_REWIND_20);
        rewind10Button.setIcon(Icons.REPLAY_10_20);
        playPauseButton.setIcon(Icons.PAUSE_20);
        nextFrameButton.setIcon(Icons.NAVIGATE_NEXT_20);
        skip10Button.setIcon(Icons.FORWARD_10_20);
        outButton.setIcon(Icons.LAST_PAGE_20);

        fixButtonKeyPress(inButton);
        fixButtonKeyPress(rewindButton);
        fixButtonKeyPress(rewind10Button);
        fixButtonKeyPress(playPauseButton);
        fixButtonKeyPress(nextFrameButton);
        fixButtonKeyPress(skip10Button);
        fixButtonKeyPress(outButton);

        sectionedSliderUI = new SectionedSliderUI(progress);
        progress.setUI(sectionedSliderUI);

        this.playBin = playBin;
        this.in = in;
        this.out = out;

        rewindButton.addActionListener(e -> {
            playBin.stop();
            playBin.seek(0);
        });
        rewind10Button.addActionListener(e -> skipTime(-10000));
        playPauseButton.addActionListener(e -> playPause());
        nextFrameButton.addActionListener(e -> {
            playBin.pause();
            skipTime(30);
        });
        skip10Button.addActionListener(e -> skipTime(10000));
        inButton.addActionListener(e -> in());
        outButton.addActionListener(e -> out());
        progress.addChangeListener(e -> {
            if (progress.getValueIsAdjusting()) {
                float pro = (float) progress.getValue() / 10000.0f;
                long length = playBin.queryDuration(Format.TIME);
                if (length > 0) {
                    playBin.seekSimple(Format.TIME, EnumSet.of(SeekFlags.FLUSH), (long) (pro * length));
                }
            }
        });
        progress.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JSlider sourceSlider = (JSlider) e.getSource();
                SectionedSliderUI ui = (SectionedSliderUI) sourceSlider.getUI();
                int value = ui.valueForXPosition(e.getX());
                progress.setValue(value);
                long length = playBin.queryDuration(Format.TIME);
                if (length > 0) {
                    float pro = value / 10000.0f;
                    playBin.seekSimple(Format.TIME, EnumSet.of(SeekFlags.FLUSH), (long) (pro * length));
                }
            }
        });

        executorService.scheduleAtFixedRate(new UpdateRunnable(playBin), 0L, 100L, TimeUnit.MILLISECONDS);
    }

    public long getPositionInMs() {
        return playBin.queryPosition(Format.TIME) / 1000000;
    }

    public long getTotalInMs() {
        return playBin.queryDuration(Format.TIME) / 1000000;
    }

    public void skipTime(long delta) {
        long newTime = getPositionInMs() + delta;
        if (newTime < 0) newTime = 0;
        if (newTime > getTotalInMs()) newTime = getTotalInMs();
        boolean playing = playBin.isPlaying();
        playBin.pause();
        playBin.seekSimple(Format.TIME, EnumSet.of(SeekFlags.FLUSH), newTime * 1000000);
        if (playing) playBin.play();
    }

    private void fixButtonKeyPress(JButton button) {
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "none");
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "none");
    }

    public void in() {
        if (in == null) return;
        long total = getTotalInMs();
        long pos = getPositionInMs();
        in.setValue(pos);
        if (total == 0) return;
        float relative = (float) pos / total;
        sectionedSliderUI.setStart(relative);
    }

    public void out() {
        if (out == null) return;
        long total = getTotalInMs();
        long pos = getPositionInMs();
        out.setValue(pos);
        if (total == 0) return;
        float relative = (float) pos / total;
        sectionedSliderUI.setEnd(relative);
    }

    public void playPause() {
        if (playBin.isPlaying()) {
            playBin.pause();
        } else {
            playBin.play();
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

        private final PlayBin playBin;

        private UpdateRunnable(PlayBin playBin) {
            this.playBin = playBin;
        }

        @Override
        public void run() {
            final long time = getPositionInMs();
            final long length = getTotalInMs();

            int timePosition;
            if (length > 0) {
                timePosition = (int) ((time * 10000.0f) / length);
                if (in != null) {
                    try {
                        Object value = in.getValue();
                        if (value instanceof Number) {
                            sectionedSliderUI.setStart(((Number) value).floatValue() / length);
                        } else {
                            sectionedSliderUI.setStart(0.0f);
                        }
                    } catch (InputException ignored) {
                        sectionedSliderUI.setStart(0.0f);
                    }
                }
                if (out != null) {
                    try {
                        Object value = out.getValue();
                        if (value instanceof Number) {
                            sectionedSliderUI.setEnd(((Number) value).floatValue() / length);
                        } else {
                            sectionedSliderUI.setEnd(1.0f);
                        }
                    } catch (InputException ignored) {
                        sectionedSliderUI.setEnd(1.0f);
                    }
                }
            } else {
                timePosition = 0;
            }
            final boolean isPlaying = playBin.isPlaying();

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
        if (progress.getValueIsAdjusting()) return;
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