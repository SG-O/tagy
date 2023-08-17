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

import com.github.weisj.darklaf.ui.slider.DarkSliderUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

@SuppressWarnings("unused")
public class SectionedSliderUI extends DarkSliderUI {
    private float start = 0.0f;
    private float end = 1.0f;


    public SectionedSliderUI(final JSlider b) {
        super(b);
    }

    private boolean isHorizontal() {
        return slider.getOrientation() == JSlider.HORIZONTAL;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    @Override
    public void paintTrack(final Graphics g) {
        super.paintTrack(g);
        Graphics2D g2 = (Graphics2D) g;
        Shape clip = g2.getClip();

        boolean horizontal = isHorizontal();
        Color selectionColor = super.thumbFocusBorderColor;
        selectionColor = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 192);
        Shape track;

        if (horizontal) {
            track = getHorizontalTrackShape(trackShape);
        } else {
            track = getVerticalTrackShape(trackShape);
        }

        if (this.start <= 0.0f) this.start = 0.0f;
        if (this.end >= 1.0f) this.end = 1.0f;
        if (this.end <= this.start) return;
        if (this.start == 0.0f && this.end == 1.0f) return;

        if (!horizontal) {
            int startPos = (int) (slider.getHeight() * this.start);
            int endPos = (int) (slider.getHeight() * this.end);
            g2.clipRect(0, startPos, slider.getWidth(), endPos - startPos);
        } else {
            int startPos = (int) (slider.getWidth() * this.start);
            int endPos = (int) (slider.getWidth() * this.end);
            g2.clipRect(startPos, 0, endPos - startPos, slider.getHeight());
        }
        g2.setColor(selectionColor);
        g2.fill(track);
        g2.setClip(clip);
    }

    private Shape getHorizontalTrackShape(final RoundRectangle2D trackShape) {
        return getHorizontalTrackShape(trackShape, trackSize);
    }

    private Shape getHorizontalTrackShape(final RoundRectangle2D trackShape, final int size) {
        int arc = arcSize;
        int yOff = (trackRect.height - size) / 2;
        int w = trackRect.width;
        trackShape.setRoundRect(trackRect.x, trackRect.y + yOff, w, size, arc, arc);
        return trackShape;
    }

    private Shape getVerticalTrackShape(final RoundRectangle2D trackShape) {
        return getVerticalTrackShape(trackShape, trackSize);
    }

    private Shape getVerticalTrackShape(final RoundRectangle2D trackShape, final int size) {
        int arc = arcSize;
        int xOff = (trackRect.width - size) / 2;
        int h = trackRect.height;
        trackShape.setRoundRect(trackRect.x + xOff, trackRect.y, size, h, arc, arc);
        return trackShape;
    }
}
