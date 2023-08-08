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

public class ParameterConverter implements PropertyConverter<TagDefinitionProto.Parameter, Integer> {
    @Override
    @NotNull
    public TagDefinitionProto.Parameter convertToEntityProperty(Integer databaseValue) {
        if (databaseValue == null) {
            return TagDefinitionProto.Parameter.NONE;
        }
        TagDefinitionProto.Parameter parameter = TagDefinitionProto.Parameter.forNumber(databaseValue);
        if (parameter == null) return TagDefinitionProto.Parameter.NONE;
        return parameter;
    }

    @Override
    public Integer convertToDatabaseValue(TagDefinitionProto.Parameter parameter) {
        return parameter == null ? 0 : parameter.getNumber();
    }
}