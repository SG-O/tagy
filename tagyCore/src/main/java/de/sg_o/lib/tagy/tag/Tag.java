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

package de.sg_o.lib.tagy.tag;

import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public abstract class Tag implements Serializable {
    private final transient TagDefinition definition;

    protected Tag(@NotNull TagDefinition definition) {
        this.definition = definition;
    }

    public static Tag create(@NotNull TagDefinition definition, @NotNull JsonNode dictionary) {
        switch (definition.getType()) {
            case LIST:
                return new TagList(definition, dictionary);
            case LONG:
                return new TagLong(definition, dictionary);
            case DOUBLE:
                return new TagDouble(definition, dictionary);
            case ENUM:
                return new TagEnum(definition, dictionary);
            case STRING:
                return new TagString(definition, dictionary);
            case DATE:
                return new TagDate(definition, dictionary);
            case BOOLEAN:
                return new TagBool(definition, dictionary);
            default:
                return null;
        }
    }

    public TagDefinition getDefinition() {
        return definition;
    }

    public String getKey() {
        return definition.getKey();
    }

    @NotNull
    public abstract Object getValue();

    public abstract String getValueAsString();

    @Override
    public abstract boolean equals(Object o);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean definitionEquals(TagDefinition definition) {
        return this.definition.equals(definition);
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
