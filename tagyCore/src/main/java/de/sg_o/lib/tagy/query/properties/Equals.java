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
import de.sg_o.proto.tagy.query.EqualsProto;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings("unused")
public class Equals extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    private Long longValue = null;
    private Double doubleValue = null;
    private Double doubleTolerance = null;
    private Boolean boolValue = null;
    private String stringValue = null;

    public Equals(TagDefinition tagDefinition, long value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.equal(value);
        longValue = value;
    }

    public Equals(TagDefinition tagDefinition, Date value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.equal(value.getTime());
        longValue = value.getTime();
    }

    public Equals(TagDefinition tagDefinition, double value, double tolerance) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.doubleValue.equal(value, tolerance);
        doubleValue = value;
        doubleTolerance = tolerance;
    }

    public Equals(TagDefinition tagDefinition, boolean value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.booleanValue.equal(value);
        boolValue = value;
    }

    public Equals(TagDefinition tagDefinition, @NotNull String value) {
        super(tagDefinition);
        if (tagDefinition.getType() == TagDefinitionProto.Type.ENUM) {
            int index = tagDefinition.getEnumerators().indexOf(value);
            if (index > -1) {
                this.queryProperty = () -> TagContainer_.longValue.equal(index);
                longValue = (long) index;
                return;
            }
        }
        this.queryProperty = () -> TagContainer_.stringValue.equal(value, QueryBuilder.StringOrder.CASE_SENSITIVE);
        stringValue = value;
    }

    public Equals(@NotNull EqualsProto.Equals proto) {
        super(proto.getQueryElement());
        if (proto.hasLongValue()) {
            this.longValue = proto.getLongValue();
            this.queryProperty = () -> TagContainer_.longValue.equal(this.longValue);
        } else if (proto.hasDoubleValue()) {
            this.doubleValue = proto.getDoubleValue();
            this.doubleTolerance = proto.getDoubleTolerance();
            this.queryProperty = () -> TagContainer_.doubleValue.equal(this.doubleValue, this.doubleTolerance);
        } else if (proto.hasBoolValue()) {
            this.boolValue = proto.getBoolValue();
            this.queryProperty = () -> TagContainer_.booleanValue.equal(this.boolValue);
        } else if (proto.hasStringValue()) {
            this.stringValue = proto.getStringValue();
            this.queryProperty = () -> TagContainer_.stringValue.equal(this.stringValue, QueryBuilder.StringOrder.CASE_SENSITIVE);
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
        if (longValue != null) return longValue.equals(tc.getLongValue());
        if (doubleValue != null && doubleTolerance != null && tc.getDoubleValue() != null) {
            return (tc.getDoubleValue() >= (doubleValue - doubleTolerance) && tc.getDoubleValue() <= (doubleValue + doubleTolerance));
        }
        if (boolValue != null) return boolValue.equals(tc.getBooleanValue());
        if (stringValue != null) return stringValue.equals(tc.getStringValue());
        return false;
    }

    @Override
    public @NotNull EqualsProto.Equals getAsProto() {
        EqualsProto.Equals.Builder builder = EqualsProto.Equals.newBuilder();
        builder.setQueryElement(getSuperProto());
        if (longValue != null) {
            builder.setLongValue(this.longValue);
        } else if (doubleValue != null) {
            builder.setDoubleValue(this.doubleValue);
            if (this.doubleTolerance != null) {
                builder.setDoubleTolerance(this.doubleTolerance);
            }
        } else if (boolValue != null) {
            builder.setBoolValue(this.boolValue);
        } else if (stringValue != null) {
            builder.setStringValue(this.stringValue);
        }
        return builder.build();
    }
}
