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

package de.sg_o.lib.tagy.query.properties;

import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.data.TagContainer_;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.query.QueryProperty;
import de.sg_o.proto.tagy.TagDefinitionProto;
import de.sg_o.proto.tagy.query.NotEqualsProto;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings("unused")
public class NotEquals extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;
    private Long longValue = null;
    private Boolean boolValue = null;
    private String stringValue = null;

    public NotEquals(TagDefinition tagDefinition, long value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.notEqual(value);
        longValue = value;
    }

    public NotEquals(TagDefinition tagDefinition, Date value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.notEqual(value.getTime());
        longValue = value.getTime();
    }

    public NotEquals(TagDefinition tagDefinition, boolean value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.booleanValue.notEqual(value);
        boolValue = value;
    }

    public NotEquals(TagDefinition tagDefinition, @NotNull String value) {
        super(tagDefinition);
        if (tagDefinition.getType() == TagDefinitionProto.Type.ENUM) {
            int index = tagDefinition.getEnumerators().indexOf(value);
            if (index > -1) {
                this.queryProperty = () -> TagContainer_.longValue.notEqual(index);
                longValue = (long) index;
                return;
            }
        }
        this.queryProperty = () -> TagContainer_.stringValue.notEqual(value, QueryBuilder.StringOrder.CASE_SENSITIVE);
        stringValue = value;
    }

    public NotEquals(@NotNull NotEqualsProto.NotEquals proto) {
        super(proto.getQueryElement());
        if (proto.hasLongValue()) {
            this.longValue = proto.getLongValue();
            this.queryProperty = () -> TagContainer_.longValue.notEqual(this.longValue);
        } else if (proto.hasBoolValue()) {
            this.boolValue = proto.getBoolValue();
            this.queryProperty = () -> TagContainer_.booleanValue.notEqual(this.boolValue);
        } else if (proto.hasStringValue()) {
            this.stringValue = proto.getStringValue();
            this.queryProperty = () -> TagContainer_.stringValue.notEqual(this.stringValue, QueryBuilder.StringOrder.CASE_SENSITIVE);
        } else {
            throw new IllegalArgumentException("Missing Values");
        }
    }


    @Override
    protected @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec() {
        return queryProperty;
    }

    @Override
    public boolean matches(TagContainer tc) {
        if (tc == null) return false;
        if (longValue != null) return !longValue.equals(tc.getLongValue());
        if (boolValue != null) return !boolValue.equals(tc.getBooleanValue());
        if (stringValue != null) return !stringValue.equals(tc.getStringValue());
        return false;
    }

    @Override
    public @NotNull NotEqualsProto.NotEquals getAsProto() {
        NotEqualsProto.NotEquals.Builder builder = NotEqualsProto.NotEquals.newBuilder();
        builder.setQueryElement(getSuperProto());
        if (longValue != null) {
            builder.setLongValue(this.longValue);
        } else if (boolValue != null) {
            builder.setBoolValue(this.boolValue);
        } else if (stringValue != null) {
            builder.setStringValue(this.stringValue);
        }
        return builder.build();
    }
}
