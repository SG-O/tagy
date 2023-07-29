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

import com.couchbase.lite.*;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

public abstract class Tag implements Serializable {
    private final transient TagDefinition definition;

    protected Tag(@NotNull TagDefinition definition) {
        this.definition = definition;
    }

    public static Tag create(@NotNull TagDefinition definition, @NotNull Dictionary dictionary) {
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
            default:
                return null;
        }
    }

    public static Tag create(@NotNull TagDefinition definition, @NotNull Array array, int index) {
        switch (definition.getType()) {
            case LIST:
                return new TagList(definition, array.getArray(index));
            case LONG:
                return new TagLong(definition, array.getLong(index));
            case DOUBLE:
                return new TagDouble(definition, array.getDouble(index));
            case ENUM:
                return new TagEnum(definition, array.getInt(index));
            case STRING:
                String value4 = array.getString(index);
                if (value4 == null) return null;
                return new TagString(definition, value4);
            case DATE:
                Date value5 = array.getDate(index);
                if (value5 == null) return null;
                return new TagDate(definition, value5);
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

    public abstract String getValueAsString();

    public abstract void addToDictionary(@NotNull MutableDictionary dictionary);
    public abstract void addToArray(@NotNull MutableArray array);

    @SuppressWarnings("unused")
    public abstract Input getInputElement();

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