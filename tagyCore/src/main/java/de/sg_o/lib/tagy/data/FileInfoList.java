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

import de.sg_o.lib.tagy.util.PagedList;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

public class FileInfoList extends AbstractTableModel {
    @NotNull
    private final PagedList<FileInfo> files;
    private final int pageSize;

    private int currentPage = 0;

    public FileInfoList(@NotNull DataManager dataManager, int pageSize) {
        files = dataManager.getFiles(false, pageSize);
        this.pageSize = pageSize;
    }

    public List<FileInfo> getFiles() {
        int toIndex = (currentPage + 1) * pageSize;
        if (toIndex > files.size()) {
            toIndex = files.size();
        }
        return files.subList(currentPage * pageSize, toIndex);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        if (page < 0) page = 0;
        int firsIndex = page * pageSize;
        if (firsIndex >= files.size()) {
            return;
        }
        files.preparePages(page * pageSize);
        this.currentPage = page;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        int toIndex = (currentPage + 1) * pageSize;
        if (toIndex > files.size()) {
            return files.size() % pageSize;
        }
        return pageSize;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return getMessageFromBundle("translations/text", "column.file");
            case 1:
                return getMessageFromBundle("translations/text", "column.annotated");
            default:
                return null;
        }
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int index = rowIndex + (currentPage * pageSize);
        if (index < 0 || index >= files.size()) return null;
        switch (columnIndex) {
            case 0:
                return files.get(rowIndex + (currentPage * pageSize)).getAbsolutePath();
            case 1:
                return files.get(rowIndex + (currentPage * pageSize)).isAnnotated();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
}
