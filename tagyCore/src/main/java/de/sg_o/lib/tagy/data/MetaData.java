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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.def.StructureDefinition;
import de.sg_o.lib.tagy.def.TagDefinition;
import de.sg_o.lib.tagy.tag.Tag;
import de.sg_o.lib.tagy.tag.TagMigration;
import de.sg_o.lib.tagy.util.Util;
import de.sg_o.lib.tagy.values.User;
import de.sg_o.proto.tagy.MetaDataProto;
import de.sg_o.proto.tagy.TagContainerProto;
import de.sg_o.proto.tagy.UserProto;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.*;
import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Uid(131994956946247807L)
public class MetaData implements Serializable {
    @Id
    @Uid(5272684610711569869L)
    Long id;

    @Uid(6707238692496575331L)
    private final ToOne<Project> project = new ToOne<>(this, MetaData_.project);
    @Index
    @Uid(4669829739444121892L)
    private final String fileReference;
    @NotNull
    @Uid(1095464932469421925L)
    private final ToMany<User> editHistory = new ToMany<>(this, MetaData_.editHistory);
    @NotNull
    @Uid(3870338667975086962L)
    private final ToMany<TagContainer> tagContainers = new ToMany<>(this, MetaData_.tagContainers);

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Uid(6433602340359118350L)
    @Deprecated //Usage to support upgrades from previous versions.
    private Map<String, String> tags;
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Uid(2449724609247634831L)
    @Deprecated//Usage to support upgrades from previous versions.
    private final ToOne<FileInfo> reference = new ToOne<>(this, MetaData_.reference);

    @Transient
    transient BoxStore __boxStore = null;
    @Transient
    private transient boolean updated = false;
    @Transient
    private transient FileInfo resolvedFileReference = null;

    public MetaData(Long id, String fileReference, Map<String, String> tags, long projectId, long referenceId) {
        this.id = id;
        this.tags = tags;
        this.fileReference = fileReference;
        this.project.setTargetId(projectId);
        this.reference.setTargetId(referenceId);
    }

    public MetaData(@NotNull FileInfo reference, @NotNull Project project) {
        this.fileReference = reference.getUrlAsString();
        this.project.setTarget(project);
        this.tags = null;
    }

    public MetaData(@NotNull MetaDataProto.MetaData proto, @NotNull Project project) {
        this.fileReference = proto.getFileReference();
        for (UserProto.User user : proto.getEditHistoryList()) {
            User u = new User(user);
            this.editHistory.add(u);
        }
        for (TagContainerProto.TagContainer tc : proto.getTagContainersList()) {
            TagContainer t = new TagContainer(tc);
            this.tagContainers.add(t);
        }
        this.project.setTarget(project);
        this.tags = null;
    }

    public static List<MetaData> query(QueryBoxSpec<MetaData> queryBoxSpec, int length, int offset) {
        return DB.query(MetaData.class, queryBoxSpec, length, offset);
    }

