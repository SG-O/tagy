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

package de.sg_o.lib.tagy.tag.string;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TagString extends Tag {
    @NotNull
    private final String value;

    public TagString(@NotNull TagDefinition definition, @NotNull String value) {
        super(definition);
        if (definition.getType() != TagDefinitionProto.Type.STRING) throw new IllegalArgumentException("Definition is not of type string");
        this.value = value;
    }

    public TagString(@NotNull TagDefinition definition, @Nullable JsonNode jsonNode) {
        super(definition);
        if (definition.getType() != TagDefinitionProto.Type.STRING) throw new IllegalArgumentException("Definition is not of type string");
        if (jsonNode == null) {
            value = "";
            return;
        }
        JsonNode value = jsonNode.get("value");
        if (value == null) value = jsonNode.get(getKey());
        String tmp = null;
        if (value != null) {
            tmp = value.asText();
        } else if (jsonNode.isValueNode()) {
            tmp = jsonNode.asText();
        }
        if (tmp == null) throw new IllegalArgumentException("Document does not contain key");
        this.value = tmp;
    }

    @JsonProperty(value = "value", index = 0)
    public @NotNull String getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagString tagString = (TagString) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        return getValue().equals(tagString.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() + "\": \"" + value + "\"";
    }
}
