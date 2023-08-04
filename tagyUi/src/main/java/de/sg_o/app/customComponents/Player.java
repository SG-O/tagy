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

import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.def.Parameter;
import de.sg_o.lib.tagy.tag.Input;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class Player extends JPanel {
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private final PlayerControls playerControls;

    public Player(List<Input> inputs) {
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent("-vv");
        setLayout(new BorderLayout());
        Player player = this;

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
        playerControls = new PlayerControls(mediaPlayerComponent.mediaPlayer(), in, out);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(e -> {
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
                mediaPlayerComponent.mediaPlayer().controls().skipTime(-2000);
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                mediaPlayerComponent.mediaPlayer().controls().skipTime(2000);
                return true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                playerControls.in();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                playerControls.out();
            }
            return false;
        });
        add(mediaPlayerComponent, BorderLayout.CENTER);

        Input finalLength = length;
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (finalLength != null) {
                    finalLength.setValue(mediaPlayer.status().length());
                }
            }
        });

        add(playerControls.getControlsPane(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    public void display(FileInfo fileInfo) {
        mediaPlayerComponent.mediaPlayer().media().play(fileInfo.getAbsolutePath());
    }

    public void stop() {
        mediaPlayerComponent.mediaPlayer().controls().stop();
    }

    public void close() {
        if (playerControls != null) {
            playerControls.close();
        }
        mediaPlayerComponent.mediaPlayer().release();
    }
}
