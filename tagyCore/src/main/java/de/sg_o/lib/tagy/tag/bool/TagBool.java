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

package de.sg_o.lib.tagy.tag.bool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.floating.DoubleInput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TagBool extends Tag {
    private final boolean value;
    public TagBool(@NotNull TagDefinition definition, boolean value) {
        super(definition);
        if (definition.getType() != Type.BOOLEAN) throw new IllegalArgumentException("Definition is not of type double");
        this.value = value;
    }

    public TagBool(@NotNull TagDefinition definition, @NotNull JsonNode document) {
        super(definition);
        if (definition.getType() != Type.BOOLEAN) throw new IllegalArgumentException("Definition is not of type double");
        JsonNode value = document.get("value");
        if (value == null) value = document.get(getKey());
        Boolean tmp = null;
        if (value != null) {
            tmp = value.booleanValue();
        } else if (document.isBoolean()){
            tmp = document.booleanValue();
        }
        if (tmp == null) throw new IllegalArgumentException("Document does not contain key");
        this.value = tmp;
    }

    @JsonProperty(value = "value", index = 0)
    public @NotNull Boolean getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new DoubleInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagBool tagDouble = (TagBool) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return Boolean.compare(tagDouble.getValue(), getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": " + getValue();
    }
}
