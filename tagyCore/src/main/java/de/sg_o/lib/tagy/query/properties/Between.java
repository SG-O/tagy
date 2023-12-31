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
import de.sg_o.proto.tagy.query.BetweenProto;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Between extends QueryProperty {
    private final @NotNull de.sg_o.lib.tagy.db.QueryProperty<TagContainer> queryProperty;
    private Long longLowerBounds = null;
    private Long longUpperBounds = null;
    private Double doubleLowerBounds = null;
    private Double doubleUpperBounds = null;

    public Between(TagDefinition tagDefinition, long lowerBounds, long upperBounds) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.between(lowerBounds, upperBounds);
        longLowerBounds = lowerBounds;
        longUpperBounds = upperBounds;
    }

    public Between(TagDefinition tagDefinition, Date lowerBounds, Date upperBounds) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.longValue.between(lowerBounds.getTime(), upperBounds.getTime());
        longLowerBounds = lowerBounds.getTime();
        longUpperBounds = upperBounds.getTime();
    }

    public Between(TagDefinition tagDefinition, double lowerBounds, double upperBounds) {
        super(tagDefinition);
        this.queryProperty = () -> TagContainer_.doubleValue.between(lowerBounds, upperBounds);
        doubleLowerBounds = lowerBounds;
        doubleUpperBounds = upperBounds;
    }

    public Between(@NotNull BetweenProto.Between proto) {
        super(proto.getQueryElement());
        if (proto.hasLongLowerBounds() && proto.hasLongUpperBounds()) {
            this.queryProperty = () -> TagContainer_.longValue.between(proto.getLongLowerBounds(), proto.getLongUpperBounds());
            longLowerBounds = proto.getLongLowerBounds();
            longUpperBounds = proto.getLongUpperBounds();
        } else if (proto.hasDoubleLowerBounds() && proto.hasDoubleUpperBounds()) {
            this.queryProperty = () -> TagContainer_.doubleValue.between(proto.getDoubleLowerBounds(), proto.getDoubleUpperBounds());
            doubleLowerBounds = proto.getDoubleLowerBounds();
            doubleUpperBounds = proto.getDoubleUpperBounds();
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
        if (longLowerBounds != null && longUpperBounds != null && tc.getLongValue() != null)
            return (tc.getLongValue() >= longLowerBounds) && (tc.getLongValue() <= longUpperBounds);
        if (doubleLowerBounds != null && doubleUpperBounds != null && tc.getDoubleValue() != null)
            return (tc.getDoubleValue() >= doubleLowerBounds) && (tc.getDoubleValue() <= doubleUpperBounds);
        return false;
    }

    public @NotNull BetweenProto.Between getAsProto() {
        BetweenProto.Between.Builder builder = BetweenProto.Between.newBuilder();
        builder.setQueryElement(getSuperProto());
        if (longLowerBounds != null && longUpperBounds != null) {
            builder.setLongLowerBounds(longLowerBounds);
            builder.setLongUpperBounds(longUpperBounds);
        } else if (doubleLowerBounds != null && doubleUpperBounds != null) {
            builder.setDoubleLowerBounds(doubleLowerBounds);
            builder.setDoubleUpperBounds(doubleUpperBounds);
        }
        return builder.build();
    }
}
