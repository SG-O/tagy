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
import io.objectbox.annotation.Uid;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

@Entity
@JsonIgnoreProperties({ "listenerList", "project"})
@Uid(937091544225069295L)
public class DataManager extends AbstractTableModel {
    private static final HashMap<Long, Long> locks = new HashMap<>();

    @Id
    @Uid(1977117253415533500L)
    private Long id;
    @Uid(4546151216607454017L)
    private final ToMany<DataSource> dataSources = new ToMany<>(this, DataManager_.dataSources);
    @Uid(804797712125925741L)
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

    public void addDataSource(@NotNull File directory) {
        DataSource newDirectory = new DataSource(directory, true);
        dataSources.add(newDirectory);
        fireTableDataChanged();
    }

    public void removeDataSource(int index) {
        if (index < 0 || index >= dataSources.size()) return;
        this.dataSources.remove(index);
        fireTableDataChanged();
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources.clear();
        save();
        if (dataSources == null) return;
        this.dataSources.addAll(dataSources);
        fireTableDataChanged();
    }

    @JsonProperty(value = "dataSources", index = 0)
    @JsonSerialize(using = DataSourceListSerializer.class)
    public @NotNull List<DataSource> getDataSources() {
        return this.dataSources;
    }

    public Project resolveProject() {
        return project.getTarget();
    }

    public ToOne<Project> getProject() {
        return project;
    }

    public boolean ingest() {
        BoxStore db = DB.getDb();
        Project resolvedProject = resolveProject();
        if (db == null || resolvedProject == null) return false;
        db.runInTx(() -> {
            for (DataSource directory : dataSources) {
                ArrayList<URL> urls = directory.getFiles();
                for (URL url : urls) {
                    if (url == null) continue;
                    FileInfo fileInfo;
                    fileInfo = FileInfo.openOrCreate(url, resolveProject());
                    MetaData metaData = MetaData.queryFirst(fileInfo, resolvedProject);
                    fileInfo.setAnnotated(metaData != null);
                    fileInfo.save();
                }
            }
        });
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
        return FileInfo.queryFirst(project.getTarget());
    }

    public @NotNull List<FileInfo> checkOutFiles(boolean nonAnnotatedOnly, int count, long checkoutFor) {
        Long lock = getLock(this.id);
        QueryBoxSpec<FileInfo> qbs = qb -> {
            if (nonAnnotatedOnly) {
                qb = qb.apply(FileInfo_.annotated.equal(false));
            }
            return qb.apply(FileInfo_.checkedOutUntil.isNull().or(FileInfo_.checkedOutUntil.less(new Date())));
        };
        AtomicReference<List<FileInfo>> fileInfos = new AtomicReference<>(new ArrayList<>());
        BoxStore db = DB.getDb();
        if (db == null) return fileInfos.get();
        synchronized (lock) {
            Date endDate = new Date(new Date().getTime() + checkoutFor);
            db.runInTx(() -> {
                fileInfos.set(FileInfo.query(project.getTarget(), qbs, count, 0));
                for (FileInfo fileInfo : fileInfos.get()) {
                    if (!fileInfo.checkOut(endDate)) {
                        fileInfos.get().remove(fileInfo);
                        continue;
                    }
                    fileInfo.save();
                }
            });
            return fileInfos.get();
        }
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
        return "{\"dataSources\":" + dataSources
                + "}";
    }

    private static Long getLock(Long id) {
        if (!locks.containsKey(id)) {
            locks.put(id, id);
        }
        return locks.get(id);
    }

    @Override
    public String getColumnName(int column)
    {
        switch (column) {
            case 0:
                return getMessageFromBundle("translations/text", "column.dataSource");
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
        DataSource directory = dataSources.get(rowIndex);
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
        return dataSources.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return dataSources.get(rowIndex).resolveSource().getName();
            case 1:
                return dataSources.get(rowIndex).isRecursive();
            case 2:
                return dataSources.get(rowIndex).getFileExtensionsAsString();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
}
