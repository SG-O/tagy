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

package de.sg_o.lib.tagy.util;

import com.couchbase.lite.Expression;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.data.MetaData;
import de.sg_o.lib.tagy.db.DbConstants;
import de.sg_o.lib.tagy.db.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MetaDataIterator implements Iterator<MetaData> {
    public static final int bufferSize = 100;

    @NotNull
    private transient final Project project;
    private transient final ArrayList<MetaData> buffer = new ArrayList<>(bufferSize);
    private transient int index = -1;
    private transient int page = -1;

    public MetaDataIterator(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public boolean hasNext() {
        int tmpIndex = index + 1;
        int neededPage = tmpIndex / bufferSize;
        int indexInPage = tmpIndex % bufferSize;
        if (neededPage != page) {
            page = neededPage;
            updateBuffer(neededPage);
        }
        return buffer.size() > indexInPage;
    }

    @Override
    public MetaData next() {
        index++;
        int neededPage = index / bufferSize;
        int indexInPage = index % bufferSize;
        if (neededPage != page) {
            page = neededPage;
            updateBuffer(page);
        }
        return buffer.get(indexInPage);
    }

    @Override
    public void remove() {
    }

    @Override
    public void forEachRemaining(Consumer<? super MetaData> action) {
        Iterator.super.forEachRemaining(action);
    }

    private void updateBuffer(int page) {
        List<MetaData> batch = getMetaDataBatch(bufferSize, page);
        buffer.clear();
        buffer.addAll(batch);
    }

    public @NotNull ArrayList<MetaData> getMetaDataBatch(int count, int batch) {
        if (count < 1) count = 1;
        if (batch < 0) batch = 0;
        int finalCount = count;
        int finalOffset = batch * count;
        QuerySpec query = (from) -> from.limit(Expression.intValue(finalCount), Expression.intValue(finalOffset));
        ArrayList<String> ids = project.queryData(DbConstants.META_COLLECTION_NAME, query, count);
        ArrayList<MetaData> metaDataList = new ArrayList<>();
        if (ids == null) return metaDataList;
        for (String id : ids) {
            try {
                FileInfo info = new FileInfo(id, project);
                metaDataList.add(info.getMetaData(project));
            } catch (Exception ignored) {
            }
        }
        return metaDataList;
    }
}