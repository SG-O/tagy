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

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.event.ThemeChangeEvent;
import com.github.weisj.darklaf.theme.event.ThemeChangeListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Map;

@SuppressWarnings("unused")
public class ScalableFont extends Font {
    private final float size;

    public ScalableFont(String name, int style, int size) {
        super(name, style, size);
        this.size = super.getSize2D();
        LafManager.addThemeChangeListener(new CustomThemeListener());
    }

    public ScalableFont(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        super(attributes);
        this.size = super.getSize2D();
        scaleFont();
        LafManager.addThemeChangeListener(new CustomThemeListener());
    }

    public ScalableFont(Font font) {
        super(font);
        this.size = super.getSize2D();
        scaleFont();
        LafManager.addThemeChangeListener(new CustomThemeListener());
    }

    private void scaleFont() {
        float factor = LafManager.getInstalledTheme().getFontSizeRule().getPercentage() / 100.0f;
        super.pointSize = this.size * factor;
        super.size = (int)(super.pointSize + 0.5);
    }

    @Override
    public int getSize() {
        return (int)(this.size + 0.5);
    }

    @NotNull
    @Override
    public ScalableFont deriveFont(float size) {
        Font f = super.deriveFont(size);
        return new ScalableFont(f);
    }

    @Override
    public ScalableFont deriveFont(int style, float size) {
        Font f =  super.deriveFont(style, size);
        return new ScalableFont(f);
    }

    @Override
    public ScalableFont deriveFont(int style, AffineTransform trans) {
        super.pointSize = this.size;
        super.size = (int)(super.pointSize + 0.5);
        Font f =  super.deriveFont(style, trans);
        scaleFont();
        return new ScalableFont(f);
    }

    @Override
    public ScalableFont deriveFont(AffineTransform trans) {
        super.pointSize = this.size;
        super.size = (int)(super.pointSize + 0.5);
        Font f =  super.deriveFont(trans);
        scaleFont();
        return new ScalableFont(f);
    }

    @Override
    public ScalableFont deriveFont(int style) {
        super.pointSize = this.size;
        super.size = (int)(super.pointSize + 0.5);
        Font f =  super.deriveFont(style);
        scaleFont();
        return new ScalableFont(f);
    }

    @Override
    public ScalableFont deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        super.pointSize = this.size;
        super.size = (int)(super.pointSize + 0.5);
        Font f =  super.deriveFont(attributes);
        scaleFont();
        return new ScalableFont(f);
    }

    class CustomThemeListener implements ThemeChangeListener {

        @Override
        public void themeChanged(ThemeChangeEvent themeChangeEvent) {
            scaleFont();
        }

        @Override
        public void themeInstalled(ThemeChangeEvent themeChangeEvent) {

        }
    }
}
