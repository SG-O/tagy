package de.sg_o.lib.tagy.util;

import com.couchbase.lite.Expression;
import de.sg_o.lib.tagy.Project;
import de.sg_o.lib.tagy.data.FileInfo;
import de.sg_o.lib.tagy.db.DbConstants;
import de.sg_o.lib.tagy.db.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FileInfoIterator implements Iterator<FileInfo> {
    public static final int bufferSize = 100;

    @NotNull
    private transient final Project project;
    private transient final ArrayList<FileInfo> buffer = new ArrayList<>(bufferSize);
    private transient int index = -1;
    private transient int page = -1;

    public FileInfoIterator(@NotNull Project project) {
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
    public FileInfo next() {
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
    public void forEachRemaining(Consumer<? super FileInfo> action) {
        Iterator.super.forEachRemaining(action);
    }

    private void updateBuffer(int page) {
        List<FileInfo> batch = getFileInfoBatch(bufferSize, page);
        buffer.clear();
        buffer.addAll(batch);
    }

    public @NotNull ArrayList<FileInfo> getFileInfoBatch(int count, int batch) {
        if (count < 1) count = 1;
        if (batch < 0) batch = 0;
        int finalCount = count;
        int finalOffset = batch * count;
        QuerySpec query = (from) -> from.limit(Expression.intValue(finalCount), Expression.intValue(finalOffset));
        ArrayList<String> ids = project.queryData(DbConstants.DATA_COLLECTION_NAME, query, count);
        ArrayList<FileInfo> metaDataList = new ArrayList<>();
        if (ids == null) return metaDataList;
        for (String id : ids) {
            try {
                metaDataList.add(new FileInfo(id, project));
            } catch (Exception ignored) {
            }
        }
        return metaDataList;
    }
}
