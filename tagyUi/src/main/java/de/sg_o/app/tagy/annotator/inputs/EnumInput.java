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
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EnumInput extends Input {
    private final JComboBox<String> component;
    private final HashMap<Integer, ArrayList<Input>> enableInputs = new HashMap<>();

    public EnumInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        ArrayList<String> enumerators = getTagDefinition().getEnumerators();
        if (enumerators == null) enumerators = new ArrayList<>();
        enumerators.add("");
        component = new JComboBox<>(enumerators.toArray(new String[0]));
        component.setSelectedIndex(enumerators.size() - 1);
        component.setToolTipText(getTagDefinition().getKey());
        component.addActionListener(e -> enableInputs(component.getSelectedIndex()));

    }

    public EnumInput(@NotNull Tag tag) {
        this(tag.getDefinition());
        reset(tag);
    }

    public void attachInputToEnable(Input input, int index) {
        if (!enableInputs.containsKey(index)) enableInputs.put(index, new ArrayList<>());
        enableInputs.get(index).add(input);
        enableInputs(component.getSelectedIndex());
    }

    private void enableInputs(int index) {
        for (Integer key :  enableInputs.keySet()) {
            if (key == index) {
                for (Input input : enableInputs.get(key)) {
                    input.addViewer(this);
                }
            } else {
                for (Input input : enableInputs.get(key)) {
                    input.removeViewer(this);
                }
            }
        }
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
        component.setSelectedIndex(component.getItemCount() - 1);
        enableInputs(component.getSelectedIndex());
        if (tag == null) return;
        if (tag instanceof TagEnum) {
            TagEnum tagEnum = (TagEnum) tag;
            if (tagEnum.getValue() >= 0 && tagEnum.getValue() < component.getItemCount() - 1) {
                component.setSelectedIndex(tagEnum.getValue());
                enableInputs(component.getSelectedIndex());
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
    }

    @SuppressWarnings("unused")
    public @Nullable Tag getTag() throws InputException {
        if (component.getSelectedIndex() == component.getItemCount() - 1) {
            if (super.getTagDefinition().isRequired()) throw new InputException(InputException.Rule.EMPTY_MANDATORY_FIELD, super.getTagDefinition().getName());
            return null;
        }
        return new TagEnum(super.getTagDefinition(), component.getSelectedIndex());
    }
}
