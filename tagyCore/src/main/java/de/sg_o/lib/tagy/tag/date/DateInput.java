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

package de.sg_o.lib.tagy.tag.date;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.exceptions.InputException;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateInput extends Input {
    private final DatePicker component;
    private final TimePicker timePicker;

    public DateInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);
        component = new DatePicker();
        component.setToolTipText(getTagDefinition().getKey());
        timePicker = new TimePicker();
        timePicker.setToolTipText(getTagDefinition().getKey());
    }

    public DateInput(@NotNull Tag tag) {
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
        panel.add(timePicker, gbc);
        return panel;
    }

    @Override
    public void reset(Tag tag) {
        component.clear();
        timePicker.clear();
        if (tag == null) return;
        if (tag instanceof TagDate) {
            TagDate tagDate = (TagDate) tag;
            LocalDateTime ldt = tagDate.getValue().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
            component.setDate(ldt.toLocalDate());
            timePicker.setTime(ldt.toLocalTime());
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
        if (value instanceof Date) {
            Date date = (Date) value;
            LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
            component.setDate(ldt.toLocalDate());
            timePicker.setTime(ldt.toLocalTime());
        }
        if (value instanceof Long) {
            Date date = new Date((Long) value);
            LocalDateTime ldt = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
            component.setDate(ldt.toLocalDate());
            timePicker.setTime(ldt.toLocalTime());
        }
    }

    @SuppressWarnings("unused")
    @Override
    public @Nullable Tag getTag() throws InputException {
        LocalDate ld = component.getDate();
        if (ld == null) {
            if (super.getTagDefinition().isRequired()) {
                throw new InputException(InputException.Rule.EMPTY_MANDATORY_FIELD, super.getTagDefinition().getName());
            }
            return null;
        }
        LocalTime time = timePicker.getTime();
        if (time == null) {
            time = LocalTime.MIN;
        }
        LocalDateTime ldt = ld.atTime(time);
        Date out = Date.from(ldt.atZone(ZoneId.of("UTC")).toInstant());
        return new TagDate(super.getTagDefinition(), out);
    }
}
