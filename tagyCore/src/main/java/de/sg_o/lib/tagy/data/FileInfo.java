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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.util.UrlConverter;
import de.sg_o.proto.tagy.FileInfoProto;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;
import org.apache.tika.Tika;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties({"checkedOut"})
@Entity
public class FileInfo {
    private static final Tika tika = new Tika();
    private static final UrlConverter urlConverter = new UrlConverter();

    @Id
    private Long id;
    @Index
    @Convert(converter = UrlConverter.class, dbType = String.class)
    private final URL absolutePath;
    private boolean annotated;
    private Date checkedOutUntil;
    private final ToOne<Project> project = new ToOne<>(this, FileInfo_.project);

    @Transient
    transient BoxStore __boxStore = null;

    public FileInfo(Long id, @NotNull URL absolutePath, boolean annotated, Date checkedOutUntil, long projectId) {
        this.id = id;
        this.absolutePath = absolutePath;
        this.annotated = annotated;
        this.checkedOutUntil = checkedOutUntil;
        this.project.setTargetId(projectId);
    }

    public FileInfo(@NotNull URL fileReference, @NotNull Project project) {
        this.absolutePath = fileReference;
        this.project.setTarget(project);
        annotated = false;
    }

    public FileInfo(@NotNull FileInfoProto.FileInfo proto, @NotNull Project project) {
        this.absolutePath = urlConverter.convertToEntityProperty(proto.getAbsolutePath());
        this.annotated = proto.getAnnotated();
        if (proto.hasCheckedOutUntil()) {
            this.checkedOutUntil = new Date(proto.getCheckedOutUntil());
        }
        if (!project.getProjectName().equals(proto.getProjectName())){
            throw new IllegalArgumentException("Invalid encoded TagDefinition");
        }
        this.project.setTarget(project);
    }

    public static @NotNull FileInfo openOrCreate(@NotNull URL url, @NotNull Project project) {
        UrlConverter urlConverter = new UrlConverter();
        String urlString = urlConverter.convertToDatabaseValue(url);
        QueryBoxSpec<FileInfo> qbs = qb -> qb
                .apply(FileInfo_.absolutePath.equal(urlString, io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE));
        FileInfo found = queryFirst(project, qbs);
        if (found == null) {
            found = new FileInfo(url, project);
            found.save();
        }
        return found;
    }

    public static List<FileInfo> query(@NotNull Project project, @NotNull QueryBoxSpec<FileInfo> queryBoxSpec, int count, int offset) {
        QueryBoxSpec<FileInfo> qbs = qb -> queryBoxSpec.buildQuery(qb)
                .apply(FileInfo_.projectId.equal(project.getId()));
        return DB.query(FileInfo.class, qbs, count, offset);
    }

    public static List<FileInfo> query(@NotNull Project project, boolean nonAnnotatedOnly, int count, int offset) {
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.apply(FileInfo_.annotated.equal(false));
            }
            return qb;
        };
        return query(project, qbs, count, offset);
    }

    public static FileInfo queryFirst(@NotNull Project project, @NotNull QueryBoxSpec<FileInfo> queryBoxSpec) {
        QueryBoxSpec<FileInfo> qbs = qb -> queryBoxSpec.buildQuery(qb)
                .apply(FileInfo_.projectId.equal(project.getId()));
        return DB.queryFirst(FileInfo.class, qbs);
    }

    public static FileInfo queryFirst(@NotNull Project project) {
        QueryBoxSpec<FileInfo> qbs = qb -> qb.apply(FileInfo_.annotated.equal(false));
        return queryFirst(project, qbs);
    }

    public static boolean deleteAll(@NotNull Project project, boolean nonAnnotatedOnly) {
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.apply(FileInfo_.annotated.equal(false));
            }
            qb = qb.apply(FileInfo_.projectId.equal(project.getId()));
            return qb;
        };
        return DB.delete(FileInfo.class, qbs);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty(value = "url", index = 0)
    public @NotNull String getUrlAsString() {
        return urlConverter.convertToDatabaseValue(absolutePath);
    }

    public InputStream getInputStream() throws IOException {
        return absolutePath.openStream();
    }

    public @NotNull URL getAbsolutePath() {
        return absolutePath;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
    }

    @JsonProperty(value = "annotated", index = 1)
    public boolean isAnnotated() {
        return annotated;
    }

    public boolean checkOut(Date until) {
        if (until == null) return false;
        if (isCheckedOut()) return false;
        checkedOutUntil = until;
        return true;
    }

    public boolean checkIn() {
        if (!isCheckedOut()) return false;
        checkedOutUntil = null;
        return true;
    }

    public Date getCheckedOutUntil() {
        if (checkedOutUntil == null) return null;
        if (checkedOutUntil.before(new Date())) return null;
        return checkedOutUntil;
    }

    public boolean isCheckedOut() {
        if (checkedOutUntil == null) return false;
        return checkedOutUntil.after(new Date());
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public FileType getFileType() {
        FileType fileType = FileType.UNKNOWN;
        String fileTypeString = null;
        try {
            fileTypeString = tika.detect(absolutePath);
        } catch (IOException ignored) {
        }

        if (fileTypeString == null) return fileType;
        String[] split = fileTypeString.split("/");
        if (split.length < 1) return fileType;
        fileTypeString = split[0];

        switch (fileTypeString) {
            case "image":
                fileType = FileType.IMAGE;
                break;
            case "audio":
            case "video":
                fileType = FileType.MEDIA;
                break;
            case "text":
                fileType = FileType.TEXT;
                break;
        }
        return fileType;
    }
    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<FileInfo> box = db.boxFor(FileInfo.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    public @NotNull FileInfoProto.FileInfo getAsProto() {
        FileInfoProto.FileInfo.Builder builder = FileInfoProto.FileInfo.newBuilder();
        builder.setAbsolutePath(getUrlAsString());
        builder.setAnnotated(isAnnotated());
        if (checkedOutUntil != null) {
            builder.setCheckedOutUntil(checkedOutUntil.getTime());
        }
        builder.setProjectName(getProject().getTarget().getProjectName());
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return isAnnotated() == fileInfo.isAnnotated() && Objects.equals(getUrlAsString(), fileInfo.getUrlAsString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrlAsString(), isAnnotated());
    }

    private String indent(int indent) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }

    public String toString(int indent) {
        return indent(indent) +
                "{\n" +
                indent(indent + 1) +
                "\"file\": \"" +
                getAbsolutePath() +
                "\",\n" +
                indent(indent + 1) +
                "\"annotated\": " +
                annotated +
                "\n" +
                indent(indent) +
                "}";
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
