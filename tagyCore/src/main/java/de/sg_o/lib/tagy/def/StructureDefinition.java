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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.util.Util;
import de.sg_o.proto.tagy.StructureDefinitionProto;
import de.sg_o.proto.tagy.TagDefinitionProto;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Entity
public class StructureDefinition implements Serializable {
    @Id
    private Long id;

    private final ToMany<TagDefinition> tagDefinitions = new ToMany<>(this, StructureDefinition_.tagDefinitions);

    private final ToOne<Project> project = new ToOne<>(this, StructureDefinition_.project);

    @Transient
    BoxStore __boxStore = null;

    @Transient
    private transient HashMap<String, TagDefinition> resolvedTagDefinitions = null;

    public StructureDefinition(Long id, long projectId) {
        this.id = id;
        this.project.setTargetId(projectId);
    }

    public StructureDefinition(@NotNull Project project) {
        this.project.setTarget(project);
    }

    public static List<StructureDefinition> query(QueryBoxSpec<StructureDefinition> queryBoxSpec, int length, int offset) {
        return DB.query(StructureDefinition.class, queryBoxSpec, length, offset);
    }

    public static StructureDefinition queryFirst(QueryBoxSpec<StructureDefinition> queryBoxSpec) {
        return DB.queryFirst(StructureDefinition.class, queryBoxSpec);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTagDefinitions(List<TagDefinition> tagDefinitions) {
        clearTagDefinitions();
        for (TagDefinition tagDefinition : tagDefinitions) {
            if (tagDefinition == null) continue;
            tagDefinition.setId(null);
            this.tagDefinitions.add(tagDefinition);
        }
    }

    public void setTagDefinitions(@NotNull StructureDefinitionProto.StructureDefinition proto) {
        clearTagDefinitions();
        for (TagDefinitionProto.TagDefinition tagDefinition : proto.getTagDefinitionList()) {
            this.tagDefinitions.add(new TagDefinition(tagDefinition));
        }
    }

    public void setTagDefinitions(JsonNode tags) {
        clearTagDefinitions();
        for (int i = 0; i < tags.size(); i++) {
            JsonNode tag = tags.get(i);
            if (tag == null) {
                continue;
            }
            try {
                this.tagDefinitions.add(new TagDefinition(tag));
            } catch (Exception ignore) {
            }
        }
    }

    public boolean setTagDefinitions(String json) {
        ObjectMapper mapper = new JsonMapper();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            if (jsonNode == null) return false;
            if (jsonNode.isArray()) setTagDefinitions(jsonNode);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void clearTagDefinitions() {
        BoxStore db = DB.getDb();
        Box<TagDefinition> box = null;
        if (db != null) box = db.boxFor(TagDefinition.class);
        for (int i = 0; i < this.tagDefinitions.size(); i++) {
            TagDefinition tagDefinition  = this.tagDefinitions.remove(i);
            this.tagDefinitions.applyChangesToDb();
            if (tagDefinition == null) continue;
            if (box == null) continue;
            if (tagDefinition.getId() == null) continue;
            if (tagDefinition.getId() == 0L) continue;
            box.remove(tagDefinition.getId());
        }
        resolvedTagDefinitions = null;
        tagDefinitions.clear();
        save();
    }

    @JsonProperty(value = "tagDefinitions", index = 0)
    @JsonSerialize(using = TagDefinitionListSerializer.class)
    public List<TagDefinition> getTagDefinitions() {
        return tagDefinitions;
    }

    public TagDefinition getTagDefinitionForKey(String key) {
        if (this.resolvedTagDefinitions == null) {
            this.resolvedTagDefinitions = new HashMap<>(this.tagDefinitions.size());
            for (TagDefinition tagDefinition : getTagDefinitions()) {
                this.resolvedTagDefinitions.put(tagDefinition.getKey(), tagDefinition);
            }
        }
        return this.resolvedTagDefinitions.get(key);
    }

    @SuppressWarnings("unused")
    public Project resolveProject() {
        return project.getTarget();
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<StructureDefinition> box = db.boxFor(StructureDefinition.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    public @NotNull StructureDefinitionProto.StructureDefinition getAsProto() {
        StructureDefinitionProto.StructureDefinition.Builder builder = StructureDefinitionProto.StructureDefinition.newBuilder();
        for(TagDefinition tagDefinition : tagDefinitions) {
            builder.addTagDefinition(tagDefinition.getAsProto());
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructureDefinition that = (StructureDefinition) o;
        return Util.betterListEquals(getTagDefinitions(), that.getTagDefinitions());
    }

    @Override
    public int hashCode() {
        return Util.betterListHash(getTagDefinitions());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < tagDefinitions.size(); i++) {
            TagDefinition tag = tagDefinitions.get(i);
            builder.append("\n");
            builder.append(tag.toString(1));
            if (i < tagDefinitions.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n]");
        return builder.toString();
    }
}
