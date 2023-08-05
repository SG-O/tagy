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
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.TagDefinition_;
import de.sg_o.lib.tagy.tag.TagContainer;
import de.sg_o.lib.tagy.tag.TagContainer_;
import org.jetbrains.annotations.NotNull;

public abstract class QueryProperty extends QueryElement {
    public final TagDefinition tagDefinition;

    public QueryProperty(TagDefinition tagDefinition) {
        this.tagDefinition = tagDefinition;
    }

    public TagDefinition getTagDefinition() {
        return tagDefinition;
    }

    public String getKey() {
        return tagDefinition.getKey();
    }

    protected abstract @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec();

    public @NotNull QueryBoxSpec<TagContainer> genrateQuerySpec() {
        return qb -> {
            qb.link(TagContainer_.tagDefinition)
                    .apply(TagDefinition_.key.equal(getKey(), io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE));
            qb.apply(getTagContainerQuerySpec().getQuery());
            return qb;
        };
    }
}
