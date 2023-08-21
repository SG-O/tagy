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
import de.sg_o.proto.tagy.query.LessProto;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings("unused")
public class Less extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    private Long longValue = null;
    private Double doubleValue = null;

    public Less(TagDefinition tagDefinition, long value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.less(value);
        longValue = value;
    }

    public Less(TagDefinition tagDefinition, Date value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.less(value.getTime());
        longValue = value.getTime();
    }

    public Less(TagDefinition tagDefinition, double value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.doubleValue.less(value);
        doubleValue = value;
    }

    public Less(@NotNull LessProto.Less proto) {
        super(proto.getQueryElement());
        if (proto.hasLongValue()) {
            this.longValue = proto.getLongValue();
            this.queryProperty = () -> TagContainer_.longValue.less(this.longValue);
        } else if (proto.hasDoubleValue()) {
            this.doubleValue = proto.getDoubleValue();
            this.queryProperty = () -> TagContainer_.doubleValue.less(this.doubleValue);
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
        if (longValue != null && tc.getLongValue() != null)
            return (tc.getLongValue() <= longValue);
        if (doubleValue != null && tc.getDoubleValue() != null)
            return (tc.getDoubleValue() <= doubleValue);
        return false;
    }

    @Override
    public @NotNull LessProto.Less getAsProto() {
        LessProto.Less.Builder builder = LessProto.Less.newBuilder();
        builder.setQueryElement(getSuperProto());
        if (longValue != null) {
            builder.setLongValue(this.longValue);
        } else if (doubleValue != null) {
            builder.setDoubleValue(this.doubleValue);
        }
        return builder.build();
    }
}
