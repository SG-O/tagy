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

package de.sg_o.lib.tagy.tag.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.proto.tagy.TagDefinitionProto;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class TagList extends Tag {
    private final ArrayList<Tag> values;

    public TagList(@NotNull TagDefinition definition) {
        this(definition, 32);
    }

    public TagList(@NotNull TagDefinition definition, int initialCapacity) {
        super(definition);
        if (definition.getType() != TagDefinitionProto.Type.LIST) throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        if (initialCapacity < 0) throw new IllegalArgumentException("Initial capacity is negative");
        values = new ArrayList<>(initialCapacity);
    }

    public TagList(@NotNull TagDefinition definition, JsonNode raw) {
        super(definition);
        if (raw == null) {
            this.values = new ArrayList<>();
            return;
        }
        JsonNode array = raw;
        if (raw.isObject()) array = raw.get("values");
        if (array == null) array = raw.get(getKey());
        if (definition.getType() != TagDefinitionProto.Type.LIST) throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        if (array != null) {
            this.values = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                JsonNode value = array.get(i);
                if (value == null) continue;
                values.add(Tag.create(definition.resolveInternal(), value));
            }
        } else {
            this.values = new ArrayList<>();
        }

    }

    public boolean addValue(Tag value) {
        if (value == null) return false;
        if (value.getDefinition().getType() != super.getDefinition().resolveInternal().getType()) return false;
        return values.add(value);
    }

    public Tag removeValue(int index) {
        if (index < 0 || index >= values.size()) return null;
        return values.remove(index);
    }

    public boolean removeValue(Tag value) {
        return values.remove(value);
    }

    @JsonProperty(value = "values", index = 0)
    @JsonSerialize(using = TagListSerializer.class)
    public @NotNull ArrayList<Tag> getValue() {
        return new ArrayList<>(values);
    }

    @Override
    public String getValueAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < values.size(); i++) {
            Tag value = values.get(i);
            builder.append(value.getValueAsString());
            if (i < values.size() - 1) builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagList tagList = (TagList) o;
        if (!super.definitionEquals(((Tag) o).getDefinition())) return false;
        if (values.size() != tagList.values.size()) return false;
        for (int i = 0; i < values.size(); i++) {
            if (!Objects.equals(values.get(i).getValueAsString(), tagList.values.get(i).getValueAsString())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getDefinition(), getValue());
    }

    @Override
    public String toString() {
        return "\"" + super.getKey() +
                "\": " +
                getValueAsString();
    }
}
