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

import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class FileInfoList extends AbstractTableModel {
    @NotNull
    private final DataManager dataManager;
    @NotNull
    private ArrayList<FileInfo> files;
    private final int pageSize;

    private int currentPage = 0;

    public FileInfoList(@NotNull DataManager dataManager, int pageSize) {
        this.dataManager = dataManager;
        files = dataManager.getFiles(false, pageSize, 0);
        this.pageSize = pageSize;
    }

    public ArrayList<FileInfo> getFiles() {
        return new ArrayList<>(files);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage < 0) currentPage = 0;
        files = dataManager.getFiles(false, pageSize, currentPage * pageSize);
        this.currentPage = currentPage;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "File";
            case 1:
                return "Annotated";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return files.get(rowIndex).getId();
            case 1:
                return files.get(rowIndex).isAnnotated();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
}
