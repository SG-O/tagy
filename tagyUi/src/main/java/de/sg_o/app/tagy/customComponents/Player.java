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

import de.sg_o.app.tagy.annotator.Input;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.elements.PlayBin;
import org.freedesktop.gstreamer.swing.GstVideoComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class Player extends JPanel {
    private static PlayBin playbin;
    private final PlayerControls playerControls;

    private final KeyboardFocusManager manager;
    private final KeyEventPostProcessor keyEventPostProcessor;

    public Player(List<Input> inputs) {
        if (playbin == null) {
            playbin = new PlayBin("playbin");
        }

        GstVideoComponent vc = new GstVideoComponent();
        playbin.setVideoSink(vc.getElement());

        setLayout(new BorderLayout());
        Player player = this;

        Input in = null;
        Input out = null;
        Input length = null;

        for (Input input : inputs) {
            if (input.getTagDefinition().getParameter() == TagDefinitionProto.Parameter.IN) {
                in = input;
            } else if (input.getTagDefinition().getParameter() == TagDefinitionProto.Parameter.OUT) {
                out = input;
            } else if (input.getTagDefinition().getParameter() == TagDefinitionProto.Parameter.LENGTH) {
                length = input;
            }
        }
        playerControls = new PlayerControls(playbin, in, out);

        manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyEventPostProcessor = e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            Rectangle bounds = player.getBounds();
            bounds.setLocation(player.getLocationOnScreen());
            if (!bounds.contains(mousePos)) return false;
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                playerControls.playPause();
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                playerControls.skipTime(-2000);
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                playerControls.skipTime(2000);
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                playerControls.in();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                playerControls.out();
            }
            return false;
        };
        manager.addKeyEventPostProcessor(keyEventPostProcessor);
        add(vc, BorderLayout.CENTER);

        Input finalLength = length;
        playbin.getBus().connect((Bus.DURATION_CHANGED) source -> {
            // handle on Swing thread!
            if (finalLength != null) {
                finalLength.setValue(playerControls.getTotalInMs());
            }
        });


        add(playerControls.getControlsPane(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    public void display(FileInfo fileInfo) {
        try {
            playbin.stop();
            playbin.setURI(fileInfo.getAbsolutePath().toURI());
            playbin.play();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        playbin.play();
    }

    public void close() {
        if (playerControls != null) {
            playerControls.close();
        }
        playbin.stop();
        manager.removeKeyEventPostProcessor(keyEventPostProcessor);
    }
}
