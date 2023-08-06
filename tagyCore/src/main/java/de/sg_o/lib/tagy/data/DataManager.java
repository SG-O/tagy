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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DB;
import de.sg_o.lib.tagy.db.QueryBoxSpec;
import de.sg_o.lib.tagy.util.FileInfoIterator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

@Entity
@JsonIgnoreProperties({ "listenerList", "project"})
public class DataManager extends AbstractTableModel {
    @Id
    Long id;
    private final ToMany<Directory> sourceDirectories = new ToMany<>(this, DataManager_.sourceDirectories);

    private final ToOne<Project> project = new ToOne<>(this, DataManager_.project);

    @Transient
    BoxStore __boxStore = null;

    public DataManager(Long id, long projectId) {
        this.id = id;
        this.project.setTargetId(projectId);
    }

    public DataManager(@NotNull Project project) {
        this.project.setTarget(project);
    }

    public static List<DataManager> query(QueryBoxSpec<DataManager> queryBoxSpec, int length, int offset) {
        return DB.query(DataManager.class, queryBoxSpec, length, offset);
    }

    public static DataManager queryFirst(QueryBoxSpec<DataManager> queryBoxSpec) {
        return DB.queryFirst(DataManager.class, queryBoxSpec);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addDirectory(@NotNull File directory) {
        Directory newDirectory = new Directory(directory, true);
        sourceDirectories.add(newDirectory);
        fireTableDataChanged();
    }

    public void removeDirectory(int index) {
        if (index < 0 || index >= sourceDirectories.size()) return;
        this.sourceDirectories.remove(index);
        fireTableDataChanged();
    }

    public void setSourceDirectories(List<Directory> sourceDirectories) {
        this.sourceDirectories.clear();
        save();
        if (sourceDirectories == null) return;
        this.sourceDirectories.addAll(sourceDirectories);
        fireTableDataChanged();
    }

    @JsonProperty(value = "directoryConfigs", index = 0)
    @JsonSerialize(using = DirectoryListSerializer.class)
    public @NotNull List<Directory> getSourceDirectories() {
        return this.sourceDirectories;
    }

    public Project resolveProject() {
        return project.getTarget();
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public boolean ingest() {
        for (Directory directory : sourceDirectories) {
            ArrayList<URL> urls = directory.getFiles();
            for (URL url : urls) {
                if (url == null) continue;
                FileInfo fileInfo;
                fileInfo = FileInfo.queryOrCreate(url, resolveProject());
                MetaData metaData = MetaData.queryFirst(fileInfo);
                fileInfo.setAnnotated(metaData != null);
                fileInfo.save();
            }
        }
        return true;
    }

    public boolean clear() {
        return FileInfo.deleteAll(resolveProject(), false);
    }

    public FileInfoList getFileInfoList(int pageSize) {
        return new FileInfoList(this, pageSize);
    }

    @SuppressWarnings("unused")
    @JsonProperty(value = "data", index = 1)
    public FileInfoIterator getFileInfoIterator() {
        return new FileInfoIterator(resolveProject());
    }

    public @NotNull List<FileInfo> getFiles(boolean nonAnnotatedOnly, int length, int offset) {
        return FileInfo.query(resolveProject(), nonAnnotatedOnly, length, offset);
    }

    public FileInfo getNextFile() {
        return FileInfo.queryFirst(project.getTarget(), true);
    }

    public boolean save() {
        BoxStore db = DB.getDb();
        if (db == null) return false;
        Box<DataManager> box = db.boxFor(DataManager.class);
        if (box == null) return false;
        this.id = box.put(this);
        return true;
    }

    @Override
    public String toString() {
        return "{\"directories\":" + sourceDirectories
                + "}";
    }

    @Override
    public String getColumnName(int column)
    {
        switch (column) {
            case 0:
                return getMessageFromBundle("translations/text", "column.directory");
            case 1:
                return getMessageFromBundle("translations/text", "column.recursive");
            case 2:
                return getMessageFromBundle("translations/text", "column.fileExtensions");
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Directory directory = sourceDirectories.get(rowIndex);
        switch (columnIndex) {
            case 1:
                directory.setRecursive((boolean) aValue);
                directory.save();
                break;
            case 2:
                directory.setFileExtensions((String) aValue);
                directory.save();
                break;
        }

        fireTableCellUpdated(rowIndex, columnIndex);// notify listeners
    }

    @Override
    public int getRowCount() {
        return sourceDirectories.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return sourceDirectories.get(rowIndex).resolveRootDirectory().getName();
            case 1:
                return sourceDirectories.get(rowIndex).isRecursive();
            case 2:
                return sourceDirectories.get(rowIndex).getFileExtensionsAsString();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
}
