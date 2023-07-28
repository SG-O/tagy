package de.sg_o.lib.tagy.tag.enumerator;

import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EnumInput extends Input {
    private final JComboBox<String> component;

    public EnumInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        ArrayList<String> enumerators = getTagDefinition().getEnumerators();
        if (enumerators == null) enumerators = new ArrayList<>();
        enumerators.add("");
        component = new JComboBox<>(enumerators.toArray(new String[0]));
        component.setSelectedIndex(enumerators.size() - 1);
        component.setToolTipText(getTagDefinition().getKey());
    }

    public EnumInput(@NotNull Tag tag) {
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
        component.setSelectedIndex(component.getItemCount() - 1);
        if (tag == null) return;
        if (tag instanceof TagEnum) {
            TagEnum tagEnum = (TagEnum) tag;
            if (tagEnum.getValue() >= 0 && tagEnum.getValue() < component.getItemCount() - 1)
                component.setSelectedIndex(tagEnum.getValue());
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
