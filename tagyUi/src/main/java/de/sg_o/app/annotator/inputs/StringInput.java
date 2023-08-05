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

package de.sg_o.app.annotator.inputs;

import de.sg_o.app.annotator.Input;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.string.TagString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class StringInput extends Input {
    private final JTextField component;

    public StringInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        component = new JTextField("", 10);
        component.setToolTipText(getTagDefinition().getKey());
    }

    public StringInput(@NotNull Tag tag) {
        this(tag.getDefinition());
        reset(tag);
    }

    public @NotNull JComponent getComponent() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        return panel;
    }

    @Override
    public void reset(Tag tag) {
        component.setText("");
        if (tag == null) return;
        if (tag instanceof TagString) {
            TagString tagString = (TagString) tag;
            component.setText(tagString.getValue());
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
        if (value instanceof String) {
            component.setText((String) value);
        } else {
            component.setText(value.toString());
        }
        component.revalidate();
        component.repaint();
    }

    @SuppressWarnings("unused")
    public @Nullable Tag getTag() throws InputException {
        String text = component.getText();
        if (text.isEmpty() && super.getTagDefinition().isRequired()) {
            throw new InputException(InputException.Rule.EMPTY_MANDATORY_FIELD, super.getTagDefinition().getName());
        }
        if (text.length() < super.getTagDefinition().getMin()) {
            throw new InputException(InputException.Rule.STRING_TOO_SHORT,
                    super.getTagDefinition().getName(),
                    super.getTagDefinition().getMin());
        }
        if (text.length() > super.getTagDefinition().getMax()) {
            throw new InputException(InputException.Rule.STRING_TOO_LONG,
                    super.getTagDefinition().getName(),
                    super.getTagDefinition().getMax());
        }
        return new TagString(super.getTagDefinition(), text);
    }
}
