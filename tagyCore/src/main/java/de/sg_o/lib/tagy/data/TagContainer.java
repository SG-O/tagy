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

package de.sg_o.lib.tagy.data;

import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.bool.TagBool;
import de.sg_o.lib.tagy.tag.date.TagDate;
import de.sg_o.lib.tagy.tag.enumerator.TagEnum;
import de.sg_o.lib.tagy.tag.floating.TagDouble;
import de.sg_o.lib.tagy.tag.integer.TagLong;
import de.sg_o.lib.tagy.tag.list.TagList;
import de.sg_o.lib.tagy.tag.string.TagString;
import de.sg_o.proto.tagy.TagContainerProto;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class TagContainer {
    @Id
    private Long id;

    private Boolean booleanValue = null;
    private Long longValue = null;
    private Double doubleValue = null;
    private String stringValue = null;

    private final ToOne<TagDefinition> tagDefinition = new ToOne<>(this, TagContainer_.tagDefinition);
    private final ToMany<TagContainer> listValues = new ToMany<>(this, TagContainer_.listValues);

    @Transient
    transient BoxStore __boxStore = null;

    public TagContainer(Long id, Boolean booleanValue, Long longValue, Double doubleValue, String stringValue, long tagDefinitionId) {
        this.id = id;
        this.booleanValue = booleanValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
        this.tagDefinition.setTargetId(tagDefinitionId);
    }

    public TagContainer(@NotNull Tag tag) {
        TagDefinition tagDefinition = tag.getDefinition();
        this.tagDefinition.setTarget(tagDefinition);
        Object value = tag.getValue();
        if (value instanceof Boolean) {
            this.booleanValue = (Boolean) value;
        } else if (value instanceof Date) {
            this.longValue = ((Date) value).getTime();
        } else if (value instanceof Integer) {
            this.longValue = ((Integer) value).longValue();
        } else if (value instanceof Long) {
            this.longValue = (Long) value;
        } else if (value instanceof Double) {
            this.doubleValue = (Double) value;
        } else if (value instanceof String) {
            this.stringValue = (String) value;
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object t : list) {
                if (!(t instanceof Tag)) continue;
                TagContainer tc = new TagContainer((Tag) t);
                this.listValues.add(tc);
            }
        } else {
            this.stringValue = value.toString();
        }
        validate();
    }

    public TagContainer(@NotNull TagContainerProto.TagContainer proto) {
        this.tagDefinition.setTarget(new TagDefinition(proto.getTagDefinition()));
        if (proto.hasBooleanValue()) {
            this.booleanValue = proto.getBooleanValue();
        } else if (proto.hasLongValue()) {
            this.longValue = proto.getLongValue();
        } else if (proto.hasDoubleValue()) {
            this.doubleValue = proto.getDoubleValue();
        } else if (proto.hasStringValue()) {
            this.stringValue = proto.getStringValue();
        } else {
            for (TagContainerProto.TagContainer tc : proto.getListValuesList()) {
                TagContainer t = new TagContainer(tc);
                this.listValues.add(t);
            }
        }
    }

    public void validate() {
        TagDefinition td = this.tagDefinition.getTarget();
        if (td == null) throw new RuntimeException("TagDefinition missing.");
        switch (td.getStorageType()) {
            case BOOLEAN:
                if (this.booleanValue == null) throw new RuntimeException("Expected boolean value.");
                break;
            case LONG:
                if (this.longValue == null) throw new RuntimeException("Expected long value.");
                break;
            case DOUBLE:
                if (this.doubleValue == null) throw new RuntimeException("Expected double value.");
                break;
            case LIST:
                break;
            case STRING:
            default:
                if (this.stringValue == null) throw new RuntimeException("Expected string value.");
        }
    }

    public Tag getTag() {
        TagDefinition td = this.tagDefinition.getTarget();
        if (td == null) throw new RuntimeException("TagDefinition missing.");
        validate();
        Tag out = null;
        switch (td.getType()) {
            case LIST:
                out = new TagList(td);
                for (TagContainer tc : this.listValues) {
                    ((TagList) out).addValue(tc.getTag());
                }
                break;
            case LONG:
                out = new TagLong(td, this.longValue);
                break;
            case DOUBLE:
                out = new TagDouble(td, this.doubleValue);
                break;
            case ENUM:
                out = new TagEnum(td, this.longValue.intValue());
                break;
            case STRING:
                out = new TagString(td, this.stringValue);
                break;
            case DATE:
                out = new TagDate(td, new Date(this.longValue));
                break;
            case BOOLEAN:
                out = new TagBool(td, this.booleanValue);
                break;
        }
        if (out == null) throw new RuntimeException("Unknown tag type.");
        return out;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TagDefinition resolveTagDefinition() {
        return this.tagDefinition.getTarget();
    }

    public ToOne<TagDefinition> getTagDefinition() {
        return tagDefinition;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public ToMany<TagContainer> getListValues() {
        return listValues;
    }

    public boolean save() {
        for (TagContainer tc : this.listValues) {
            if (!tc.save()) return false;
        }
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<TagContainer> box = db.boxFor(TagContainer.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    public @NotNull TagContainerProto.TagContainer getAsProto() {
        TagContainerProto.TagContainer.Builder builder = TagContainerProto.TagContainer.newBuilder();
        builder.setTagDefinition(resolveTagDefinition().getAsProto());
        if (this.booleanValue != null) {
            builder.setBooleanValue(this.booleanValue);
        } else if (this.longValue != null) {
            builder.setLongValue(this.longValue);
        } else if (this.doubleValue != null) {
            builder.setDoubleValue(this.doubleValue);
        } else if (this.stringValue != null) {
            builder.setStringValue(this.stringValue);
        } else {
            ArrayList<TagContainerProto.TagContainer> listValues = new ArrayList<>();
            for (TagContainer tc : this.listValues) {
                listValues.add(tc.getAsProto());
            }
            builder.addAllListValues(listValues);
            builder.setIsList(true);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagContainer container = (TagContainer) o;
        return Objects.equals(getTag(), container.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTag());
    }

    @Override
    public String toString() {
        return getTag().toString();
    }
}
