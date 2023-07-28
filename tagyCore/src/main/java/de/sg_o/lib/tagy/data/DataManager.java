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

import com.couchbase.lite.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.db.DbConstants;
import de.sg_o.lib.tagy.db.QuerySpec;
import de.sg_o.lib.tagy.util.FileInfoIterator;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({ "listenerList"})
public class DataManager extends AbstractTableModel {
    @NotNull
    private final ArrayList<Directory> sourceDirectories;
    @NotNull
    private transient final Project project;

    public DataManager(@NotNull Project project) {
        this.sourceDirectories = new ArrayList<>();
        this.project = project;
        loadConfig();
    }

    public boolean loadConfig() {
        Document document = project.getData(DbConstants.CONFIG_COLLECTION_NAME, DbConstants.DIRECTORY_CONFIG_DOCUMENT_NAME);
        if (document == null) return false;
        Array directories = document.getArray("directories");
        if (directories == null) return false;
        for (int i = 0; i < directories.count(); i++) {
            Dictionary directory = directories.getDictionary(i);
            if (directory == null) {
                continue;
            }
            this.sourceDirectories.add(new Directory(directory));
        }
        return true;
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

    public void setSourceDirectories(@NotNull List<Directory> sourceDirectories) {
        this.sourceDirectories.clear();
        this.sourceDirectories.addAll(sourceDirectories);
    }

    public @NotNull ArrayList<Directory> getSourceDirectories() {
        return new ArrayList<>(sourceDirectories);
    }

    public boolean ingest() {
        ArrayList<MutableDocument> documents = new ArrayList<>();
        for (Directory directory : sourceDirectories) {
            ArrayList<File> files = directory.getFiles();
            for (File file : files) {
                FileInfo fileInfo = new FileInfo(file);
                Document document = project.getData(DbConstants.META_COLLECTION_NAME, fileInfo.getId());
                fileInfo.setAnnotated(document != null);
                fileInfo.save(project);
            }
        }
        return project.saveData(DbConstants.DATA_COLLECTION_NAME, documents);
    }

    public boolean clear() {
        return project.clearCollection(DbConstants.DATA_COLLECTION_NAME);
    }

    public FileInfoList getFileInfoList(int pageSize) {
        return new FileInfoList(this, pageSize);
    }

    @SuppressWarnings("unused")
    @JsonProperty("data")
    public FileInfoIterator getFileInfoIterator() {
        return new FileInfoIterator(project);
    }

    public @NotNull ArrayList<FileInfo> getFiles(boolean nonAnnotatedOnly, int count, int offset) {
        if (count < 1) count = 1;
        if (offset < 0) offset = 0;
        int finalCount = count;
        int finalOffset = offset;
        QuerySpec query = (from) -> {
            if (nonAnnotatedOnly) {
                return from.where(Expression.property("annotated").equalTo(Expression.booleanValue(false)))
                        .limit(Expression.intValue(finalCount), Expression.intValue(finalOffset));
            } else {
                return from.limit(Expression.intValue(finalCount), Expression.intValue(finalOffset));
            }
        };
        ArrayList<String> ids = project.queryData(DbConstants.DATA_COLLECTION_NAME, query, count);
        ArrayList<FileInfo> files = new ArrayList<>();
        if (ids == null) return files;
        for (String id : ids) {
            files.add(new FileInfo(id, project));
        }
        return files;
    }

    public FileInfo getNextFile() {
        ArrayList<FileInfo> files = getFiles(true, 1, 0);
        if (files.isEmpty()) return null;
        return files.get(0);
    }

    public boolean saveConfig() {
        MutableDocument document = new MutableDocument(DbConstants.DIRECTORY_CONFIG_DOCUMENT_NAME);
        MutableArray array = new MutableArray();
        for (Directory directory : sourceDirectories) {
            array.addDictionary(directory.getEncoded());
        }
        document.setArray("directories", array);
        return project.saveData(DbConstants.CONFIG_COLLECTION_NAME, document);
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
                return "Directory";
            case 1:
                return "Recursive";
            case 2:
                return "File Extensions";
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
        switch (columnIndex) {
            case 1:
                sourceDirectories.get(rowIndex).setRecursive((boolean) aValue);
                break;
            case 2:
                sourceDirectories.get(rowIndex).setFileExtensions((String) aValue);
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
                return sourceDirectories.get(rowIndex).getRootDirectory().getName();
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
