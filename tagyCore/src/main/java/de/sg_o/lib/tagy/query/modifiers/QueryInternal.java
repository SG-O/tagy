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

package de.sg_o.lib.tagy.query.modifiers;

import com.google.protobuf.Any;
import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.data.TagContainer_;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.TagDefinition_;
import de.sg_o.lib.tagy.query.QueryElement;
import de.sg_o.proto.tagy.query.QueryInternalProto;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QueryInternal extends QueryElement {

    private final @NotNull QueryElement queryElement;
    private final QueryInternalProto.MatchCondition matchCondition;

    public QueryInternal(@NotNull TagDefinition tagDefinition, @NotNull QueryElement queryElement, QueryInternalProto.MatchCondition matchCondition) {
        super(tagDefinition);
        this.queryElement = queryElement;
        this.matchCondition = matchCondition;
    }

    public QueryInternal(QueryInternalProto.QueryInternal proto) {
        super(proto.getQueryElement());
        QueryElement queryElement = QueryElement.decodeAny(proto.getQueryElementChild());
        if (queryElement == null) throw new IllegalArgumentException("QueryElement is null");
        this.queryElement = queryElement;
        this.matchCondition = proto.getMatchCondition();
    }

    @Override
    public @NotNull QueryBoxSpec<TagContainer> generateQuerySpec() {
        return qb -> {
            qb.link(TagContainer_.tagDefinition)
                    .apply(TagDefinition_.key.equal(getKey(), io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE));
            QueryBuilder<TagContainer> internal = qb.link(TagContainer_.listValues);
            queryElement.generateQuerySpec().buildQuery(internal);
            return qb;
        };
    }

    @Override
    public boolean matches(TagContainer tc) {
        if (tc == null) return false;
        List<TagContainer> entries = tc.getListValues();
        if (matchCondition == QueryInternalProto.MatchCondition.MATCH_ONE) {
            for (TagContainer entry : entries) {
                if (queryElement.matches(entry)) return true;
            }
        } else if (matchCondition == QueryInternalProto.MatchCondition.MATCH_ALL) {
            for (TagContainer entry : entries) {
                if (!queryElement.matches(entry)) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public QueryInternalProto.QueryInternal getAsProto() {
        QueryInternalProto.QueryInternal.Builder builder = QueryInternalProto.QueryInternal.newBuilder();
        builder.setQueryElement(getSuperProto());
        Any any = Any.pack(queryElement.getAsProto());
        builder.setQueryElementChild(any);
        builder.setMatchCondition(matchCondition);
        return builder.build();
    }
}
