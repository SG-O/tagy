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

package de.sg_o.lib.tagy.tag.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.tag.Input;
import de.sg_o.lib.tagy.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class TagEnum extends Tag {
    private static final String UNRECOGNIZED = "UNRECOGNIZED";

    private final int value;

    public TagEnum(@NotNull TagDefinition definition, int value) {
        super(definition);
        if (definition.getType() != Type.ENUM) throw new IllegalArgumentException("Definition is not of type enum");
        if (value < 0) value = -1;
        this.value = value;
    }

    public TagEnum(@NotNull TagDefinition definition, @NotNull JsonNode document) {
        super(definition);
        if (definition.getType() != Type.ENUM) throw new IllegalArgumentException("Definition is not of type enum");
        JsonNode value = document.get("value");
        if (value == null) throw new IllegalArgumentException("Document does not contain key");
        this.value = value.intValue();
    }

    @JsonProperty(value = "value", index = 0)
    public @NotNull Integer getValue() {
        return value;
    }

    public List<String> getEnumerators() {
        return super.getDefinition().getEnumerators();
    }

    @Override
    public String getValueAsString() {
        if (value < 0) return UNRECOGNIZED;
        List<String> enumerators = super.getDefinition().getEnumerators();
        if (value >= enumerators.size()) return UNRECOGNIZED;
        return enumerators.get(value);
    }

    @SuppressWarnings("unused")
    @Override
    public Input getInputElement() {
        return new EnumInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagEnum tagEnum = (TagEnum) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return getValue().equals(tagEnum.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": \"" + getValueAsString() + "\"";
    }
}
