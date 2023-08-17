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

package de.sg_o.app.tagy.annotator.inputs;

import de.sg_o.app.tagy.annotator.Input;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class BoolInput extends Input {
    private final JCheckBox component;

    public BoolInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        component = new JCheckBox();
        component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setToolTipText(getTagDefinition().getKey());
    }

    public BoolInput(@NotNull Tag tag) {
        this(tag.getDefinition());
        reset(tag);
    }

    public @NotNull JComponent getComponent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(Box.createHorizontalGlue(), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        panel.add(Box.createHorizontalGlue(), BorderLayout.EAST);
        return panel;
    }

    @Override
    public void reset(Tag tag) {
        component.setSelected(false);
        if (tag == null) return;
        if (tag instanceof TagBool) {
            TagBool tagBool = (TagBool) tag;
            component.setSelected(tagBool.getValue());
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
        if (value instanceof Boolean) {
            component.setSelected((Boolean) value);
        } else {
            component.setSelected(false);
        }
        component.revalidate();
        component.repaint();
    }

    @SuppressWarnings("unused")
    public @Nullable Tag getTag() {
        Boolean value = getValue();
        if (value == null) return null;
        return new TagBool(super.getTagDefinition(), value);
    }

    public @Nullable Boolean getValue() {
        return component.isSelected();
    }
}