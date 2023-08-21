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
import de.sg_o.proto.tagy.query.StartsWithProto;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

public class StartsWith extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    private final String stringValue;

    public StartsWith(TagDefinition tagDefinition, @NotNull String value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.stringValue.startsWith(value, QueryBuilder.StringOrder.CASE_SENSITIVE);
        stringValue = value;
    }

    public StartsWith(@NotNull StartsWithProto.StartsWith proto) {
        super(proto.getQueryElement());
        this.stringValue = proto.getStringValue();
        this.queryProperty = () -> TagContainer_.stringValue.startsWith(this.stringValue, QueryBuilder.StringOrder.CASE_SENSITIVE);
    }

    @Override
    protected @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec() {
        return queryProperty;
    }

    @Override
    public boolean matches(TagContainer tc) {
        if (tc == null) return false;
        if (tc.getStringValue() != null) return tc.getStringValue().startsWith(stringValue);
        return false;
    }

    @Override
    public @NotNull StartsWithProto.StartsWith getAsProto() {
        StartsWithProto.StartsWith.Builder builder = StartsWithProto.StartsWith.newBuilder();
        builder.setQueryElement(getSuperProto());
        builder.setStringValue(this.stringValue);
        return builder.build();
    }
}
