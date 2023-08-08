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

import java.awt.*;

@SuppressWarnings("unused")
public class ScalableDimension extends Dimension {
    private float factor = 1.0f;

    private int originalWidth = 0;
    private int originalHeight = 0;

    public ScalableDimension() {
        initFactor();
    }

    public ScalableDimension(Dimension d) {
        super(d);
        this.originalWidth = d.width;
        this.originalHeight = d.height;
        initFactor();
    }

    public ScalableDimension(int width, int height) {
        super(width, height);
        this.originalWidth = width;
        this.originalHeight = height;
        initFactor();
    }

    private void initFactor() {
        setFactor(LafManager.getInstalledTheme().getFontSizeRule().getPercentage() / 100.0f);
        LafManager.addThemeChangeListener(new CustomThemeListener());
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
        super.width = Math.round(originalWidth * factor);
        super.height = Math.round(originalHeight * factor);
    }

    class CustomThemeListener implements ThemeChangeListener {

        @Override
        public void themeChanged(ThemeChangeEvent themeChangeEvent) {
            setFactor(themeChangeEvent.getNewTheme().getFontSizeRule().getPercentage() / 100.0f);
        }

        @Override
        public void themeInstalled(ThemeChangeEvent themeChangeEvent) {

        }
    }
}
