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
import de.sg_o.proto.tagy.query.IsNullProto;
import org.jetbrains.annotations.NotNull;

public class IsNull extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    public IsNull(TagDefinition tagDefinition) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.isNull()
                .and(TagContainer_.doubleValue.isNull())
                .and(TagContainer_.booleanValue.isNull())
                .and(TagContainer_.stringValue.isNull());
    }

    public IsNull(@NotNull IsNullProto.IsNull proto) {
        super(proto.getQueryElement());
        this.queryProperty = () -> TagContainer_.longValue.isNull()
                .and(TagContainer_.doubleValue.isNull())
                .and(TagContainer_.booleanValue.isNull())
                .and(TagContainer_.stringValue.isNull());
    }

    @Override
    protected @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec() {
        return queryProperty;
    }

    @Override
    public boolean matches(TagContainer tc) {
        if (tc == null) return false;
        return (tc.getLongValue() == null && tc.getDoubleValue() == null && tc.getBooleanValue() == null && tc.getStringValue() == null);
    }

    @Override
    public @NotNull IsNullProto.IsNull getAsProto() {
        IsNullProto.IsNull.Builder builder = IsNullProto.IsNull.newBuilder();
        builder.setQueryElement(getSuperProto());
        return builder.build();
    }
}