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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.NewDB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.util.Util;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class StructureDefinition implements Serializable {
    @Id
    private Long id;

    private final String tagDefinitions = "";

    private final ToOne<Project> project = new ToOne<>(this, StructureDefinition_.project);

    @Transient
    BoxStore __boxStore = null;

    @Transient
    private ArrayList<TagDefinition> decodedTagDefinitions = new ArrayList<>();

    public StructureDefinition(Long id, String tagDefinitions, long projectId) {
        this.id = id;
        setTagDefinitions(tagDefinitions);
        this.project.setTargetId(projectId);
    }

    public StructureDefinition(@NotNull Project project) {
        this.project.setTarget(project);
    }

    public static List<StructureDefinition> query(QueryBoxSpec<StructureDefinition> queryBoxSpec, int length, int offset) {
        return NewDB.query(StructureDefinition.class, queryBoxSpec, length, offset);
    }

    public static StructureDefinition queryFirst(QueryBoxSpec<StructureDefinition> queryBoxSpec) {
        return NewDB.queryFirst(StructureDefinition.class, queryBoxSpec);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private void parseTagArray(@NotNull JsonNode tags) {
        for (int i = 0; i < tags.size(); i++) {
            JsonNode tag = tags.get(i);
            if (tag == null) {
                continue;
            }
            try {
                this.decodedTagDefinitions.add(new TagDefinition(tag));
            } catch (Exception ignore) {
            }
        }
    }

    public void setTagDefinitions(List<TagDefinition> tagDefinitions) {
        clearTagDefinitions();
        this.decodedTagDefinitions.addAll(tagDefinitions);
    }

    public void setTagDefinitions(JsonNode tags) {
        clearTagDefinitions();
        parseTagArray(tags);
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
        decodedTagDefinitions.clear();
    }

    public String getTagDefinitions() {
        return toString();
    }

    @JsonProperty(index = 0)
    public List<TagDefinition> getDecodedTagDefinitions() {
        return decodedTagDefinitions;
    }

    public Project resolveProject() {
        return project.getTarget();
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public boolean save() {
        BoxStore db = NewDB.getDb();
        if (db == null) return false;
        Box<StructureDefinition> box = db.boxFor(StructureDefinition.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructureDefinition that = (StructureDefinition) o;
        return Util.betterListEquals(getDecodedTagDefinitions(), that.getDecodedTagDefinitions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDecodedTagDefinitions());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<TagDefinition> tagDefinitions = new ArrayList<>(getDecodedTagDefinitions());
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