    public static List<MetaData> queryAll(Project project, int length, int offset) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb = qb.equal(MetaData_.projectId, project.getId());
            return qb;
        };
        return query(qbs, length, offset);
    }

    public static MetaData queryFirst(QueryBoxSpec<MetaData> queryBoxSpec) {
        return DB.queryFirst(MetaData.class, queryBoxSpec);
    }

    public static MetaData queryFirst(FileInfo reference, Project project) {
        if (reference == null) return null;
        if (reference.getId() == null) return null;
        QueryBoxSpec<MetaData> qbs = qb -> qb.apply(MetaData_.fileReference
                .equal(reference.getUrlAsString(), QueryBuilder.StringOrder.CASE_SENSITIVE)
                .and(MetaData_.projectId.equal(project.getId())));
        MetaData found = queryFirst(qbs);
        if (found == null) return null;
        if(found.getTagContainers().isEmpty() && found.getEditHistory().isEmpty()) {
            return null;
        }
        return found;
    }

    public static MetaData openOrCreate(FileInfo reference, Project project) {
        MetaData found = queryFirst(reference, project);
        if (found == null) {
            found = new MetaData(reference, project);
            found.save();
        }
        return found;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean deleteAll(@NotNull Project project) {
        QueryBoxSpec<MetaData> qbs = qb -> {
            qb = qb.apply(MetaData_.projectId.equal(project.getId()));
            return qb;
        };
        return DB.delete(MetaData.class, qbs);
    }

    public void addTag(@NotNull Tag tag) {
        TagContainer container = new TagContainer(tag);
        container.save();
        tagContainers.add(container);
        this.resolveFileReference().setAnnotated(true);
        if (!this.updated) {
            editHistory.add(resolveProject().resolveUser());
            this.updated = true;
        }
    }

    @SuppressWarnings("unused")
    @JsonProperty(value = "id", index = 0)
    public String getID() {
        return resolveFileReference().getUrlAsString();
    }

    public void clearTags() {
        BoxStore db = DB.getDb();
        Box<TagContainer> box = null;
        if (db != null) box = db.boxFor(TagContainer.class);

        for (int i = 0; i < this.tagContainers.size(); i++) {
            TagContainer tagContainer = this.tagContainers.remove(i);
            if (this.id == null || this.id == 0L) continue;
            this.tagContainers.applyChangesToDb();
            if (tagContainer == null) continue;
            if (box == null) continue;
            if (tagContainer.getId() == null) continue;
            if (tagContainer.getId() == 0L) continue;
            box.remove(tagContainer.getId());
        }
        tagContainers.clear();
        save();
    }

    @SuppressWarnings("unused")
    public void setTags(@Nullable List<Tag> tags) {
        clearTags();
        if (tags == null) return;
        for (Tag tag : tags) {
            addTag(tag);
        }
    }

    public Map<String, String> getTags() {
        return null;
    }

    private void migrateTags() {
        if (tags == null) return;
        StructureDefinition structureDefinition = resolveProject().resolveStructureDefinition();
        for (TagDefinition definition : structureDefinition.getTagDefinitions()) {
            String encoded = tags.get(definition.getKey());
            if (encoded == null) continue;
            TagMigration tagMigration = new TagMigration(definition, encoded);
            Tag tag = tagMigration.getTag();
            if (tag == null) continue;
            TagContainer container = new TagContainer(tag);
            container.save();
            tagContainers.add(container);
        }
        tags.clear();
        tags = null;
    }

    @JsonProperty(index = 1)
    @JsonSerialize(using = TagContainerListSerializer.class)
    public ToMany<TagContainer> getTagContainers() {
        if (this.tags != null) migrateTags();
        return tagContainers;
    }

    public void repairTagContainer() {
        System.out.println("Repairing: " + getFileReference());
        for (int i = 0; i < tagContainers.size(); i++) {
            TagContainer container = tagContainers.get(i);
            if (container.getTagDefinition().getTarget() == null) {
                StructureDefinition structureDefinition = resolveProject().resolveStructureDefinition();
                container.getTagDefinition().setTarget(structureDefinition.getTagDefinitions().get(i));
                container.save();
            }
        }
    }

    public Project resolveProject() {
        return project.getTarget();
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public ToOne<FileInfo> getReference() {
        getFileReference();
        return new ToOne<>(this, MetaData_.reference);
    }

    public FileInfo resolveFileReference() {
        if (this.resolvedFileReference != null) return this.resolvedFileReference;
        String fileRef = getFileReference();
        QueryBoxSpec<FileInfo> qbs = qb -> qb.apply(FileInfo_.absolutePath
                .equal(fileRef, QueryBuilder.StringOrder.CASE_SENSITIVE).and(FileInfo_.projectId
                .equal(resolveProject().getId())));
        this.resolvedFileReference = FileInfo.queryFirst(resolveProject(), qbs);
        return this.resolvedFileReference;
    }

    public @NotNull String getFileReference() {
        if (fileReference == null) {
            FileInfo ref = reference.getTarget();
            if (ref == null) return "";
            return ref.getUrlAsString();
        }
        return fileReference;
    }

    public List<User> getEditHistory() {
        return editHistory;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean save() {
        if (this.tags != null) migrateTags();
        for (TagContainer container : tagContainers) {
            if (!container.save()) return false;
        }
        if(tagContainers.isEmpty() && editHistory.isEmpty()) {
            return false;
        }
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<MetaData> box = db.boxFor(MetaData.class);
        if (box == null) return false;
        this.id = box.put(this);
        FileInfo ref = this.resolveFileReference();
        if (ref != null) ref.save();
        return true;
    }

    public @NotNull MetaDataProto.MetaData getAsProto() {
        MetaDataProto.MetaData.Builder builder = MetaDataProto.MetaData.newBuilder();
        builder.setFileReference(this.fileReference);
        for (User user : this.editHistory) {
            builder.addEditHistory(user.getAsProto());
        }
        for (TagContainer container : this.tagContainers) {
            builder.addTagContainers(container.getAsProto());
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(resolveProject(), metaData.resolveProject()) &&
                Objects.equals(resolveFileReference(), metaData.resolveFileReference()) &&
                Util.betterListEquals(getEditHistory(), metaData.getEditHistory()) &&
                Util.betterListEquals(getTagContainers(), metaData.getTagContainers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(resolveProject(),
                resolveFileReference(),
                Util.betterListHash(getEditHistory()),
                Util.betterListHash(getTagContainers()));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("\t\"_id\": \n");
        if (resolveFileReference() != null) {
            builder.append(resolveFileReference().toString(2));
        } else {
            builder.append("\t\tnull");
        }
        builder.append(",\n");
        List<TagContainer> tags = getTagContainers();
        for (TagContainer tag : tags) {
            builder.append("\t");
            builder.append(tag.toString());
            builder.append(",\n");
        }
        builder.append("\t\"_editHistory\": \n\t\t[\n");
        for (int i = 0; i < editHistory.size(); i++) {
            User user = editHistory.get(i);
            builder.append("\t\t\t");
            builder.append(user.toString());
            if (i < editHistory.size() - 1) builder.append(",");
            builder.append("\n");
        }
        builder.append("\t\t]\n");
        builder.append('}');
        return builder.toString();
    }
}
