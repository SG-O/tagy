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

package de.sg_o.lib.tagy.tag;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.data.MetaData_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.TagDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TagMigration {

    private Tag tag = null;
    private final String encoded;

    public TagMigration(Tag tag) {
        this.tag = tag;
        ObjectMapper mapper = new JsonMapper();
        mapper.setVisibility(
                mapper.getSerializationConfig()
                        .getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        );
        String encoded = null;
        try {
            encoded = mapper.writeValueAsString(tag);
        } catch (JsonProcessingException ignored) {
        }
        this.encoded = encoded;
    }

    public TagMigration(@NotNull TagDefinition definition, String encoded) {
        this.encoded = encoded;
        if (encoded == null) return;
        ObjectMapper mapper = new JsonMapper();
        try {
            JsonNode json = mapper.readTree(encoded);
            if (json == null) return;
            if (json.isObject()) tag = Tag.create(definition, json);
        } catch (JsonProcessingException ignored) {
        }
    }

    public Tag getTag() {
        return tag;
    }

    public String getEncoded() {
        return encoded;
    }

    private static List<MetaData> needsMigration(Project project) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb.apply(MetaData_.projectId.equal(project.getId())
                    .and(MetaData_.tags.equal("")));
            return qb;
        };
        return DB.query(MetaData.class, qbs, 100, 0);
    }

    public static boolean projectNeedsMigration(Project project) {
        return !needsMigration(project).isEmpty();
    }

    public static void migrate(Project project) {
        List<MetaData> needsMigration = needsMigration(project);
        while (!needsMigration.isEmpty()) {
            for (MetaData md : needsMigration) {
                md.save();
            }
            needsMigration = needsMigration(project);
        }
    }
}
