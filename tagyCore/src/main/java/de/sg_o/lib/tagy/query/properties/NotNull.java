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

public class NotNull extends QueryProperty {
    private final @org.jetbrains.annotations.NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;

    public NotNull(TagDefinition tagDefinition) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.notNull()
                .or(TagContainer_.doubleValue.notNull())
                .or(TagContainer_.booleanValue.notNull())
                .or(TagContainer_.stringValue.notNull());
    }

    @Override
    protected @org.jetbrains.annotations.NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> getTagContainerQuerySpec() {
        return queryProperty;
    }

    @Override
    protected boolean matches(TagContainer tc) {
        if (tc == null) return false;
        return (tc.getLongValue() != null || tc.getDoubleValue() != null || tc.getBooleanValue() != null || tc.getStringValue() != null);
    }
}