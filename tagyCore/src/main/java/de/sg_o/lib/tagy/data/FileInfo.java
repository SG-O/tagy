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

import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.Project_;
import de.sg_o.lib.tagy.db.NewDB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

@Entity
public class FileInfo implements Serializable {
    @Id
    private Long id;
    @Index
    @NotNull
    private final String absolutePath;
    private boolean annotated;
    private final ToOne<Project> project = new ToOne<>(this, FileInfo_.project);

    public FileInfo(Long id, @NotNull String absolutePath, boolean annotated, long projectId) {
        this.id = id;
        this.absolutePath = absolutePath;
        this.annotated = annotated;
        this.project.setTargetId(projectId);
    }

    public FileInfo(@NotNull File file, @NotNull Project project) {
        this.absolutePath = file.getAbsolutePath();
        this.project.setTarget(project);
        validateFile();
        annotated = false;
    }

    private void validateFile() {
        File file = getFile();
        if (file.isDirectory()) throw new RuntimeException("File is a directory");
        if (!file.exists()) throw new RuntimeException("File does not exist");
        if (!file.canRead()) throw new RuntimeException("File is not readable");
    }

    public static FileInfo queryOrCreate(File file, Project project) {
        String absolutePathSearch = file.getAbsolutePath();
        QueryBoxSpec<FileInfo> qbs = qb -> {
            qb = qb.equal(FileInfo_.absolutePath, absolutePathSearch, io.objectbox.query.QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .equal(FileInfo_.projectId, project.getId());
            return qb;
        };
        FileInfo found = queryFirst(qbs);
        if (found == null) {
            found = new FileInfo(file, project);
        }
        return found;
    }

    public static List<FileInfo> query(Project project, boolean nonAnnotatedOnly, int length, int offset) {
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.equal(FileInfo_.annotated, false);
            }
            qb = qb.equal(FileInfo_.projectId, project.getId());
            return qb;
        };
        return NewDB.query(FileInfo.class, qbs, length, offset);
    }

    public static FileInfo queryFirst(QueryBoxSpec<FileInfo> queryBoxSpec) {
        return NewDB.queryFirst(FileInfo.class, queryBoxSpec);
    }

    public static FileInfo queryFirst(Project project, boolean nonAnnotatedOnly) {
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.equal(FileInfo_.annotated, false);
            }
            qb = qb.equal(FileInfo_.projectId, project.getId());
            return qb;
        };
        return NewDB.queryFirst(FileInfo.class, qbs);
    }

    public static boolean deleteAll(Project project, boolean nonAnnotatedOnly) {
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.equal(FileInfo_.annotated, false);
            }
            qb = qb.equal(FileInfo_.projectId, project.getId());
            return qb;
        };
        return NewDB.delete(FileInfo.class, qbs);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull File getFile() {
        return new File(absolutePath);
    }
    public @NotNull String getAbsolutePath() {
        return absolutePath;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
    }

    public boolean isAnnotated() {
        return annotated;
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public FileType getFileType() {
        FileType fileType = FileType.UNKNOWN;
        String fileTypeString = "";
        try {
            fileTypeString = Files.probeContentType(getFile().toPath());
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
        BoxStore db = NewDB.getDb();
        if (db == null) return false;
        Box<FileInfo> box = db.boxFor(FileInfo.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return isAnnotated() == fileInfo.isAnnotated() && Objects.equals(getFile(), fileInfo.getFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFile(), isAnnotated());
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
                getFile().getName() +
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
