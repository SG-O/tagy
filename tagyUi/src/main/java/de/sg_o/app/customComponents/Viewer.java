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
import de.sg_o.app.annotator.Input;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Viewer extends JPanel {

    private static final String PLAYER = "Player";
    private static final String TEXT_VIEWER = "TextViewer";
    private static final String UNKNOWN = "Unknown";
    private static final String IMAGE = "Image";

    private final Player player;
    private final TextViewer textViewer;
    private final ImageViewer imageViewer;

    private final CardLayout layout;

    public Viewer(List<Input> inputs) {
        this.layout = new CardLayout();
        setLayout(this.layout);
        this.player = new Player(inputs);
        this.textViewer = new TextViewer(inputs);
        this.imageViewer = new ImageViewer();
        add(player, PLAYER);
        add(textViewer, TEXT_VIEWER);
        add(imageViewer, IMAGE);
        add(new UnknownMedia(), UNKNOWN);
        revalidate();
        repaint();
    }

    public void display(FileInfo fileInfo) {
        textViewer.stop();
        player.stop();
        imageViewer.stop();
        try {
            switch (fileInfo.getFileType()) {
                case TEXT:
                    textViewer.display(fileInfo);
                    layout.show(this, TEXT_VIEWER);
                    break;
                case IMAGE:
                    imageViewer.display(fileInfo);
                    layout.show(this, IMAGE);
                    break;
                case MEDIA:
                    player.display(fileInfo);
                    layout.show(this, PLAYER);
                    break;
                default:
                    layout.show(this, UNKNOWN);
                    break;
            }
        } catch (Exception ignored) {
            layout.show(this, UNKNOWN);
        }
        revalidate();
        repaint();
    }

    public void close() {
        textViewer.close();
        player.close();
        imageViewer.close();
    }


}
