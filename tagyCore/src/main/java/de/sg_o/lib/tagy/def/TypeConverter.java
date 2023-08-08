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

package de.sg_o.lib.tagy.def;

import de.sg_o.proto.tagy.TagDefinitionProto;
import io.objectbox.converter.PropertyConverter;
import org.jetbrains.annotations.NotNull;

public class TypeConverter implements PropertyConverter<TagDefinitionProto.Type, Integer> {
    @Override
    @NotNull
    public TagDefinitionProto.Type convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return TagDefinitionProto.Type.UNKNOWN;
        }
        TagDefinitionProto.Type type = TagDefinitionProto.Type.forNumber(databaseValue + 1);
        if (type == null) return TagDefinitionProto.Type.UNKNOWN;
        return type;
    }

    @Override
    public Integer convertToDatabaseValue(TagDefinitionProto.Type entityProperty) {
        return entityProperty == null ? null : (entityProperty.getNumber() - 1);
    }
}