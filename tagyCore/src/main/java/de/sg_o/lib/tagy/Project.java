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

package de.sg_o.lib.tagy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.data.DataManager;
import de.sg_o.lib.tagy.data.DataManager_;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.StructureDefinition_;
import de.sg_o.lib.tagy.util.MetaDataIterator;
import de.sg_o.lib.tagy.values.User;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"user"})
public class Project {
    @Id
    Long id;
    @NotNull
    @Unique
    private final String projectName;
    @NotNull
    private final ToOne<User> user = new ToOne<>(this, Project_.user);

    @Transient
    transient BoxStore __boxStore = null;

    public Project(Long id, @NotNull String projectName, long userId) {
        this.id = id;
        this.projectName = projectName;
        this.user.setTargetId(userId);
    }


    public Project(@NotNull String projectName, @NotNull User user) {
        if (projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("projectName");
        }
        this.projectName = projectName.trim();
        this.user.setTarget(user);
    }

    public static List<Project> query() {
        List<Project> projects = new ArrayList<>();
        BoxStore db = DB.getDb();
        if (db == null) return projects;
        Box<Project> box = db.boxFor(Project.class);
        if (box == null) return projects;
        return box.getAll();
    }

    public static Project queryFirst(QueryBoxSpec<Project> queryBoxSpec) {
        return DB.queryFirst(Project.class, queryBoxSpec);
    }

    public static Project openOrCreate(String name, User user) {
        QueryBoxSpec<Project> qbs = qb -> {
            qb = qb.equal(Project_.projectName, name, io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE);
            return qb;
        };
        Project found = queryFirst(qbs);
        if (found == null) {
            found = new Project(name, user);
            found.save();
        }
        return found;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(value = "projectName", index = 0)
    public @NotNull String getProjectName() {
        return projectName;
    }

    @JsonProperty(value = "structureDefinition", index = 2)
    public @NotNull StructureDefinition resolveStructureDefinition() {
        if (this.getId() == null) return new StructureDefinition(this);
        QueryBoxSpec<StructureDefinition> qbs = qb -> {
            qb = qb.equal(StructureDefinition_.projectId, this.getId());
            return qb;
        };
        StructureDefinition structureDefinition = StructureDefinition.queryFirst(qbs);
        if (structureDefinition == null) {
            structureDefinition = new StructureDefinition(this);
        }
        return structureDefinition;
    }

    @JsonProperty(value = "dataManager", index = 3)
    public DataManager resolveDataManager() {
        QueryBoxSpec<DataManager> qbs = qb -> {
            qb = qb.equal(DataManager_.projectId, this.getId());
            return qb;
        };
        DataManager dataManager = DataManager.queryFirst(qbs);
        if (dataManager == null) {
            dataManager = new DataManager(this);
        }
        return dataManager;
    }

    @JsonProperty(value = "user", index =1)
    public @NotNull User resolveUser() {
        return user.getTarget();
    }
    public @NotNull ToOne<User> getUser() {
        return user;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<Project> box = db.boxFor(Project.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean delete() {
        QueryBoxSpec<Project> qbs = qb -> {
            qb = qb.equal(Project_.id, getId());
            return qb;
        };
        this.id = 0L;
        return DB.delete(Project.class, qbs);
    }

    @SuppressWarnings("unused")
    @JsonProperty(value = "annotated", index = 4)
    private MetaDataIterator getMetaDataIterator() {
        return new MetaDataIterator(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(getProjectName(), project.getProjectName()) && Objects.equals(resolveUser(), project.resolveUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectName(), resolveUser());
    }

    @Override
    public String toString() {
        return "{" +
                "\"_id\": \"" + projectName + '\"' +
                '}';
    }
}
