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

package de.sg_o.lib.tagy.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.sg_o.lib.tagy.tag.Tag;

import java.io.IOException;
import java.util.List;

public class TagContainerListSerializer extends JsonSerializer<List<TagContainer>> {

    @Override
    public void serialize(List<TagContainer> value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        for (TagContainer td : value) {
            Tag tag = td.getTag();
            jgen.writePOJOField(tag.getKey(), tag.getValue());
        }
        jgen.writeEndObject();
    }
}