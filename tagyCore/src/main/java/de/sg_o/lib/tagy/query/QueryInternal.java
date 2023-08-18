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

import de.sg_o.lib.tagy.data.TagContainer;
import de.sg_o.lib.tagy.data.TagContainer_;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.TagDefinition_;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QueryInternal extends QueryElement{
    public enum MatchCondition {
        MATCH_ONE,
        MATCH_ALL
    }

    private final @NotNull QueryProperty queryProperty;
    private final MatchCondition matchCondition;

    public QueryInternal(@NotNull TagDefinition tagDefinition, @NotNull QueryProperty queryProperty, MatchCondition matchCondition) {
        super(tagDefinition);
        this.queryProperty = queryProperty;
        this.matchCondition = matchCondition;
    }

    @Override
    public @NotNull QueryBoxSpec<TagContainer> genrateQuerySpec() {
        return qb -> {
            qb.link(TagContainer_.tagDefinition)
                    .apply(TagDefinition_.key.equal(getKey(), io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE));
            QueryBuilder<TagContainer> internal = qb.link(TagContainer_.listValues);
            queryProperty.genrateQuerySpec().buildQuery(internal);
            return qb;
        };
    }

    @Override
    protected boolean matches(TagContainer tc) {
        if (tc == null) return false;
        List<TagContainer> entries = tc.getListValues();
        if (matchCondition == MatchCondition.MATCH_ONE) {
            for (TagContainer entry : entries) {
                if (queryProperty.matches(entry)) return true;
            }
        } else if (matchCondition == MatchCondition.MATCH_ALL) {
            for (TagContainer entry : entries) {
                if (!queryProperty.matches(entry)) return false;
            }
            return true;
        }
        return false;
    }
}
