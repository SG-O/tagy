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

import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.tag.TagContainer;
import de.sg_o.lib.tagy.tag.TagContainer_;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

public class QueryInternal extends QueryElement{
    private final @NotNull QueryProperty queryProperty;

    public QueryInternal(@NotNull QueryProperty queryProperty) {
        this.queryProperty = queryProperty;
    }

    @Override
    public @NotNull QueryBoxSpec<TagContainer> genrateQuerySpec() {
        return qb -> {
            QueryBuilder<TagContainer> internal = qb.link(TagContainer_.listValues);
            queryProperty.genrateQuerySpec().buildQuery(internal);
            return qb;
        };
    }
}