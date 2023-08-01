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

package de.sg_o.lib.tagy.tag;

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.date.DateInput;
import de.sg_o.lib.tagy.tag.enumerator.EnumInput;
import de.sg_o.lib.tagy.tag.floating.DoubleInput;
import de.sg_o.lib.tagy.tag.integer.LongInput;
import de.sg_o.lib.tagy.tag.list.ListInput;
import de.sg_o.lib.tagy.tag.string.StringInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public abstract class Input {
    @NotNull
    private final TagDefinition tagDefinition;

    public Input(@NotNull TagDefinition tagDefinition) {
        this.tagDefinition = tagDefinition;
    }

    public @NotNull TagDefinition getTagDefinition() {
        return tagDefinition;
    }

    public static ArrayList<Input> parseProject(Project project) {
        StructureDefinition structureDefinition = project.resolveStructureDefinition();
        ArrayList<Input> inputs = new ArrayList<>();
        for (TagDefinition tagDefinition : structureDefinition.getDecodedTagDefinitions()) {
            Input input = create(tagDefinition);
            if (input != null) {
                inputs.add(input);
            }
        }
        return inputs;
    }

    public static Input create(@NotNull Tag tag) {
        TagDefinition tagDefinition = tag.getDefinition();
        switch (tagDefinition.getType()) {
            case LIST:
                return new ListInput(tag);
            case LONG:
                return new LongInput(tag);
            case DOUBLE:
                return new DoubleInput(tag);
            case ENUM:
                return new EnumInput(tag);
            case STRING:
                return new StringInput(tag);
            case DATE:
                return new DateInput(tag);
        }
        return null;
    }

    public static Input create(@NotNull TagDefinition tagDefinition) {
        switch (tagDefinition.getType()) {
            case LIST:
                return new ListInput(tagDefinition);
            case LONG:
                return new LongInput(tagDefinition);
            case DOUBLE:
                return new DoubleInput(tagDefinition);
            case ENUM:
                return new EnumInput(tagDefinition);
            case STRING:
                return new StringInput(tagDefinition);
            case DATE:
                return new DateInput(tagDefinition);
        }
        return null;
    }

    public @NotNull abstract JComponent getComponent();

    public abstract void reset(Tag tag);

    public abstract void setValue(@NotNull Object value);

    public @NotNull JPanel getModule() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(200, 160));
        panel.setPreferredSize(new Dimension(200, 160));
        TitledBorder title;
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), getTagDefinition().getName());
        panel.setBorder(title);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JComponent component = getComponent();
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        panel.add(component);
        return panel;
    }

    public abstract @Nullable Tag getTag() throws InputException;
}
