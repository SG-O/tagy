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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class TagList extends Tag {
    private final ArrayList<Tag> values;
    private final int fixedSize;

    public TagList(@NotNull TagDefinition definition) {
        this(definition, 32);
    }

    public TagList(@NotNull TagDefinition definition, int initialCapacity) {
        super(definition);
        if (definition.getType() != TagDefinitionProto.Type.LIST) throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        if (definition.getFixedSize() > -1) {
            this.fixedSize = definition.getFixedSize();
            initialCapacity = definition.getFixedSize();
        } else {
            this.fixedSize = -1;
        }
        if (initialCapacity < 0) throw new IllegalArgumentException("Initial capacity is negative");
        values = new ArrayList<>(initialCapacity);
        if (isFixedSize()) {
            while (values.size() < this.fixedSize) {
                values.add(Tag.create(definition.resolveInternal(), null));
            }
        }
    }

    public TagList(@NotNull TagDefinition definition, @Nullable JsonNode jsonNode) {
        super(definition);
        if (definition.getType() != TagDefinitionProto.Type.LIST)
            throw new IllegalArgumentException("Definition is not of type list");
        if (definition.getInternal() == null) throw new IllegalArgumentException("Internal definition is null");
        this.fixedSize = Math.max(definition.getFixedSize(), -1);
        if (jsonNode == null) {
            this.values = new ArrayList<>();
            return;
        }
        JsonNode array = jsonNode;
        if (jsonNode.isObject()) array = jsonNode.get("values");
        if (array == null) array = jsonNode.get(getKey());
        if (array != null) {
            this.values = new ArrayList<>(array.size());
            int limit = array.size();
            if (isFixedSize()) {
                if (this.fixedSize > array.size()) throw new IllegalArgumentException("Less values than required");
                limit = this.fixedSize;
            }
            for (int i = 0; i < limit; i++) {
                JsonNode value = array.get(i);
                if (value == null) continue;
                values.add(Tag.create(definition.resolveInternal(), value));
            }
        } else {
            this.values = new ArrayList<>();
        }

    }

    @SuppressWarnings("unused")
    public int getFixedSize() {
        return fixedSize;
    }

    @JsonProperty(value = "isFixedSize", index = 1)
    public boolean isFixedSize() {
        return fixedSize > -1;
    }

    public boolean addValue(Tag value) {
        if (value == null) return false;
        if (isFixedSize() && (this.values.size() >= this.fixedSize)) return false;
        if (value.getDefinition().getType() != super.getDefinition().resolveInternal().getType()) return false;
        return values.add(value);
    }

    public boolean setValue(Tag value, int index) {
        if (value == null) return false;
        if (isFixedSize() && (index >= this.fixedSize)) return false;
        if (value.getDefinition().getType() != super.getDefinition().resolveInternal().getType()) return false;
        if (index >= values.size()) return addValue(value);
        values.set(index, value);
        return true;
    }

    public Tag removeValue(int index) {
        if (isFixedSize()) return null;
        if (index < 0 || index >= values.size()) return null;
        return values.remove(index);
    }

    public boolean removeValue(Tag value) {
        if (isFixedSize()) return false;
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
