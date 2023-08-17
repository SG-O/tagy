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

package de.sg_o.app.tagy.annotator;

import de.sg_o.app.tagy.annotator.inputs.*;
import de.sg_o.app.tagy.customComponents.ScalableDimension;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashSet;

public abstract class Input {
    public static final ScalableDimension DEFAULT_DIMENSION = new ScalableDimension(200, 160);

    @NotNull
    private final TagDefinition tagDefinition;
    private JPanel module;
    private final HashSet<Input> viewers = new HashSet<>();

    public Input(@NotNull TagDefinition tagDefinition) {
        this.tagDefinition = tagDefinition;
        viewers.add(this);
    }

    public @NotNull TagDefinition getTagDefinition() {
        return tagDefinition;
    }

    public @NotNull abstract JComponent getComponent();

    public abstract void reset(Tag tag);

    public abstract void setValue(@NotNull Object value);

    public abstract @Nullable Tag getTag() throws InputException;

    public abstract @Nullable Object getValue() throws InputException;

    public void addViewer(Input viewer) {
        viewers.add(viewer);
        getModule().setVisible(!viewers.isEmpty());
        getModule().revalidate();
        getModule().repaint();
    }

    public void removeViewer(Input viewer) {
        viewers.remove(viewer);
        getModule().setVisible(!viewers.isEmpty());
        getModule().revalidate();
        getModule().repaint();
    }

    @SuppressWarnings("unused")
    public boolean isVisible() {
        return getModule().isVisible();
    }

    public @NotNull JPanel getModule() {
        if (module == null) {
            module = generateModule();
        }
        return module;
    }

    protected @NotNull JPanel generateModule() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(DEFAULT_DIMENSION);
        panel.setPreferredSize(DEFAULT_DIMENSION);
        panel.setMaximumSize(DEFAULT_DIMENSION);
        TitledBorder title;
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), getTagDefinition().getName());
        panel.setBorder(title);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JComponent component = getComponent();
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        component.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.add(component);
        return panel;
    }

    protected static Input create(@NotNull TagDefinition tagDefinition) {
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
            case BOOLEAN:
                return new BoolInput(tagDefinition);
        }
        return null;
    }
}
