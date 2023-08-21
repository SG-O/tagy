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

package de.sg_o.lib.tagy.query;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.query.modifiers.QueryInternal;
import de.sg_o.lib.tagy.query.properties.*;
import de.sg_o.proto.tagy.query.*;
import org.jetbrains.annotations.NotNull;

public abstract class QueryElement {

    public final TagDefinition tagDefinition;

    public QueryElement(TagDefinition tagDefinition) {
        this.tagDefinition = tagDefinition;
    }

    public QueryElement(QueryElementProto.QueryElement proto) {
        if (proto == null) throw new IllegalArgumentException("QueryElement null");
        if (!proto.hasTagDefinition()) throw new IllegalArgumentException("TagDefinition missing");
        this.tagDefinition = new TagDefinition(proto.getTagDefinition());
    }

    public TagDefinition getTagDefinition() {
        return tagDefinition;
    }

    public String getKey() {
        return tagDefinition.getKey();
    }

    public abstract @NotNull QueryBoxSpec<TagContainer> generateQuerySpec();

    public abstract boolean matches(TagContainer tc);

    public abstract com.google.protobuf.GeneratedMessageV3 getAsProto();

    public QueryElementProto.QueryElement getSuperProto() {
        QueryElementProto.QueryElement.Builder builder = QueryElementProto.QueryElement.newBuilder();
        builder.setTagDefinition(tagDefinition.getAsProto());
        return builder.build();
    }

    public static QueryElement decodeAny(@NotNull Any any) {

        try {
            if (any.is(QueryInternalProto.QueryInternal.class)) {
                return new QueryInternal(any.unpack(QueryInternalProto.QueryInternal.class));
            } else if (any.is(BetweenProto.Between.class)) {
                return new Between(any.unpack(BetweenProto.Between.class));
            } else if (any.is(ContainsProto.Contains.class)) {
                return new Contains(any.unpack(ContainsProto.Contains.class));
            } else if (any.is(EndsWithProto.EndsWith.class)) {
                return new EndsWith(any.unpack(EndsWithProto.EndsWith.class));
            } else if (any.is(EqualsProto.Equals.class)) {
                return new Equals(any.unpack(EqualsProto.Equals.class));
            } else if (any.is(GreaterProto.Greater.class)) {
                return new Greater(any.unpack(GreaterProto.Greater.class));
            } else if (any.is(IsNullProto.IsNull.class)) {
                return new IsNull(any.unpack(IsNullProto.IsNull.class));
            } else if (any.is(LessProto.Less.class)) {
                return new Less(any.unpack(LessProto.Less.class));
            } else if (any.is(NotEqualsProto.NotEquals.class)) {
                return new NotEquals(any.unpack(NotEqualsProto.NotEquals.class));
            } else if (any.is(NotNullProto.NotNull.class)) {
                return new de.sg_o.lib.tagy.query.properties.NotNull(any.unpack(NotNullProto.NotNull.class));
            } else if (any.is(StartsWithProto.StartsWith.class)) {
                return new StartsWith(any.unpack(StartsWithProto.StartsWith.class));
            }
        } catch (InvalidProtocolBufferException ignored) {
            return null;
        }
        return null;
    }
}
