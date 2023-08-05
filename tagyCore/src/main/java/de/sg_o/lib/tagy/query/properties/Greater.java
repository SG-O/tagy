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

import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.def.Type;
import de.sg_o.lib.tagy.query.QueryProperty;
import de.sg_o.lib.tagy.tag.TagContainer;
import de.sg_o.lib.tagy.tag.TagContainer_;
import io.objectbox.query.QueryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings("unused")
public class Greater extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    public Greater(TagDefinition tagDefinition, long value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.greater(value);
    }

    public Greater(TagDefinition tagDefinition, Date value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.greater(value.getTime());
    }

    public Greater(TagDefinition tagDefinition, double value) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.doubleValue.greater(value);
    }


    @Override
    protected @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec() {
        return queryProperty;
    }
}
