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

package de.sg_o.lib.tagy.def;

import com.fasterxml.jackson.databind.JsonNode;
import de.sg_o.lib.tagy.util.JsonPrintable;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class TagEnablerDefinition extends JsonPrintable implements Serializable {
    @Id
    private Long id;
    @NotNull
    private final String selectorKey;
    private final int enumIndex;

    private final String enumString;

    @SuppressWarnings("unused")
    public TagEnablerDefinition(Long id, @NotNull String selectorKey, int enumIndex, String enumString) {
        this.id = id;
        this.selectorKey = selectorKey;
        this.enumIndex = enumIndex;
        if (enumIndex > -1) enumString = null;
        this.enumString = enumString;
    }

    public TagEnablerDefinition(@NotNull String selectorKey, int enumIndex) {
        this.selectorKey = selectorKey;
        this.enumIndex = enumIndex;
        this.enumString = null;
    }

    public TagEnablerDefinition(@NotNull String selectorKey, String enumString) {
        this.selectorKey = selectorKey;
        this.enumIndex = -1;
        this.enumString = enumString;
    }

    public TagEnablerDefinition(@NotNull JsonNode encoded) {
        JsonNode selectorKeyNode = encoded.get(StructureConstants.SELECTOR_KEY_KEY);
        if (selectorKeyNode != null) {
            this.selectorKey = selectorKeyNode.textValue();
        } else {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        JsonNode enumIndexNode = encoded.get(StructureConstants.ENUM_ENTRY_KEY);
        if (enumIndexNode != null) {
            if (enumIndexNode.canConvertToInt()) {
                this.enumIndex = enumIndexNode.intValue();
                this.enumString = null;
            } else {
                this.enumString = enumIndexNode.textValue();
                this.enumIndex = -1;
            }
        } else {
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull String getSelectorKey() {
        return selectorKey;
    }

    public int getEnumIndex() {
        return enumIndex;
    }

    public String getEnumString() {
        return enumString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagEnablerDefinition that = (TagEnablerDefinition) o;
        return getEnumIndex() == that.getEnumIndex() && Objects.equals(getSelectorKey(),
                that.getSelectorKey()) && Objects.equals(getEnumString(), that.getEnumString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelectorKey(), getEnumIndex(), getEnumString());
    }

    public String toString(int indent) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateIndent(indent));
        builder.append("{\n");
        builder.append(generateEntity(StructureConstants.SELECTOR_KEY_KEY, selectorKey, true, indent));
        if (this.enumIndex > -1) {
            builder.append(",\n").append(generateEntity(StructureConstants.ENUM_ENTRY_KEY, enumIndex, false, indent));
        } else if (this.enumString != null) {
            builder.append(",\n").append(generateEntity(StructureConstants.ENUM_ENTRY_KEY, enumString, true, indent));
        }
        builder.append("\n");
        builder.append(generateIndent(indent));
        builder.append("}");
        return builder.toString();
    }
}
