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
import de.sg_o.lib.tagy.tag.integer.TagLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LongInput extends Input {
    private final JComponent component;
    private final long min;
    private final long max;

    public LongInput(@NotNull TagDefinition tagDefinition) {
        super(tagDefinition);

        if (tagDefinition.getMin() == Double.NEGATIVE_INFINITY) {
            min = Long.MIN_VALUE;
        } else {
            min = Math.round(getTagDefinition().getMin());
        }
        if (tagDefinition.getMax() == Double.POSITIVE_INFINITY) {
            max = Long.MAX_VALUE;
        } else {
            max = Math.round(getTagDefinition().getMax());
        }

        if (min >= -50 && max <= 50) {
            component = new JSlider((int) (min), (int) max, (int) min) {
                private SliderPopupListener popupHandler;
                @Override public void updateUI() {
                    removeMouseMotionListener(popupHandler);
                    removeMouseListener(popupHandler);
                    removeMouseWheelListener(popupHandler);
                    super.updateUI();
                    popupHandler = new SliderPopupListener();
                    addMouseMotionListener(popupHandler);
                    addMouseListener(popupHandler);
                    addMouseWheelListener(popupHandler);
                }
            };
        } else {
            component = new JTextField("", 10);
        }
        component.setToolTipText(getTagDefinition().getKey());
    }

    public LongInput(@NotNull Tag tag) {
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
        if (component instanceof JSlider) {
            ((JSlider) component).setValue((int) min);
        }
        if (component instanceof JTextField) {
            ((JTextField) component).setText("");
        }
        if (tag == null) return;
        if (tag instanceof TagLong) {
            TagLong tagLong = (TagLong) tag;
            if (component instanceof JSlider) {
                ((JSlider) component).setValue(tagLong.getValue().intValue());
            }
            if (component instanceof JTextField) {
                ((JTextField) component).setText(String.valueOf(tagLong.getValue()));
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void setValue(@NotNull Object value) {
        Long longValue = null;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                longValue = Long.parseLong((String) value);
            } catch (Exception e) {
                return;
            }
        }
        if (longValue == null) return;
        if (longValue < min) return;
        if (longValue > max) return;
        if (component instanceof JSlider) {
            ((JSlider) component).setValue(longValue.intValue());
        }
        if (component instanceof JTextField) {
            ((JTextField) component).setText(longValue.toString());
        }
        component.revalidate();
        component.repaint();
    }

    @SuppressWarnings("unused")
    public @Nullable Tag getTag() throws InputException {
        Long value = getValue();
        if (value == null) return null;
        return new TagLong(super.getTagDefinition(), value);
    }

    public @Nullable Long getValue() throws InputException {
        long value = 0;
        if (component instanceof JSlider) {
            value = ((JSlider) component).getValue();
        }
        if (component instanceof JTextField) {
            String text = ((JTextField) component).getText();
            if (text.isEmpty() && super.getTagDefinition().isRequired()) {
                throw new InputException(InputException.Rule.EMPTY_MANDATORY_FIELD, super.getTagDefinition().getName());
            }
            try {
                value = Long.parseLong(text);
            } catch (Exception ignore) {
                if (super.getTagDefinition().isRequired())
                    throw new InputException(InputException.Rule.INVALID_MANDATORY_FIELD, super.getTagDefinition().getName());
                return null;
            }
            if (value < super.getTagDefinition().getMin()) {
                throw new InputException(InputException.Rule.LESS_THAN_MIN,
                        super.getTagDefinition().getName(),
                        super.getTagDefinition().getMin());
            }
            if (value > super.getTagDefinition().getMax()) {
                throw new InputException(InputException.Rule.MORE_THAN_MAX,
                        super.getTagDefinition().getName(),
                        super.getTagDefinition().getMax());
            }
        }
        return value;
    }

    static class SliderPopupListener extends MouseAdapter {
        private final JWindow toolTip = new JWindow();
        private final JLabel label = new JLabel("", SwingConstants.CENTER);
        private final Dimension size = new Dimension(60, 20);
        private int prevValue = -1;

        public SliderPopupListener() {
            super();
            label.setOpaque(false);
            label.setBackground(UIManager.getColor("ToolTip.background"));
            label.setBorder(UIManager.getBorder("ToolTip.border"));
            toolTip.add(label);
            toolTip.setSize(size);
        }
        protected void updateToolTip(MouseEvent me) {
            JSlider slider = (JSlider) me.getComponent();
            int intValue = slider.getValue();
            if (prevValue != intValue) {
                label.setText(String.valueOf(slider.getValue()));
                Point pt = me.getPoint();
                pt.y = -size.height;
                SwingUtilities.convertPointToScreen(pt, me.getComponent());
                pt.translate(-size.width / 2, 0);
                toolTip.setLocation(pt);
            }
            prevValue = intValue;
        }
        @Override public void mouseDragged(MouseEvent me) {
            toolTip.setVisible(true);
            updateToolTip(me);
        }
        @Override public void mouseReleased(MouseEvent me) {
            toolTip.setVisible(false);
        }
    }
}
