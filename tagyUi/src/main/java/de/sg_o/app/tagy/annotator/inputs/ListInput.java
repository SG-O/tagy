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
import de.sg_o.app.tagy.annotator.InputHolder;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.TagEnablerDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.list.TagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ListInput extends Input {
    private @NotNull final JPanel component;
    private final JPanel list;

    private final ArrayList<ChildListEntry> children = new ArrayList<>();
    private ArrayList<Input> otherInputs;

    public ListInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        component = new JPanel();
        component.setLayout(new BoxLayout(component, BoxLayout.Y_AXIS));
        list = new JPanel(new GridBagLayout());
        JScrollPane scrollList = new JScrollPane(list);
        scrollList.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        component.add(scrollList);
        JButton addButton = new JButton("Add");
        addButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        component.add(addButton);
        component.setToolTipText(getTagDefinition().getKey());
        addButton.addActionListener(e -> addChild(null));
    }

    public ListInput(Tag tag) {
        this(tag.getDefinition());
        reset(tag);
    }

    public void setOtherInputs(ArrayList<Input> otherInputs) {
        this.otherInputs = otherInputs;
    }

    private void addChild(Tag tag) {
        TagDefinition childTagDefinition = super.getTagDefinition().resolveInternal();
        if (childTagDefinition == null) return;
        Input child;
        if (tag != null && tag.getDefinition().getType() == childTagDefinition.getType()) {
            child = createChildInput(tag);
        } else {
            child = create(childTagDefinition);
        }
        if (child == null) return;
        if (child instanceof EnumInput) {
            EnumInput enumChild = (EnumInput) child;
            if (otherInputs != null) {
                for (Input input : otherInputs) {
                    TagEnablerDefinition ted = input.getTagDefinition().resolveTagEnabler();
                    if (ted == null) continue;
                    if (childTagDefinition.getKey().equals(ted.getSelectorKey())) {
                        enumChild.attachInputToEnable(input, InputHolder.getEnumIndex(ted, enumChild.getTagDefinition()));
                    }
                }
            }
        }

        children.add(new ChildListEntry(child));
        refreshList();
    }

    private void refreshList() {
        list.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        list.add(new JPanel(), gbc);
        for (ChildListEntry child : children) {
            gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            list.add(child, gbc, 0);
        }
        component.validate();
        component.repaint();
    }

    public @NotNull JComponent getComponent() {
        return component;
    }

    @Override
    public void reset(Tag tag) {
        for (ChildListEntry cle :  children) {
            cle.getChild().reset(null);
        }
        children.clear();
        refreshList();
        if (tag == null) return;
        if (tag instanceof TagList) {
            TagList tagList = (TagList) tag;
            for (Tag child : tagList.getValue()) {
                addChild(child);
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
    }

    @SuppressWarnings("unused")
    public @Nullable Tag getTag() throws InputException {
        if (children.size() < super.getTagDefinition().getMin()) {
            throw new InputException(InputException.Rule.LIST_NOT_ENOUGH_VALUES,
                    super.getTagDefinition().getName(),
                    super.getTagDefinition().getMin());
        }
        if (children.size() > super.getTagDefinition().getMax()) {
            throw new InputException(InputException.Rule.LIST_TOO_MANY_VALUES,
                    super.getTagDefinition().getName(),
                    super.getTagDefinition().getMax());
        }
        TagList tagList = new TagList(super.getTagDefinition(), children.size());
        for (ChildListEntry child : children) {
            tagList.addValue(child.getChild().getTag());
        }
        return tagList;
    }

    private class ChildListEntry extends JPanel {
        private final Input child;

        public ChildListEntry(Input child) {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(child.getComponent());
            JButton removeButton = new JButton("x");
            removeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            removeButton.addActionListener(e -> {
                children.remove(this);
                child.reset(null);
                refreshList();
            });
            add(removeButton);
            this.child = child;
        }

        public Input getChild() {
            return child;
        }
    }

    private static Input createChildInput(@NotNull Tag tag) {
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
            case BOOLEAN:
                return new BoolInput(tag);
        }
        return null;
    }
}
