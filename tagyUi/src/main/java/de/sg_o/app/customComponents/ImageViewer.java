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

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

public class ImageViewer  extends JComponent {

    private Image image = null;
    private SizeMode sizeMode = SizeMode.ZOOM;

    public ImageViewer() {
        this.setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        int width = image.getWidth(this);
        int height = image.getHeight(this);

        switch (getSizeMode()) {
            case ZOOM:
                height = this.getHeight();
                width = (image.getWidth(this) * height) / image.getHeight(this);
                break;
            case STRETCH:
                width = this.getWidth();
                height = this.getHeight();
                break;
        }

        int x = (this.getWidth() / 2) - (width / 2);
        int y = (this.getHeight() / 2) - (height / 2);
        g.drawImage(image, x, y, width, height, null);
    }

    public SizeMode getSizeMode() {
        return sizeMode;
    }

    @SuppressWarnings("unused")
    public void setSizeMode(SizeMode sizeMode) {
        this.sizeMode = sizeMode;
    }

    @SuppressWarnings("unused")
    public enum SizeMode {
        NORMAL,
        STRETCH,
        ZOOM
    }

    public void display(FileInfo fileInfo) {
        try (ImageInputStream input = ImageIO.createImageInputStream(fileInfo.getFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException("No reader for: " + fileInfo.getFile());
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);
                BufferedImage tempImage = reader.read(0);
                if (tempImage != null) image = tempImage;
            } finally {
                reader.dispose();
            }
        } catch (IOException | IllegalStateException ignore) {
        }
        revalidate();
        repaint();
    }

    public void stop() {
        image = null;
    }

    public void close() {
        image = null;
    }
}
